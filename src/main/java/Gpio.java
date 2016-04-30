import Errors.IllegalPinModeException;

/**
 * Created by Acer on 20-4-2016.
 * <p/>
 * To control the Gpio of the FOXG20, within the MP3-baseboard, easier. All usable pin are already combined with
 * the right kernel ID, native library is already loaded and it will check if the selected pin is a output or
 * a input.
 *
 * @author David de Prez
 * @version 1.5
 */
class Gpio {
    static {
        //when class used for the first time, load Gpio library
        System.loadLibrary("Gpio");
    }

    /**
     * Constructor. This will load the library and initialize the Gpio library.
     */
    Gpio() {
        //initialize the Gpio library
        ioinit();
    }

    /**
     * Initialize the Gpio pins. This method is from the Gpio library
     */
    private native void ioinit();

    /**
     * Read the status of the given Gpio pin. This method is from the Gpio library
     *
     * @param a The kernel ID of the Gpio pin.
     * @param v The value the set the Gpio pin
     */
    private native void iowrite(int a, int v);

    /**
     * Read the status of the given Gpio pin. This method is from the Gpio library
     *
     * @param a The kernel ID of the given Gpio pin.
     */
    private native int ioread(int a);

    /**
     * De-initialize the Gpio library.  This method is from the Gpio library
     */
    private native void iodeinit();

    /**
     * To de-initialise the Gpio pin. Must be called when close program or when the Gpio pins are no longer be used.
     */
    void deinit() {
        iodeinit();
    }

    /**
     * With this method you can change the value of the given pin. The given pin must be a output pin. The value
     * of the pin will be 1 or 0.
     *
     * @param pin      The pin to change the value
     * @param isHeight The value. If set to true, the pin goes to high (1). If set to false, the pin goes to low (0)
     */
    void setPin(PINS pin, boolean isHeight) {
        //check if the pin has the right mode
        if (pin.isOutput) {
            //get kernel ID; if value > 0 -> set pin to 1, else set pin to 0
            iowrite(pin.ID, isHeight ? 1 : 0);
        } else {
            //throw exception
            throw new IllegalPinModeException();
        }
    }

    /**
     * Read the value of the given pin. Pin must be an input pin.
     *
     * @param pin A digital input pin to read from
     * @return false if pin is low, true if pin is high
     */
    boolean getPin(PINS pin) {
        if (!pin.isOutput) {
            return ioread(pin.ID) != 0;
        }

        throw new IllegalPinModeException();
    }

    /**
     * List of all usable Gpio pins, save with the right kernel ID and isOutput (input or output)
     */
    enum PINS {
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


        /**
         * Meaning of bits:
         * ab
         * a -> output(0)/input(1)
         * b -> GPIO(0)/SPI(1)
         */
        private final boolean isOutput;
        private final int ID;

        PINS(boolean isOutput, int ID) {
            this.isOutput = isOutput;
            this.ID = ID;
        }
    }
}