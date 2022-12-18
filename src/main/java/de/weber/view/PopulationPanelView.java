package de.weber.view;

import de.weber.controller.interfaces.IPopulationController;
import de.weber.model.Automaton;
import de.weber.util.diy_framwork.DInjector;
import de.weber.util.diy_framwork.annotations.Vessel;
import de.weber.util.loggerino.LoggerFactorino;
import de.weber.view.interfaces.IPopulationPanelView;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

@Vessel
public class PopulationPanelView extends Region implements IPopulationPanelView {

    //Logger
    private static final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("PopulationPanel").grabLogger("PopulationPanel");

    private Automaton automaton;

    private IPopulationController populationController;


    // Canvas modifier constants
    public static final int INNER_GAP_X = 1;
    public static final int INNER_GAP_Y = 1;
    public static final int INSET_X = 1;
    public static final int INSET_Y = 1;

    private Canvas canvas;
    private GraphicsContext gc;
    private final List<Color> colorList = new ArrayList<>();

    private volatile int cellSize = 10;


    public PopulationPanelView() {
        this.canvas = new Canvas(0, 0);
        this.gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);
    }

    @Override
    public void initializePanel() {
        DInjector.getService(IPopulationController.class).initializePanel();
    }

    @Override
    public void initializePanel(IPopulationController populationController) {
        this.populationController = populationController;
        this.populationController.initializePanel();
    }

    @Override
    public void setAutomaton(Automaton automaton) {
        this.automaton = automaton;
        this.canvas.setWidth(automaton.getNumberOfColumns() * cellSize);
        this.canvas.setHeight(automaton.getNumberOfRows() * cellSize);

        //DInjector.getService(IPopulationController.class).setAutomaton(automaton);
        this.populationController.setAutomaton(automaton);
    }

    @Override
    public void setColorSpectrum(List<Color> colors) {
        if (!colorList.isEmpty()) {
            colorList.clear();
        }
        colorList.addAll(colors);
    }

    @Override
    public synchronized void draw() {
        Platform.runLater(() -> {
            clearField();

            for (int i = 0; i < this.automaton.getNumberOfRows(); i++) {
                for (int j = 0; j < this.automaton.getNumberOfColumns(); j++) {
                    var cell = automaton.getCell(i, j);
                    var cellState = cell.getState();
                    Color color;
                    try {
                        color = colorList.get(cellState);
                    } catch (IndexOutOfBoundsException e) {
                        color = Color.WHITE;
                    }
                    this.gc.setFill(color);
                    this.gc.fillRect((i * cellSize) + INSET_X, (j * cellSize) + INSET_Y, cellSize - INNER_GAP_X, cellSize - INNER_GAP_Y);
                }
            }
        });
    }

    @Override
    public synchronized void resize(int newValue) {
        Platform.runLater(() -> {
            clearField();

            this.cellSize = newValue;
            this.canvas.setWidth(automaton.getNumberOfColumns() * cellSize);
            this.canvas.setHeight(automaton.getNumberOfRows() * cellSize);

            for (int i = 0; i < this.automaton.getNumberOfRows(); i++) {
                for (int j = 0; j < this.automaton.getNumberOfColumns(); j++) {
                    var cell = this.automaton.getCell(i, j);
                    var cellState = cell.getState();

                    this.gc.setFill(colorList.get(cellState));
                    this.gc.fillRect((i * cellSize) + INSET_X, (j * cellSize) + INSET_Y, cellSize - INNER_GAP_X, cellSize - INNER_GAP_Y);
                }
            }
        });
    }

    @Override
    public void clearField() {
        for (int i = 0; i < this.automaton.getNumberOfRows(); i++) {
            for (int j = 0; j < this.automaton.getNumberOfColumns(); j++) {
                this.gc.clearRect((i * cellSize) + INSET_X, (j * cellSize) + INSET_Y, cellSize - INNER_GAP_X, cellSize - INNER_GAP_Y);
            }
        }
    }

    @Override
    public void setCellSize(int newSize) {
        this.cellSize = newSize;
    }

    @Override
    public int getCellSize() { return this.cellSize; }

    @Override
    public boolean areColorsSet() {
        return colorList.size() > 1;
    }

    @Override
    public Canvas getCanvas() {
        return this.canvas;
    }
}
