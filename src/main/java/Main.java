import IO.GPIO;
import IO.UI;

/**
 * Created by Acer on 20-4-2016.
 *
 * @author David de Prez
 * @version 1.2
 */
public class Main {
    private static boolean _doTest;

    public static void main(String[] args) {
        Gpio gpio = new Gpio();
        GPIO.setDefaultGpio(gpio);

        setArgs(args);
        if (_doTest) {
            Gpio_Test test = new Gpio_Test();
            test.run();

            SinusTest sinusTest = new SinusTest();
            sinusTest.startTest();
            try {
                Thread.sleep(1000 * 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sinusTest.endTest();
        }
    }

    private static void setArgs(String[] args) {
        for (String argument : args) {
            //can not use switch, because switch with strings is released in JDK 7. We using JDK 6!
            if (argument.equals("-t")) {
                //do all tests
                _doTest = true;
            } else if (argument.equals("-l")) {
                //allow logging
                UI.set_log(true);
            }
        }
    }
}
