package de.weber.model.variants;

import de.weber.model.Automaton;
import de.weber.model.Cell;
import de.weber.util.loggerino.LoggerFactorino;

import java.util.Arrays;

public class GameOfLifeAutomaton extends Automaton {

    LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("GameOfLife").grabLogger("GameOfLife");

    public GameOfLifeAutomaton(int rows, int columns, boolean isTorus) {
        super(rows, columns, 2, true, isTorus);
    }
    public GameOfLifeAutomaton() {
        this(50, 50, true);
    }

    protected synchronized Cell transform(Cell cell, Cell[] neighbors) {
        var amountOfLivingNeighbors = (int) Arrays.stream(neighbors)
                .filter(c -> c != null && c.getState() == 1)
                .count();

        if (cell.getState() == 0 && amountOfLivingNeighbors == 3) {
            return new Cell(1);
        } else if (cell.getState() == 1 && (amountOfLivingNeighbors == 2 || amountOfLivingNeighbors == 3)) {
            return new Cell(1);
        } else {
            return new Cell(0);
        }
    }
}
