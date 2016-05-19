package Test;

import IO.UI;

/**
 * Created by Acer on 19-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public abstract class Test {
    private boolean failed;

    /**
     * get if all tests are passed or not
     *
     * @return true if all test are passed, false if one ore more test failed
     */
    public boolean isFailed() {
        return failed;
    }

    /**
     * Run the tests
     */
    public abstract void run();

    /**
     * Call when a test fail. Give a message about the failure
     *
     * @param message Message about the failure
     */
    void Fail(String message) {
        failed = true;
        UI.println("Test failed");
        UI.println(message);
    }
}
