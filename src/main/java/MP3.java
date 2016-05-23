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
    private boolean run = true;

    public MP3() {
        UI.println("Initialize MP3...");
        gpio = new GPIO();
        buttons = new MUX(gpio);
        ShiftRegister dataPins = new ShiftRegister(gpio, GPIO.Pin.PB17, GPIO.Pin.PB16, GPIO.Pin.PA28);
        display = new Dislay(gpio, dataPins, GPIO.Pin.PA22, GPIO.Pin.PA11);
        rotaryDial = new RotaryDial(gpio, GPIO.Pin.PB30, GPIO.Pin.PB20);
        UI.println("MP3 initialized");
    }

    void Stop() {
        run = false;
        UI.println("Stopping MP3");
    }

    void Run() {

    }

    private void checkGPIO() {

    }
}
