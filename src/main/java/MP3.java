import IO.*;

/**
 * Created by Acer on 23-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class MP3 {
    private GPIO gpio;
    private MUX buttons;
    private Dislay display;
    private RotaryDial rotaryDial;
    private VS1033 vs1033;
    private boolean run = true;

    public MP3() {
        UI.println("Initialize MP3...");
        gpio = new GPIO();
        buttons = new MUX(gpio);
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

        }
        close();
    }

    private void checkGPIO() {

    }

    private void close() {
        gpio.deinit();
        vs1033.deinit();
    }
}
