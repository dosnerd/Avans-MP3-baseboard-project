package IO;

/**
 * Created by Acer on 23-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class MUX {
    private GPIO gpio;
    private GPIO.Pin select1;
    private GPIO.Pin select2;
    private GPIO.Pin select3;
    private GPIO.Pin read;

    public MUX(GPIO gpio) {
        this.gpio = gpio;
    }

    public boolean getPin(int pin) {
        gpio.setPin(select1, (pin & 1) > 0);
        gpio.setPin(select2, (pin & ~(1 << 1)) > 0);
        gpio.setPin(select3, (pin & ~(1 << 2)) > 0);

        return gpio.getPin(read);
    }
}
