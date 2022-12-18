package de.weber.services;

import de.weber.controller.MainController;
import de.weber.io.AutomataIO;
import de.weber.model.Automaton;
import de.weber.services.interfaces.IAutomataService;
import de.weber.services.interfaces.IMicroBusDistributor;
import de.weber.util.diy_framwork.DInjector;
import de.weber.util.diy_framwork.annotations.Vessel;
import de.weber.util.jfx_peculiarities.JFXManagerino;
import de.weber.util.jfx_peculiarities.interfaces.IJFXManagerino;
import de.weber.util.loggerino.LoggerFactorino;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Vessel
public class AutomataService implements IAutomataService {
    private static final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("AutomataService").grabLogger("AutomataService");
    private static final String JAVA_FILE_EXTENSION = ".java";
    private static final String AUTOMATA_DIR = "automata";
    private static final String DEFAULT_AUTOMATON_SPECIFIER = "DefaultAutomaton";

    private final AutomataIO automataIO;
    private final List<String> knownSimulations = new ArrayList<>();


    private IMicroBusDistributor busDistributor = DInjector.getService(InjectionRepoJavaFX.class).getBusDistributor();
    private IJFXManagerino jfxManagerino = DInjector.getService(IJFXManagerino.class);


    public AutomataService() {
        this.automataIO = new AutomataIO();
        try {
            automataIO.createDefaultFolderAndAutomaton();
        } catch (IOException e) {
            logger.critical("An error occurred while the creation of the default-automaton!");
        }
    }

    @Override
    public Automaton createDefaultFolderAndFile() {
        try {
            automataIO.createDefaultFolderAndAutomaton();
            knownSimulations.add(DEFAULT_AUTOMATON_SPECIFIER);
            return retrieveSimulationFromFile(DEFAULT_AUTOMATON_SPECIFIER);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void createNewSimulationFromFile(String filename) {
        var automatonName = filename;
        if (filename.contains(JAVA_FILE_EXTENSION)) {
            automatonName = filename.split("\\.")[0];
        }

        logger.info("Filename: {0}, AutomatonName: {1}", filename, automatonName);

        File automataRoot = new File(AUTOMATA_DIR);
        File automatonFileHandler = new File(automataRoot.getAbsolutePath() + File.separator + automatonName + JAVA_FILE_EXTENSION);


        if (!knownSimulations.contains(automatonName)) {
            if (!automatonFileHandler.exists()) {
                try {
                    automataIO.createNewAutomatonWithName(automatonName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            knownSimulations.add(automatonName);
        } else {
            logger.error("The automaton you want to create exists already! Choose another name please.");
            return;
        }

        var identifier = jfxManagerino.initNewRootStage(automatonName, "main", "Willkommen zur Simulation diverser Dinge!", 820, 700);
        jfxManagerino.showStage(JFXManagerino.JFXStageLevel.ROOT, identifier);

        MainController controller = jfxManagerino.getControllerGeneric(automatonName);
        controller.setSimulationIdentifier(identifier);
        controller.initializeDeps();
        controller.initializeJfx();

        if (automatonFileHandler.exists()) {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            var res = compiler.run(null, null, null, automatonFileHandler.getAbsolutePath());

            if (res == 0) {
                try (URLClassLoader classLoader = new URLClassLoader(new URL[] { new File(AUTOMATA_DIR).toURI().toURL() })){
                    Class<?> automatonClass = classLoader.loadClass(automatonName);
                    Automaton automaton = (Automaton) automatonClass.getDeclaredConstructor().newInstance();

                    controller.setAutomaton(automaton);
                } catch (IOException | IllegalAccessException | InvocationTargetException | InstantiationException |
                         ClassNotFoundException | NoSuchMethodException e) {
                    logger.error("Failed to load class: {0}! (in createNewSimulationFromFile)", filename);
                }
            } else {
                logger.error("Failed to compile automaton: {0}! (in createNewSimulationFromFile)", filename);
            }
        }
    }

    @Override
    public Automaton retrieveSimulationFromFile(String filename) {
        if (filename.equals("main")) {
            filename = DEFAULT_AUTOMATON_SPECIFIER;
        }
        File automataRoot = new File(AUTOMATA_DIR);
        File automatonFileHandler = new File(automataRoot.getAbsolutePath() + File.separator + filename + JAVA_FILE_EXTENSION);

        if (automatonFileHandler.exists()) {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            var res = compiler.run(null, null, null, automatonFileHandler.getAbsolutePath());

            if (res == 0) {
                try (URLClassLoader classLoader = new URLClassLoader(new URL[] { new File(AUTOMATA_DIR).toURI().toURL() })){
                    Class<?> automatonClass = classLoader.loadClass(filename);

                    return (Automaton) automatonClass.getDeclaredConstructor().newInstance();
                } catch (IOException | IllegalAccessException | InvocationTargetException | InstantiationException |
                         ClassNotFoundException | NoSuchMethodException e) {
                    logger.error("Failed to load class: {0}! (in retrieveSimulationFromFile)", filename);
                }
            } else {
                logger.error("Failed to compile automaton: {0}! (in retrieveSimulationFromFile)", filename);
            }
        }
        return null;
    }

    @Override
    public String compileAndSetAutomaton(String simulationIdentifier, String filename) {
        OutputStream errorStream = new ByteArrayOutputStream();
        File automataRoot = new File(AUTOMATA_DIR);
        File automatonFileHandler = new File(automataRoot.getAbsolutePath() + File.separator + filename + JAVA_FILE_EXTENSION);

        logger.info("Filename: {0}", filename);

        if (!knownSimulations.contains(filename)) {
            knownSimulations.add(filename);
        }
        if (automatonFileHandler.exists()) {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            var res = compiler.run(null, null, errorStream, automatonFileHandler.getAbsolutePath());

            if (res == 0) {
                MainController controller;

                if (simulationIdentifier.equals("main") || filename.contains(DEFAULT_AUTOMATON_SPECIFIER)) {
                    controller = jfxManagerino.getControllerGeneric(simulationIdentifier);
                } else {
                    controller = jfxManagerino.getControllerGeneric(filename);
                }
                controller.setSimulationIdentifier(simulationIdentifier);

                try (URLClassLoader classLoader = new URLClassLoader(new URL[] { new File(AUTOMATA_DIR).toURI().toURL() })){
                    logger.debug("Trying to load class: {0}", filename);

                    Class<?> automatonClass = classLoader.loadClass(filename);
                    Automaton automaton = (Automaton) automatonClass.getDeclaredConstructor().newInstance();

                    controller.setAutomaton(automaton);
                } catch (IOException | IllegalAccessException | InvocationTargetException | InstantiationException |
                         ClassNotFoundException | NoSuchMethodException e) {
                    logger.error("Failed to load class: {0}! (in compileAndSetAutomaton)", filename);
                }
            } else {
                logger.error("Failed to compile automaton: {0}! (in compileAndSetAutomaton)", filename);
            }
        }
        var result = errorStream.toString();
        try {
            errorStream.close();
        } catch (IOException e) {
            logger.critical("Failed to close the error-stream of the compilation output!");
        }
        return result;
    }

    @Override
    public void saveContentsToFile(String contents) {
        AtomicReference<String> filename = new AtomicReference<>("");
        knownSimulations.forEach(sim -> {
            if (contents.contains(sim)) {
                filename.set(sim);
            }
        });
        try {
            logger.debug("Saving file to {0}", filename.get());
            automataIO.saveContentsToFile(filename.get(), contents);
        } catch (IOException e) {
            logger.error("Failed to save the file contents of: {0}", filename.get());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getFileContents(String automatonName) {
        if (automatonName.contains("main")) {
            automatonName = DEFAULT_AUTOMATON_SPECIFIER;
        }
        try {
            return automataIO.getFileContents(automatonName);
        } catch (IOException e) {
            logger.error("Failed to retrieve the file contents of: {0}", automatonName);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeAutomatonSession(String automatonName) {
        knownSimulations.remove(automatonName);
    }

    @Override
    public void clearAutomataFolder() {
        try {
            automataIO.clearAutomataFolder();
        } catch (IOException e) {
            logger.error("Failed to clear the 'automata' folder!");
        }
    }

    @Override
    public boolean validateFileExistence(String filename) {
        return automataIO.validateFileExistence(filename);
    }
}
