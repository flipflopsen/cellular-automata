package de.weber.model.variants;

import de.weber.model.Automaton;
import de.weber.model.Cell;

import java.util.Arrays;
import java.util.Optional;

public class KruemelmonsterAutomaton extends Automaton {

    public KruemelmonsterAutomaton(int rows, int columns, boolean isTorus) {
        super(rows, columns, 10, true, isTorus);
    }
    public KruemelmonsterAutomaton() {
        this(100, 100, true);
    }
    protected Cell transform(Cell cell, Cell[] neighbors) {
        if (cell.getState() == super.getNumberOfStates() - 1) {
            return new Cell(0);
        }

        Optional<Cell> candidate = Arrays.stream(neighbors)
                .filter(c -> c != null && c.getState() == cell.getState() + 1)
                .findFirst();

        return candidate.map(value -> new Cell(value.getState()))
                .orElseGet(() -> new Cell(cell.getState()));
    }
}