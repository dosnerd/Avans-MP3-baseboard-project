package MP3player.IO;

import java.io.*;

/**
 * Created by Acer on 2-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class VS1033 implements Runnable {
    private final byte[] _INIT = {0x02, 0x00, 0x08, (byte) 0x06}; //MODE46, 68
    private final GPIO gpio;
    private int bufferSize = 32;
    private byte[] buffer = new byte[bufferSize];
    private RandomAccessFile SCI;
    private RandomAccessFile SDI;
    private InputStream fileStream;
    private boolean run = true;
    private boolean play = false;
    private String source = "";
    private boolean valid = true;
    private long blinkPlay;

    public VS1033(GPIO gpio) {
        this.gpio = gpio;

        init();
    }

    public boolean isPlaying() {
        return play;
    }

    public boolean ValidFile() {
        return valid;
    }

    public void changeBuffer(int size) {
        this.bufferSize = size;
        buffer = new byte[bufferSize];
    }

    /**
     * Initialize the VS1033. This is done by a software reset and setting the volume. This will also open de SCI
     * and SDI connection.
     */
    private void init() {
        //initialize and volume data
        //byte[] clockf = {0x02, 0x03, (byte) 0x90, (byte) 0x00};//CLOCKF
        byte[] clockf = {0x02, 0x03, (byte) 0x84, (byte) 0xE2};//CLOCKF
        byte[] vol = {0x02, 0x0B, 0x1F, 0x1F};  //VOL
        byte[] sampleRate = {0x02, 0x05, (byte) 0xAC, (byte) 0x45};//audata
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
            Write(_INIT, true);

            UI.println("Send clock data");
            Write(clockf, true);

            UI.println("Send audata data");
            Write(sampleRate, true);

            UI.println("Send volume data");
            Write(vol, true);

            UI.println("VS1033 initialized");
        } catch (IOException ignored) {
            MP3player.IO.UI.println("IO exception. Probably can't open /dev/spidev1.*");
        }
    }

    /**
     * This wil de-initialize the VS1033. This means that it will stop playing, remove the filepath,
     * close the file stream, close, the SCI connection and close the SDI connection.
     */
    public void deinit() {
        UI.println("Closing vs1033");

        //stop thread
        run = false;
        play = false;

        //wait until thread is stopped
        while (!run) sleep(5);

        //close stream
        source = "";
        closeFileStream();

        //close SCI
        try {
            if (SCI != null) {
                SCI.close();
                SCI = null;
            }
        } catch (IOException ex) {
            UI.error("Can not close SCI", 6);
        }

        //close SDI
        try {
            if (SDI != null) {
                SDI.close();
                SDI = null;
            }
        } catch (IOException ex) {
            UI.error("Can not close SCI", 6);
        }
    }

    /**
     * Resume the track
     */
    public void Play() {
        play = true;
        UI.println("play");
        gpio.setPin(GPIO.Pin.PA25, false);

        gpio.cancelBlick(GPIO.Pin.PA9);
        gpio.setPin(GPIO.Pin.PA9, true);
    }

    /**
     * Start playing the given track. The filename is without the path!
     *
     * @param filename The name of the track without path
     */
    public void Play(String filename) {
        UI.println("Request " + filename);

        //stop playing, preventing sending more data of the current file to VS1033
        play = false;

        //software reset
        Write(_INIT, true);

        //set path of file and close old file
        source = filename;
        closeFileStream();

        try {
            //check if file exists
            File f = new File(source);
            if (f.exists()) {
                //open file
                fileStream = new FileInputStream(source);
                valid = true;
                //tick = 0;
                UI.println("New file");
            } else {
                UI.println("File not found");
            }
        } catch (IOException ex) {
            UI.error("Can not open file", 12);
        }

        Play();
    }

    /**
     * Try to safely close the file stream
     */
    private synchronized void closeFileStream() {
        try {
            if (fileStream != null) {
                UI.println("Close file");
                fileStream.close();
                fileStream = null;
            }
        } catch (IOException ex) {
            UI.error("Can not close filestream", 6);
        }
    }

    /**
     * Stop sending data to the VS1033. This will cause the VS1033 to give sound. The location
     * in the file will be remembered. When
     */
    public void Pauze() {
        UI.println("Pauze");
        play = false;
    }

    /**
     * Stop the track. This will first pauze the track, then it will scroll to the start of the track.
     */
    public void Stop() {
        Pauze();
        UI.println("Stop");
        gpio.setPin(GPIO.Pin.PA25, true);
        try {
            sleep(100);
            closeFileStream();
            for (int i = 0; i < 50; i++) {
                Write(new byte[]{0, 0, 0, 0}, false);
            }
            fileStream = new FileInputStream(source);
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
    public void Write(byte[] data, boolean isOperation) {
        //UI.println("Prepaire for sending over SCI/SDI...");

        //check if allowed to send data/command
        //noinspection StatementWithEmptyBody
        while (!gpio.getPin(GPIO.Pin.PB19)) ;

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

    @Override
    public void run() {
        UI.println("Stream thread running");
        while (run) {
            tick();
        }

        //confirm thread is stopped
        run = true;
        UI.println("Stream thread stopped");
    }

    /**
     * The actions and check performed by every tick
     */
    private void tick() {
        if (valid) {
            InputStream tempRdr = fileStream;
            if (play) {//&& gpio.getPin(GPIO.Pin.PB19)) {
                if (tempRdr != null) {
                    try {
                        if (tempRdr.read(buffer) > -1) {
                            valid = true;
                            Write(buffer, false);
                        } else {
                            valid = false;
                        }
                    } catch (IOException ex) {
                        if (!ex.getMessage().equals("Stream Closed")) {
                            UI.error("Can not read from file", 3);
                            valid = false;
                        }
                    }
                } else {
                    valid = false;
                }
            } else {
                if (blinkPlay <= System.currentTimeMillis()) {
                    gpio.blick(GPIO.Pin.PA9, 250);
                    blinkPlay = System.currentTimeMillis() + 500;
                }
                sleep(10);
            }
        }
    }

    private void sleep(int miliSecondes) {
        try {
            Thread.sleep(miliSecondes);
        } catch (InterruptedException ex) {
            UI.error("Can not sleep", 4);
        }
    }
}