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
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Vessel
public class AutomataService implements IAutomataService {
    private static final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("AutomataService").grabLogger("AutomataService");

    private IMicroBusDistributor busDistributor = DInjector.getService(InjectionRepoJavaFX.class).getBusDistributor();
    private IJFXManagerino jfxManagerino = DInjector.getService(IJFXManagerino.class);
    private final AutomataIO automataIO;
    private final List<String> knownSimulations = new ArrayList<>();

    public AutomataService() {
        this.automataIO = new AutomataIO();
    }

    @Override
    public void createDefaultFolderAndFile(String filename) {
        try {
            automataIO.createFolderAndDummyFile(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createNewSimulationFromFile(String filename) {
        knownSimulations.add(filename.split("\\.")[0]);
        logger.info("file: {0}",filename.split("\\.")[0] );
        var identifier = jfxManagerino.initNewRootStage(filename, "main", "Willkommen zur Simulation diverser Dinge!", 820, 700);
        jfxManagerino.showStage(JFXManagerino.JFXStageLevel.ROOT, identifier);
        MainController controller = jfxManagerino.getControllerGeneric(filename);
        controller.setSimulationIdentifier(identifier);
        File automataRoot = new File("automata");
        File automatonFileHandler = new File(automataRoot.getAbsolutePath() + "/" + filename + ".java");
        if (automatonFileHandler.exists()) {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            var res = compiler.run(null, null, null, automatonFileHandler.getAbsolutePath());
            logger.info("Res: {0}", res);

            try (URLClassLoader classLoader = new URLClassLoader(new URL[] { new File("automata").toURI().toURL() })){
                Class<?> automatonClass = classLoader.loadClass(filename);
                Automaton automaton = (Automaton) automatonClass.getDeclaredConstructor().newInstance();
                controller.setAutomaton(automaton);
            } catch (IOException | IllegalAccessException | InvocationTargetException | InstantiationException |
                     ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
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
            automataIO.saveContentsToFile(filename.get(), contents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getFileContents(String automatonName) {
        if (automatonName.contains("main")) {
            automatonName = "GOL";
        }
        try {
            return automataIO.getFileContents(automatonName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
