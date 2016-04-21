import Errors.IllegalPinModeException;

/**
 * Created by Acer on 20-4-2016.
 * <p/>
 * To control the Gpio of the FOXG20, within the MP3-baseboard, easier. All usable pin are already combined with
 * the right kernel ID, native library is already loaded and it will check if the selected pin is a output or
 * a input.
 *
 * @author David de Prez
 * @version 1.0
 */
public class Gpio {
    static {
        //when class used for the first time, load Gpio library
        System.loadLibrary("Gpio");
    }

    /**
     * Constructor. This will load the library and initialize the Gpio library.
     */
    public Gpio() {
        //initialize the Gpio library
        ioinit();
    }

    /**
     * Initialize the Gpio pins. This method is from the Gpio library
     *
     * @return unknown
     */
    public native int ioinit();

    /**
     * Read the status of the given Gpio pin. This method is from the Gpio library
     *
     * @param a The kernel ID of the Gpio pin.
     * @param v The value the set the Gpio pin
     * @return unknown
     */
    public native int iowrite(int a, int v);

    /**
     * Read the status of the given Gpio pin. This method is from the Gpio library
     *
     * @param a The kernel ID of the given Gpio pin.
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
     * To de-initialise the Gpio pin. Must be called when close program or when the Gpio pins are no longer be used.
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
        if ((pin.type & 0x11) == 0x10) {
            //get kernel ID; if value > 0 -> set pin to 1, else set pin to 0
            iowrite(pin.ID, isHeight ? 1 : 0);
        } else {
            //throw exception
            throw new IllegalPinModeException();
        }
    }

    /**
     * List of all usable Gpio pins, save with the right kernel ID and type (input or output)
     */
    public enum PINS {
        PB2(0x11, 66),  //CLK
        PB3(0x11, 67),  //SS
        PB0(0x01, 68),  //MISO
        PB1(0x11, 69),  //MOSI
        PB31(0x00, 95),
        PB30(0x00, 94),
        PB21(0x00, 85),
        PB20(0x00, 84),
        PB17(0x10, 81),
        PB16(0x10, 80),
        PA28(0x10, 60),
        PA27(0x10, 59),
        PA26(0x10, 58),
        PA25(0x10, 57),
        PA22(0x10, 54),
        PA11(0x10, 43),
        PA10(0x10, 42),
        PA9(0x10, 42),
        PA7(0x10, 39),
        PA6(0x10, 38);


        /**
         * Meaning of bits:
         * ab
         * a -> output(0)/input(1)
         * b -> GPIO(0)/SPI(1)
         */
        private final short type;
        private final int ID;

        PINS(int type, int ID) {
            this.type = (short) type;
            this.ID = ID;
        }
    }
}