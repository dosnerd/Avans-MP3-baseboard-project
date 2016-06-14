package MP3player.IO;

import MP3player.Errors.IllegalPinModeException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Acer on 11-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class GPIO {
    private static Object defaultGpio;
    private final Map<Pin, Long> blickData;

    public GPIO() {
        UI.println("Initialize GPIO...");
        blickData = new HashMap<Pin, Long>();
    }

    /**
     * Set the Gpio object. This is needed because an object of class can not create an object from the
     * default package. With the reflection API it will be possible to access the objects from classes
     * from the default package.
     *
     * @param defaultGpio An object of the type Gpio
     */
    public static void setDefaultGpio(Object defaultGpio) {
        GPIO.defaultGpio = defaultGpio;
    }

    /**
     * Read the status of the given Gpio pin. This method reflect to de class in the default package
     *
     * @param a The kernel ID of the Gpio pin.
     * @param v The value the set the Gpio pin
     */
    private void iowrite(int a, int v) {
        try {
            //get the iowrite method from the Gpio class by using the reflection API
            Method method = defaultGpio.getClass().getMethod("iowrite", int.class, int.class);

            //invoke (call) the iowrite method.
            method.invoke(defaultGpio, a, v);
        } catch (NoSuchMethodException e) {
            UI.error("iowrite method doesn't exists", 7);
        } catch (InvocationTargetException e) {
            UI.error("Error on iowrite method", 8);
        } catch (IllegalAccessException e) {
            UI.error("Can not access iowrite method", 9);
        }
    }

    /**
     * Read the status of the given Gpio pin. This method reflect to de class in the default package
     *
     * @param a The kernel ID of the given Gpio pin.
     */
    private int ioread(int a) {
        try {
            //get the ioread method from the Gpio class by using the reflection API
            Method method = defaultGpio.getClass().getMethod("ioread", int.class);

            //invoke (call) the ioread method, cast (convert) it to an integer and return the value
            return (Integer) method.invoke(defaultGpio, a);
        } catch (NoSuchMethodException e) {
            UI.error("ioread method doesn't exists", 7);
        } catch (InvocationTargetException e) {
            UI.error("Error on ioread method", 8);
        } catch (IllegalAccessException e) {
            UI.error("Can not access ioread method", 9);
        }
        return -1;
    }

    /**
     * To de-initialise the Gpio pin. Must be called when close program or when the Gpio pins are no longer be used.
     */
    public void deinit() {
        try {
            //get the iodeinit method from the Gpio class by using the reflection API
            Method method = defaultGpio.getClass().getMethod("iodeinit");

            //invoke (call) the iodeinit method.
            method.invoke(defaultGpio);
        } catch (NoSuchMethodException e) {
            UI.error("iodeinit method doesn't exists", 7);
        } catch (InvocationTargetException e) {
            UI.error("Error on iodeinit method", 8);
        } catch (IllegalAccessException e) {
            UI.error("Can not access iodeinit method", 9);
        }

        UI.println("Gpio deinitialized");
    }

    /**
     * With this method you can change the value of the given pin. The given pin must be a output pin. The value
     * of the pin will be 1 or 0.
     *
     * @param pin      The pin to change the value
     * @param isHeight The value. If set to true, the pin goes to high (1). If set to false, the pin goes to low (0)
     */
    public void setPin(Pin pin, boolean isHeight) {
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
    public boolean getPin(Pin pin) {
        //check is pin is an input pin and read the value of the pin.
        if (!pin.isOutput) {
            return (ioread(pin.ID) != 0);
        }
        throw new IllegalPinModeException();
    }

    /**
     * Turn on the given pin and turn it automatically off after the given time.
     *
     * @param pin   Pin to blick
     * @param milis Time before turning off in milliseconds
     */
    public void blick(Pin pin, int milis) {
        //Add pin to array and the time when it needs to turn off
        blickData.put(pin, System.currentTimeMillis() + milis);

        //turn on pin
        setPin(pin, true);
    }

    /**
     * Check for all pins if it need to turn off. It will only turn the pin off after the time that
     * is set by the blick function.
     */
    public void checkBlick() {
        //go through all pins
        for (Pin pin : Pin.values()) {
            //check if it needs to be turned off
            if (pin.isOutput && blickData.containsKey(pin) && blickData.get(pin) <= System.currentTimeMillis()) {
                setPin(pin, false);
                blickData.remove(pin);
            }

            //let a other thread do there jobs
            Thread.yield();
        }
    }

    /**
     * Cancel the blick. This will remove the time that is set by the blick function.
     *
     * @param pin pin to cancel the blink
     */
    void cancelBlick(Pin pin) {
        blickData.remove(pin);
    }

    /**
     * List of all usable Gpio pins, save with the right kernel ID and isOutput (input or output) <br />
     * An enum (enumeration) is a list of objects. These objects a from the same class. This objects are
     * created once and can not be created again. So the programmer write the list, and you can only chose from
     * this list. You can not create (easily) you own object from this class.
     */
    public enum Pin {
        PB31(false, 95),//MUX
        PB30(false, 94),//DIAL
        PB21(false, 85),//DIAL push
        PB20(false, 84),//DIAL
        PB19(false, 83),//DREQ
        PB17(true, 81),//SHIFT clk
        PB16(true, 80),//SHIFT serial
        PA28(true, 60),//SHIFT latch
        PA27(true, 59),//MUX 0
        PA26(true, 58),//MUX 1
        PA25(true, 57),//STOP
        PA22(true, 54),//LCD r/w
        PA11(true, 43),//LCD rs
        PA10(true, 42),//PREV LED
        PA9(true, 41),//PLAY LED
        PA7(true, 39),//NEXT LED
        PA6(true, 38);//PWR LED


        private final boolean isOutput;
        private final int ID;

        /**
         * Represent a pin with information about itself
         * @param isOutput true if it is an output pin, false if it is an input pin
         * @param ID The id to write/read by the imported Gpio class.
         */
        Pin(boolean isOutput, int ID) {
            this.isOutput = isOutput;
            this.ID = ID;
        }
    }
}
