package de.weber.io;

public class AutomatonTemplates {
    private AutomatonTemplates() {
        //Default private Constructor
    }
    public static final String DEFAULT_TEMPLATE = """
import de.weber.model.Automaton;
import de.weber.model.Cell;

public class {0} extends Automaton {
    final private static int initNumberOfRows = 50;
    final private static int initNumberOfColumns = 50;
    final private static int numberOfStates = 2;
    final private static boolean mooreNeighborhood = true;
    final private static boolean initTorus = true;
    
    public {0}() {
        super(initNumberOfRows, initNumberOfColumns,
        numberOfStates, mooreNeighborhood,
        initTorus);
    }
    
    protected Cell transform(Cell cell, Cell[] neighbors) {
        return cell;
    }
}
""";

    public static final String GOL_TEMPLATE = """
import de.weber.model.Automaton;
import de.weber.model.Cell;

public class GameOfLifeAutomaton extends Automaton {
    final private static int initNumberOfRows = 50;
    final private static int initNumberOfColumns = 50;
    final private static int numberOfStates = 2;
    final private static boolean mooreNeighborhood = true;
    final private static boolean initTorus = true;
    
    public GameOfLifeAutomaton() {
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
""";

    public static final String KRUEMELMONSTER_TEMPLATE = """
import de.weber.model.Automaton;
import de.weber.model.Cell;

public class KruemelmonsterAutomaton extends Automaton {
    final private static int initNumberOfRows = 100;
    final private static int initNumberOfColumns = 100;
    final private static int numberOfStates = 10;
    final private static boolean mooreNeighborhood = true;
    final private static boolean initTorus = true;
    
    public KruemelmonsterAutomaton() {
        super(initNumberOfRows, initNumberOfColumns,
        numberOfStates, mooreNeighborhood,
        initTorus);
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
""";

}
