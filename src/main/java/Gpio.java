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
@SuppressWarnings("WeakerAccess")
public class Gpio {
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
    public native void iowrite(int a, int v);

    /**
     * Read the status of the given Gpio pin. This method is from the Gpio library
     *
     * @param a The kernel ID of the given Gpio pin.
     */
    public native int ioread(int a);

    /**
     * De-initialize the Gpio library.  This method is from the Gpio library
     */
    public native void iodeinit();
}