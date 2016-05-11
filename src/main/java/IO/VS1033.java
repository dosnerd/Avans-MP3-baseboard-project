package IO;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Acer on 2-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class VS1033 {
    private final GPIO gpio;
    private RandomAccessFile SCI;
    private RandomAccessFile SDI;

    public VS1033(GPIO gpio) throws IOException {
        this.gpio = gpio;

        try {
            UI.println("Load libraries: SCI and SDI");
            SCI = new RandomAccessFile("/dev/spidev1.0", "rw");
            SDI = new RandomAccessFile("/dev/spidev1.1", "rw");
        } catch (IOException ex) {
            UI.error("An error occurs. Probably could find SCI/SDI file");
            throw ex;
        }
    }

    private void init() {

    }

    public void Play() {

    }

    public void Play(String file) {

    }

    public void Pauze() {

    }

    public void Stop() {

    }

    public void Write(byte data) {

    }
}