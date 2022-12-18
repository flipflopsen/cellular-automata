package de.weber.controller;

import de.weber.services.interfaces.IAutomataService;
import de.weber.util.diy_framwork.DInjector;
import de.weber.util.diy_framwork.annotations.Vessel;
import de.weber.util.loggerino.LoggerFactorino;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.Objects;

@Vessel
public class EditorController {
    private final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("EditorController").grabLogger("EditorController");
    private String simulationIdentifier;
    @FXML
    TextArea codeArea;
    @FXML
    Button bCompile;
    @FXML
    Button bSave;

    public EditorController() { /* Default Constructor */ }

    @FXML
    public void m_onSave(ActionEvent event) {
        Platform.runLater(() -> DInjector.getService(IAutomataService.class).saveContentsToFile(codeArea.getText()));
    }

    @FXML
    public void m_onCompile(ActionEvent event) {
        Platform.runLater(() -> {
            var filename = getAutomatonName();

            DInjector.getService(IAutomataService.class).saveContentsToFile(codeArea.getText());
            var compilationErrorOutput = DInjector.getService(IAutomataService.class).compileAndSetAutomaton(simulationIdentifier, filename);
            createCompilationResultAlert(compilationErrorOutput);
        });
    }

    @FXML
    public void m_onExit(ActionEvent event) {
        Platform.runLater(() -> {
            var thisStage = (Stage) codeArea.getScene().getWindow();
            thisStage.close();
        });
    }

    @FXML
    public void onSaveBtn(ActionEvent event) {
        Platform.runLater(() -> DInjector.getService(IAutomataService.class).saveContentsToFile(codeArea.getText()));
    }

    @FXML
    public void onCompileBtn(ActionEvent event) {
        Platform.runLater(() -> {
            var filename = getAutomatonName();

            DInjector.getService(IAutomataService.class).saveContentsToFile(codeArea.getText());
            var compilationErrorOutput = DInjector.getService(IAutomataService.class).compileAndSetAutomaton(simulationIdentifier, filename);
            createCompilationResultAlert(compilationErrorOutput);
        });
    }

    public void setCodeAreaText(String contents) {
        Platform.runLater(() -> codeArea.setText(contents));
    }

    public void setSimulationIdentifier(String identifier) {
        logger.debug("Setting identifier: {0} into editor", identifier);
        simulationIdentifier = identifier;
    }

    private String getAutomatonName() {
        if (!Objects.equals(simulationIdentifier, "")) {
            return simulationIdentifier;
        }
        var paragraphs = codeArea.getParagraphs();
        var line = paragraphs.get(11);
        var lineText = line.toString();
        logger.debug("Found: {0}", lineText);
        var automatonName = lineText.trim()
                        .replace("public", "")
                        .replace("() {", "")
                        .trim();
        logger.info("Automaton-Name: {0}", automatonName);
        return automatonName;
    }

    private void createCompilationResultAlert(String compilationResult) {
        Alert alert;
        if (compilationResult.length() > 0) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Compilation failed!");
            alert.setHeaderText("The compilation failed!");
            alert.setContentText(compilationResult);
        } else {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Compilation successful!");
            alert.setHeaderText("The compilation succeeded!");
            alert.setContentText("You can try the simulation now, have fun! :)");
        }
        alert.showAndWait();
    }
}
