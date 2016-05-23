package Test;

import IO.UI;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Acer on 28-4-2016.
 * <p/>
 * This class will allow you to let the VS1033 give a sinus wave. This can be used to test the VS1033. If this test
 * fails, means that the SPI communication doesn't work, the output of the IO.VS1033 or the VS1033 doesn't work
 * at all. Don't run this test while communicating with the IO.VS1033.
 *
 * @author David de Prez
 * @version 1.0
 */
class SinusTest {
    private RandomAccessFile SCI;
    private RandomAccessFile SDI;

    /**
     * Constructor
     * Run a software reset on the VS1033 and set some settings in VS1033 to allow a sinus test.
     */
    SinusTest() {
        UI.println("Initialize VS1033");
        //commands for initialize VS1033 and volume
        byte[] init = {0x02, 0x00, 0x08, 0x26};
        byte[] vol = {0x02, 0x0B, 0x1F, 0x1F};


        try {
            //load interfaces
            IO.UI.println("Load lib for SPI");
            SCI = new RandomAccessFile("/dev/spidev1.0", "rw");
            SDI = new RandomAccessFile("/dev/spidev1.1", "rw");

            //send commands, wait between commands so the IO.VS1033 can run the command
            IO.UI.println("Init");
            SCI.write(init);
            Thread.sleep(1);
            SCI.write(vol);
            Thread.sleep(1);

            IO.UI.println("VS1033 is ready to test");
        } catch (IOException ignored) {
            IO.UI.println("IO exception. Probably can't open /dev/spidev1.*");
        } catch (InterruptedException ignored) {
            IO.UI.println("Can't sleep");
        }
    }

    /**
     * Send the command to the IO.VS1033 to run the sinus test. The IO.VS1033 give on its output a sinus signal.
     */
    void startTest() {
        try {
            //create commando and send it
            byte[] startTest = {0x53, (byte) 0xEf, 0x6E, 0x7E, 0, 0, 0, 0};
            IO.UI.println("Start test");
            SDI.write(startTest);
        } catch (IOException e) {
            IO.UI.println("Can't write to SDI");
        }
    }

    /**
     * End the test by stopping the IO.VS1033 with sending the sinus and close the interfaces.
     */
    void endTest() {
        try {
            //create commando and send it
            byte[] endTest = {0x45, 0x78, 0x69, 0x74, 0, 0, 0, 0};
            IO.UI.println("End test");
            SDI.write(endTest);
        } catch (IOException ignored) {
            IO.UI.println("Can't write SDI");
        } finally {

            try {
                //close Serial Control Interface
                SCI.close();
            } catch (IOException e) {
                IO.UI.println("Can't close SCI");
            }

            try {
                //close Serial Data Interface
                SDI.close();
            } catch (IOException e) {
                IO.UI.println("Can't close SDI");
            }
        }
    }
}
