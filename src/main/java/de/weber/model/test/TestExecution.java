package de.weber.model.test;

import de.weber.util.loggerino.LoggerFactorino;

public class TestExecution {
    private static final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("TestExecution").grabLogger("TestExecution");

    public static void main(String[] args) {
        testLoggerColors();
        simpleGameOfLifeTest(10, true, true, 50, 50);
        simpleKruemelmonsterTest(10, true, true, 50, 50);
    }

    private static void simpleGameOfLifeTest(int iterations, boolean print, boolean torus, int rows, int columns) {
        GameOfLifeTest gameOfLifeTest = new GameOfLifeTest(iterations, print, torus, rows, columns);
        try {
            gameOfLifeTest.simulate();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static void simpleKruemelmonsterTest(int iterations, boolean print, boolean torus, int rows, int columns) {
        KruemelmonsterTest kruemelmonsterTest = new KruemelmonsterTest(iterations, print, torus, rows, columns);
        try {
            kruemelmonsterTest.simulate();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static void testLoggerColors() {
        logger.supidupi("test supidupi");
        logger.fine("test fine");
        logger.info("test info");
        logger.debug("test debug");
        logger.warn("test warn");
        logger.error("test error");
        logger.critical("test critical");
        logger.feierabend("test feierabend");
    }
}
