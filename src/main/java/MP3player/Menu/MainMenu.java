package MP3player.Menu;

import MP3player.IO.UI;
import MP3player.MP3;
import MP3player.Sources.FileSearch;

/**
 * Created by Acer on 2-6-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class MainMenu extends Menu {
    private static boolean _secreteMode;

    public MainMenu(MP3 mp3, FileSearch fileSearch) {
        //set name and give display
        super("Main menu", mp3.getDisplay());

        //create and add submenu's
        addSubMenu(new Displaymenu(mp3));
        addSubMenu(new PlaylistSelect(mp3.getDisplay(), fileSearch));
        if (_secreteMode) {
            addSubMenu(new ShuffleSelect(mp3.getDisplay(), fileSearch));
            addSubMenu(new SelectSong(mp3, fileSearch));
        }
        addSubMenu(new DiscoSelect(mp3));
    }

    public static void set_secreteMode(boolean _secreteMode) {
        if (!MainMenu._secreteMode) {
            UI.println("Secrete mode enabled");
        }
        MainMenu._secreteMode = _secreteMode;
    }
}
