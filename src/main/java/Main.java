import IO.GPIO;
import IO.UI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

//import Test.*;

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
            /*create a Gpio object and give it to the IO.GPIO class
             * Now can the IO.GPIO class work with the Gpio class
             * while it's in the default package
            */
            GPIO.setDefaultGpio(new Gpio());
            GPIO gpio = new GPIO();
            if (_doTest) {
                try {
                    runTest(new Test.Gpio_Test(gpio));
                    runTest(new Test.ShiftRegister_Test(gpio));
                    runTest(new Test.MUXLED_Test(gpio));

                    runTest(new Test.Display_Test(gpio));
                } catch (Exception ex) {
                    UI.error("Unknown error", 5);
                }

                //run sinus test for about 1000ms
                Test.SinusTest sinusTest = new Test.SinusTest();
                sinusTest.startTest();
                Scanner read = new Scanner(System.in);
                while (!read.next().equals("exit")) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        UI.error("Can not sleep", 4);
                    }
                }
                sinusTest.endTest();
            }

            MP3 mp3;
            if (_manualMode) {
                mp3 = new ManualControl(gpio);
            } else {
                mp3 = new MP3(gpio);
            }

            if (_saveMode) {
                try {
                    UI.println("Start in safe mode");
                    mp3.Run();
                } catch (Exception ex) {
                    UI.error("An error occurs", 5);
                    mp3.Stop();
                    ex.printStackTrace();
                }
            } else {
                UI.println("Start in unsafe mode");
                mp3.Run();
            }
        }
    }


    private static void runTest(Test.Test test) {
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
