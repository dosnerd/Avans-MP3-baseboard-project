package IO;

/**
 * Created by Acer on 21-4-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class UI {
    /**
     * Write a line, with after the line a return
     *
     * @param text The text to write to the screen
     */
    public static void println(String text) {
        System.out.println(text);
    }

    /**
     * Write a line, without adding a return on the end
     *
     * @param text The text to write to the screen.
     */
    public static void print(String text) {
        System.out.print(text);
    }
}
