package IO;

import Errors.IllegalPinModeException;

/**
 * Created by Acer on 20-4-2016.
 * <p/>
 * To control the GPIO of the FOXG20, within the MP3-baseboard, easier. All usable pin are already combined with
 * the right kernel ID, native library is already loaded and it will check if the selected pin is a output or
 * a input.
 *
 * @author David de Prez
 * @version 1.0
 */
public class GPIO {
    /**
     * Constructor. This will load the library and initialize the GPIO library.
     */
    public GPIO() {
        //load and initialize the GPIO library
        System.loadLibrary("Gpio");
        ioinit();
    }


    /**
     * Initialize the GPIO pins. This method is from the Gpio library
     *
     * @return unknown
     */
    public native int ioinit();

    /**
     * Read the status of the given GPIO pin. This method is from the Gpio library
     *
     * @param a The kernel ID of the GPIO pin.
     * @param v The value the set the GPIO pin
     * @return unknown
     */
    public native int iowrite(int a, int v);

    /**
     * Read the status of the given GPIO pin. This method is from the Gpio library
     *
     * @param a The kernel ID of the given GPIO pin.
     * @return unknown
     */
    public native int ioread(int a);

    /**
     * De-initialize the Gpio library.  This method is from the Gpio library
     *
     * @return unknown
     */
    public native int iodeinit();

    /**
     * To de-initialise the GPIO pin. Must be called when close program or when the GPIO pins are no longer be used.
     */
    public void deinit() {
        iodeinit();
    }

    /**
     * With this method you can change the value of the given pin. The given pin must be a output pin. The value
     * of the pin will be 1 or 0.
     *
     * @param pin      The pin to change the value
     * @param isHeight The value. If set to true, the pin goes to high (1). If set to false, the pin goes to low (0)
     */
    public void setPin(PINS pin, boolean isHeight) {
        //check if the pin has the right mode
        if (pin.isOut) {
            //get kernel ID; if value > 0 -> set pin to 1, else set pin to 0
            iowrite(pin.ID, isHeight ? 1 : 0);
        } else {
            //throw exception
            throw new IllegalPinModeException();
        }
    }


    /**
     * List of all usable GPIO pins, save with the right kernel ID and type (input or output)
     */
    public enum PINS {
        PB31(false, 95),
        PB30(false, 94),
        PB21(false, 85),
        PB20(false, 84),
        PB17(true, 81),
        PB16(true, 80),
        PA28(true, 60),
        PA27(true, 59),
        PA26(true, 58),
        PA25(true, 57),
        PA22(true, 54),
        PA11(true, 43),
        PA10(true, 42),
        PA9(true, 42),
        PA7(true, 39),
        PA6(true, 38);

        private final boolean isOut;
        private final int ID;

        PINS(boolean isOut, int ID) {
            this.isOut = isOut;
            this.ID = ID;
        }
    }
}