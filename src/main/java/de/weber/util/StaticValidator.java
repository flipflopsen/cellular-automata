package de.weber.util;

public class StaticValidator {

    public static boolean isJavaClassNameConform(String classname) {
        int ctr = 0;
        var valid = false;
        for (char c : classname.toCharArray()) {
            if (ctr == 0) {
                valid = Character.isJavaIdentifierStart(c);
            } else {
                valid = Character.isJavaIdentifierPart(c);
            }
            if (!valid)
                break;
            ctr++;
        }
        return valid;
    }

    public static boolean isNumberBetween(int value, int lower, int upper) {
        return value >= lower && value <= upper;
    }

    public static String simulationResizeRegex() {
        return "^(10|[1-9][0-9]|[1-2][0-9][0-9])$";
    }
}
