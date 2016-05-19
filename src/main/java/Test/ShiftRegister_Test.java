package Test;

import IO.GPIO;
import IO.ShiftRegister;
import IO.UI;

/**
 * Created by Acer on 19-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class ShiftRegister_Test extends Test {
    private GPIO io;
    private ShiftRegister shiftRegister;

    public ShiftRegister_Test() {
        try {
            io = new GPIO();
            shiftRegister = new ShiftRegister(io, GPIO.Pin.PB17, GPIO.Pin.PB16, GPIO.Pin.PA28);
        } catch (Exception ex) {
            Fail("Can not create instance");
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (!isFailed()) {
            UI.println("Start shift register test");
            UI.print("Counting: ");
            count();

            UI.print("Lightbar: ");
            lightBar();

            if (isFailed()) {
                UI.println("Test failed");
            } else {
                UI.println("Test passed");
            }
        }
        if (io != null) {
            io.deinit();
        }
    }

    /**
     * This test will count in binary from 0 to 255. It will send data by rewriting the memory of the
     * class directly.
     */
    private void count() {
        //count binaire from 0 to 255
        for (int i = 0; i < 255; i++) {
            shiftRegister.setData((byte) i);
            shiftRegister.update();
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                UI.error("Can not sleep", 4);
                return;
            }
        }

        //set all pins to 0
        shiftRegister.setData((byte) 0);
        shiftRegister.update();
        UI.println("Passed");
    }

    /**
     * This test will set the pin one by one high. After that it will set every pin
     * one by on low. This will use the setPin method of the class.
     */
    private void lightBar() {
        try {
            //Set all pin, one by one, high
            for (int i = 0; i < 8; i++) {
                shiftRegister.setPin(i, true);
                shiftRegister.update();
                Thread.sleep(1);
            }
            Thread.sleep(1);

            //Set all pin, one by one, low
            for (int i = 0; i < 8; i++) {
                shiftRegister.setPin(i, true);
                shiftRegister.update();
                Thread.sleep(1);
            }
            UI.println("Passed");
        } catch (InterruptedException ex) {
            UI.error("Can not sleep", 4);
        }
    }
}
