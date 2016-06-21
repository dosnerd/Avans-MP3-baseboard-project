import MP3player.IO.GPIO;
import MP3player.IO.UI;
import MP3player.MP3;

import java.util.Scanner;

/**
 * Created by Acer on 25-5-2016.
 * <p/>
 * This class can be used as replacement for the buttons and rotary dial. Is continuously ask for a command.
 *
 * @author David de Prez
 * @version 1.0
 */
class ManualControl extends MP3 implements Runnable {

    /**
     * Constructor for manual control. Here it start itself async. Gpio is needed for the super class.
     *
     * @param gpio needed for super class
     */
    ManualControl(GPIO gpio) {
        //call constructor super class
        super(gpio);

        //start itself async.
        Thread t = new Thread(this);
        t.setName("Manual control");
        t.start();
    }

    /**
     * This override the method Runnable. This function called when it starts as a separate thread.
     */
    @Override
    public void run() {
        Scanner rdr = new Scanner(System.in);
        while (true) {
            //Ask for command
            UI.print("Command: ");
            String line = rdr.nextLine();

            //run command
            if (line.equals("exit")) {
                Stop();
                return;
            } else if (line.equals("next")) {
                nextSong();
            } else if (line.equals("prev")) {
                prevSong();
            } else if (line.equals("stop")) {
                getVs1033().Stop();
            } else if (line.equals("pause")) {
                getVs1033().Pause();
            } else if (line.equals("play")) {
                getVs1033().Play();
            } else if (line.equals("VH")) {
                increaseVolume();
            } else if (line.equals("VL")) {
                decreaseVolume();
            }
        }
    }
}
