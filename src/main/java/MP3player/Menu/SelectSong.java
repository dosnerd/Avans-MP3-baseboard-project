package MP3player.Menu;

import MP3player.MP3;
import MP3player.Sources.FileSearch;

/**
 * Created by Acer on 21-6-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
class SelectSong extends Menu {
    private FileSearch files;
    private MP3 mp3;
    private int selected;

    SelectSong(MP3 mp3, FileSearch fileSearch) {
        super("Select a song", mp3.getDisplay());
        files = fileSearch;
        this.mp3 = mp3;
        setStandardValue(files.getSong(selected).getTitle());
    }

    @Override
    public Menu select() {
        mp3.getVs1033().Play(files.getSong(selected).getPath());
        mp3.setSong(selected);
        mp3.showSong();
        return null;
    }

    @Override
    public void up() {
        if (++selected >= files.getFileList().size()) {
            selected = 0;
        }

        getDisplay().WriteNewLine(files.getSong(selected).getTitle(), false);
    }

    @Override
    public void down() {
        if (--selected < 0) {
            selected = files.getFileList().size() - 1;
        }

        getDisplay().WriteNewLine(files.getSong(selected).getTitle(), false);
    }
}
