package MP3player.Menu;

import MP3player.IO.Display;
import MP3player.Sources.FileSearch;

/**
 * Created by Acer on 21-6-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
class ShuffleSelect extends Menu {
    private FileSearch fileSearch;

    ShuffleSelect(Display display, FileSearch fileSearch) {
        super("Shuffle", display);
        this.fileSearch = fileSearch;
        setStandardValue(fileSearch.isShuffleMode() ? "On" : "Off");
    }

    @Override
    public Menu select() {
        return null;
    }

    @Override
    public void up() {
        fileSearch.setShuffleMode(!fileSearch.isShuffleMode());
        getDisplay().WriteNewLine(fileSearch.isShuffleMode() ? "On" : "Off", false);
    }

    @Override
    public void down() {
        up();
    }
}
