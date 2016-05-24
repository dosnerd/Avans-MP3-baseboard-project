import IO.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acer on 23-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class MP3 {
    private GPIO gpio;
    private MUX multyplexer;
    private Dislay display;
    private RotaryDial rotaryDial;
    private VS1033 vs1033;
    private boolean run = true;
    private byte volume = 0x2F;
    private List<String> playList;
    private int song;

    public MP3() {
        UI.println("Initialize MP3...");
        playList = new ArrayList<String>();
        //TODO: add path to music folder
        findFiles(new File("/"));

        gpio = new GPIO();
        multyplexer = new MUX(gpio);
        ShiftRegister dataPins = new ShiftRegister(gpio, GPIO.Pin.PB17, GPIO.Pin.PB16, GPIO.Pin.PA28);
        display = new Dislay(gpio, dataPins, GPIO.Pin.PA22, GPIO.Pin.PA11);
        rotaryDial = new RotaryDial(gpio, GPIO.Pin.PB30, GPIO.Pin.PB20);
        vs1033 = new VS1033(gpio);

        UI.println("MP3 initialized");
        UI.println("Starting data thread");

        Thread dataThread = new Thread(vs1033);
        dataThread.start();

        UI.println("MP3 created");
    }

    void Stop() {
        run = false;
        UI.println("Stopping MP3");
    }

    void Run() {
        while (run) {
            checkGPIO();
            gpio.checkBlick();
        }
        close();
    }

    private void checkGPIO() {
        if (multyplexer.getPin(2)) {
            vs1033.Stop();
        } else if (multyplexer.getPin(1)) {
            if (vs1033.isPlaying()) {
                vs1033.Pauze();
            } else {
                vs1033.Play();
            }
        } else if (multyplexer.getPin(3)) {
            if (++song >= playList.size()) {
                song = 0;
            }

            vs1033.Play(playList.get(song));
        } else if (multyplexer.getPin(0)) {
            if (--song < 0) {
                song = playList.size() - 1;
            }

            vs1033.Play(playList.get(song));
        } else {
            RotaryDial.Direction direction = rotaryDial.getDirection();
            if (direction == RotaryDial.Direction.LEFT) {
                if (--volume >= 0x00) {
                    vs1033.Write(new byte[]{0x02, 0x0B, volume, volume}, true);
                } else {
                    volume++;
                }
            } else if (direction == RotaryDial.Direction.RIGHT) {
                if (++volume < 0xFE) {
                    vs1033.Write(new byte[]{0x02, 0x0B, volume, volume}, true);
                } else {
                    volume--;
                }
            }
        }
    }

    private void close() {
        gpio.deinit();
        vs1033.deinit();
    }

    private void findFiles(File entry) {
        if (entry.isDirectory()) {
            try {
                //noinspection ConstantConditions
                for (File child : entry.listFiles()) {
                    findFiles(child);
                }
            } catch (NullPointerException ex) {
                UI.error("No list of file found", 11);
            }
        } else {
            playList.add(entry.getAbsolutePath());
        }
    }
}
