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
    private final FileSearch fileSearch;
    private final List<String> filters;
    private int filter;

    PlaylistSelect(Display display, FileSearch fileSearch) {
        //set name and give display
        super("Filter", display);
        this.fileSearch = fileSearch;
        filters = new ArrayList<String>();
        filters.add("All");

        //add all possible filters
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

        //set standard value
        setStandardValue(fileSearch.getFilter());

        //set selected filter to current filter (if exists)
        if (filters.contains(fileSearch.getFilter())) {
            filter = filters.indexOf(fileSearch.getFilter());
        }
    }

    @Override
    public Menu select() {
        //apply filter
        fileSearch.setFilter(filters.get(filter));
        return null;
    }

    @Override
    public void up() {
        //select next filter, start from beginning if current is at the end
        if (++filter >= filters.size()) {
            filter = 0;
        }

        //show selected filter to second line of display
        getDisplay().WriteNewLine(filters.get(filter), false);
    }

    @Override
    public void down() {
        //select previous filter, start from end if current is at the beginning
        if (--filter < 0) {
            filter = filters.size() - 1;
        }

        //show selected filter to second line of display
        getDisplay().WriteNewLine(filters.get(filter), false);
    }
}
