package de.weber.io;

import de.weber.util.diy_framwork.annotations.Vessel;
import de.weber.util.loggerino.LoggerFactorino;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

@Vessel
public class AutomataIO {
    private static final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("AutomataIO").grabLogger("AutomataIO");

    public AutomataIO() {
        //Default Constructor
    }

    public void createFolderAndDummyFile(String automatonName) throws IOException {
        File automatonDir = new File("automata");
        if (!automatonDir.exists()) {
            automatonDir.mkdirs();
        }
        File automatonFile = new File(automatonDir.getAbsolutePath() + "/" + automatonName + ".java");
        if (!automatonFile.exists()) {
            try (FileWriter writer = new FileWriter(automatonFile.getAbsoluteFile())) {
                var template = AutomatonTemplateFiles.GOL_TEMPLATE.replace("{0}", automatonName);
                writer.write(template);
            }
            logger.info("Wrote GOL Template to: {0}", automatonFile.getAbsolutePath());
        }
    }

    public void saveContentsToFile(String automatonName, String contents) throws IOException {
        File automatonDir = new File("automata");
        File automatonFile = new File(automatonDir.getAbsolutePath() + "/" + automatonName + ".java");
        if (automatonFile.exists()) {
            new FileWriter(automatonFile, false).close();
            automatonFile = new File(automatonDir.getAbsolutePath() + "/" + automatonName + ".java");
            try (FileWriter writer = new FileWriter(automatonFile.getAbsoluteFile())) {
                writer.write(contents);
            }
            logger.info("Wrote Template to: {0}", automatonFile.getAbsolutePath());
        }
    }

    public String getFileContents(String automatonName) throws IOException {
        StringBuilder contents = new StringBuilder();
        File automatonDir = new File("automata");
        File automatonFile = new File(automatonDir.getAbsolutePath() + "/" + automatonName + ".java");
        if (automatonFile.exists()) {
            automatonFile = new File(automatonDir.getAbsolutePath() + "/" + automatonName + ".java");
            var lines = Files.readAllLines(automatonFile.toPath());
            lines.forEach(line -> contents.append(line + "\n"));
            logger.info("Red File-Contents from: {0}", automatonFile.getAbsolutePath());
        }
        return contents.toString();
    }
}
