import MP3player.IO.GPIO;
import MP3player.IO.UI;
import MP3player.MP3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//import MP3player.Test.*;

/**
 * Created by Acer on 20-4-2016.
 *
 * @author David de Prez
 * @version 1.2
 */
class Main {
    private static boolean _doTest;
    private static boolean _saveMode = true;
    private static boolean _manualMode;

    /**
     * The main function. Here all the arguments are checked, setup Gpio and start the program
     * If necessary setup and start the test.
     *
     * @param args the arguments given by the terminal
     */
    public static void main(String[] args) {
        //check all arguments
        if (setArgs(args)) {
            /*create a Gpio object and give it to the MP3player.IO.GPIO class
             * Now can the MP3player.IO.GPIO class work with the Gpio class
             * while it's in the default package
            */
            GPIO.setDefaultGpio(new Gpio());
            GPIO gpio = new GPIO();

            //run test if use asked for it
            if (_doTest) {
                try {
                    //run normal tests
                    runTest(new MP3player.Test.Gpio_Test(gpio));
                    runTest(new MP3player.Test.ShiftRegister_Test(gpio));
                    runTest(new MP3player.Test.MUXLED_Test(gpio));

                    runTest(new MP3player.Test.Display_Test(gpio));
                } catch (Exception ex) {
                    UI.error("Unknown error", 5);
                }

                //start sinus test + volume test
                MP3player.Test.SinusTest sinusTest = new MP3player.Test.SinusTest();
                sinusTest.startTest();

                //increase volume slightly until max
                for (int i = 0xFE; i >= 0; i--) {
                    try {
                        //write volume
                        UI.println(i + "");
                        sinusTest.getSCI().write(new byte[]{0x02, 0x0B, (byte) i, (byte) i});

                        //wait for a bit
                        Thread.sleep(100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //end the sinus test
                sinusTest.endTest();
            }

            //create MP3 with/without manual mode.
            MP3 mp3;
            if (_manualMode) {
                mp3 = new ManualControl(gpio);
            } else {
                mp3 = new MP3(gpio);
            }

            //start MP3 mode, always in save mode. Unless user asked for unsave mode
            if (_saveMode) {
                try {
                    UI.println("Start in safe mode");
                    mp3.Run();

                } catch (Exception ex) {
                    UI.error("An error occurs", 5);
                    mp3.Stop();
                }
            } else {
                UI.println("Start in unsafe mode");
                mp3.Run();
            }
        }
    }


    /**
     * Run the given test
     *
     * @param test test to run
     */
    private static void runTest(MP3player.Test.Test test) {
        //run the test
        test.run();
    }


    /**
     * Check all the arguments and set some variable. It check for example if there is a -l argument,
     * the enable logging. It will return a boolean value if the program can go futher. This can
     * be false if for example --help in the arguments are. Then the program should only run the arguments
     * before --help and --help itself.
     *
     * @param args A string array with all the arguments
     * @return true if program can go further, false if need to stop
     */
    private static boolean setArgs(String[] args) {
        for (String argument : args) {
            //can not use switch, because switch with strings is released in JDK 7. We using JDK 6!
            if (argument.equals("-t")) {
                //do all tests
                _doTest = true;
            } else if (argument.equals("-l")) {
                //allow logging
                UI.set_log(true);
            } else if (argument.equals("--unsave") || argument.equals("-u")) {
                //disable save mode
                _saveMode = false;
            } else if (argument.equals("--manual") || argument.equals("-m")) {
                //disable save mode
                _manualMode = true;
            } else if (argument.equals("--help") || argument.equals("-h")) {
                //read help file
                BufferedReader rdr = null;
                try {
                    rdr = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("Help")));
                    String line;

                    //read every line and prints it
                    while ((line = rdr.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException ex) {
                    UI.error("Can not read line from help file", 3);
                } finally {
                    //this code will ALWAYS run
                    //check if the file is opend
                    if (rdr != null) {
                        //try to close the file
                        try {
                            rdr.close();
                        } catch (IOException ex) {
                            UI.error("Can not close Help file", 6);
                        }
                    }
                }

                return false;
            }
        }

        return true;
    }
}
