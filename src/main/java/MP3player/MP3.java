package MP3player;

import MP3player.Errors.IllegalPinModeException;
import MP3player.IO.*;
import MP3player.Menu.MainMenu;
import MP3player.Sources.FileSearch;
import MP3player.Sources.Save;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by Acer on 23-5-2016.
 * <p/>
 * This class is the 'master control'. This is the most upper layer of the control. This call the right function
 * if a button is pressed or if the rotary dial is turned. It also goes to the next song if it is
 * at the end of the file or the file is corrupt. The LED are also controled here.
 *
 * @author David de Prez
 * @version 1.0
 */
public class MP3 {
    private final VS1033 vs1033;
    private final GPIO gpio;
    private final MUX multyplexer;
    private final Display display;
    private final RotaryDial rotaryDial;
    private final FileSearch files;
    private boolean run = true;
    private short volume = 9;
    private int song;
    private MP3player.Menu.Menu menuActive;
    private long dialPushTime;
    private boolean disco;
    private long hideLines;
    private int timeToHide = 10000;

    /**
     * Constructor of MP3. Here all the components are initialized, search for music files, starts threads
     * and load settings.
     *
     * @param gpio GPIO for initializing components and
     */
    public MP3(GPIO gpio) {
        UI.println("Initialize MP3player.MP3...");
        UI.println("Loading files...");
        files = new FileSearch();

        //create components
        this.gpio = gpio;
        ShiftRegister dataPins = new ShiftRegister(gpio, GPIO.Pin.PB17, GPIO.Pin.PB16, GPIO.Pin.PA28);
        display = new Display(gpio, dataPins, GPIO.Pin.PA22, GPIO.Pin.PA11);
        multyplexer = new MUX(gpio, GPIO.Pin.PA27, GPIO.Pin.PA26, null, GPIO.Pin.PB31);
        rotaryDial = new RotaryDial(gpio, GPIO.Pin.PB20, GPIO.Pin.PB30);
        vs1033 = new VS1033(gpio);

        //reset output pin (at startup the can be random values)
        for (GPIO.Pin pin : GPIO.Pin.values()) {
            try {
                gpio.setPin(pin, false);
            } catch (IllegalPinModeException ignored) {

            }
        }

        //enable display and show volyme
        UI.set_display(display);
        gpio.setPin(GPIO.Pin.PA6, true);
        display.WriteNewLine("Vol: " + (15 - volume), false);


        //starting all threads
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

        //load settings
        load();
    }

    public void setSong(int song) {
        this.song = song;
    }

    /**
     * Loads the settings like volume, scroll speed, standby time, disco mode and filter
     */
    private void load() {
        try {
            UI.println("Loading settings");
            InputStream stream = MP3.class.getResourceAsStream("save");
            if (stream == null) {
                UI.error("Stream is null", 15);
                return;
            }

            ObjectInputStream rdr = new ObjectInputStream(stream);
            Object saveFile = rdr.readObject();
            if (saveFile instanceof Save) {
                Save save = (Save) saveFile;
                volume = save.getVolume();
                timeToHide = save.getTimeToHide();
                display.setShiftTime(save.getScrollSpeed());
                files.setFilter(save.getFilter());
                disco = save.isDisco();

                setVolume();
                UI.println("Settings loaded");
            }
        } catch (IOException ex) {
            UI.error("Can not load settings", 15);
        } catch (ClassNotFoundException ex) {
            UI.error("Save file is in wrong format", 15);
        }

    }

    /**
     * Save the settings like volume, scroll speed, standby time, disco mode and filter
     */
    private void save() {
        try {
            UI.println("Saving settings");
            URL url = MP3.class.getResource("save");

            if (url == null) {
                UI.error("url is null", 16);
                return;
            }

            File saveFile = new File(url.toURI());
            ObjectOutputStream wrt = new ObjectOutputStream(new FileOutputStream(saveFile));

            Save save = new Save();
            save.setTimeToHide(timeToHide);
            save.setDisco(disco);
            save.setFilter(files.getFilter());
            save.setScrollSpeed(display.getShiftTime());
            save.setVolume(volume);
            wrt.writeObject(save);
            UI.println("Settings saved");
        } catch (URISyntaxException ex) {
            UI.error("Can not convert URL", 16);
        } catch (FileNotFoundException ex) {
            UI.error("Can not find file", 16);
        } catch (IOException ex) {
            ex.printStackTrace();
            UI.error("Can not save settings", 16);
        }
    }

    /**
     * Get the time it takes before it goes to standby mode.
     *
     * @return time to standby mode in ms
     */
    public int getTimeToHide() {
        return timeToHide;
    }

    /**
     * Set the time it takes before it goes to standby mode.
     *
     * @param timeToHide time to standby mode in ms
     */
    public void setTimeToHide(int timeToHide) {
        if (timeToHide >= 5000) {
            this.timeToHide = timeToHide;
        }
    }

    /**
     * Get if the MP3 is in disco mode
     *
     * @return true when in disco mode
     */
    public boolean isDisco() {
        return disco;
    }

    /**
     * Set the MP3 disco mode
     *
     * @param disco true to enable disco mode
     */
    public void setDisco(boolean disco) {
        this.disco = disco;

        if (!disco) {
            gpio.setPin(GPIO.Pin.PA9, vs1033.isPlaying());
            gpio.setPin(GPIO.Pin.PA25, false);
            gpio.setPin(GPIO.Pin.PA10, false);
            gpio.setPin(GPIO.Pin.PA7, false);
        }
    }

    /**
     * Get the display that is initialized and in use
     *
     * @return initialized display
     */
    public Display getDisplay() {
        return display;
    }

    /**
     * Get the VS1033 that is initialized and in use
     *
     * @return initialized VS1033
     */
    public VS1033 getVs1033() {
        return vs1033;
    }

    /**
     * Put the MP3 out standby mode. this will recover the status of the LEDS and set the timer
     */
    private void setHideLines() {
        //recover status of leds
        gpio.setPin(GPIO.Pin.PA9, vs1033.isPlaying());
        gpio.setPin(GPIO.Pin.PA10, false);
        gpio.setPin(GPIO.Pin.PA7, false);
        gpio.setPin(GPIO.Pin.PA6, true);

        //set timer
        hideLines = System.currentTimeMillis() + timeToHide;
    }

    /**
     * Stop the MP3 en close the program after deinitialize all components.
     */
    public void Stop() {
        display.WriteNewLine("Shutdown...", true);
        UI.println("de-initialized MP3 player...");

        //stop thread
        run = false;

        //save settings
        save();

        //stop other threads
        display.Stop();
        vs1033.Stop();
        rotaryDial.Stop();
        vs1033.deinit();

        //reset UI
        display.ClearScreen();
        gpio.setPin(GPIO.Pin.PA6, false);

        //diinit GPIO
        gpio.deinit();


        UI.println("MP3player de-initialized");
    }

    /**
     * Start the MP3 and run it until the stop button is hold down to close the progam
     */
    public void Run() {
        //set timer for standby mode
        setHideLines();

        //check if there is are songs available
        if (files.getPlayList().size() > 0) {
            //start random song
            song = (int) (Math.random() * files.getPlayList().size());
            vs1033.Play(files.getSong(song).getPath());
            showSong();

            //control the MP3
            while (run) {
                //update input multiplexer
                multyplexer.check();
                Thread.yield();

                //update GPIO input/output
                checkGPIO();
                Thread.yield();

                if (!run)
                    break;

                //update volume
                checkVolume();
                Thread.yield();

                if (disco) {
                    //set random values to the first 2 LEDs
                    gpio.setPin(GPIO.Pin.PA25, Math.random() > .5);
                    gpio.setPin(GPIO.Pin.PA10, Math.random() > .5);
                    Thread.yield();

                    //set random values to the other LEDs
                    gpio.setPin(GPIO.Pin.PA9, Math.random() > .5);
                    gpio.setPin(GPIO.Pin.PA7, Math.random() > .5);
                    Thread.yield();
                }

                //check if LEDS in blick mode must be off
                gpio.checkBlick();
                Thread.yield();

                //check if MP3 need to go in standby mode
                if (hideLines < System.currentTimeMillis()) {
                    //clear volume line
                    display.WriteNewLine("                ", false);

                    //check if menu is opened
                    if (menuActive != null) {
                        //close menu and set timer for standby mode again
                        menuActive = null;
                        showSong();
                        setHideLines();
                    } else {
                        //set all leds off
                        gpio.setPin(GPIO.Pin.PA10, false);
                        gpio.setPin(GPIO.Pin.PA9, false);
                        gpio.setPin(GPIO.Pin.PA7, false);
                        gpio.setPin(GPIO.Pin.PA6, false);
                        hideLines = Long.MAX_VALUE;
                    }

                    //give other threads time to do jobs
                    Thread.yield();
                }

                //check if file is still valid
                checkFile();

                //allow other threads more time to do jobs
                sleep(10);
            }
        }

        //confirm closed
        run = true;
    }

    /**
     * check if the current file is valid. If is isn't valid (this include end of file), it will go
     * to the next song
     */
    private void checkFile() {
        if (!vs1033.ValidFile()) {
            nextSong();
        }
    }

    /**
     * Go to the previous song
     */
    protected void prevSong() {
        //get the index out of the list of filtered songs
        int index = files.getSongOrder(song);

        //go to previous index
        if (--index < 0) {
            index = files.getPlayList().size() - 1;
        }

        //get index of song out of list of ALL songs
        song = files.getSongUID(index);

        //update song to screen
        showSong();

        //play the new song
        vs1033.Play(files.getSong(song).getPath());

        //if standby mode, turn off the play led
        if (hideLines == Long.MAX_VALUE) {
            gpio.setPin(GPIO.Pin.PA9, false);
        }

        //set previous led 500ms on
        gpio.blick(GPIO.Pin.PA10, 500);
    }

    /**
     * Go to next song
     */
    protected void nextSong() {
        //get index of song out of the list of filtered songs
        int index = files.getSongOrder(song);

        //go to the next song
        if (++index >= files.getPlayList().size()) {
            index = 0;
        }

        //get index of song out of the list of ALL songs
        song = files.getSongUID(index);

        //update song to screen
        showSong();

        //play the song
        vs1033.Play(files.getSong(song).getPath());

        //turn off play led if in standby mode
        if (hideLines == Long.MAX_VALUE) {
            gpio.setPin(GPIO.Pin.PA9, false);
        }

        //set next led for 500ms on
        gpio.blick(GPIO.Pin.PA7, 500);
    }

    /**
     * Write the name of the song and artiest to the first line of the display. If there is no tag available,
     * it will write the file name.
     */
    public void showSong() {
        //check if menu is opened
        if (menuActive == null) {
            //get the title and artist, if no tag available, title wil give the file name
            String text = files.getSong(song).getTitle();
            if (files.getSong(song).hasTag()) {
                text += "<" + files.getSong(song).getArtist() + ">";
            }

            //write to the first line of the display
            display.WriteNewLine(text, true);
        }
    }

    /**
     * This will convert the volume to the volume for the VS1033 (0-FE) and send this
     * to the VS1033. It also write the volume to the display
     */
    private void setVolume() {
        //convert to the VS1033
        double volume = Math.pow(1.446, this.volume);

        //if volume is 1, set it to 0. Else do nothing
        volume = volume == 1 ? 0 : volume;

        //send volume to VS1033
        vs1033.Write(new byte[]{0x02, 0x0B, (byte) volume, (byte) volume}, true);

        //write volume to display
        UI.println("Actual volume: " + volume);
        display.WriteNewLine("Vol: " + (15 - this.volume), false);

        //wake up out of standby and/or set timer
        setHideLines();
    }

    /**
     * Increase the volume by 1, if possible
     */
    protected void increaseVolume() {
        //check if volume the is to the limit
        if (--volume >= 0x00) {
            setVolume();
        } else {
            //reset volume
            volume = 0;
        }

        UI.println("Volume(" + volume + ")");
    }

    /**
     * Decrease volume by 1, if possible
     */
    protected void decreaseVolume() {
        //check if the volume is to the limit
        if (++volume < 16) {
            setVolume();
        } else {
            //reset volume
            volume = 15;
        }
        UI.println("Volume(" + volume + ")");
    }

    /**
     * Check if there is a button pressed. If there is an button pressed,
     * run the right command.
     */
    private void checkGPIO() {
        //Check if there is an button pressed
        if (multyplexer.pressed(2)) {
            //Wake up out of standby mode / reset timer
            setHideLines();

            //check if menu is open
            if (menuActive == null) {
                //stop the song
                UI.println("Stop");
                vs1033.Stop();
            } else {
                //close the menu and rewrite song/volume info
                menuActive = null;
                showSong();
                display.WriteNewLine("Vol: " + (15 - volume), false);
            }
        } else if (multyplexer.pressed(1)) {
            //Wake up out of standby mode / reset timer
            setHideLines();

            //check if it need to paused or played
            if (vs1033.isPlaying()) {
                //pause song
                UI.println("pause");
                vs1033.Pauze();
            } else {
                //play song
                UI.println("play");
                vs1033.Play();
            }
        } else if (multyplexer.pressed(3)) {
            //Wake up out of standby mode / reset timer
            setHideLines();

            //check if menu is open
            if (menuActive == null) {
                //to to next song
                UI.println("next");
                nextSong();
            } else {
                //scroll up in menu
                menuActive.up();
            }
        } else if (multyplexer.pressed(0)) {
            //Wake up out of standby mode / reset timer
            setHideLines();

            //check if menu is open
            if (menuActive == null) {
                //to the previous song
                UI.println("prev");
                prevSong();
            } else {
                //scroll down in menu
                menuActive.down();
            }
        } else if (gpio.getPin(GPIO.Pin.PB21) && dialPushTime != -1 && System.currentTimeMillis() - dialPushTime > 50) {
            //Wake up out of standby mode / reset timer
            setHideLines();

            //show main menu or next menyu
            showMenu();

            //prevent spam of button push
            dialPushTime = -1;
        } else if (multyplexer.timePin(2) > 2000) {
            //stop the MP3
            Stop();
            return;
        } else if (multyplexer.timePin(3) > 2000) {
            MainMenu.set_secreteMode(true);
            display.WriteNewLine("Secrete mode enabled", false);
        }

        //set time of rotary dial when not pushed (for debouching, need to push more then 50 ms)
        if (!gpio.getPin(GPIO.Pin.PB21)) {
            dialPushTime = System.currentTimeMillis();
        }
    }

    /**
     * Check the direction of the rotary dial. If it has been turned, is change the volume or scroll through
     * the menu.
     */
    private void checkVolume() {
        //check what direction the rotary dial is turning
        RotaryDial.Direction direction = rotaryDial.getDirection();
        if (direction == RotaryDial.Direction.RIGHT) {
            //check if menu is open
            if (menuActive == null) {
                increaseVolume();
            } else {
                //Wake up out of standby mode / reset timer
                setHideLines();

                //scroll up in menu
                menuActive.up();
            }

        } else if (direction == RotaryDial.Direction.LEFT) {
            //check if menu is open
            if (menuActive == null) {
                decreaseVolume();
            } else {
                //Wake up out of standby mode / reset timer
                setHideLines();

                //scroll down in menu
                menuActive.down();
            }

        }
    }

    /**
     * Let the thread sleep. This method includes the try/catch block.
     *
     * @param mili amount of milliseconds
     */
    private void sleep(int mili) {
        try {
            Thread.sleep(mili);
        } catch (InterruptedException ex) {
            UI.error("Can not sleep", 4);
        }
    }

    /**
     * Open the main menu. If the main menu is already open, it will open the selected sub menu.
     */
    private void showMenu() {
        //check if the (sub) menu is open
        if (menuActive == null) {
            //reset screen
            UI.println("Open main menu");
            display.ClearScreen();
            sleep(1);

            //open menu
            menuActive = new MP3player.Menu.MainMenu(this, files);
            display.WriteNewLine(menuActive.getName(), true);
            display.WriteNewLine(menuActive.getStandardValue(), false);
        } else {
            //select sub menu
            menuActive = menuActive.select();

            //check menu is closed
            if (menuActive == null) {
                //put play info to display
                showSong();
                display.WriteNewLine("Vol: " + (15 - volume), false);
            }
        }
    }
}
