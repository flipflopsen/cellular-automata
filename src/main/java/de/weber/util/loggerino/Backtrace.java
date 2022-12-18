package de.weber.util.loggerino;

public class Backtrace {
    //Source: https://stackoverflow.com/questions/4152317/can-i-get-a-reference-to-a-calling-class-in-a-static-method

    private static final StackWalker stackWalker = StackWalker.getInstance(StackWalker.Option.SHOW_HIDDEN_FRAMES);


    public static StackWalker.StackFrame backTrace() {
        var stackFrame = stackWalker.walk((stream -> stream
                .limit(2)
                .reduce((one, two) -> two)));
        return stackFrame.orElseThrow();
    }

    public static StackWalker.StackFrame backTrace(int framesBack) {
        var stackFrame = stackWalker.walk((stream -> stream
                .limit(2 + framesBack)
                .reduce((one, two) -> two)));

        return stackFrame.orElseThrow();
    }
}
