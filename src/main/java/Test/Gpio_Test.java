package Test;

import Errors.IllegalPinModeException;
import IO.GPIO;
import IO.UI;

/**
 * Created by Acer on 21-4-2016.
 * <p/>
 * test class for Gpio
 *
 * @author David de Prez
 * @version 1.1
 */
public class Gpio_Test {
    private boolean failed;
    private GPIO io;

    public Gpio_Test() {
        try {
            io = new GPIO();
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
    public void run() {
        UI.print("writeToInput: ");
        writeToInput();

        UI.println("writeToOutput: ");
        writeToOutput();

        UI.print("readFromOutput: ");
        readFromOutput();

        UI.println("readFromInput: ");
        readFromInput();

        if (failed) {
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

        UI.println("Passed");
    }

    /**
     * Test if it possible to set normal output pin. If this succeed, you can control the GPIO pins. With this test,
     * every GPIO output pin would be 500ms high.
     */
    private void writeToOutput() {
        for (int i = 5; i < GPIO.Pin.values().length; i++) {
            try {
                Thread.sleep(1000);
                UI.println("Testing pin: " + GPIO.Pin.values()[i].name());
                io.setPin(GPIO.Pin.values()[i], true);
                Thread.sleep(1000);
                io.setPin(GPIO.Pin.values()[i], false);
                Thread.sleep(1000);
                io.setPin(GPIO.Pin.values()[i], true);
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
        for (int i = 5; i < GPIO.Pin.values().length; i++) {
            try {
                io.getPin(GPIO.Pin.values()[i]);
                Fail("Read from output is possible. This is not allowed.");
                return;
            } catch (IllegalPinModeException ignored) {
            }
        }

        UI.println("passed");
    }

    /**
     * This will try to read from an input. If it can and the input is high, it will give a warning. When it
     * can't read from the input, the test will fail. This test will pass if it can read all input pins.
     */
    private void readFromInput() {
        for (int i = 0; i < 5; i++) {
            try {
                //check if value of input is high and give a warning is so
                if (io.getPin(GPIO.Pin.values()[i])) {
                    UI.println("Warning: " + GPIO.Pin.values()[i].name() + " is high!");
                }
            } catch (Exception ex) {
                //fail the test because the unexpected error
                Fail("An unexpected error occurs");

                //print the error
                ex.printStackTrace();
                return;
            }
        }
        UI.println("Passed");
    }
}
