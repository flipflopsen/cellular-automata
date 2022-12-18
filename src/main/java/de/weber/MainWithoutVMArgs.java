package de.weber;

import de.weber.util.loggerino.LoggerFactorino;

public class MainWithoutVMArgs {
    public static void main(String[] args) {
        LoggerFactorino.FACTORINO.makeLogger("MainController");
        Main.main(args);
    }

}