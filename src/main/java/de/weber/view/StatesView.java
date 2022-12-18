package de.weber.view;

import de.weber.controller.MainController;
import de.weber.util.diy_framwork.annotations.Vessel;
import de.weber.view.interfaces.IStatesView;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

@Vessel
public class StatesView implements IStatesView {

    private List<ColorPicker> stateColors = new ArrayList<>();
    private List<RadioButton> stateColorButtons = new ArrayList<>();
    private List<HBox> stateHBoxes = new ArrayList<>();
    private int oldRadioButtonIndex = -1;
    private final VBox states;
    private final MainController mainController;


    public StatesView(MainController controller) {
        this.states = controller.getStates();
        this.mainController = controller;
    }


    public void resetStateColors() {
        stateColors.clear();
        stateColorButtons.clear();
        stateHBoxes.clear();
        states.getChildren().clear();

        for (int i = 0; i < mainController.getAutomaton().getNumberOfStates() + 1; i++) {
            stateColors.add(new ColorPicker(Color.WHITE));
            HBox.setMargin(stateColors.get(i), new Insets(4, 0, 15, 20));
            stateColors.get(i).setPrefHeight(30.0);
            stateColors.get(i).setPrefWidth(127);

            stateColors.get(i).valueProperty().addListener((observable, oldValue, newValue) -> {
                var hit = false;
                var idx = 0;

                while (!hit) {

                    if (idx >= stateColors.size()) { return; }

                    if (newValue.equals(stateColors.get(idx).getValue())) {
                        stateColors.get(idx).setValue(newValue);
                        mainController.setPopulationColorSpectrum();

                        hit = true;
                    } else {
                        idx++;
                    }
                }
            });

            stateColorButtons.add(new RadioButton(String.format("%d", i)));
            HBox.setMargin(stateColorButtons.get(i), new Insets(10, 0, 0, 10));

            stateColorButtons.get(i).selectedProperty().addListener((observable, oldValue, newValue) -> {
                var hit = false;
                var idx = 0;

                if (oldRadioButtonIndex != -1) { stateColorButtons.get(oldRadioButtonIndex).setSelected(false); }

                while (!hit) {

                    if (idx >= stateColorButtons.size()) { return; }

                    if (newValue.equals(stateColorButtons.get(idx).isSelected())) {
                        mainController.setActiveState(Integer.parseInt(stateColorButtons.get(idx).getText()));

                        oldRadioButtonIndex = idx;

                        hit = true;
                    } else {
                        idx++;
                    }
                }
            });

            stateHBoxes.add(new HBox(stateColorButtons.get(i), stateColors.get(i)));
            VBox.setMargin(stateHBoxes.get(i), new Insets(10, 10, 10, 10));
            states.getChildren().add(new HBox(stateColorButtons.get(i), stateColors.get(i)));

            Separator separator = new Separator(Orientation.HORIZONTAL);
            separator.setMinWidth(states.getMinWidth());
            VBox.setMargin(separator, new Insets(0, 0, 8, 0));
            states.getChildren().add(separator);
        }

        stateColorButtons.get(0).setSelected(true);
    }

    public List<ColorPicker> getStateColors() {
        return stateColors;
    }
}
