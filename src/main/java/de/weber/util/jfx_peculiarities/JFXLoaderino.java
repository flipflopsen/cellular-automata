package de.weber.util.jfx_peculiarities;

import de.weber.util.loggerino.LoggerFactorino;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Popup;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JFXLoaderino {
    private final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("JFXLoaderino").grabLogger("JFXLoaderino");
    private static final Map<String, FXMLLoader> stageLoaderMap = new ConcurrentHashMap<>();
    private static final String STYLE_SHEET = "css/main.css";

    public JFXLoaderino() { /* Default Constructor */ }

    private Parent initFxml(String fxmlFileName, String identifier) throws IOException {
        logger.info("File: {0}", fxmlFileName);
        var fileName = fxmlFileName.split("/")[2].split("\\.")[0];

        stageLoaderMap.put(identifier, new FXMLLoader());
        logger.debug("Added new fxml: {0}", identifier);

        Parent pane;
        try {
            URL url = getClass().getResource(fxmlFileName);
            var loader = stageLoaderMap.get(identifier);
            loader.setLocation(url);
            pane = loader.load();
        } catch (Exception e) {
            throw new IOException(String.format("Could not load FXML! (%s)", e.getMessage()), e);
        }
        return pane;
    }

    public Scene createSceneFromFXML(String identifier, String fxml, int width, int height) throws IOException {
        Parent root = initFxml(fxml, identifier);
        Scene scene = new Scene(root, width, height);

        scene.getStylesheets().add(STYLE_SHEET);

        return scene;
    }

    public Popup createNewPopup(String fxml) throws IOException {
        Popup popup;
        try {
            popup = new Popup();
            var ldr = new FXMLLoader(getClass().getResource(fxml));
            popup.getContent().add(ldr.load());
        } catch (Exception e) {
            throw new IOException(String.format("Could not load FXML! (%s)", e.getMessage()), e);
        }
        return popup;
    }

    public FXMLLoader getLoader(String identifier) {
        return stageLoaderMap.getOrDefault(identifier, null);
    }
}
