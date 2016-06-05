package MP3player;

import MP3player.Errors.IllegalPinModeException;
import MP3player.IO.*;
import MP3player.Sources.FileSearch;

/**
 * Created by Acer on 23-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class MP3 /*implements Runnable*/ {
    private final VS1033 vs1033;
    private final GPIO gpio;
    private final MUX multyplexer;
    private final Dislay display;
    private final RotaryDial rotaryDial;
    private final FileSearch files;
    private boolean run = true;
    private short volume = 0x2F;
    private int song;
    private MP3player.Menu.Menu menuActive;
    private long dialPushTime;
    private boolean disco;
    private long hideLines;
    private int timeToHide = 10000;

    public MP3(GPIO gpio) {
        UI.println("Initialize MP3player.MP3...");
        UI.println("Loading files...");
        files = new FileSearch();

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

        UI.println("MP3player.MP3 initialized");
        UI.println("Starting threads");

        Thread dataThread = new Thread(vs1033);
        dataThread.setName("Data thread");
        dataThread.start();

        Thread displayThread = new Thread(display);
        displayThread.setName("Display thread");
        displayThread.start();

        Thread volumeThread = new Thread(rotaryDial);
        volumeThread.setName("Rotary thread");
        volumeThread.start();

        UI.println("MP3player.MP3 created");
    }

    public int getTimeToHide() {
        return timeToHide;
    }

    public void setTimeToHide(int timeToHide) {
        if (timeToHide >= 5000) {
            this.timeToHide = timeToHide;
        }
    }

    public boolean isDisco() {
        return disco;
    }

    public void setDisco(boolean disco) {
        this.disco = disco;

        if (!disco) {
            gpio.setPin(GPIO.Pin.PA9, vs1033.isPlaying());
            gpio.setPin(GPIO.Pin.PA25, false);
            gpio.setPin(GPIO.Pin.PA10, false);
            gpio.setPin(GPIO.Pin.PA7, false);
        }
    }

    public Dislay getDisplay() {
        return display;
    }

    protected VS1033 getVs1033() {
        return vs1033;
    }

    private void setHideLines() {
        gpio.setPin(GPIO.Pin.PA9, vs1033.isPlaying());
        gpio.setPin(GPIO.Pin.PA10, false);
        gpio.setPin(GPIO.Pin.PA7, false);
        gpio.setPin(GPIO.Pin.PA6, true);
        hideLines = System.currentTimeMillis() + timeToHide;
    }

    public void Stop() {
        run = false;
        display.Stop();
        gpio.setPin(GPIO.Pin.PA6, false);
        vs1033.Stop();
        vs1033.deinit();
        rotaryDial.Stop();
        display.ClearScreen();
        UI.println("Stopping MP3player.MP3");
    }

    public void Run() {
        setHideLines();

        if (files.getPlayList().size() > 0) {
            song = 0;
            vs1033.Play(files.getSong(0).getPath());
            showSong();
        }
        //sleep(1000 * 60 * 5);
        while (run) {
            multyplexer.check();
            Thread.yield();

            checkGPIO();
            Thread.yield();

            checkVolume();
            Thread.yield();

            if (disco) {
                gpio.setPin(GPIO.Pin.PA25, Math.random() > .5);
                gpio.setPin(GPIO.Pin.PA10, Math.random() > .5);
                Thread.yield();

                gpio.setPin(GPIO.Pin.PA9, Math.random() > .5);
                gpio.setPin(GPIO.Pin.PA7, Math.random() > .5);
                Thread.yield();
            }

            gpio.checkBlick();
            Thread.yield();

            if (hideLines < System.currentTimeMillis()) {
                display.WriteNewLine("                ", false);

                if (menuActive != null) {
                    menuActive = null;
                    showSong();
                } else {
                    gpio.setPin(GPIO.Pin.PA10, false);
                    gpio.setPin(GPIO.Pin.PA9, false);
                    gpio.setPin(GPIO.Pin.PA7, false);
                    gpio.setPin(GPIO.Pin.PA6, false);
                    hideLines = Long.MAX_VALUE;
                }


                Thread.yield();
            }

            checkFile();
            sleep(10);
        }
        close();
    }

    private void checkFile() {
        if (!vs1033.ValidFile()) {
            nextSong();
        }
    }

    protected void prevSong() {
        int index = files.getIndex(song);

        if (--index < 0) {
            index = files.getPlayList().size() - 1;
        }

        song = files.getSongIndex(index);

        showSong();

        vs1033.Play(files.getSong(song).getPath());
        if (hideLines == Long.MAX_VALUE) {
            gpio.setPin(GPIO.Pin.PA9, false);
        }
        gpio.blick(GPIO.Pin.PA10, 500);
    }

    protected void nextSong() {
        int index = files.getIndex(song);

        if (++index >= files.getPlayList().size()) {
            index = 0;
        }
        song = files.getSongIndex(index);

        showSong();

        vs1033.Play(files.getSong(song).getPath());
        if (hideLines == Long.MAX_VALUE) {
            gpio.setPin(GPIO.Pin.PA9, false);
        }
        gpio.blick(GPIO.Pin.PA7, 500);
    }

    private void showSong() {
        if (menuActive == null) {
            String text = files.getSong(song).getTitle();
            if (files.getSong(song).hasTag()) {
                text += "<" + files.getSong(song).getArtist() + ">";
            }
            display.WriteNewLine(text, true);
        }
    }

    private void increaseVolume(int amount) {

        if (volume > 0x00) {
            volume -= amount;
            if (volume < 0x00) {
                volume = 0x00;
            }

            vs1033.Write(new byte[]{0x02, 0x0B, (byte) volume, (byte) volume}, true);
            UI.println("Volume(" + volume + ")");
        }
    }

    protected void increaseVolume() {
        UI.println("increase volume");
        if (--volume >= 0x00) {
            vs1033.Write(new byte[]{0x02, 0x0B, (byte) volume, (byte) volume}, true);
        } else {
            volume++;
        }
        UI.println("Volume(" + volume + ")");
    }

    private void decreaseVolume(int amount) {

        if (volume < 0xFE) {
            volume += amount;
            if (volume > 0xFE) {
                volume = 0xFE;
            }

            vs1033.Write(new byte[]{0x02, 0x0B, (byte) volume, (byte) volume}, true);
            UI.println("Volume(" + volume + ")");
        }
    }

    protected void decreaseVolume() {
        UI.println("decrease volume");
        if (++volume < 0xFE) {
            vs1033.Write(new byte[]{0x02, 0x0B, (byte) volume, (byte) volume}, true);
        } else {
            volume--;
        }
        UI.println("Volume(" + volume + ")");
    }

    private void checkGPIO() {
        if (multyplexer.pressed(2)) {
            setHideLines();
            if (menuActive == null) {
                UI.println("Stop");
                vs1033.Stop();
            } else {
                menuActive = null;
                showSong();
                display.WriteNewLine("Vol: " + (int) (100 * (1 - volume / 254.0)) + "%", false);
            }
        } else if (multyplexer.pressed(1)) {
            UI.println("pauze/play");
            setHideLines();
            if (vs1033.isPlaying()) {
                vs1033.Pauze();
            } else {
                vs1033.Play();
            }
        } else if (multyplexer.pressed(3)) {
            UI.println("next");
            setHideLines();
            nextSong();
        } else if (multyplexer.pressed(0)) {
            UI.println("prev");
            setHideLines();
            prevSong();
        } else if (gpio.getPin(GPIO.Pin.PB21) && dialPushTime != -1 && System.currentTimeMillis() - dialPushTime > 50) {
            setHideLines();
            showMenu();
            dialPushTime = -1;
        } else if (multyplexer.timePin(2) > 2000) {
            Stop();
        }

        if (!gpio.getPin(GPIO.Pin.PB21)) {
            dialPushTime = System.currentTimeMillis();
        }
    }

    private void checkVolume() {
        RotaryDial.Direction direction = rotaryDial.getDirection();
        if (direction == RotaryDial.Direction.RIGHT) {
            setHideLines();
            if (menuActive == null) {
                increaseVolume(6);
            } else {
                menuActive.up();
                return;
            }

        } else if (direction == RotaryDial.Direction.LEFT) {
            setHideLines();
            if (menuActive == null) {
                decreaseVolume(6);
            } else {
                menuActive.down();
                return;
            }

        } else {
            return;
        }

        display.WriteNewLine("Vol: " + (int) (100 * (1 - volume / 254.0)) + "%", false);
    }

    private void close() {
        gpio.deinit();
        vs1033.deinit();
    }

    private void sleep(int mili) {
        try {
            Thread.sleep(mili);
        } catch (InterruptedException ex) {
            UI.error("Can not sleep", 4);
        }
    }

    private void showMenu() {
        if (menuActive == null) {
            UI.println("Open main menu");
            display.ClearScreen();
            sleep(1);

            menuActive = new MP3player.Menu.MainMenu(this, files);
            display.WriteNewLine(menuActive.getName(), true);
            display.WriteNewLine(menuActive.getStandardValue(), false);
        } else {
            menuActive = menuActive.select();
            if (menuActive == null) {
                showSong();
                display.WriteNewLine("Vol: " + (int) (100 * (1 - volume / 254.0)) + "%", false);
            }
        }
    }
}
