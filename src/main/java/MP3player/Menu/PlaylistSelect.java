package MP3player.Menu;

import MP3player.IO.Display;
import MP3player.Sources.File;
import MP3player.Sources.FileSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acer on 2-6-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
class PlaylistSelect extends Menu {
    private FileSearch fileSearch;
    private List<String> filters;
    private int filter;

    PlaylistSelect(Display display, FileSearch fileSearch) {
        super("Filter", display);
        this.fileSearch = fileSearch;
        filters = new ArrayList<String>();
        filters.add("All");
        setStandardValue(fileSearch.getFilter());

        for (File file : fileSearch.getFileList()) {
            String artistFilter = "<ARTIEST>" + file.getArtist();
            String albumFilter = "<ALBUM>" + file.getAlbum();
            String genreFilter = "<GENRE>" + file.getGenre();

            if (!filters.contains(artistFilter)) {
                filters.add(artistFilter);
            }

            if (!filters.contains(albumFilter)) {
                filters.add(albumFilter);
            }

            if (!filters.contains(genreFilter)) {
                filters.add(genreFilter);
            }
        }
    }

    @Override
    public Menu select() {
        fileSearch.setFilter(filters.get(filter));
        return null;
    }

    @Override
    public void up() {
        if (++filter >= filters.size()) {
            filter = 0;
        }

        getDisplay().WriteNewLine(filters.get(filter), false);
    }

    @Override
    public void down() {
        if (--filter < 0) {
            filter = filters.size() - 1;
        }

        getDisplay().WriteNewLine(filters.get(filter), false);
    }
}
