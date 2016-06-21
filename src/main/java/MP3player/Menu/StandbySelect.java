package MP3player.Menu;

import MP3player.MP3;

/**
 * Created by Acer on 3-6-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
class StandbySelect extends Menu {
    private final MP3 mp3;

    StandbySelect(MP3 mp3) {
        //set name and give display
        super("Standby time", mp3.getDisplay());
        this.mp3 = mp3;

        //set standard value in seconds
        setStandardValue(mp3.getTimeToHide() / 1000 + "s");
    }

    @Override
    public Menu select() {
        return null;
    }

    @Override
    public void up() {
        //increase standby time by 1 second and update is to the second line of the LCD
        mp3.setTimeToHide(mp3.getTimeToHide() + 1000);
        mp3.getDisplay().WriteNewLine(mp3.getTimeToHide() / 1000 + "s", false);
    }

    @Override
    public void down() {
        //decrease standby time by 1 second and update is to the second line of the LCD
        mp3.setTimeToHide(mp3.getTimeToHide() - 1000);
        mp3.getDisplay().WriteNewLine(mp3.getTimeToHide() / 1000 + "s", false);
    }
}
