package IO;

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

    public MUX(GPIO gpio, GPIO.Pin select1, GPIO.Pin select2, GPIO.Pin select3, GPIO.Pin read) {
        this.gpio = gpio;
        this.select1 = select1;
        this.select2 = select2;
        this.select3 = select3;
        this.read = read;
        timeTable = new long[8];
        timeHistory = new long[8];
    }

    public boolean getPin(int pin) {
        return timePin(pin) > 50;

    }

    public boolean pressed(int pin) {
        if (!readPin(pin)) {
            try {
                return timePin(pin) > 10 && timePin(pin) < 2000;
            } finally {
                resetTime(pin);
            }
        }

        return false;

    }

    private void resetTime(int pin) {
        if (pin >= 0 && pin < timeHistory.length) {
            timeHistory[pin] = 0;
        }
    }

    private boolean readPin(int pin) {
        gpio.setPin(select1, (pin & 1) > 0);
        gpio.setPin(select2, (pin & (1 << 1)) > 0);
        if (select3 != null) {
            gpio.setPin(select3, (pin & (1 << 2)) > 0);
        }

        return gpio.getPin(read);
    }

    public long timePin(int pin) {
        if (pin >= 0 && pin < timeHistory.length) {
            return timeHistory[pin];
        }

        return 0;
    }

    public void check() {
        for (int i = 0; i < timeTable.length; i++) {
            if (readPin(i)) {
                timeHistory[i] = System.currentTimeMillis() - timeTable[i];
            } else {
                timeTable[i] = System.currentTimeMillis();
            }
        }
    }
}
