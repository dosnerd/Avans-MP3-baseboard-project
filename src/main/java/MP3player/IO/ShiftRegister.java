package MP3player.IO;

/**
 * Created by Acer on 17-5-2016.
 * This control the shift register. The values will be save in the memory. When updating, it will send
 * the memory to the shift register over a serial line. When the data has been send, the out of the shift
 * register will be updated. this is done by enabling the latch pin of the shift register.
 * <p>
 * TODO: configure this class to the right component type
 *
 * @author David de Prez
 * @version 1.0
 */
public class ShiftRegister {
    private final GPIO.Pin clock;
    private final GPIO.Pin serial;
    private final GPIO.Pin latch;
    private final GPIO gpio;
    private byte data;

    public ShiftRegister(GPIO gpio, GPIO.Pin clock, GPIO.Pin serial, GPIO.Pin latch) {
        UI.println("Initialize Shift register...");
        this.gpio = gpio;
        this.clock = clock;
        this.serial = serial;
        this.latch = latch;
    }

    /**
     * Set the value of the pin in the memory.
     *
     * @param pin   Pin to set the value
     * @param value The value. True is high and false is low.
     */
    public void setPin(int pin, boolean value) {
        //check if the pin needs to be high or low
        if (value) {
            //set the bit in the memory high(1) in the right location
            data = (byte) (data | (1 << pin));
        } else {
            //set the bit in the memory low(0) in the right location
            data = (byte) (data & ~(1 << pin));
        }
    }

    /**
     * Set the memory.
     * @param data data that replace the memory
     */
    void setData(byte data) {
        this.data = data;
    }

    /**
     * Send the values in the memory to the shift register. After that is wil update
     * the output of the shift register (latch).
     */
    public void update() {

        //Send memory to shift register
        gpio.setPin(latch, false);
        for (int i = 7; i >= 0; i--) {
            sendBit(i);
        }

        //update output of shift register (latch)
        gpio.setPin(latch, true);
        //TODO: check if this is to fast
        sleep(1);
        gpio.setPin(latch, false);
    }

    /**
     * Send one bit to the shift register. It won't update is to the output of the shift register (latch)
     *
     * @param position the position in the memory
     */
    private void sendBit(int position) {
        //get the value of the give position in the memory
        boolean value = (data & (1 << position)) > 0;

        //send to the shift register
        gpio.setPin(clock, false);
        gpio.setPin(serial, value);
        sleep(1);
        gpio.setPin(clock, true);
        sleep(1);
        gpio.setPin(serial, false);
    }

    private void sleep(int mili) {
        try {
            Thread.sleep(mili);
        } catch (InterruptedException ex) {
            UI.error("Can not sleep", 4);
        }
    }
}
