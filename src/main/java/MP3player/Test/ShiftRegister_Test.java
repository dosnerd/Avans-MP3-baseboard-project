package MP3player.Test;

import MP3player.IO.GPIO;
import MP3player.IO.ShiftRegister;
import MP3player.IO.UI;

/**
 * Created by Acer on 19-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class ShiftRegister_Test extends Test {
    private ShiftRegister shiftRegister;

    public ShiftRegister_Test(GPIO io) {
        try {
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

            UI.print("Light bar: ");
            lightBar();

            if (isFailed()) {
                UI.println("MP3player.Test failed");
            } else {
                UI.println("MP3player.Test passed");
            }
        }
    }

    /**
     * This test will set the pin one by one high. After that it will set every pin
     * one by on low. This will use the setPin method of the class.
     */
    private void lightBar() {
        try {
            //Set all pin, one by one, high
            for (int i = 0; i < 8; i++) {
                UI.println(i + " is high");
                shiftRegister.setPin(i, true);
                shiftRegister.update();
                shiftRegister.setPin(i, false);
                waitForConfirm();
            }
            Thread.sleep(1);

            UI.println("Passed");
        } catch (InterruptedException ex) {
            UI.error("Can not sleep", 4);
        }
    }
}
