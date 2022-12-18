package de.weber.controller;

import de.weber.controller.interfaces.IPopulationController;
import de.weber.services.InjectionRepoJavaFX;
import de.weber.services.interfaces.IAutomataService;
import de.weber.services.interfaces.IMicroBusDistributor;
import de.weber.model.Automaton;
import de.weber.simulation.ISimulationThread;
import de.weber.simulation.SimulationThread;
import de.weber.model.variants.GameOfLifeAutomaton;
import de.weber.util.StaticValidator;
import de.weber.util.diy_framwork.DInjector;
import de.weber.util.diy_framwork.annotations.Vessel;
import de.weber.util.jfx_peculiarities.JFXManagerino;
import de.weber.util.jfx_peculiarities.interfaces.IJFXManagerino;
import de.weber.util.loggerino.LoggerFactorino;

import de.weber.view.StatesView;
import de.weber.view.interfaces.IPopulationPanelView;
import de.weber.view.PopulationPanelView;
import de.weber.view.interfaces.IStatesView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

import java.io.File;
import java.util.*;

@Vessel
public class MainController {
    //--- Constants ---//
    private static final String SET_COLOR_SPECTRUM_ERROR = "Failed to set color spectrum from color pickers for the PopulationPanel!";
    private static final String NEXT_GENERATION_ERROR = "Failed to enter the next generation for Automaton!";

    private String simulationIdentifier = "main";

    //--- Logger ---//
    private final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.grabLogger("MainController");

    //--- Interfaces ---//
    private IMicroBusDistributor busDistributor = DInjector.getService(InjectionRepoJavaFX.class).getBusDistributor();
    private IJFXManagerino jfxManagerino = DInjector.getService(IJFXManagerino.class);
    private Automaton automaton;

    //--- JavaFX ---//
    private IPopulationPanelView populationPanelView;
    private IPopulationController populationController;
    private IStatesView statesView;

    @FXML
    ToggleButton view_torus_btn;
    @FXML
    ScrollPane scrollP;
    @FXML
    ToggleButton start_sim_btn;
    @FXML
    ToggleButton pause_sim_btn;
    @FXML
    Slider sim_pace_slider;
    @FXML
    VBox states;

    //--- Simulation Thread ---//
    private ISimulationThread simulationThread;

    //Constructor
    public MainController() {
        initializeDeps();
        initializeJfx();
    }

    public void initializeDeps() {
        automaton = DInjector.getService(IAutomataService.class).retrieveSimulationFromFile(simulationIdentifier);

        logger.info("Automaton null: {0}", automaton == null);

        populationPanelView = new PopulationPanelView();
        populationController = new PopulationController(populationPanelView);

        populationPanelView.initializePanel(populationController);
        populationPanelView.setAutomaton(automaton);

        simulationThread = new SimulationThread(populationPanelView);
        simulationThread.setAutomaton(automaton);
    }

    public void initializeJfx() {
        Platform.runLater( () -> {
            statesView = new StatesView(this);
            statesView.resetStateColors();

            view_torus_btn.setSelected(automaton.isTorus());

            scrollP.setContent((PopulationPanelView) populationPanelView);

            scrollP.setOnMousePressed(event -> {
                if (event.getButton() == MouseButton.SECONDARY) scrollP.setPannable(true);
            });
            scrollP.setOnMouseReleased(event -> {
                if (event.getButton() == MouseButton.SECONDARY) scrollP.setPannable(true);
            });
            scrollP.setOnMousePressed(event -> {
                if (event.getButton() == MouseButton.PRIMARY) scrollP.setPannable(false);
            });
            scrollP.setOnMouseReleased(event -> {
                if (event.getButton() == MouseButton.PRIMARY) scrollP.setPannable(true);
            });

            sim_pace_slider.setMin(50);
            sim_pace_slider.setMax(500);
            sim_pace_slider.setValue(250);
            sim_pace_slider.valueProperty().addListener(this::simulationPaceHandler);

            view_torus_btn.setSelected(automaton.isTorus());
            setPopulationColorSpectrum();
            populationPanelView.draw();
        });
    }

    //--- Menu bar handlers ---//
    @FXML
    public void a_newActionHandler(ActionEvent actionEvent) {
        newAutomatonDialog();
    }

    @FXML
    public void a_loadActionHandler(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("automata"));
        var chosenFile = chooser.showOpenDialog(jfxManagerino.getStage(JFXManagerino.JFXStageLevel.ROOT, this.simulationIdentifier));
        logger.info("{0}", chosenFile);
        DInjector.getService(IAutomataService.class).createNewSimulationFromFile(chosenFile.getName());
    }

    @FXML
    public void a_editorActionHandler(ActionEvent actionEvent) {
        jfxManagerino.initNewSubStage(simulationIdentifier, simulationIdentifier + "-editor", "editor", simulationIdentifier + " - Code Editor", 720, 800);
        var controller = (EditorController) jfxManagerino.getControllerGeneric(simulationIdentifier + "-editor");
        controller.setCodeAreaText(DInjector.getService(IAutomataService.class).getFileContents(simulationIdentifier));
        controller.setSimulationIdentifier(simulationIdentifier);
        jfxManagerino.showStage(JFXManagerino.JFXStageLevel.SUB, simulationIdentifier + "-editor");
    }

    @FXML
    public void a_quitActionHandler(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            if (simulationThread.isRunning()) {
                simulationThread.stopSimulation();
            }
            var thisStage = (Stage) view_torus_btn.getScene().getWindow();
            thisStage.fireEvent(new WindowEvent(thisStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
    }

    @FXML
    public void p_resizeActionHandler(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            simulationThread.pauseSimulation();
            resizePopulationDialog();
            try {
                setPopulationColorSpectrum();
                simulationThread.startSimulation();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    public void p_deleteActionHandler(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            automaton.clearPopulation();
            populationPanelView.draw();
        });
    }

    @FXML
    public void p_generateActionHandler(ActionEvent actionEvent) {
        onGenerateAndLoadButton(actionEvent);
    }

    @FXML
    public void p_chkTorusActionHandler(ActionEvent actionEvent) {
        onTorusSwitchButton(actionEvent);
    }

    @FXML
    public void p_enlargeActionHandler(ActionEvent actionEvent) {
        onZoomInButton(actionEvent);
    }

    @FXML
    public void p_shrinkActionHandler(ActionEvent actionEvent) {
        onZoomOutButton(actionEvent);
    }

    @FXML
    public void p_save_xmlActionHandler(ActionEvent actionEvent) {
        //Placeholder
    }

    @FXML
    public void p_save_serializedActionHandler(ActionEvent actionEvent) {
        //Placeholder
    }

    @FXML
    public void p_load_xmlActionHandler(ActionEvent actionEvent) {
        //Placeholder
    }

    @FXML
    public void p_load_serializedActionHandler(ActionEvent actionEvent) {
        //Placeholder
    }

    @FXML
    public void p_printActionHandler(ActionEvent actionEvent) {
        //Placeholder
    }

    @FXML
    public void s_stepActionHandler(ActionEvent actionEvent) {
        onCycleOnceButton(actionEvent);
    }

    @FXML
    public void s_startActionHandler(ActionEvent actionEvent) {
        onStartSimulationButton(actionEvent);
    }

    @FXML
    public void s_stopActionHandler(ActionEvent actionEvent) {
        onPauseSimulationButton(actionEvent);
    }

    //--- Button menu bar handlers ---//
    @FXML
    public void onGenerateAndLoadButton(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            view_torus_btn.setSelected(automaton.isTorus());
            try {
                automaton.nextGeneration();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            statesView.resetStateColors();
            setPopulationColorSpectrum();
            populationPanelView.draw();
            start_sim_btn.setDisable(false);
            if (simulationThread != null) {
                simulationThread = null;
            }
        });
    }

    @FXML
    public void onLoadMachineButton(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("automata"));
        var chosenFile = chooser.showOpenDialog(jfxManagerino.getStage(JFXManagerino.JFXStageLevel.ROOT, this.simulationIdentifier));
        logger.info("{0}", chosenFile);
        DInjector.getService(IAutomataService.class).createNewSimulationFromFile(chosenFile.getName());
    }

    @FXML
    public void onResizePopulationButton(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            resizePopulationDialog();
            try {
                setPopulationColorSpectrum();
            } catch (Throwable e) {
                logger.error(SET_COLOR_SPECTRUM_ERROR);
            }
        });
    }

    @FXML
    public void onResetCellsButton(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            automaton.clearPopulation();
            try {
                automaton.nextGeneration();
            } catch (Throwable e) {
                logger.error(NEXT_GENERATION_ERROR);
            }
            populationPanelView.draw();
        });
    }

    @FXML
    public void onRandomPopulationButton(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            try {
                if (automaton != null) {
                    automaton.randomPopulation();
                    automaton.nextGeneration();
                }
            } catch (Throwable e) {
                logger.error(NEXT_GENERATION_ERROR);
            }
            try {
                if (automaton != null) {
                    setPopulationColorSpectrum();
                    populationPanelView.draw();
                }
            } catch (Throwable e) {
                logger.error(SET_COLOR_SPECTRUM_ERROR);
            }
        });
    }

    @FXML
    public void onTorusSwitchButton(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            automaton.setTorus(!automaton.isTorus());
            view_torus_btn.setSelected(automaton.isTorus());
        });
    }

    @FXML
    public void onPrintPopulationButton(ActionEvent actionEvent) {
        //Placeholder
    }

    @FXML
    public void onZoomInButton(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            var actualSize = populationPanelView.getCellSize();
            if (actualSize >= 35) {
                return;
            }

            populationPanelView.resize(actualSize + 1);
            setPopulationColorSpectrum();
        });
    }

    @FXML
    public void onZoomOutButton(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            var actualSize = populationPanelView.getCellSize();
            if (actualSize <= 5) {
                return;
            }
            populationPanelView.resize(actualSize - 1);
            setPopulationColorSpectrum();
        });
    }

    @FXML
    public void onCycleOnceButton(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            try {
                automaton.nextGeneration();
            } catch (Throwable e) {
                logger.error(NEXT_GENERATION_ERROR);
            }
            populationPanelView.draw();
        });
    }

    @FXML
    public void onStartSimulationButton(ActionEvent actionEvent) {
        pause_sim_btn.setSelected(false);
        try {
            setPopulationColorSpectrum();
        } catch (Throwable e) {
            logger.error(SET_COLOR_SPECTRUM_ERROR);
        }

        if (automaton != null && populationPanelView.areColorsSet()) {
            if (simulationThread == null) {
                simulationThread = new SimulationThread(populationPanelView);
                simulationThread.setAutomaton(automaton);
            }
            simulationThread.setSimulationPace((long) sim_pace_slider.getValue());
            logger.debug("Starting sim..");
            simulationThread.startSimulation();
            start_sim_btn.setSelected(true);
            start_sim_btn.setDisable(true);
            pause_sim_btn.setDisable(false);
        } else {
            start_sim_btn.setSelected(false);
            start_sim_btn.setDisable(true);
            pause_sim_btn.setSelected(true);
        }
    }

    @FXML
    public void onPauseSimulationButton(ActionEvent actionEvent) {
        start_sim_btn.setDisable(false);
        start_sim_btn.setSelected(false);
        pause_sim_btn.setSelected(true);
        pause_sim_btn.setDisable(true);
        if (simulationThread.isRunning()) {
            simulationThread.pauseSimulation();
        }
    }


    //--- Helper Methods ---//


    //Helper Method to set the picked colors in the simulation.
    public void setPopulationColorSpectrum() {
        List<Color> colors = statesView.getStateColors().stream()
                .filter(Objects::nonNull)
                .map(ComboBoxBase::getValue)
                .toList();

        populationPanelView.setColorSpectrum(colors);
        populationPanelView.draw();
    }

    public void setActiveState(int state) {
        populationController.setActiveState(state);
    }

    //Helper Method for the "Resize Population" Button -> Dialog
    private synchronized void resizePopulationDialog() {
        Dialog<Pair<String, String>> resizeDialog = new Dialog<>();
        resizeDialog.setTitle("Resize the population.");
        resizeDialog.setHeaderText("Specify a new width and height for the population.");

        resizeDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField width = new TextField();
        width.setPromptText("New Width");
        TextField height = new TextField();
        height.setPromptText("New Height");

        grid.add(new Label("Width: "), 0, 0);
        grid.add(width, 1, 0);
        grid.add(new Label("Height: "), 0, 1);
        grid.add(height, 1, 1);

        resizeDialog.getDialogPane().setContent(grid);

        resizeDialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty()
                .bind(Bindings.createBooleanBinding( () ->
                      !(width.getText().matches(StaticValidator.simulationResizeRegex())) ||
                        !(height.getText().matches(StaticValidator.simulationResizeRegex())),
                      width.textProperty(),
                      height.textProperty()));

        Platform.runLater(width::requestFocus);

        resizeDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(width.getText(), height.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> newSizePair = resizeDialog.showAndWait();

        if (simulationThread != null) {
            simulationThread.pauseSimulation();
        }

        newSizePair.ifPresent(widthHeight -> {
            var w = newSizePair.get().getKey();
            var h= newSizePair.get().getValue();
            try {
                automaton.changeSize(Integer.parseInt(w), Integer.parseInt(h));
                populationPanelView.setAutomaton(automaton);
            } catch (ArrayIndexOutOfBoundsException e) {
                logger.warn("Failed to set the new size because of invalid width/height.");
            }

        });

        if (simulationThread != null) {
            simulationThread.startSimulation();
        }
    }

    private void newAutomatonDialog() {
        Dialog<String> newAutomatonDialog = new Dialog<>();
        newAutomatonDialog.setTitle("Create a new Automaton.");
        newAutomatonDialog.setHeaderText("Enter a name for a new Automaton.");

        newAutomatonDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField name = new TextField();
        name.setPromptText("Identifier: ");

        newAutomatonDialog.getDialogPane().setContent(name);

        newAutomatonDialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty()
                .bind(Bindings.createBooleanBinding( () ->
                                !StaticValidator.isJavaClassNameConform(name.getText()) ||
                                        !DInjector.getService(IAutomataService.class).validateFileExistence(name.getText()),
                                name.textProperty()));

        newAutomatonDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return name.getText();
            }
            return null;
        });

        Optional<String> newAutomatonIdentifier = newAutomatonDialog.showAndWait();
        newAutomatonIdentifier.ifPresent(this::createNewAutomatonFromDialog);
    }
    private void createNewAutomatonFromDialog(String filename) {
        DInjector.getService(IAutomataService.class).createNewSimulationFromFile(filename);
    }

    public synchronized void simulationPaceHandler(ObservableValue<? extends Number> o, Number old, Number n) {
        if (this.simulationThread != null)
            this.simulationThread.setSimulationPace((long) 550 - n.intValue());
    }

    public Automaton getAutomaton() {
        return this.automaton;
    }

    public VBox getStates() {
        return this.states;
    }

    public void setAutomaton(Automaton automaton) {
        if (simulationThread != null && simulationThread.isRunning()) {
            //simulationThread.pauseSimulation();
            Platform.runLater(() -> {
                start_sim_btn.setSelected(false);
                pause_sim_btn.setSelected(true);
            });
        }
        logger.debug("Setting new automaton into: {0}", simulationIdentifier);
        this.automaton = automaton;

        if (simulationThread == null) {
            simulationThread = new SimulationThread(populationPanelView);
        }

        simulationThread.setAutomaton(automaton);
        populationController.setAutomaton(automaton);
        populationPanelView.setAutomaton(automaton);

        Platform.runLater(() -> {
            statesView.resetStateColors();
            populationPanelView.draw();
            view_torus_btn.setSelected(automaton.isTorus());
        });
    }

    public void setSimulationIdentifier(String simulationIdentifier) {
        this.simulationIdentifier = simulationIdentifier;
    }

    public String getSimulationIdentifier() {
        return this.simulationIdentifier;
    }

    public void stopSimulation() {
        simulationThread.stopSimulation();
    }

    public void pauseSimulation() {
        simulationThread.pauseSimulation();
    }
}
