package de.weber.io;

public class AutomatonTemplateFiles {
    public static final String GOL_TEMPLATE = """
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

}
