package de.weber.model.test;

import de.weber.model.variants.GameOfLifeAutomaton;

public class GameOfLifeTest {
    private final GameOfLifeAutomaton gameOfLifeAutomaton;
    private final int iterations;
    private final boolean print;

    public GameOfLifeTest(int iterations, boolean print, boolean torus, int rows, int columns) {
        this.gameOfLifeAutomaton = new GameOfLifeAutomaton(rows, columns, torus);
        this.iterations = iterations;
        this.print = print;
    }

    public void simulate() throws Throwable {
        gameOfLifeAutomaton.randomPopulation();

        for (int i = 0; i < iterations; i++) {
            if (print) { gameOfLifeAutomaton.print(); }
            gameOfLifeAutomaton.nextGeneration();
        }
    }
}
