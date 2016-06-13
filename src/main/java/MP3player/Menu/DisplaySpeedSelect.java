package MP3player.Menu;

import MP3player.IO.Display;

/**
 * Created by Acer on 2-6-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
class DisplaySpeedSelect extends Menu {

    DisplaySpeedSelect(Display display) {
        super("Scroll speed", display);
        setStandardValue(display.getShiftTime() + "");
    }

    @Override
    public Menu select() {
        return null;
    }

    @Override
    public void up() {
        getDisplay().setShiftTime(getDisplay().getShiftTime() + 10);
        getDisplay().WriteNewLine(getDisplay().getShiftTime() + "", false);
    }

    @Override
    public void down() {
        getDisplay().setShiftTime(getDisplay().getShiftTime() - 10);
        getDisplay().WriteNewLine(getDisplay().getShiftTime() + "", false);
    }
}
