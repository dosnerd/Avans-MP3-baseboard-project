package MP3player.Test;

import MP3player.IO.GPIO;
import MP3player.IO.MUX;
import MP3player.IO.UI;

/**
 * Created by Acer on 25-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class MUXLED_Test extends Test {
    private final MUX mux;
    private final GPIO io;

    public MUXLED_Test(GPIO io) {
        this.io = io;
        mux = new MUX(io, GPIO.Pin.PA27, GPIO.Pin.PA26, null, GPIO.Pin.PB31);
    }

    @Override
    public void run() {
        UI.println("Start MUXLED test");
        mux.check();

        UI.println("findPin");
        findPin();

        UI.println("hitRightButton");
        hitRightButton();
    }

    private void findPin() {
        UI.print("Pin 0: ");
        io.setPin(GPIO.Pin.PA10, true);
        while (!mux.getPin(0)) mux.check();
        io.setPin(GPIO.Pin.PA10, false);
        UI.println("Found");

        UI.print("Pin 1: ");
        io.setPin(GPIO.Pin.PA9, true);
        while (!mux.getPin(1)) mux.check();
        io.setPin(GPIO.Pin.PA9, false);
        UI.println("Found");

        UI.print("Pin 2: ");
        io.setPin(GPIO.Pin.PA25, true);
        while (!mux.getPin(2)) mux.check();
        io.setPin(GPIO.Pin.PA25, false);
        UI.println("Found");

        UI.print("Pin 3: ");
        io.setPin(GPIO.Pin.PA7, true);
        while (!mux.getPin(3)) mux.check();
        io.setPin(GPIO.Pin.PA7, false);
        UI.println("Found");
    }

    private void hitRightButton() {
        readInput(0);
        readInput(1);
        readInput(2);
        readInput(3);
    }

    private void readInput(int pin) {
        mux.check();
        UI.print("Hit pin 0 (2 sec long): ");
        for (int i = 0; i < 200; i++) {
            mux.check();
            sleep(2);
        }

        mux.check();

        if (mux.getPin(pin)) {
            UI.println("Passed(" + mux.timePin(pin) + ")");
        } else {
            Fail("Pin " + pin + " is not high!");
        }
    }

    private void sleep(int mili) {
        try {
            Thread.sleep(mili);
        } catch (InterruptedException ex) {
            UI.error("Can sleep", 4);
        }
    }
}
