import IO.GPIO;
import IO.UI;

import java.util.Scanner;

/**
 * Created by Acer on 25-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
class ManualControl extends MP3 implements Runnable {

    ManualControl(GPIO gpio) {
        super(gpio);
        Thread t = new Thread(this);
        t.setName("Manual control");
        t.start();
    }

    @Override
    public void run() {
        Scanner rdr = new Scanner(System.in);
        while (true) {
            /*UI.println((Runtime.getRuntime().freeMemory() + ""));
            try{
                Thread.sleep(1000);
            } catch (InterruptedException ex){

            }*/

            UI.print("Command: ");
            String line = rdr.nextLine();
            if (line.equals("exit")) {
                Stop();
                return;
            } else if (line.equals("next")) {
                nextSong();
            } else if (line.equals("prev")) {
                prevSong();
            } else if (line.equals("stop")) {
                getVs1033().Stop();
            } else if (line.equals("pauze")) {
                getVs1033().Pauze();
            } else if (line.equals("play")) {
                getVs1033().Play();
            } else if (line.startsWith("bit:")) {
                String[] args = line.split(":");
                getVs1033().changeBuffer(Integer.parseInt(args[1]));
            } else if (line.equals("VH")) {
                increaseVolume();
            } else if (line.equals("VL")) {
                decreaseVolume();
            }
        }
    }
}
