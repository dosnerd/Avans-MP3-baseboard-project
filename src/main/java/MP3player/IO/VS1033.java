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
    private final int bufferSize = 32;
    private final byte[] buffer = new byte[bufferSize];
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

    /**
     * Get if the VS1033 is allowed to play
     *
     * @return true is allowed, false if disallowed
     */
    public boolean isPlaying() {
        return play;
    }

    /**
     * Get if the file is valid. This is false if it is at the end of the file,
     * file can not be read or if there were an exception by loaded/streaming
     *
     * @return true is file is valid, false if file is invalid
     */
    public boolean ValidFile() {
        return valid;
    }

    /**
     * Initialize the VS1033. This is done by a software reset and setting the volume. This will also open de SCI
     * and SDI connection.
     */
    private void init() {
        //initialize and volume data

        //fast clock (to play song at normal speed, can glitch the song)
        //byte[] clockf = {0x02, 0x03, (byte) 0x90, (byte) 0x00};

        //slow clock (to play song little slower, less glitch in the song)
        byte[] clockf = {0x02, 0x03, (byte) 0x84, (byte) 0xE2};
        byte[] vol = {0x02, 0x0B, 0x1F, 0x1F};  //VOL
        byte[] sampleRate = {0x02, 0x05, (byte) 0xAC, (byte) 0x45};//audata

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
     * Allow the VS1033 to play again.
     */
    public void Play() {
        //allow playing
        play = true;
        UI.println("play");

        //set LEDS at right status
        gpio.setPin(GPIO.Pin.PA25, false);

        gpio.cancelBlink(GPIO.Pin.PA9);
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
        valid = false;

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

                UI.println("New file");
            } else {
                UI.println("File not found");
            }
        } catch (IOException ex) {
            UI.error("Can not open file", 12);
        }

        //start playing song
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
            UI.error("Can not close file stream", 6);
        }
    }

    /**
     * Stop sending data to the VS1033. This will cause the VS1033 to give sound. The location
     * in the file will be remembered. When
     */
    public void Pause() {
        UI.println("Pause");
        play = false;
    }

    /**
     * Stop the track. This will first pause the track, then it will scroll to the start of the track.
     */
    public void Stop() {
        //pause the song and update UI
        Pause();
        UI.println("Stop");
        gpio.setPin(GPIO.Pin.PA25, true);

        try {
            //close stream
            sleep(100);
            closeFileStream();

            //write few 0 to give a smoother start (without a part of the current location)
            for (int i = 0; i < 50; i++) {
                Write(new byte[]{0, 0, 0, 0}, false);
            }

            //reopen file
            fileStream = new FileInputStream(source);
        } catch (IOException ex) {
            UI.error("Can not reset file", 10);
        }
    }

    /**
     * Send data to the VS1033. It will check if it is allowed to send data (by DREQ pin). When not allowed,
     * it will wait until it is allowed. When it is allowed to send data, it send data. If it is an
     * operation, it will send offer SCI, else over SDI.
     * <p>
     * This function need to be synchronized the problems when data been send and a command (such volume) by
     * a different thread. This may not at the same time.
     *
     * @param data        The data or the command(s) to send at once.
     * @param isOperation If true, the data will be send over SCI. If false, the data will be send over SDI.
     */
    public void Write(byte[] data, boolean isOperation) {
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
     * Send data and check performed by every tick
     */
    private void tick() {
        //check if it is allowed to send data
        if (valid) {
            //copy stream to separate variable, if it will remove by a other thread, it is still usable
            InputStream tempRdr = fileStream;
            if (play) {
                if (tempRdr != null) {
                    //try sending data
                    try {
                        //check if and of file
                        if (tempRdr.read(buffer) > -1) {
                            //send data
                            valid = true;
                            Write(buffer, false);
                        } else {
                            //end of file, file isn't valid anymore
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
                //song is paused/stopped, check if the led needs to blink again.
                if (blinkPlay <= System.currentTimeMillis()) {
                    //blink led for 500ms (250 on, 250 off)
                    gpio.blink(GPIO.Pin.PA9, 250);
                    blinkPlay = System.currentTimeMillis() + 500;
                }
                sleep(10);
            }
        }
    }

    /**
     * sleep thread for give time
     *
     * @param milliSeconds time in milliseconds
     */
    private void sleep(int milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException ex) {
            UI.error("Can not sleep", 4);
        }
    }
}