package de.weber.view.interfaces;

import javafx.scene.control.ColorPicker;

import java.util.List;

public interface IStatesView {

    List<ColorPicker> getStateColors();
    void resetStateColors();
}
