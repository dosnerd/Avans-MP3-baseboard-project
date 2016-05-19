import IO.GPIO;
import IO.UI;
import Test.*;

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
            runTest(new Gpio_Test());
            runTest(new ShiftRegister_Test());
            runTest(new Display_Test());

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

    private static boolean runTest(Test test) {
        test.run();
        return test.isFailed();
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
