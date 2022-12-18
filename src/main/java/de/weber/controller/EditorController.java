package de.weber.controller;

import de.weber.services.interfaces.IAutomataService;
import de.weber.util.diy_framwork.DInjector;
import de.weber.util.diy_framwork.annotations.Vessel;
import de.weber.util.loggerino.LoggerFactorino;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

@Vessel
public class EditorController {
    private final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("EditorController").grabLogger("EditorController");
    @FXML
    TextArea codeArea;

    public EditorController() { /* Default Constructor */ }

    @FXML
    public void m_onSave(ActionEvent event) {
        Platform.runLater(() -> DInjector.getService(IAutomataService.class).saveContentsToFile(codeArea.getText()));
    }

    @FXML
    public void m_onCompile(ActionEvent event) {
        Platform.runLater(() -> {
            //Placeholder
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
            //Placeholder
        });
    }

    public void setCodeAreaText(String contents) {
        Platform.runLater(() -> codeArea.setText(contents));
    }
}
