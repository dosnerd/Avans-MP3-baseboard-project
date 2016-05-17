package IO;

import java.io.*;

/**
 * Created by Acer on 2-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class VS1033 implements Runnable {
    private final GPIO gpio;
    private final String PATH = System.getProperty("user.home") + "/music/";
    private RandomAccessFile SCI;
    private RandomAccessFile SDI;
    private InputStream fileStream;
    private boolean run = true;
    private boolean play = false;
    private String source = "";

    private int bufferSize = 128;
    private long tick;

    private long bitsSended = 0;

    public VS1033(GPIO gpio) {
        this.gpio = gpio;

        init();
    }

    /**
     * Check if the file stream is on the end of the file
     *
     * @return true if on the end of the file. False if there is data available.
     */
    private boolean getEndOfFile() {
        try {
            return fileStream == null || fileStream.available() <= 0;
        } catch (IOException ignored) {
            return false;
        }
    }

    /**
     * Initialize the VS1033. This is done by a software reset and setting the volume. This will also open de SCI
     * and SDI connection.
     */
    private void init() {
        //initialize and volume data
        byte[] init = {0x02, 0x00, 0x08, 0x46}; //MODE
        byte[] clockf = {0x02, 0x03, (byte) 0x90, (byte) 0x00};//CLOCKF
        byte[] vol = {0x02, 0x0B, 0x2F, 0x2F};  //VOL
        //TODO: BASS

        UI.println("Initializing VS1033...");

        try {
            //load interfaces
            SCI = new RandomAccessFile("/dev/spidev1.0", "rw");
            UI.println("SCI loaded");
            SDI = new RandomAccessFile("/dev/spidev1.1", "rw");
            UI.println("SDI loaded");

            //send commands
            UI.println("Send initialize data");
            Write(init, true);

            UI.println("Send clock data");
            Write(clockf, true);

            UI.println("Send volume data");
            Write(vol, true);

            UI.println("VS1033 initialized");
        } catch (IOException ignored) {
            IO.UI.println("IO exception. Probably can't open /dev/spidev1.*");
        }
    }

    /**
     * This wil de-initialize the VS1033. This means that it will stop playing, remove the filepath,
     * close the file stream, close, the SCI connection and close the SDI connection,
     */
    public void deinit() {
        run = false;
        play = false;
        source = "";
        closeFileStream();

        try {
            if (SCI != null) {
                SCI.close();
                SCI = null;
            }
        } catch (IOException ex) {
            UI.error("Can not close SCI", 6);
        }

        try {
            if (SDI != null) {
                SDI.close();
                SDI = null;
            }
        } catch (IOException ex) {
            UI.error("Can not close SCI", 6);
        }
        UI.println("Gpio deinitialized");
    }

    /**
     * Resume the track
     */
    public void Play() {
        play = true;
    }

    /**
     * Start playing the given track. The filename is without the path!
     *
     * @param filename The name of the track without path
     */
    public void Play(String filename) {
        //stop playing, preventing sending more data of the current file to VS1033
        play = false;

        //set path of file
        source = PATH + filename;

        //force to create a new file stream
        closeFileStream();
        play = true;
    }

    /**
     * Try to safely close the file stream
     */
    private void closeFileStream() {
        try {
            if (fileStream != null) {
                fileStream.close();
                fileStream = null;
                tick = 0;
            }
        } catch (IOException ex) {
            UI.error("Can not close filestream", 6);
        }
    }

    /**
     * Stop sending data to the VS1033. This will cause the VS1033 to give sound. The location
     * in the file will be remembered. When
     */
    private void Pauze() {
        play = false;
    }

    /**
     * Stop the track. This will first pauze the track, then it will scroll to the start of the track.
     */
    public void Stop() {
        Pauze();
        try {
            fileStream.reset();
            tick = 0;
        } catch (IOException ex) {
            UI.error("Can not reset file", 10);
        }
    }

    /**
     * Send data to the VS1033. It will check if it is allowed to send data (by DREQ pin). When not allowed,
     * it will wait until it is allowed. When it is allowed to send data, it send data. If it is an
     * operation, it will send ofer SCI, else over SDI.
     * <p>
     * This function need to be synchronized the problems when data been send and a command (such volume) by
     * a different thread. This may not at the same time.
     *
     * @param data        The data or the command(s) to send at once.
     * @param isOperation If true, the data will be sended over SCI. If false, the data will be send over SDI.
     */
    private synchronized void Write(byte[] data, boolean isOperation) {
        //UI.println("Prepaire for sending over SCI/SDI...");

        //check if allowed to send data/command
        while (!gpio.getPin(GPIO.Pin.PB19)) {
            //wait when not allowed to send data/command
            try {
                UI.println("wait");
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }

        //try sending data/command
        try {
            if (isOperation) {
                UI.println("Send over SCI");
                SCI.write(data);
            } else {
                //UI.println("Send over SDI");
                SDI.write(data);
            }
        } catch (IOException ex) {
            UI.error("Can not write to SCI/SDI", 2);
        }
    }

    private synchronized byte[] Read(byte[] command, int length) {
        //Write(command, true);
        /*while (!gpio.getPin(GPIO.Pin.PB19)) {
            //wait when not allowed to send data/command
            try {
                UI.println("wait");
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }*/

        byte[] read = new byte[length];
        try {
            SCI.write(command);
            Thread.sleep(1);
            for (int i = 0; i < length; i++) {
                read[i] = SCI.readByte();
            }
        } catch (IOException ex) {
            UI.error("Can not read SCI", 3);
        } catch (Exception ignored) {
            //TODO: make this beter catch
        }

        return read;
    }

    private void checkBitRate() {
        //byte[] a = Read(new byte[]{0x03, 0x08}, 2);
        byte[] HDAT = Read(new byte[]{0x03, 0x09}, 2);
        Read(new byte[]{}, 2);
        byte[] a = Read(new byte[]{0x03, 0x08}, 2);
        //byte[] HDAT = Read(new byte[]{0x03, 0x09}, 2);
        byte biterate = HDAT[0];

        UI.println("{}");
        for (byte b : a) {
            UI.println(String.valueOf(b));
        }

        UI.println("{}");
        for (byte b : HDAT) {
            UI.println(String.valueOf(b));
        }

        UI.println("{}");

        byte layer = (byte) (HDAT[1] >> 1 & 3);
        byte ID = (byte) (HDAT[1] >> 3 & 3);

        UI.println(String.valueOf(layer));
        UI.println(String.valueOf(ID));


        UI.println("{}");
        //byte biterate = (byte) Read(new byte[]{0x03, 0x08}, 3)[0];
        biterate = (byte) (((biterate >> 4) & 15) >> 1);
        UI.println(String.valueOf(biterate));

    }

    @Override
    public void run() {
        UI.println("Stream thread running");
        while (run) {
            tick();
        }
        UI.println("Stream thread stopped");
    }

    /**
     * Check if the file stream is available. If it isn't available, it will try to create a
     * new file stream from the source. If it still not available, it return false, else return true.
     *
     * @return if the file stream is available
     */
    private boolean fileStreamAvailable() {
        //check if file stream is available
        if (fileStream == null) {
            if (source.isEmpty()) {
                return false;
            } else {
                //try to make file stream
                try {
                    closeFileStream();

                    UI.println(source);

                    File f = new File(source);
                    if (f.exists()) {
                        fileStream = new FileInputStream(source);
                        tick = 0;
                        UI.println("New file");
                        return true;
                    } else {
                        UI.println("File not found");
                        return false;
                    }
                } catch (IOException ex) {
                    return false;
                }
            }
        }

        if (getEndOfFile()) {
            UI.println("End file");
        }

        //return if end of file
        return !getEndOfFile();
    }

    /**
     * The actions and check performed by every tick
     */
    private void tick() {
        if (play && gpio.getPin(GPIO.Pin.PB19)) {

            //UI.println(String.valueOf((tick + 1) / (bitsSended + 1)));

            if (fileStreamAvailable() && bitsSended / ((tick++ + 1.0) / 10) <= bufferSize) {
                checkBitRate();
                try {
                    byte[] buffer = new byte[bufferSize];
                    //TODO: remove t
                    int t = fileStream.read(buffer);
                    Write(buffer, false);

                    bitsSended += bufferSize;

                } catch (IOException ex) {
                    UI.error("Can not read from file", 3);
                }
            }
        }
    }
}