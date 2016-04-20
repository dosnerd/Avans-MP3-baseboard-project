package IO;

import Errors.IllegalPinModeException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Acer on 20-4-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class GPIOTest {
    GPIO gpioController;

    @BeforeClass
    public void setup() {
        gpioController = new GPIO();
    }

    @AfterClass
    public void teardown() {
        gpioController.deinit();
    }

    @Test(expectedExceptions = IllegalPinModeException.class)
    public void writeInputPin() {
        gpioController.setPin(GPIO.PINS.PB31, false);
        gpioController.setPin(GPIO.PINS.PB30, false);
        gpioController.setPin(GPIO.PINS.PB21, false);
        gpioController.setPin(GPIO.PINS.PB20, false);
    }
}