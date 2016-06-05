package MP3player.Errors;

/**
 * Created by Acer on 20-4-2016.
 * <p/>
 * This exception occurs when trying to read or write from a pin, when it isn't possible. This is when you are trying
 * reading from a output pin or writing to a input pin.
 *
 * @author David de Prez
 * @version 1.0
 */
public class IllegalPinModeException extends RuntimeException {
    public IllegalPinModeException() {
        super("Can not read/write from given pin because the mode doesn't match with the action. If you try to read " +
                "from this pin, it's a output pin. If you try to write to this pin, it's a input pin.");
    }
}
