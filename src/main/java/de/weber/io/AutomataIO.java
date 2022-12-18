package de.weber.io;

import de.weber.util.loggerino.LoggerFactorino;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class AutomataIO {
    private static final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("AutomataIO").grabLogger("AutomataIO");

    private static final String JAVA_FILE_EXTENSION = ".java";

    public AutomataIO() {
        //Default Constructor
    }

    public void createDefaultFolderAndAutomaton() throws IOException {
        File automatonDir = new File("automata");
        if (!automatonDir.exists()) {
            automatonDir.mkdirs();
            logger.debug("Created 'automata' folder at: {0}", automatonDir.getAbsolutePath());
        }
        createNewAutomatonWithName("DefaultAutomaton");
    }

    public void createNewAutomatonWithName(String name) throws IOException {
        File automatonDir = new File("automata");
        File automatonFile = new File(automatonDir.getAbsolutePath() + File.separator + name + JAVA_FILE_EXTENSION);

        if (!automatonFile.exists()) {
            try (FileWriter writer = new FileWriter(automatonFile.getAbsoluteFile())) {
                var template = AutomatonTemplates.DEFAULT_TEMPLATE.replace("{0}", name);
                writer.write(template);
            }
            logger.debug("Wrote {0} to: {1}", name, automatonFile.getAbsolutePath());
        }
    }

    public void saveContentsToFile(String automatonName, String contents) throws IOException {
        File automatonDir = new File("automata");
        File automatonFile = new File(automatonDir.getAbsolutePath() + File.separator + automatonName + JAVA_FILE_EXTENSION);

        if (automatonFile.exists()) {
            //Clear file contents.
            new FileWriter(automatonFile, false).close();
            automatonFile = new File(automatonDir.getAbsolutePath() + File.separator + automatonName + JAVA_FILE_EXTENSION);

            try (FileWriter writer = new FileWriter(automatonFile.getAbsoluteFile())) {
                writer.write(contents);
            }

            logger.debug("Wrote Template to: {0}", automatonFile.getAbsolutePath());
        }
    }

    public String getFileContents(String automatonName) throws IOException {
        StringBuilder contents = new StringBuilder();
        File automatonDir = new File("automata");
        File automatonFile = new File(automatonDir.getAbsolutePath() + File.separator + automatonName + JAVA_FILE_EXTENSION);

        if (automatonFile.exists()) {
            automatonFile = new File(automatonDir.getAbsolutePath() + File.separator + automatonName + JAVA_FILE_EXTENSION);

            var lines = Files.readAllLines(automatonFile.toPath());
            lines.forEach(line -> contents.append(line).append("\n"));

            logger.debug("Red File-Contents from: {0}", automatonFile.getAbsolutePath());
        }
        return contents.toString();
    }

    public void clearAutomataFolder() throws IOException {
        File automatonDirHandle = new File("automata");
        if (automatonDirHandle.exists() && automatonDirHandle.isDirectory()) {
            for (File f : Objects.requireNonNull(automatonDirHandle.listFiles())) {
                if (f.getAbsoluteFile().getName().contains(".class")) {
                    Files.delete(f.toPath());
                }
            }
        }
    }

    public boolean validateFileExistence(String filename) {
        File automatonDirHandle = new File("automata");
        if (automatonDirHandle.exists() && automatonDirHandle.isDirectory()) {
            for (File f : Objects.requireNonNull(automatonDirHandle.listFiles())) {
                if (f.getAbsoluteFile().getName().equals(filename + JAVA_FILE_EXTENSION)) {
                    return false;
                }
            }
        }
        return true;
    }
}
