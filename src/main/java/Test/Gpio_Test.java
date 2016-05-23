import Errors.IllegalPinModeException;
import IO.UI;

/**
 * Created by Acer on 21-4-2016.
 * <p/>
 * test class for Gpio
 *
 * @author David de Prez
 * @version 1.1
 */
public class Gpio_Test extends Test {
    private boolean failed;
    private GPIO io;

    Gpio_Test() {
        try {
            io = new Gpio();
        } catch (Exception ex) {
            failed = true;
            ex.printStackTrace();
        }
    }

    /**
     * get if all tests are passed or not
     *
     * @return true if all test are passed, false if one ore more test failed
     */
    public boolean isFailed() {
        return failed;
    }

    /**
     * Run the tests
     */
    void run() {
        if (!isFailed()) {
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
                UI.println("Test failed");
            } else {
                UI.println("Test passed");
            }

        io.deinit();
    }

    /**
     * Call when a test fail. Give a message about the failure
     *
     * @param message Message about the failure
     */
    private void Fail(String message) {
        failed = true;
        UI.println("Test failed");
        UI.println(message);
    }

    /**
     * This test if you could set a input pin. If this test fails, you could set a input pin. We need to prevent
     * this because every PGIO pin is a input OR output.
     */
    private void writeToInput() {
        try {
            io.setPin(Gpio.PINS.PB31, false);
            Fail("No exception was threw when trying write value to input pin");
            return;
        } catch (IllegalPinModeException ignored) {

        }

        try {
            io.setPin(Gpio.PINS.PB30, false);
            Fail("No exception was threw when trying write value to input pin");
            return;
        } catch (IllegalPinModeException ignored) {

        }

        try {
            io.setPin(Gpio.PINS.PB21, false);
            Fail("No exception was threw when trying write value to input pin");
            return;
        } catch (IllegalPinModeException ignored) {

        }

        try {
            io.setPin(Gpio.PINS.PB20, false);
            Fail("No exception was threw when trying write value to input pin");
            return;
        } catch (IllegalPinModeException ignored) {

        }

        IO.UI.println("Passed");
    }

    /**
     * Test if it possible to set normal output pin. If this succeed, you can control the GPIO pins. With this test,
     * every GPIO output pin would be 500ms high.
     */
    private void writeToOutput() {
        for (int i = 4; i < Gpio.PINS.values().length; i++) {
            try {
                Thread.sleep(1000);
                UI.println("Testing pin: " + Gpio.PINS.values()[i].name());
                io.setPin(Gpio.PINS.values()[i], true);
                Thread.sleep(1000);
                io.setPin(Gpio.PINS.values()[i], false);
                Thread.sleep(1000);
                io.setPin(Gpio.PINS.values()[i], true);
            } catch (InterruptedException ignored) {
            } catch (Exception ex) {
                Fail("Exception occurs");
                ex.printStackTrace();
            }
        }
        UI.println("Test passed");
    }

    /**
     * Test if it possible to read an output pin. This can not harm the board, but
     * the class should not allowed it. It wouldn't be dangerous if this test fails,
     * but it would be nice if it doesn't. This because it will decrease the change of
     * picking a wrong pin to read from.
     */
    private void readFromOutput() {
        for (int i = 4; i < Gpio.PINS.values().length; i++) {
            try {
                io.getPin(Gpio.PINS.values()[i]);
                Fail("Read from output is possible. This is not allowed.");
                return;
            } catch (IllegalPinModeException ignored) {
            }
        }

        UI.println("passed");
    }


    private void readFromInput() {
        for (int i = 0; i < 4; i++) {
            try {
                if (io.getPin(Gpio.PINS.values()[i])) {
                    UI.println("Warning: " + Gpio.PINS.values()[i].name() + " is high!");
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
