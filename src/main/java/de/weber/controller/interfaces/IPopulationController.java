package de.weber.controller.interfaces;

import de.weber.model.Automaton;
import javafx.scene.input.MouseEvent;

public interface IPopulationController {

    void initializePanel();
    void colorCellFromMouse(MouseEvent event);
    void colorCellFromMouseDragged(MouseEvent event);
    void setAutomaton(Automaton automaton);

    void setActiveState(int activeState);
}
