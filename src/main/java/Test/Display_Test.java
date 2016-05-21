package Test;

import IO.Dislay;
import IO.GPIO;
import IO.ShiftRegister;
import IO.UI;

/**
 * Created by Acer on 19-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class Display_Test extends Test {
    private GPIO io;
    private IO.Dislay dislay;

    public Display_Test() {
        try {
            io = new GPIO();
            dislay = new Dislay(
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
            UI.println("Start shift register test");
            UI.print("Clearing: ");
            Clearing();

            UI.print("Hello world: ");
            HelloWorld();

            UI.print("Seconde Line: ");
            SecondLine();

            UI.print("Clearing (2nd time): ");
            Clearing();

            if (isFailed()) {
                UI.println("Test failed");
            } else {
                UI.println("Test passed");
            }
        }
        if (io != null) {
            io.deinit();
        }
    }

    /**
     * This test try to clear the screen
     */
    private void Clearing() {
        dislay.ClearScreen();
        UI.println("Passed");
    }

    /**
     * This test will true to write Hello World to the first line
     */
    private void HelloWorld() {
        dislay.WriteNewLine("Hello world", true);
        UI.println("Passed");
    }

    /**
     * It will try to write on the second line 'Second line'.
     */
    private void SecondLine() {
        dislay.WriteNewLine("Second line", false);
        UI.println("Passed");
    }
}
