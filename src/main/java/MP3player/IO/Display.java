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
public class Display implements Runnable {
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

    /**
     * Constructor. This will initialize the LCD for two lines. After initializing it will write
     * Loading... to the screen.
     *
     * @param gpio     Object for controling some GPIO pins.
     * @param dataPins a shift register where the data pins of the LCD is connect to.
     * @param enable   The enable pin of the LCD
     * @param rs       The RS pin of the LCD
     */
    public Display(GPIO gpio, ShiftRegister dataPins, GPIO.Pin enable, GPIO.Pin rs) {
        UI.println("Initialize Display...");
        this.gpio = gpio;
        this.dataPins = dataPins;
        this.enable = enable;
        this.rs = rs;
        lines = new String[]{"", ""};
        pos = new int[2];

        run = true;

        //initialize and write loading...
        initialize();
        setDDRAMaddress(0);
        Write("Loading...");
    }

    /**
     * Get the scroll speed
     *
     * @return scroll speed
     */
    public int getShiftTime() {
        return shiftTime;
    }

    /**
     * Set the scroll speed of how fast the lines scrolls.
     *
     * @param scrollSpeed speed that a line scroll
     */
    public void setShiftTime(int scrollSpeed) {
        //check if scroll speed is to low
        if (scrollSpeed > 0) {
            this.shiftTime = scrollSpeed;
        }
    }

    /**
     * Stop the LCD. This means that it will not send the lines with text to the display. You can still
     * clear the screen.
     */
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

        //enable the display (show the text)
        setDisplay(true);

        UI.println("LCD screen initialized");
    }

    /**
     * Write settings. This includes shifting and in which direction.
     *
     * @param SH Enable shift
     * @param ID Increase/Decrease position after write a character. Increase if true, decrease if false
     */
    private void entryModeSet(boolean SH, boolean ID) {
        //set data
        dataPins.setPin(0, SH);
        dataPins.setPin(1, ID);
        for (int i = 3; i < 8; i++) {
            dataPins.setPin(i, false);
        }

        //send data
        Send();
    }

    /**
     * Set the visual settings of the display. This includes if the cursor is visible,
     * if the cursor is blinking and if the display is on/off
     *
     * @param show Show the data in the display (turn display on/off). If true, then it shows the data. If false,
     *             the data is invisible.
     */
    private void setDisplay(boolean show) {
        //set data
        gpio.setPin(rs, false);
        for (int i = 0; i < 8; i++) {
            if (i == 2) {
                //set text (in)visible
                dataPins.setPin(2, show);
            } else if (i == 3) {
                dataPins.setPin(3, true);
            } else
                dataPins.setPin(i, false);
        }

        //send data
        Send();
    }

    /**
     * Set startup information. This includes interface data lenght (how many pins we use),
     * how many lines we use and type of font (5x8 dots, 5x11 dots)
     */
    private void functionSet() {
        //set data
        gpio.setPin(rs, false);
        dataPins.setPin(7, false);
        dataPins.setPin(6, false);
        dataPins.setPin(5, true);

        dataPins.setPin(4, true);
        dataPins.setPin(3, true);
        dataPins.setPin(2, false);
        dataPins.setPin(1, false);
        dataPins.setPin(0, false);

        //send data
        Send();

        //wait so it can process
        sleep(1);
    }

    /**
     * Set the cursor to the given position.
     *
     * @param index posistion of cursor in memory
     */
    private void setDDRAMaddress(int index) {
        //check if out of limits
        if (index < 0 || index > 0x67) {
            throw new IndexOutOfBoundsException();
        }

        //set data
        gpio.setPin(rs, false);
        dataPins.setData((byte) index);
        dataPins.setPin(7, true);

        //send
        Send();
    }

    /**
     * Send data to the display. In here the shift register will be updated. Don't do this
     * before sending, because the enable line must be high first.
     */
    private synchronized void Send() {
        try {
            //set enable before setting data
            gpio.setPin(enable, true);

            //set data to data pins
            dataPins.update();

            //set enable, LCD will read data
            gpio.setPin(enable, false);

        } catch (Exception ex) {
            UI.println("Crash");
        }
    }

    /**
     * This will send the given line to the LCD. It will try to convert the
     * given line to ASCII, else it use it default. The cursor will not be set, it
     * will write where the cursor is. It will stop writing when the line is writing or
     * as it has wrote 16 characters.
     * @param line The line to write.
     */
    private void Write(String line) {
        //convert string to byte array. If possible, in SCII
        byte[] data;

        try {
            data = line.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            UI.println("US-ACII conversion not supported");
            data = line.getBytes();
        }

        //write the line to the screen.
        for (int i = 0; i < data.length; i++) {
            //if 16 character send, stop writing
            if (i > 16) {
                return;
            }

            //get 1 character at the current position
            byte character = data[i];

            //send character
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
        /*
        * check if the line need to be on the first line or second. if true, index will
        * be 0. If false, index will be 1
        */
        int index = firstLine ? 0 : 1;

        //clear screen and save data
        gpio.setPin(rs, true);

        //add spaces until it is as long as the previous line or 16 characters
        while (data.length() <= lines[index].length() && data.length() < 16) {
            data += " ";
        }

        //save line and force rewrite of line
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

        //confirm it stopped
        run = true;
        UI.println("Stop display thread");
    }

    /**
     * This will check if a line need to write to the screen. It calculate which line and which part of
     * the line by reading the pos array. If this is -1, it will rewrite the line at the beginning. If
     * this is higher than -1, it will represent which part is writing to the screen. The number in the
     * variable means what the starting point were. If the line is longer that 16 character, it will
     * scroll.
     */
    private void update() {
        //copy array to other variable. This will prevent data corruption cause by multithreading
        String[] lines = new String[this.lines.length];
        System.arraycopy(this.lines, 0, lines, 0, lines.length);

        //check every line
        for (int i = 0; i < lines.length; i++) {
            //wait until screen is cleared (if it is clearing)
            //noinspection StatementWithEmptyBody
            while (lock) ;
            lock = true;

            //get position
            int p = pos[i];
            if (p == -1) {
                //force rewrite

                //set which line (first or second)
                setDDRAMaddress(0x40 * i);

                //write line
                Write(lines[i]);

                /*
                * check if line is still the same
                * it is possible that another thread has change the line. if it is, it need to rewrite
                 * the line.
                */
                if (lines[i].equals(this.lines[i])) {
                    pos[i] = 0;
                }
            } else if (lines[i].length() > 16 && nextShift < System.currentTimeMillis()) {
                //current line is longer than 16 and may shift

                //Add the position
                pos[i]++;
                p++;

                //check if the part of the line is shorter that the size of the LCD
                if (lines[i].length() - p - 18 < 0) {
                    //check if it needs to start over again
                    if (lines[i].length() - p - 8 < 0) {
                        //reset position
                        pos[i] = 0;
                        p = 0;
                    } else {
                        //write with spaces at the end
                        setDDRAMaddress(0x40 * i);
                        Write(lines[i].substring(p) + "        ");
                        lock = false;
                        continue;
                    }
                }

                //write the part of the line
                setDDRAMaddress(0x40 * i);
                Write(lines[i].substring(p));
            }

            lock = false;
        }

        //set the time when the next shift take place
        if (nextShift < System.currentTimeMillis()) {
            nextShift = System.currentTimeMillis() + shiftTime;
        }

    }

    /**
     * Clear the screen and set the cursor to begin of the first line
     */
    public void ClearScreen() {
        //wait until the text it wrote to the screen (if it is writing)
        //noinspection StatementWithEmptyBody
        while (lock) ;
        lock = true;

        //set data
        UI.println("Clear screen");
        gpio.setPin(rs, false);
        dataPins.setPin(0, true);
        for (int i = 1; i < 8; i++) {
            dataPins.setPin(i, false);
        }

        //send data
        Send();

        //wait until screen is cleared. 1 millisecond is enough
        sleep(1);
        lock = false;
    }

    /**
     * This will let the thread sleep. This containt the try/catch block, so you won't need
     * to write the try/catch block again.
     * @param milis amount of time in milliseconds to sleep
     */
    private void sleep(int milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException ignored) {

        }
    }
}
