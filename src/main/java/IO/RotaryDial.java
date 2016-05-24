package IO;

/**
 * Created by Acer on 20-5-2016.
 * This class managed the rotary dial turning. It make it possible to get
 * the direction that the rotary dial is turning to.
 *
 * @author David de Prez
 * @version 1.0
 */
public class RotaryDial {
    private GPIO gpio;
    private GPIO.Pin pinA;
    private GPIO.Pin pinB;
    private boolean previousValueA;

    /**
     * Constructor
     *
     * @param gpio GPIO class to read the necessary pins
     * @param pinA One of the pins of the rotary dial
     * @param pinB The other pin of the rotary dial
     */
    public RotaryDial(GPIO gpio, GPIO.Pin pinA, GPIO.Pin pinB) {
        this.gpio = gpio;
        this.pinA = pinA;
        this.pinB = pinB;

        //Set the previous value to the current value, so it won't detect a edge
        //on startup
        previousValueA = gpio.getPin(pinA);
    }

    /**
     * Get the direction that the rotary dial is turning to. This code is partially from
     * http://playground.arduino.cc/Main/RotaryEncoders#Waveform.
     *
     * @return the direction. NO_WAY is it hasn't been turned
     */
    public Direction getDirection() {
        try {
            //check for the rising edge of the first pin of the rotary dial.
            if (!previousValueA && gpio.getPin(pinA)) {

                //look if it turning right or left by reading pin B
                if (gpio.getPin(pinB)) {
                    UI.println("Rotary dial turning left");
                    return Direction.LEFT;
                } else {
                    UI.println("Rotary dial turning right");
                    return Direction.RIGHT;
                }
            }

            return Direction.NO_WAY;
        } finally {
            previousValueA = gpio.getPin(pinA);
        }
    }

    public enum Direction {
        LEFT,
        RIGHT,
        NO_WAY
    }
}
