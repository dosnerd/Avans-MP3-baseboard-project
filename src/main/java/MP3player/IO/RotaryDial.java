package MP3player.IO;

/**
 * Created by Acer on 20-5-2016.
 * This class managed the rotary dial turning. It make it possible to get
 * the direction that the rotary dial is turning to.
 *
 * @author David de Prez
 * @version 1.0
 */
public class RotaryDial implements Runnable {
    private final GPIO gpio;
    private final GPIO.Pin pinA;
    private final GPIO.Pin pinB;
    private boolean previousValueA;
    private int counts;
    private boolean run = true;

    /**
     * Constructor
     *
     * @param gpio GPIO class to read the necessary pins
     * @param pinA One of the pins of the rotary dial
     * @param pinB The other pin of the rotary dial
     */
    public RotaryDial(GPIO gpio, GPIO.Pin pinA, GPIO.Pin pinB) {
        UI.println("Initialize Rotary dial...");
        this.gpio = gpio;
        this.pinA = pinA;
        this.pinB = pinB;

        //Set the previous value to the current value, so it won't detect a edge
        //on startup
        previousValueA = gpio.getPin(pinA);
    }

    /**
     * Stop reading the rotary dial
     */
    public void Stop() {
        run = false;
    }

    @Override
    public void run() {
        UI.println("Start volume thread");
        while (run) {
            //check status rotary dial
            check();
            try {
                //let other threads to there jobs
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //confirm that thread is stopped
        run = true;
        UI.println("Stop volume thread");
    }

    /**
     * Check which way the rotary dial is turning. This code is partially from
     * http://playground.arduino.cc/Main/RotaryEncoders#Waveform.
     */
    private void check() {
        //check if pin A went from high to low
        boolean statusA = gpio.getPin(pinA);
        if (previousValueA && !statusA) {
            previousValueA = false;

            //look if it turning right or left by reading pin B
            if (gpio.getPin(pinB)) {
                UI.println("Rotary dial turning left");
                counts--;
            } else {
                UI.println("Rotary dial turning right");
                counts++;
            }
        }
        previousValueA = statusA;
    }

    /**
     * Get the direction that the rotary dial is turning to.
     * @return the direction. NO_WAY is it hasn't been turned
     */
    public Direction getDirection() {
        try {
            //check for the rising edge of the first pin of the rotary dial.
            if (counts > 0) {
                return Direction.LEFT;
            } else if (counts < 0) {
                return Direction.RIGHT;
            }

            return Direction.NO_WAY;
        } finally {
            counts = 0;
        }

    }

    /**
     * Possible directions
     */
    public enum Direction {
        LEFT,
        RIGHT,
        NO_WAY
    }
}
