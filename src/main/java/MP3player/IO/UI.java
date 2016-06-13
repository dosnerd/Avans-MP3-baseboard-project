package MP3player.IO;

/**
 * Created by Acer on 21-4-2016.
 *
 * @author David de Prez
 * @version 1.1
 */
public class UI {
    private static boolean _log;
    private static Display _display;

    /**
     * This will set the _display. When this is set, the errornumber will be displayed when there
     * is an error. This will not happen when it is testing
     *
     * @param _display The display to print the error number to
     */
    public static void set_display(Display _display) {
        UI._display = _display;
    }

    /**
     * This will set the _log. If _log is true, it can write text to the console. If _log is false, it won't write
     * text to the console.
     *
     * @param _log Value for _log
     */
    public static void set_log(boolean _log) {
        UI._log = _log;
    }

    /**
     * Write a line, with after the line a return. This works only if _log is set to true
     *
     * @param text The text to write to the screen
     */
    public static void println(String text) {
        if (_log) {
            System.out.println(text);
        }
    }

    /**
     * Write a line, without adding a return on the end. This works only if _log is set to true
     *
     * @param text The text to write to the screen.
     */
    public static void print(String text) {
        if (_log) {
            System.out.print(text);
        }
    }

    /**
     * Write the text to the standard error output stream. This will always works and does not depend
     * on the _log variable.
     *
     * @param text Text that says that there is an error and the description of the error.
     */
    static public void error(String text, int errorNumber) {
        System.err.println("E:" + errorNumber + ": " + text);

        try {
            //check if can write to display display
            if (_display != null) {
                //Show error number to display
                _display.ClearScreen();
                _display.WriteNewLine("<<E:" + errorNumber + ">>", true);
            }
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            System.err.println("E:" + errorNumber + ": " + text);
        }
    }
}
