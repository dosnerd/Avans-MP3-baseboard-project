import IO.GPIO;
import IO.UI;
import IO.VS1033;

/**
 * Created by Acer on 12-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class MP3 {
    private GPIO gpio;
    private VS1033 vs1033;

    MP3() {
        gpio = new GPIO();
        vs1033 = new VS1033(gpio);

        Thread dataThread = new Thread(vs1033);
        dataThread.start();
    }

    void run() {
        //TODO:make loop
        UI.println("Start file");
        vs1033.Play("idiot.mp3");
        try {
            UI.println("sleep");
            Thread.sleep(1000 * 5);
            UI.println("wake up");
        } catch (InterruptedException ignored) {
            UI.error("", 4);
        }
        close();
    }

    private void close() {
        gpio.deinit();
        vs1033.deinit();
    }

}
