package de.weber.util;

public class StaticValidator {

    public static boolean isJavaClassNameConform(String classname) {
        return classname.matches("^[A-Z_]($[A-Z_]|[\\w_]){4,20}");
    }

    public static boolean isNumberBetween(int value, int lower, int upper) {
        return value >= lower && value <= upper;
    }

    public static String simulationResizeRegex() {
        return "^(10|[1-9][0-9]|[1-2][0-9][0-9])$";
    }
}
