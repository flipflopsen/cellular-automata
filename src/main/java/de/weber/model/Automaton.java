package de.weber.model;

import de.weber.util.loggerino.ConsoleColors;
import de.weber.util.loggerino.LoggerFactorino;

import java.util.Arrays;
import java.util.Random;

/**
 * Abstrakte Klasse zur Repräsentation eines zellulären Automaten
 */
public abstract class Automaton {

    private final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("Automaton").grabLogger("Automaton");
    private volatile int rows;
    private volatile int columns;
    private final int numberOfStates;
    private final boolean isMooreNeighborHood;
    private volatile boolean isTorus;
    private final Random rand = new Random();

    //Usage of Array for pace.
    private Cell[][] cells;

    /**
     * Konstruktor
     *
     * @param rows Anzahl an Reihen
     * @param columns Anzahl an Spalten
     * @param numberOfStates Anzahl an Zuständen; die Zustände
     * des Automaten
     * sind dann die Werte 0 bis
     * numberOfStates-1
     * @param isMooreNeighborHood true, falls der Automat die
     * Moore-Nachbarschaft
     * benutzt; false, falls der Automat die
     * von-Neumann-Nachbarschaft benutzt
     * @param isTorus true, falls die Zellen als
     * Torus betrachtet werden
     */
    public Automaton(int rows, int columns, int numberOfStates, boolean isMooreNeighborHood, boolean isTorus) {
        this.rows = rows;
        this.columns = columns;
        this.numberOfStates = numberOfStates - 1;
        this.isMooreNeighborHood = isMooreNeighborHood;
        this.isTorus = isTorus;
        this.cells = new Cell[rows][columns];

        Arrays.stream(cells).forEach(row -> Arrays.fill(row, new Cell()));

    }

    /**
     * Implementierung der Transformationsregel
     *
     * @param cell die betroffene Zelle (darf nicht verändert
     * werden!!!)
     * @param neighbors die Nachbarn der betroffenen Zelle (dürfen nicht
     * verändert werden!!!)
     * @return eine neu erzeugte Zelle, die gemäß der
     * Transformationsregel aus der
     * betroffenen Zelle hervorgeht
     * @throws Throwable moeglicherweise wirft die Methode eine Exception
     */
    protected abstract Cell transform(Cell cell, Cell[] neighbors) throws Throwable;

    /**
     * Liefert die Anzahl an Zuständen des Automaten; gültige Zustände sind
     * int-Werte zwischen 0 und Anzahl-1
     *
     * @return die Anzahl an Zuständen des Automaten
     */
    public synchronized int getNumberOfStates() {
        return this.numberOfStates;
    }

    /**
     * Liefert die Anzahl an Reihen
     *
     * @return die Anzahl an Reihen
     */
    public synchronized int getNumberOfRows() {
        return this.rows;
    }

    /**
     * Liefert die Anzahl an Spalten
     *
     * @return die Anzahl an Spalten
     */
    public synchronized int getNumberOfColumns() {
        return this.columns;
    }

    /**
     * Ändert die Größe des Automaten; Achtung: aktuelle Belegungen nicht
     * gelöschter Zellen sollen beibehalten werden; neue Zellen sollen im
     * Zustand 0 erzeugt werden
     *
     * @param rows die neue Anzahl an Reihen
     * @param columns die neue Anzahl an Spalten
     */
    public synchronized void changeSize(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        //Create new board with given size
        Cell[][] newBoard = new Cell[rows][columns];

        //Fill it with new cells
        Arrays.stream(newBoard).forEach(row -> Arrays.fill(row, new Cell(0)));

        //Iterate over all the rows
        if (newBoard[0].length >= cells[0].length) {
            for (int i = 0; i < cells.length; i++) {
                //Copy the old state into the new array
                System.arraycopy(cells[i], 0, newBoard[i], 0, cells[i].length);
            }
        } else {
            for (int i = 0; i < newBoard.length; i++) {
                //Copy the old state into the new array
                System.arraycopy(cells[i], 0, newBoard[i], 0, newBoard[i].length);
            }
        }

        //Set the new board
        this.cells = newBoard;
    }

    /**
     * Liefert Informationen, ob der Automat als Torus betrachtet wird
     *
     * @return true, falls der Automat als Torus betrachtet wird; false
     * sonst
     */
    public synchronized boolean isTorus() {
        return this.isTorus;
    }

    /**
     * Ändert die Torus-Eigenschaft des Automaten
     *
     * @param isTorus true, falls der Automat als Torus betrachtet wird;
     * false sonst
     */
    public synchronized void setTorus(boolean isTorus) {
        this.isTorus = isTorus;
    }

    /**
     * Liefert Informationen über die Nachbarschaft-Eigenschaft des
     * Automaten
     * (Hinweis: Die Nachbarschaftseigenschaft kann nicht verändert werden)
     *
     * @return true, falls der Automat die Moore-Nachbarschaft berücksicht;
     * false, falls er die von-Neumann-Nachbarschaft berücksichtigt
     */
    public boolean isMooreNeighborHood() {
        return this.isMooreNeighborHood;
    }

    /**
     * setzt alle Zellen in den Zustand 0
     */
    public synchronized void clearPopulation() {
        Arrays.stream(cells).forEach(row -> Arrays.fill(row, new Cell(0)));
    }

    /**
     * setzt für jede Zelle einen zufällig erzeugten Zustand
     */
    public synchronized void randomPopulation() {
        /*
        Idea: 2 Streams like Ping-Pong

        - Stream 1 contains all states from 0 to numberOfStates and picks a random value each time
        - Stream 2 streams the cells-array sequentially and gets a random value from Stream 1

        To be implemented.
         */


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                this.cells[i][j] = new Cell(rand.nextInt(0, numberOfStates + 1));
            }
        }



        /*
        //Other solution:
        Arrays.stream(cells).forEach(row ->
                Arrays.stream(row).forEach( cell ->
                        cell.setState(rand.nextInt(0, numberOfStates + 1))));

         */


    }
    /**
     * Liefert eine Zelle des Automaten
     *
     * @param row Reihe der Zelle
     * @param column Spalte der Zelle
     * @return Cell-Objekt an Position row/column
     */
    public synchronized Cell getCell(int row, int column) {
        return cells[row][column];
    }
    /**
     * Aendert den Zustand einer Zelle
     *
     * @param row Reihe der Zelle
     * @param column Spalte der Zelle
     * @param state neuer Zustand der Zelle
     */
    public synchronized void setState(int row, int column, int state) {
        //logger.info("Setting state with row: {0}, col: {1}, state: {2}", row, column, state);
        cells[row][column].setState(state);
    }
    /**
     * Aendert den Zustand eines ganzen Bereichs von Zellen
     *
     * @param fromRow Reihe der obersten Zelle
     * @param fromColumn Spalte der obersten Zelle
     * @param toRow Reihe der untersten Zelle
     * @param toColumn Spalte der untersten Zelle
     * @param state neuer Zustand der Zellen
     */
    public synchronized void setState(int fromRow, int fromColumn, int toRow, int toColumn, int state) {
        if (fromRow <= toRow && fromColumn < toColumn) {
            for (int i = fromRow; i <= toRow; i++) {
                for (int j = fromColumn; j <= toColumn; j++) {
                    cells[i][j].setState(state);
                }
            }
        } else if (fromRow >= toRow && fromColumn >= toColumn) {
            for (int i = toRow; i <= fromRow; i++) {
                for (int j = toColumn; j <= fromColumn; j++) {
                    cells[i][j].setState(state);
                }
            }
        } else if (fromRow < toRow && fromColumn >= toColumn) {
            for (int i = fromRow; i <= toRow; i++) {
                for (int j = toColumn; j <= fromColumn; j++) {
                    cells[i][j].setState(state);
                }
            }
        } else {
            for (int i = toRow; i <= fromRow; i++) {
                for (int j = fromColumn; j <= toColumn; j++) {
                    cells[i][j].setState(state);
                }
            }
        }
    }
    /**
     * überführt den Automaten in die nächste Generation; ruft dabei die
     * abstrakte Methode "transform" für alle Zellen auf; Hinweis: zu
     * berücksichtigen sind die Nachbarschaftseigenschaft und die
     * Torus-Eigenschaft des Automaten
     *
     * @throws Throwable Exceptions der transform-Methode werden
     * weitergeleitet
     */
    public synchronized void nextGeneration() throws Throwable {
        Cell[][] newCells = new Cell[rows][columns];

        //var start = Instant.now();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                newCells[i][j] = transform(cells[i][j], getNeighborsAt(i, j));
            }
        }

        //var end = Instant.now();
        //logger.debug("Took {0}ms to transform", Duration.between(start, end).toMillis());

        this.cells = newCells;
    }

    private synchronized Cell[] getNeighborsAt(int row, int column) {
        Cell[] neighbors = new Cell[8];

        int ctr = 0;

        //Iterate through a 3x3 matrix
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (! (i == 0 && j == 0)) {
                    int x = row + i;
                    int y = column + j;

                    //check if any constraints would get violated
                    if (checkConstraints(x, y)) {
                        //if not everything is fine, and we add the cell to the return array
                        neighbors[ctr] = cells[x][y];
                    } else {
                        //if constraints were violated, we check for the torus boolean
                        if (isTorus) {
                            //Nested ternary isn't nice, but in this case...
                            x = x < 0 ? rows - 1 : x > rows - 1 ? 0 : x;
                            y = y < 0 ? columns - 1 : y > columns - 1 ? 0 : y;

                            //check constraints again
                            if (checkConstraints(x, y)) {
                                neighbors[ctr] = cells[x][y];
                            } else {
                                logger.feierabend("Caught some big logic mistake in getNeighborsAt-Method (Automaton.java)");
                            }
                        }
                    }
                    ctr++;
                }
            }
        }

        //Check at the end for the Moore-Neighborhood, because in doing it in the for loop would be heavy (because we've already called the method from a for loop...).
        if (!isMooreNeighborHood) {
            neighbors[0] = null;
            neighbors[2] = null;

            // 5 and 7 instead of 6 and 9,
            // because the ctr value only increment in case of i != 0 && j != 0,
            // so we skip the cell for which we wanted to find the neighbors, while adding the neighbors to the neighbors array.
            neighbors[5] = null;
            neighbors[7] = null;
        }
        return neighbors;
    }

    private synchronized boolean checkConstraints(int row, int column) {
        return row >= 0 && column >= 0 && row < rows && column < columns;
    }

    public void print() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                var state = this.cells[i][j].getState();
                var color = determineCellColor(state);

                System.out.print(color + this.cells[i][j].getState() + ConsoleColors.RESET /*+ " "*/);
            }
            System.out.print("\n");
        }
        System.out.println("---------------------------------------------");
    }

    private String determineCellColor(int cellState) {
        return switch (cellState) {
            case 0 -> ConsoleColors.WHITE_BACKGROUND_BRIGHT;
            case 1, 10 -> ConsoleColors.GREEN_BACKGROUND_BRIGHT;
            case 2 -> ConsoleColors.RED_BACKGROUND_BRIGHT;
            case 3 -> ConsoleColors.BLUE_BACKGROUND_BRIGHT;
            case 4 -> ConsoleColors.YELLOW_BACKGROUND_BRIGHT;
            case 5 -> ConsoleColors.PURPLE_BACKGROUND_BRIGHT;
            case 6 -> ConsoleColors.CYAN_BACKGROUND_BRIGHT;
            case 7 -> "\033[0;108m";
            case 8 -> "\033[0;109m";
            case 9 -> "\033[0;110m";
            default -> "";
        };
    }
}