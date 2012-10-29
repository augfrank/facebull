
import java.io.PrintStream;

/**
 * Debugging support methods
 */
public class Debug {
    static int debugLevel = 0;          // level 0 means OFF
    static PrintStream stream = System.out;

    public static void on() {
        debugLevel = 1;
    }

    public static void off() {
        debugLevel = 0;
    }

    public static boolean isOn() {
        return debugLevel > 0;
    }

    public static int getLevel() {
        return debugLevel;
    }

    public static void setLevel(int level) {
        debugLevel = level;
    }

    public static void setPrintStream(PrintStream s) {
        stream = s;
    }

    public static void print(String s) {
        if (isOn()) {
            stream.print(s);
        }
    }
    public static void println(String s) {
        if (isOn()) {
            stream.println(s);
        }
    }

    public static void print(int level, String s) {
        if (debugLevel >= level) {
            stream.print(s);
        }
    }
    public static void println(int level, String s) {
        if (debugLevel >= level) {
            stream.println(s);
        }
    }
}
