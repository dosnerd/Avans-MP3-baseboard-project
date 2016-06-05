package MP3player.Test;

import MP3player.Errors.IllegalPinModeException;
import MP3player.IO.GPIO;
import MP3player.IO.UI;

/**
 * Created by Acer on 21-4-2016.
 * <p/>
 * test class for Gpio
 *
 * @author David de Prez
 * @version 1.1
 */
public class Gpio_Test extends Test {
    private GPIO io;

    public Gpio_Test(GPIO io) {
        try {
            this.io = io;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Run the tests
     */
    @Override
    public void run() {
        if (!isFailed()) {
            UI.print("Prepaire GPIO test");
            for (GPIO.Pin p : GPIO.Pin.values()) {
                try {
                    io.setPin(p, false);
                } catch (IllegalPinModeException ignored) {

                } catch (Exception ex) {
                    UI.print("Can not prepaire pin: " + p.name());
                }
            }

            UI.print("Start GPIO test");
            UI.print("writeToInput: ");
            writeToInput();

            UI.println("writeToOutput: ");
            writeToOutput();

            UI.print("readFromOutput: ");
            readFromOutput();

            UI.println("readFromInput: ");
            readFromInput();

            if (isFailed()) {
                UI.println("MP3player.Test failed");
            } else {
                UI.println("MP3player.Test passed");
            }
        }
    }

    /**
     * This test if you could set a input pin. If this test fails, you could set a input pin. We need to prevent
     * this because every PGIO pin is a input OR output.
     */
    private void writeToInput() {
        try {
            io.setPin(GPIO.Pin.PB31, false);
            Fail("No exception was threw when trying write value to input pin");
            return;
        } catch (IllegalPinModeException ignored) {

        }

        try {
            io.setPin(GPIO.Pin.PB30, false);
            Fail("No exception was threw when trying write value to input pin");
            return;
        } catch (IllegalPinModeException ignored) {

        }

        try {
            io.setPin(GPIO.Pin.PB21, false);
            Fail("No exception was threw when trying write value to input pin");
            return;
        } catch (IllegalPinModeException ignored) {

        }

        try {
            io.setPin(GPIO.Pin.PB20, false);
            Fail("No exception was threw when trying write value to input pin");
            return;
        } catch (IllegalPinModeException ignored) {

        }

        try {
            io.setPin(GPIO.Pin.PB19, false);
            Fail("No exception was threw when trying write value to input pin");
            return;
        } catch (IllegalPinModeException ignored) {

        }

        MP3player.IO.UI.println("Passed");
    }

    /**
     * MP3player.Test if it possible to set normal output pin. If this succeed, you can control the GPIO pins. With this test,
     * every GPIO output pin would be 500ms high.
     */
    private void writeToOutput() {
        for (int i = 5; i < GPIO.Pin.values().length; i++) {
            try {
                UI.println("Testing pin: " + GPIO.Pin.values()[i].name());
                io.setPin(GPIO.Pin.values()[i], true);
                waitForConirm();
                io.setPin(GPIO.Pin.values()[i], false);
            } catch (Exception ex) {
                Fail("Exception occurs");
                ex.printStackTrace();
            }
        }
        UI.println("MP3player.Test passed");
    }

    /**
     * MP3player.Test if it possible to read an output pin. This can not harm the board, but
     * the class should not allowed it. It wouldn't be dangerous if this test fails,
     * but it would be nice if it doesn't. This because it will decrease the change of
     * picking a wrong pin to read from.
     */
    private void readFromOutput() {
        for (int i = 5; i < GPIO.Pin.values().length; i++) {
            try {
                io.getPin(GPIO.Pin.values()[i]);
                Fail("Read from output is possible. This is not allowed. (" + GPIO.Pin.values()[i].name() + ")");
                return;
            } catch (IllegalPinModeException ignored) {
            }
        }

        UI.println("passed");
    }


    private void readFromInput() {
        for (int i = 0; i < 5; i++) {
            try {
                if (io.getPin(GPIO.Pin.values()[i])) {
                    UI.println("Warning: " + GPIO.Pin.values()[i].name() + " is high!");
                }
            } catch (Exception ex) {
                Fail("An unaccepted error occurs");
                ex.printStackTrace();
                return;
            }
        }
        UI.println("Passed");
    }

}
