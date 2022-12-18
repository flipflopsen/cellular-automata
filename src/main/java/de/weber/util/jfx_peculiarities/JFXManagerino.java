package de.weber.util.jfx_peculiarities;

import de.weber.controller.MainController;
import de.weber.util.diy_framwork.annotations.Vessel;
import de.weber.util.jfx_peculiarities.interfaces.IJFXManagerino;
import de.weber.util.loggerino.LoggerFactorino;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Vessel
public class JFXManagerino implements IJFXManagerino {
    private final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("JFXManagerino").grabLogger("JFXManagerino");
    private final JFXLoaderino loader = new JFXLoaderino();
    private static final Map<JFXStageLevel, Map<String, Scene>> stageLevelSceneIdentifierMap = new ConcurrentHashMap<>();
    private static final Map<JFXStageLevel, Map<String, Stage>> stageMap = new ConcurrentHashMap<>();
    private static final Map<String, List<String>> stageReferenceMap = new ConcurrentHashMap<>();

    public JFXManagerino() {

    }
    public JFXManagerino(Stage primaryStage) {
        stageLevelSceneIdentifierMap.put(JFXStageLevel.ROOT, new ConcurrentHashMap<>());
        stageLevelSceneIdentifierMap.put(JFXStageLevel.SUB, new ConcurrentHashMap<>());
        stageLevelSceneIdentifierMap.put(JFXStageLevel.POPUP, new ConcurrentHashMap<>());
        stageMap.put(JFXStageLevel.ROOT, new ConcurrentHashMap<>());
        stageMap.get(JFXStageLevel.ROOT).put("main", primaryStage);
        stageReferenceMap.put("main", new ArrayList<>());
    }

    public enum JFXStageLevel {
        ROOT, //TODO: Think about renaming it to PRIMARY or similar.
        SUB,
        POPUP,
    }

    public void initializeService(Stage primaryStage) {
        stageLevelSceneIdentifierMap.put(JFXStageLevel.ROOT, new ConcurrentHashMap<>());
        stageLevelSceneIdentifierMap.put(JFXStageLevel.SUB, new ConcurrentHashMap<>());
        stageLevelSceneIdentifierMap.put(JFXStageLevel.POPUP, new ConcurrentHashMap<>());
        stageMap.put(JFXStageLevel.ROOT, new ConcurrentHashMap<>());
        stageMap.get(JFXStageLevel.ROOT).put("main", primaryStage);
        stageReferenceMap.put("main", new CopyOnWriteArrayList<>());
    }

    public void initMainStage(String title) {
        try {
            var scene = loader.createSceneFromFXML("main", "/fxml/main.fxml", 820, 700);
            //scene.getRoot().setStyle();
            scene.getStylesheets().add("css/dark.css");
            stageLevelSceneIdentifierMap.get(JFXStageLevel.ROOT).put("main", scene);
            var stage = stageMap.get(JFXStageLevel.ROOT).get("main");
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setOnCloseRequest(we -> System.exit(1));
        } catch (IOException e) {
            logger.error("Failed to find the FXML file: {} ", "main.fxml");
            var stage = stageMap.get(JFXStageLevel.ROOT).get("main");
            stage.setTitle(title);
            logger.info("Program execution will continue without a set Scene on the Main stage, keep in mind to set it manually!");
        }
    }

    public String initNewRootStage(String identifier, String filename, String title, int width, int height) {
        String newIdentifier = identifier;
        try {
            if (stageMap.getOrDefault(JFXStageLevel.ROOT, null) == null) {
                stageMap.put(JFXStageLevel.ROOT, new ConcurrentHashMap<>());
            }
            if (stageMap.get(JFXStageLevel.ROOT).getOrDefault(identifier, null) == null) {
                stageMap.get(JFXStageLevel.ROOT).put(identifier, new Stage());
                logger.info("inserted new stage");
            } else {
                var hit = false;
                var ctr = 1;
                while (!hit) {
                    newIdentifier = newIdentifier + ctr;
                    if (!stageMap.get(JFXStageLevel.ROOT).containsKey(newIdentifier)) {
                        stageMap.get(JFXStageLevel.ROOT).put(newIdentifier, new Stage());
                        hit = true;
                    }
                    ctr++;
                }
            }
            if (!stageReferenceMap.containsKey(newIdentifier)) {
                stageReferenceMap.put(newIdentifier, new CopyOnWriteArrayList<>());
            }

            var scene = loader.createSceneFromFXML(newIdentifier, "/fxml/" + filename + ".fxml", width, height);
            scene.getStylesheets().add("css/dark.css");
            stageLevelSceneIdentifierMap.get(JFXStageLevel.ROOT).put(newIdentifier, scene);
            var stage = stageMap.get(JFXStageLevel.ROOT).get(newIdentifier);
            stage.setTitle(title);
            stage.setScene(scene);

            setOnCloseHandling(newIdentifier, stage);

            return newIdentifier;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Failed to locate the FXML file: {} ", filename);
            var stage = stageMap.get(JFXStageLevel.ROOT).get(newIdentifier);
            stage.setTitle(title);
            logger.info("Program execution will continue without a set Scene on the Main stage, keep in mind to set it manually!");
            return newIdentifier;
        }
    }

    public String initNewSubStage(String parentIdentifier, String identifier, String filename, String title, int width, int height) {
        String newIdentifier = identifier;
        try {
            if (stageMap.getOrDefault(JFXStageLevel.SUB, null) == null) {
                stageMap.put(JFXStageLevel.SUB, new ConcurrentHashMap<>());
            }
            if (stageMap.get(JFXStageLevel.SUB).getOrDefault(newIdentifier, null) == null) {
                stageMap.get(JFXStageLevel.SUB).put(newIdentifier, new Stage());
            } else {
                var hit = false;
                var ctr = 1;
                while (!hit) {
                    newIdentifier = newIdentifier + ctr;
                    if (!stageMap.get(JFXStageLevel.SUB).containsKey(newIdentifier)) {
                        stageMap.get(JFXStageLevel.SUB).put(newIdentifier, new Stage());
                        hit = true;
                    }
                    ctr++;
                }
            }
            if (!stageReferenceMap.get(parentIdentifier).contains(newIdentifier)) {
                stageReferenceMap.get(parentIdentifier).add(newIdentifier);
            } else {
                logger.error("Failed to find the parentIdentifier {0} for subStage {1}!", parentIdentifier, newIdentifier);
            }

            var scene = loader.createSceneFromFXML(newIdentifier, "/fxml/" + filename + ".fxml", width, height);
            scene.getStylesheets().add("css/dark.css");
            stageLevelSceneIdentifierMap.get(JFXStageLevel.SUB).put(newIdentifier, scene);
            var stage = stageMap.get(JFXStageLevel.SUB).get(newIdentifier);

            setOnCloseHandling(parentIdentifier, stage);

            stage.setTitle(title);
            stage.setScene(scene);
            return newIdentifier;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Failed to locate the FXML file: {} ", filename);
            var stage = stageMap.get(JFXStageLevel.SUB).get(newIdentifier);
            stage.setTitle(title);
            logger.info("Program execution will continue without a set Scene on the Main stage, keep in mind to set it manually!");
            return newIdentifier;
        }
    }

    private void setOnCloseHandling(String parentIdentifier, Stage stage) {
        String finalNewIdentifier = parentIdentifier;
        stage.setOnCloseRequest(we -> {
            if (!finalNewIdentifier.contains("editor")) {
                var controller = (MainController) getControllerGeneric(finalNewIdentifier);
                controller.stopSimulation();
            }
            stageReferenceMap.get(finalNewIdentifier).forEach(sub -> {
                if (stageMap.get(JFXStageLevel.ROOT).containsKey(sub))
                    stageMap.get(JFXStageLevel.ROOT).get(sub).close();
                if (stageMap.get(JFXStageLevel.SUB).containsKey(sub))
                    stageMap.get(JFXStageLevel.SUB).get(sub).close();
            });
        });
    }

    public void createNewPopup(String identifier, String filename, String title) {
        try {
            var popup = loader.createNewPopup(filename);
            stageLevelSceneIdentifierMap.get(JFXStageLevel.POPUP).put(identifier, popup.getScene());
        } catch (IOException e) {
            logger.error("Failed to locate the FXML file: {} ", filename + ".fxml!");
        }
    }

    public void showStage(JFXStageLevel level, String stageIdentifier) {
        var stage = stageMap.getOrDefault(level, null).getOrDefault(stageIdentifier, null);
        if (stage == null) {
            throw new IllegalArgumentException("Specified stage level does not contain any stage!");
        } else {
            stage.show();
        }
    }

    public void showStage(JFXStageLevel level, String stageIdentifier, String sceneIdentifier) {
        var stage = stageMap.getOrDefault(level, null).getOrDefault(stageIdentifier, null);
        if (stage == null) {
            throw new IllegalArgumentException("Specified stage level does not contain any stage!");
        } else {
            stage.setScene(stageLevelSceneIdentifierMap.get(level).get(sceneIdentifier));
            stage.show();
        }
    }

    public void changeScene(JFXStageLevel level, String stageIdentifier, String sceneIdentifier) {
        stageMap.get(level).get(stageIdentifier).setScene(stageLevelSceneIdentifierMap.get(level).get(sceneIdentifier));
    }

    public void changeAndShowScene(JFXStageLevel level, String stageIdentifier, String sceneIdentifier) {
        stageMap.get(level).get(stageIdentifier).setScene(stageLevelSceneIdentifierMap.get(level).get(sceneIdentifier));
        stageMap.get(level).get(stageIdentifier).show();
    }

    public void constructSceneFromGroup(JFXStageLevel level, String sceneIdentifier, Group group, int width, int height) {
        stageLevelSceneIdentifierMap.get(level).putIfAbsent(sceneIdentifier, new Scene(group, width, height));
    }

    public void constructSceneFromFXML(JFXStageLevel level, String sceneIdentifier, String fxmlFileName, int width, int height) {
        try {
            var scene = loader.createSceneFromFXML(sceneIdentifier, fxmlFileName, width, height);
            stageLevelSceneIdentifierMap.get(level).putIfAbsent(sceneIdentifier, scene);
        } catch (IOException e) {
            logger.error("failed to find the FXML file: {} ",fxmlFileName);
        }
    }

    public Stage getStage(JFXStageLevel level, String stageIdentifier) {
        return stageMap.get(level).get(stageIdentifier);
    }

    public <T> T getControllerGeneric(String stageIdentifier) {
        return loader.getLoader(stageIdentifier).getController();
    }

}
