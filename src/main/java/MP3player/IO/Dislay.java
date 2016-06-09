package MP3player.IO;

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
public class Dislay implements Runnable {
    private final ShiftRegister dataPins;
    private final GPIO gpio;
    private final GPIO.Pin enable;
    private final GPIO.Pin rs;
    private final String[] lines;
    private final int[] pos;
    private int shiftTime = 1000;
    private long nextShift;
    private boolean run;
    private boolean lock;

    public Dislay(GPIO gpio, ShiftRegister dataPins, GPIO.Pin enable, GPIO.Pin rs) {
        UI.println("Initialize Display...");
        this.gpio = gpio;
        this.dataPins = dataPins;
        this.enable = enable;
        this.rs = rs;
        lines = new String[]{"", ""};
        pos = new int[2];

        run = true;

        initialize();

        setDDRAMaddress(0);
        Write("Loading");
    }

    public int getShiftTime() {
        return shiftTime;
    }

    public void setShiftTime(int shiftTime) {
        if (shiftTime > 0) {
            this.shiftTime = shiftTime;
        }
    }

    public void Stop() {
        run = false;
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
        sleep(1);

        //setup
        functionSet();
        setDisplay(false);
        ClearScreen();
        sleep(1);
        entryModeSet(false, true);
        sleep(1);

        setDisplay(true);

        //functionSet();

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
        gpio.setPin(rs, false);
        dataPins.setData((byte) index);
        dataPins.setPin(7, true);
        Send();
    }

    /**
     * Send data to the display. In here the shift register will be updated. Don't do this
     * before sending, because the enable line must be high first.
     */
    private synchronized void Send() {
        try {
            gpio.setPin(enable, true);
            dataPins.update();
            gpio.setPin(enable, false);

        } catch (Exception ex) {
            UI.println("Crash");
        }
    }

    private void Write(String line) {
        byte[] data;

        try {
            data = line.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            UI.println("US-ACII conversion not supported");
            data = line.getBytes();
        }


        for (int i = 0; i < data.length; i++) {
            if (i > 16) {
                return;
            }

            byte character = data[i];

            dataPins.setData(character);
            gpio.setPin(rs, true);
            Send();
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
    public synchronized void WriteNewLine(String data, boolean firstLine) {
        int index = firstLine ? 0 : 1;

        //clear screen and save data
        gpio.setPin(rs, true);

        while (data.length() <= lines[index].length() && data.length() < 16) {
            data += " ";
        }

        lines[index] = data;
        pos[index] = -1;

    }

    @Override
    public void run() {
        UI.println("Start display thread");
        while (run) {
            update();
            sleep(10);
        }
        UI.println("Stop display thread");
    }

    private void update() {
        synchronized (lines) {
            for (int i = 0; i < lines.length; i++) {
                //noinspection StatementWithEmptyBody
                while (lock) ;
                lock = true;
                int p = pos[i];
                if (p == -1) {
                    setDDRAMaddress(0x40 * i);
                    Write(lines[i]);
                    pos[i] = 0;
                } else if (lines[i].length() > 16 && nextShift < System.currentTimeMillis()) {
                    pos[i]++;
                    p++;

                    if (lines[i].length() - p - 18 < 0) {
                        if (lines[i].length() - p - 8 < 0) {
                            pos[i] = 0;
                            p = 0;
                        } else {
                            setDDRAMaddress(0x40 * i);
                            Write(lines[i].substring(p) + "        ");
                            lock = false;
                            continue;
                        }
                    }

                    setDDRAMaddress(0x40 * i);
                    Write(lines[i].substring(p));
                }

                lock = false;
            }

            if (nextShift < System.currentTimeMillis()) {
                nextShift = System.currentTimeMillis() + shiftTime;
            }
        }
    }

    /**
     * Clear the screen and set the cursor to begin of the first line
     */
    public void ClearScreen() {
        //noinspection StatementWithEmptyBody
        while (lock) ;
        lock = true;

        UI.println("Clear screen");
        gpio.setPin(rs, false);
        dataPins.setPin(0, true);
        for (int i = 1; i < 8; i++) {
            dataPins.setPin(i, false);
        }

        Send();
        sleep(1);
        lock = false;
    }

    private void sleep(int milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException ignored) {

        }
    }
}
