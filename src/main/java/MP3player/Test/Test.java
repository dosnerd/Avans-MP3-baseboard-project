package MP3player.Test;

import MP3player.IO.UI;

import java.util.Scanner;

/**
 * Created by Acer on 19-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public abstract class Test {
    private final Scanner reader;
    private boolean failed;

    Test() {
        reader = new Scanner(System.in);
    }

    boolean waitForConfirm() {
        String line;

        UI.print("Passed? (y/n): ");
        while (!(line = reader.nextLine().toLowerCase()).equals("y") && !line.equals("n")) try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            UI.error("Can not sleep", 4);
        }
        return line.equals("y");
    }

    /**
     * get if all tests are passed or not
     *
     * @return true if all test are passed, false if one ore more test failed
     */
    boolean isFailed() {
        return failed;
    }

    /**
     * Run the tests
     */
    public abstract void run();

    /**
     * Call when a test fail. Give a message about the failure
     *
     * @param message Message about the failure
     */
    void Fail(String message) {
        failed = true;
        UI.println("MP3player.Test failed");
        UI.println(message);
    }
}
