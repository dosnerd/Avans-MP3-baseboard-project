package MP3player.Menu;

import MP3player.MP3;
import MP3player.Sources.FileSearch;

/**
 * Created by Acer on 2-6-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class MainMenu extends Menu {
    public MainMenu(MP3 mp3, FileSearch fileSearch) {
        //set name and give display
        super("Main menu", mp3.getDisplay());

        //create and add submenu's
        addSubMenu(new Displaymenu(mp3));
        addSubMenu(new PlaylistSelect(mp3.getDisplay(), fileSearch));
        addSubMenu(new ShuffleSelect(mp3.getDisplay(), fileSearch));
        addSubMenu(new DiscoSelect(mp3));
    }
}
