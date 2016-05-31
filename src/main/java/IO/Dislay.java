package IO;

import java.io.UnsupportedEncodingException;

/**
 * Created by Acer on 19-5-2016.
 * <p>
 * This class simplify the use of the LCD screen that we use. It initialized the display immediately. You
 * can easily clear the screen or edit one of the two lines, without rewriting the other line.
 *
 * @author David de Prez
 * @version 1.0
 */
public class Dislay {
    private final ShiftRegister dataPins;
    private final GPIO gpio;
    private final GPIO.Pin enable;
    private final GPIO.Pin rs;
    private final String[] lines;

    public Dislay(GPIO gpio, ShiftRegister dataPins, GPIO.Pin enable, GPIO.Pin rs) {
        UI.println("Initialize Display...");
        this.gpio = gpio;
        this.dataPins = dataPins;
        this.enable = enable;
        this.rs = rs;
        lines = new String[]{"", ""};

        initialize();

    }

    /**
     * This initialize the display.
     */
    private void initialize() {
        UI.println("Initialize LCD screen...");
        //3 time function set as suggested by the datasheet
        functionSet();
        sleep(4);
        functionSet();
        sleep(1);
        functionSet();

        //setup
        functionSet();
        setDisplay(false);
        ClearScreen();
        sleep(1);
        entryModeSet(false, true);
        sleep(1);

        UI.println("LCD screen initialized");
    }

    /**
     * Write settings. This includes shifting and in which direction.
     *
     * @param SH Enable shift
     * @param ID Increase/Decrease position after write a character. Increase if true, decrease if false
     */
    private void entryModeSet(boolean SH, boolean ID) {
        dataPins.setPin(0, SH);
        dataPins.setPin(1, ID);
        for (int i = 3; i < 8; i++) {
            dataPins.setPin(i, false);
        }
    }

    /**
     * Set the visual settings of the display. This includes if the cursor is visible,
     * if the cursor is blinking and if the display is on/off
     *
     * @param show Show the data in the display (turn display on/off). If true, then it shows the data. If false,
     *             the data is invisible.
     */
    public void setDisplay(boolean show) {
        gpio.setPin(rs, false);
        for (int i = 0; i < 8; i++) {
            if (i == 2) {
                dataPins.setPin(2, show);
            } else if (i == 3) {
                dataPins.setPin(3, true);
            } else
                dataPins.setPin(i, false);
        }
        Send();
    }

    /**
     * Set startup information. This includes interface data lenght (how many pins we use),
     * how many lines we use and type of font (5x8 dots, 5x11 dots)
     */
    private void functionSet() {
        gpio.setPin(rs, false);
        dataPins.setPin(7, false);
        dataPins.setPin(6, false);
        dataPins.setPin(5, true);

        dataPins.setPin(4, true);
        dataPins.setPin(3, true);
        dataPins.setPin(2, false);
        dataPins.setPin(1, false);
        dataPins.setPin(0, false);
        Send();
        sleep(1);
    }

    /**
     * Set the cursor to the given position.
     *
     * @param index posistion of cursor in memory
     */
    private void setDDRAMaddress(int index) {
        if (index < 0 || index > 0x67) {
            throw new IndexOutOfBoundsException();
        }
        dataPins.setData((byte) index);
        dataPins.setPin(7, true);
        Send();
    }

    /**
     * Send data to the display. In here the shift register will be updated. Don't do this
     * before sending, because the enable line must be high first.
     */
    private void Send() {
        try {
            gpio.setPin(enable, true);
            dataPins.update();
            gpio.setPin(enable, false);

        } catch (Exception ex) {
            UI.println("Crash");
        }
    }

    /**
     * Write text to screen. This will first reset the position, then write the
     * text to the screen. This will clear the line first. If the data is longer than 39
     * then is will display E11 instead of the data
     *
     * @param data      Data shorten then 39 for in the line.
     * @param firstLine True if the data is on the top line. False if data on second line
     */
    public void WriteNewLine(String data, boolean firstLine) {
        //check size of data
        if (data.length() > 39) {
            UI.error("String is to long", 13);
            data = "<E13>";
        }

        //clear screen and save data
        gpio.setPin(rs, true);

        if (firstLine) {
            while (data.length() < lines[0].length()) {
                data += " ";
            }
            lines[0] = data;
        } else {
            while (data.length() < lines[1].length()) {
                data += " ";
            }
            lines[1] = data;
        }


        functionSet();

        //print both lines, because the both cleared
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            //if not the firstline, set cursor to second line
            if (i != 0) {
                setDDRAMaddress(0x40);
            } else {
                setDDRAMaddress(0x0);
                //setDDRAMaddress(0x0);
            }

            //print text to screen
            try {
                //get bytes in ACII and writes those to screen
                for (byte character : line.getBytes("US-ASCII")) {
                    dataPins.setData(character);
                    gpio.setPin(rs, true);
                    Send();
                }
                setDisplay(true);
            } catch (UnsupportedEncodingException ex) {
                UI.println("US-ACII conversion not supported");

                //get bytes and writes those to screen
                for (byte character : line.getBytes()) {
                    gpio.setPin(rs, true);
                    dataPins.setData(character);
                    Send();
                }
            }
        }
    }

    /**
     * Clear the screen and set the cursor to begin of the first line
     */
    public void ClearScreen() {
        UI.println("Clear screen");
        dataPins.setPin(0, true);
        for (int i = 1; i < 8; i++) {
            dataPins.setPin(i, false);
        }

        sleep(1);
        Send();
        sleep(1);
    }

    private void sleep(int milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException ignored) {

        }
    }
}
