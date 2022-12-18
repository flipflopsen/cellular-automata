package de.weber.util.jfx_peculiarities.interfaces;

import de.weber.util.jfx_peculiarities.JFXManagerino;
import javafx.scene.Group;
import javafx.stage.Stage;

public interface IJFXManagerino {

    void initializeService(Stage primaryStage);
    void initMainStage(String title);

    String initNewRootStage(String identifier, String filename, String title, int width, int height);
    String initNewSubStage(String parentIdentifier, String identifier, String filename, String title, int width, int height);

    void createNewPopup(String identifier, String filename, String title);

    void showStage(JFXManagerino.JFXStageLevel level, String stageIdentifier);

    void showStage(JFXManagerino.JFXStageLevel level, String stageIdentifier, String sceneIdentifier);

    void changeScene(JFXManagerino.JFXStageLevel level, String stageIdentifier, String sceneIdentifier);

    void changeAndShowScene(JFXManagerino.JFXStageLevel level, String stageIdentifier, String sceneIdentifier);

    void constructSceneFromGroup(JFXManagerino.JFXStageLevel level, String sceneIdentifier, Group group, int width, int height);

    void constructSceneFromFXML(JFXManagerino.JFXStageLevel level, String sceneIdentifier, String fxmlFileName, int width, int height);

    <T> T getControllerGeneric(String stageIdentifier);

    Stage getStage(JFXManagerino.JFXStageLevel level, String stageIdentifier);
}
