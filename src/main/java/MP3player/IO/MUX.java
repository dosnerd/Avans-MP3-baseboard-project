package MP3player.IO;

/**
 * Created by Acer on 23-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class MUX {
    private final GPIO gpio;
    private final GPIO.Pin select1;
    private final GPIO.Pin select2;
    private final GPIO.Pin select3;
    private final GPIO.Pin read;

    private final long[] timeTable;
    private final long[] timeHistory;

    /**
     * @param gpio    object to control the pins
     * @param select1 the first pin of the multiplexer to select a line
     * @param select2 the second pin of the multiplexer to select a line
     * @param select3 the third pin of the multiplexer to select a line (can be null if not used)
     * @param read    Pin where the multiplexer set the data of the selected line
     */
    public MUX(GPIO gpio, GPIO.Pin select1, GPIO.Pin select2, GPIO.Pin select3, GPIO.Pin read) {
        this.gpio = gpio;
        this.select1 = select1;
        this.select2 = select2;
        this.select3 = select3;
        this.read = read;
        timeTable = new long[8];
        timeHistory = new long[8];
    }

    /**
     * Get the status (high=true, low=false) of the given line
     *
     * @param pin the line to read from
     * @return status of the line
     */
    public boolean getPin(int pin) {
        //check if it pushed longer that 50ms (debouching)
        return timePin(pin) > 50;

    }

    /**
     * Check if the given line is pressed: high and low for longer than 10ms and shorter than 2000ms.
     *
     * @param pin line to check
     * @return if the line is pressed.
     */
    public boolean pressed(int pin) {
        //check if the line isn't high anymore
        if (!readPin(pin)) {
            try {
                //check if the time is where high is within the limits
                return timePin(pin) > 10 && timePin(pin) < 2000;
            } finally {
                //reset always the time of the line (will run after return)
                resetTime(pin);
            }
        }

        //line still high, so it isn't pressed (need to be low first)
        return false;

    }

    /**
     * reset the time that the pin is pressed
     * @param pin the line where the time needs to be reset
     */
    private void resetTime(int pin) {
        //check if line exits
        if (pin >= 0 && pin < timeHistory.length) {
            //reset time
            timeHistory[pin] = 0;
        }
    }


    /**
     * Read the status of the given line
     * @param pin line to read from
     * @return status. True if high, false if low.
     */
    private boolean readPin(int pin) {
        //check first bit of pin is high
        gpio.setPin(select1, (pin & 1) > 0);

        //check second bit of pin is high
        gpio.setPin(select2, (pin & (1 << 1)) > 0);

        //check third bit of pin is high, if third select is in use
        if (select3 != null) {
            gpio.setPin(select3, (pin & (1 << 2)) > 0);
        }

        //return status of line
        return gpio.getPin(read);
    }

    /**
     * Get the time that the line is high
     * @param pin line to check from
     * @return the time in milliseconds.
     */
    public long timePin(int pin) {
        //return time if pin exists
        if (pin >= 0 && pin < timeHistory.length) {
            return timeHistory[pin];
        }

        return 0;
    }

    /**
     * update the time that a line is high.
     */
    public void check() {
        //go through all lines
        for (int i = 0; i < timeTable.length; i++) {
            if (readPin(i)) {
                //update time the line is high
                timeHistory[i] = System.currentTimeMillis() - timeTable[i];
            } else {
                //update the time that the line is for the last low
                timeTable[i] = System.currentTimeMillis();
            }
        }
    }
}
