package MP3player.Menu;

import MP3player.MP3;

/**
 * Created by Acer on 3-6-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
class Displaymenu extends Menu {
    Displaymenu(MP3 mp3) {
        super("Display", mp3.getDisplay());
        addSubMenu(new StandbySelect(mp3));
        addSubMenu(new DisplaySpeedSelect(mp3.getDisplay()));
    }
}
