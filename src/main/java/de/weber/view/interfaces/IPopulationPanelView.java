package de.weber.view.interfaces;

import de.weber.controller.interfaces.IPopulationController;
import de.weber.model.Automaton;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.List;

public interface IPopulationPanelView {
    void initializePanel();

    void initializePanel(IPopulationController populationController);

    void setAutomaton(Automaton automaton);

    void setColorSpectrum(List<Color> colors);

    void draw();

    void resize(int newValue);

    void clearField();

    void setCellSize(int newSize);

    int getCellSize();

    boolean areColorsSet();

    Canvas getCanvas();
}
