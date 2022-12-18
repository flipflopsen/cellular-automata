package de.weber.model.test;

import de.weber.model.variants.KruemelmonsterAutomaton;

public class KruemelmonsterTest {
    private final KruemelmonsterAutomaton kruemelmonsterAutomaton;
    private final int iterations;
    private final boolean print;

    public KruemelmonsterTest(int iterations, boolean print, boolean torus, int rows, int columns) {
        this.kruemelmonsterAutomaton = new KruemelmonsterAutomaton(rows, columns, torus);
        this.iterations = iterations;
        this.print = print;
    }

    public void simulate() throws Throwable {
        kruemelmonsterAutomaton.randomPopulation();

        for (int i = 0; i < iterations; i++) {
            if (print) { kruemelmonsterAutomaton.print(); }
            kruemelmonsterAutomaton.nextGeneration();
        }
    }
}
