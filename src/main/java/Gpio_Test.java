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
public class Gpio_Test {
    private boolean failed;
    private Gpio io;

    public Gpio_Test() {
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
    public void run() {
        IO.UI.print("writeToInput: ");
        writeToInput();

        IO.UI.println("writeToOutput: ");
        writeToOutput();

        if (failed) {
            IO.UI.println("Test failed");
        } else {
            IO.UI.println("Test passed");
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
        IO.UI.println(message);
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
        for (int i = 8; i < Gpio.PINS.values().length; i++) {
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
    }
}
