package de.weber.controller;

import de.weber.controller.interfaces.IPopulationController;
import de.weber.model.Automaton;
import de.weber.util.diy_framwork.annotations.Vessel;
import de.weber.util.loggerino.LoggerFactorino;
import de.weber.view.interfaces.IPopulationPanelView;
import javafx.scene.input.MouseEvent;

import java.util.concurrent.atomic.AtomicBoolean;

@Vessel
public class PopulationController implements IPopulationController {

    private static final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("PopulationController").grabLogger("PopulationController");

    //public IPopulationPanelView populationPanel = DInjector.getService(IPopulationPanelView.class);
    public IPopulationPanelView populationPanel;

    private static final int INSET_X = 1;
    private static final int INSET_Y = 1;

    private volatile Automaton automaton;

    private int activeStateFromRadioButton;
    private int colorDragStartingRow;
    private int colorDragStartingCol;

    private final AtomicBoolean inDrag = new AtomicBoolean(false);


    public PopulationController() {
        //Default Constructor
    }

    public PopulationController(IPopulationPanelView populationPanel) {
        this.populationPanel = populationPanel;
    }

    @Override
    public void initializePanel() {
        populationPanel.getCanvas().addEventHandler(MouseEvent.MOUSE_PRESSED, this::colorCellFromMouse);
        populationPanel.getCanvas().addEventHandler(MouseEvent.MOUSE_DRAGGED, this::colorCellFromMouseDragged);
        populationPanel.getCanvas().addEventHandler(MouseEvent.MOUSE_RELEASED, ev -> {
            inDrag.set(false);
            colorDragStartingCol = 0;
            colorDragStartingRow = 0;
        });
    }

    @Override
    public void colorCellFromMouse(MouseEvent event) {
        if (event.isSecondaryButtonDown()) { return; }

        var row = (int) ((event.getX() - INSET_X) / populationPanel.getCellSize());
        var col = (int) ((event.getY() - INSET_Y) / populationPanel.getCellSize());

        if (row < this.automaton.getNumberOfRows() && row >= 0 &&
                col < this.automaton.getNumberOfColumns() && col >= 0) {
            this.automaton.setState(row, col, activeStateFromRadioButton);
        }

        populationPanel.draw();
    }

    @Override
    public void colorCellFromMouseDragged(MouseEvent event) {

        if (event.isMiddleButtonDown()) {
            colorCellFromMouse(event);
        }

        if (!event.isSecondaryButtonDown() && event.isPrimaryButtonDown()) {
            var row = (int) ((event.getX() - INSET_X) / populationPanel.getCellSize());
            var col = (int) ((event.getY() - INSET_Y) / populationPanel.getCellSize());
            if (!inDrag.get()) {
                inDrag.set(true);
                colorDragStartingRow = row;
                colorDragStartingCol = col;
            }

            if (row < this.automaton.getNumberOfRows() && row >= 0 && col < this.automaton.getNumberOfColumns() && col >= 0) {
                automaton.setState(colorDragStartingRow, colorDragStartingCol, row, col, activeStateFromRadioButton);
                populationPanel.draw();
            }
        }
    }

    @Override
    public void setAutomaton(Automaton automaton) {
        this.automaton = automaton;
    }

    @Override
    public void setActiveState(int activeState) {
        this.activeStateFromRadioButton = activeState;
    }



}
