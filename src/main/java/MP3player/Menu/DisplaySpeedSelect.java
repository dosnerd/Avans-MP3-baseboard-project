package MP3player.Menu;

/**
 * Created by Acer on 2-6-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
class DisplaySpeedSelect extends Menu {

    DisplaySpeedSelect(MP3player.IO.Dislay display) {
        super("Display speed", display);
        setStandardValue(display.getShiftTime() + "");
    }

    @Override
    public Menu select() {
        return null;
    }

    @Override
    public void up() {
        getDislay().setShiftTime(getDislay().getShiftTime() + 10);
        getDislay().WriteNewLine(getDislay().getShiftTime() + "", false);
    }

    @Override
    public void down() {
        getDislay().setShiftTime(getDislay().getShiftTime() - 10);
        getDislay().WriteNewLine(getDislay().getShiftTime() + "", false);
    }
}
