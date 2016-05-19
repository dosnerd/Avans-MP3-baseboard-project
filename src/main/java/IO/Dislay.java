package IO;

import java.io.UnsupportedEncodingException;

/**
 * Created by Acer on 19-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class Dislay {
    private ShiftRegister dataPins;
    private GPIO gpio;
    private GPIO.Pin enable;
    private GPIO.Pin rs;

    public Dislay(GPIO gpio, ShiftRegister dataPins, GPIO.Pin enable, GPIO.Pin rs) {
        this.gpio = gpio;
        this.dataPins = dataPins;
        this.enable = enable;
        this.rs = rs;
    }

    private void initialize() throws InterruptedException {
        UI.println("Initialize LCD screen...");
        //3 time function set as suggested by the datasheet
        functionSet();
        Thread.sleep(4);
        functionSet();
        Thread.sleep(1);
        functionSet();

        //setup
        functionSet();
        setDisplay(false);
        ClearScreen();
        Thread.sleep(1);
        entryModeSet(false, true);
        UI.println("LCD screen initialized");
    }

    private void entryModeSet(boolean SH, boolean ID) {
        dataPins.setPin(0, SH);
        dataPins.setPin(1, ID);
        for (int i = 3; i < 8; i++) {
            dataPins.setPin(i, false);
        }
    }

    private void setDisplay(boolean show) {
        gpio.setPin(rs, false);
        for (int i = 0; i < 8; i++) {
            if (i == 3)
                dataPins.setPin(3, show);
            else
                dataPins.setPin(i, false);
        }
        Send();
    }

    private void functionSet() throws InterruptedException {
        gpio.setPin(rs, false);
        dataPins.setPin(7, false);
        dataPins.setPin(6, false);
        dataPins.setPin(5, true);
        dataPins.setPin(4, true);

        dataPins.setPin(3, true);
        dataPins.setPin(2, true);
        Send();
        Thread.sleep(1);
    }

    private void setDDRAMaddress(int index) {
        if (index < 0 || index > 0x67) {
            throw new IndexOutOfBoundsException();
        }
        Send();
    }

    private void Send() {
        gpio.setPin(enable, true);
        dataPins.update();
        gpio.setPin(enable, false);
    }

    public void Write(String data, boolean firstLine) {
        if (firstLine) {
            setDDRAMaddress(0);
        } else {
            setDDRAMaddress(0x40);
        }

        if (data.length() > 39){
            data = "<E11>";
        }

        try {
            for (byte character : data.getBytes("US-ASCII")) {
                dataPins.setData(character);
                Send();
            }
        } catch (UnsupportedEncodingException ex) {
            UI.println("US-ACII conversion not supported");
            for (byte character : data.getBytes()) {
                dataPins.setData(character);
                Send();
            }
        }
    }

    public void ClearScreen() {
        dataPins.setPin(0, true);
        for (int i = 1; i < 8; i++) {
            dataPins.setPin(i, false);
        }

        Send();
    }
}
