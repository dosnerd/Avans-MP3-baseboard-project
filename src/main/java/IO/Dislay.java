package IO;

import java.io.UnsupportedEncodingException;

/**
 * Created by Acer on 19-5-2016.
 *
 * This class simplify the use of the LCD screen that we use. It initialized the display immediately. You
 * can easily clear the screen or edit one of the two lines, without rewriting the other line.
 *
 * @author David de Prez
 * @version 1.0
 */
public class Dislay {
    private ShiftRegister dataPins;
    private GPIO gpio;
    private GPIO.Pin enable;
    private GPIO.Pin rs;
    private String[] lines;

    public Dislay(GPIO gpio, ShiftRegister dataPins, GPIO.Pin enable, GPIO.Pin rs) {
        this.gpio = gpio;
        this.dataPins = dataPins;
        this.enable = enable;
        this.rs = rs;
        lines = new String[]{"", ""};

        try {
            initialize();
        } catch (InterruptedException ex) {
            UI.error("Can not sleep", 4);
        }
    }

    /**
     * This initialize the display.
     *
     * @throws InterruptedException if the thread can not sleep (Thread.sleep(x) throws this)
     */
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

    /**
     * Set startup information. This includes interface data lenght (how many pins we use),
     * how many lines we use and type of font (5x8 dots, 5x11 dots)
     * @throws InterruptedException
     */
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

    /**
     * Set the cursor to the given position.
     * @param index posistion of cursor in memory
     */
    private void setDDRAMaddress(int index) {
        if (index < 0 || index > 0x67) {
            throw new IndexOutOfBoundsException();
        }
        Send();
    }

    /**
     * Send data to the display. In here the shift register will be updated. Don't do this
     * before sending, because the enable line must be high first.
     */
    private void Send() {
        gpio.setPin(enable, true);
        dataPins.update();
        gpio.setPin(enable, false);
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
            data = "<E11>";
        }

        //clear screen and save data
        ClearScreen();
        if (!firstLine) {
            lines[0] = data;
        } else {
            lines[1] = data;
        }

        //print both lines, because the both cleared
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            //if not the firstline, set cursor to second line
            if (i != 0) {
                setDDRAMaddress(0x40);
            }

            //print text to screen
            try {
                //get bytes in ACII and writes those to screen
                for (byte character : line.getBytes("US-ASCII")) {
                    dataPins.setData(character);
                    Send();
                }
            } catch (UnsupportedEncodingException ex) {
                UI.println("US-ACII conversion not supported");

                //get bytes and writes those to screen
                for (byte character : line.getBytes()) {
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
        dataPins.setPin(0, true);
        for (int i = 1; i < 8; i++) {
            dataPins.setPin(i, false);
        }

        Send();
    }
}
