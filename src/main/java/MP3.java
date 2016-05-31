import Errors.IllegalPinModeException;
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
class MP3 {
    private final VS1033 vs1033;
    private final GPIO gpio;
    private final MUX multyplexer;
    private final Dislay display;
    private final RotaryDial rotaryDial;
    private final List<String> playList;
    private boolean run = true;
    private byte volume = 0x2F;
    private int song;

    public MP3(GPIO gpio) {
        UI.println("Initialize MP3...");
        playList = new ArrayList<String>();
        //TODO: add path to music folder
        UI.println("Loading files...");
        findFiles(new File("/media/data/root"));

        this.gpio = gpio;
        multyplexer = new MUX(gpio, GPIO.Pin.PA27, GPIO.Pin.PA26, null, GPIO.Pin.PB31);
        ShiftRegister dataPins = new ShiftRegister(gpio, GPIO.Pin.PB17, GPIO.Pin.PB16, GPIO.Pin.PA28);
        display = new Dislay(gpio, dataPins, GPIO.Pin.PA22, GPIO.Pin.PA11);
        rotaryDial = new RotaryDial(gpio, GPIO.Pin.PB20, GPIO.Pin.PB30);
        vs1033 = new VS1033(gpio);

        for (GPIO.Pin pin : GPIO.Pin.values()) {
            try {
                gpio.setPin(pin, false);
            } catch (IllegalPinModeException ignored) {

            }
        }

        UI.set_display(display);

        gpio.setPin(GPIO.Pin.PA6, true);
        display.WriteNewLine("Vol: " + (int) (100 * (1 - volume / 254.0)) + "%", false);

        //vs1033.Play(playList.get(0));
        //vs1033.run();

        UI.println("MP3 initialized");
        UI.println("Starting data thread");

        Thread dataThread = new Thread(vs1033);
        dataThread.setPriority(Thread.MAX_PRIORITY);
        dataThread.setName("Data thread");
        dataThread.start();


        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        UI.println("MP3 created");
    }

    VS1033 getVs1033() {
        return vs1033;
    }

    void Stop() {
        run = false;
        vs1033.Stop();
        gpio.setPin(GPIO.Pin.PA6, false);
        vs1033.deinit();
        UI.println("Stopping MP3");
    }

    void Run() {
        if (playList.size() > 0) {
            vs1033.Play(playList.get(0));
        }
        //sleep(1000 * 60 * 5);
        while (run) {
            multyplexer.check();
            Thread.yield();

            checkGPIO();
            Thread.yield();

            checkVolume();
            Thread.yield();

            gpio.checkBlick();
            Thread.yield();

            checkFile();
            Thread.yield();
            //sleep(10);
        }
        close();
    }

    private void checkFile() {
        if (!vs1033.ValidFile()) {
            nextSong();
        }
    }

    void prevSong() {
        if (--song < 0) {
            song = playList.size() - 1;
        }

        vs1033.Play(playList.get(song));
        gpio.blick(GPIO.Pin.PA10, 500);
    }

    void nextSong() {
        if (++song >= playList.size()) {
            song = 0;
        }

        vs1033.Play(playList.get(song));
        gpio.blick(GPIO.Pin.PA7, 500);
    }

    void increaseVolume() {
        UI.println("increase volume");
        if (--volume >= 0x00) {
            vs1033.Write(new byte[]{0x02, 0x0B, volume, volume}, true);
        } else {
            volume++;
        }
        UI.println("Volume(" + volume + ")");
    }

    void decreaseVolume() {
        UI.println("decrease volume");
        if (++volume < 0xFE) {
            vs1033.Write(new byte[]{0x02, 0x0B, volume, volume}, true);
        } else {
            volume--;
        }
        UI.println("Volume(" + volume + ")");
    }

    private void checkGPIO() {
        if (multyplexer.pressed(2)) {
            UI.println("Stop");
            vs1033.Stop();
        } else if (multyplexer.pressed(1)) {
            UI.println("pauze/play");
            if (vs1033.isPlaying()) {
                vs1033.Pauze();
            } else {
                vs1033.Play();
            }
        } else if (multyplexer.pressed(3)) {
            UI.println("next");
            nextSong();
        } else if (multyplexer.pressed(0)) {
            UI.println("prev");
            prevSong();
        } else if (multyplexer.timePin(2) > 2000) {
            Stop();
        }
    }

    private void checkVolume() {
        RotaryDial.Direction direction = rotaryDial.getDirection();
        if (direction == RotaryDial.Direction.RIGHT) {
            for (int i = 0; i < 3; i++) {
                increaseVolume();
            }

        } else if (direction == RotaryDial.Direction.LEFT) {
            for (int i = 0; i < 3; i++) {
                decreaseVolume();
            }
        } else {
            return;
        }

        display.setDisplay(true);
        display.WriteNewLine("Vol: " + (int) (100 * (1 - volume / 254.0)) + "%", false);
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
        } else if (entry.getName().endsWith(".mp3")) {
            playList.add(entry.getAbsolutePath());
            UI.println(entry.getName() + " Added to playlist");
        }
    }

    private void sleep(int mili) {
        try {
            Thread.sleep(mili);
        } catch (InterruptedException ex) {
            UI.error("Can not sleep", 4);
        }
    }
}
