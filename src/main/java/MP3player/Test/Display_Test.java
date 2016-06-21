package MP3player.Test;

import MP3player.IO.Display;
import MP3player.IO.GPIO;
import MP3player.IO.ShiftRegister;
import MP3player.IO.UI;

/**
 * Created by Acer on 19-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class Display_Test extends Test {
    private GPIO io;
    private Display display;

    public Display_Test(GPIO io) {
        try {
            this.io = io;
            display = new Display(
                    io,
                    new ShiftRegister(io, GPIO.Pin.PB17, GPIO.Pin.PB16, GPIO.Pin.PA28),
                    GPIO.Pin.PA22, GPIO.Pin.PA11);

        } catch (Exception ex) {
            Fail("Can not create instance");
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (!isFailed()) {
            io.setPin(GPIO.Pin.PA6, true);

            UI.println("Start shift register test");
            UI.print("Clearing: ");
            Clearing();

            UI.print("Hello world: ");
            HelloWorld();

            UI.print("Second Line: ");
            SecondLine();

            UI.print("Clearing (2nd time): ");
            Clearing();
            waitForConfirm();

            if (isFailed()) {
                UI.println("MP3player.Test failed");
            } else {
                UI.println("MP3player.Test passed");
            }
        }
    }

    /**
     * This test try to clear the screen
     */
    private void Clearing() {
        display.ClearScreen();
        UI.println("Passed");
    }

    /**
     * This test will true to write Hello World to the first line
     */
    private void HelloWorld() {
        display.WriteNewLine("Hello world", true);
        if (waitForConfirm()) {
            UI.println("Passed");
        } else {
            Fail("");
        }
    }

    /**
     * It will try to write on the second line 'Second line'.
     */
    private void SecondLine() {
        display.WriteNewLine("Second line", false);
        if (waitForConfirm()) {
            UI.println("Passed");
        } else {
            Fail("");
        }
    }
}
