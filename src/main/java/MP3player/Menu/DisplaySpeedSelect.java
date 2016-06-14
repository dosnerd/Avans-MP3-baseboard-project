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
        //set name and give display
        super("Scroll speed", display);

        //set standard value
        setStandardValue(display.getShiftTime() / 10 + "");
    }

    @Override
    public Menu select() {
        return null;
    }

    @Override
    public void up() {
        //Add scroll speed by 10 and write now value to second line of display
        getDisplay().setShiftTime(getDisplay().getShiftTime() + 10);
        getDisplay().WriteNewLine(getDisplay().getShiftTime() / 10 + "", false);
    }

    @Override
    public void down() {
        //Subtract scroll speed by 1 and write now value to second line of display
        getDisplay().setShiftTime(getDisplay().getShiftTime() - 10);
        getDisplay().WriteNewLine(getDisplay().getShiftTime() / 10 + "", false);
    }
}
