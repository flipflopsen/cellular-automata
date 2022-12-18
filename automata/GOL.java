import de.weber.model.Automaton;
import de.weber.model.Cell;

import java.util.Arrays;

public class GOL extends Automaton {
    final private static int initNumberOfRows = 50;
    final private static int initNumberOfColumns = 50;
    final private static int numberOfStates = 2;
    final private static boolean mooreNeighborhood = true;
    final private static boolean initTorus = true;
    
    public GOL() {
        super(initNumberOfRows, initNumberOfColumns,
        numberOfStates, mooreNeighborhood,
        initTorus);
    }
    
    protected Cell transform(Cell cell, Cell[] neighbors) {
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

