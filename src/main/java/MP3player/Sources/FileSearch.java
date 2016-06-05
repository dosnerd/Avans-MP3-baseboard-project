package MP3player.Sources;

import MP3player.IO.UI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acer on 2-6-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class FileSearch {
    private String filter;
    private List<File> fileList;
    private List<Integer> playList;

    public FileSearch() {
        fileList = new ArrayList<File>();
        playList = new ArrayList<Integer>();
        setFilter("All");
    }

    public List<Integer> getPlayList() {
        return playList;
    }

    public int getIndex(int songOutFileList) {
        int index = playList.indexOf(songOutFileList);
        if (index == -1) {
            return 0;
        }

        return index;
    }

    public int getSongIndex(int songOutPlaylist) {
        return playList.get(songOutPlaylist);
    }

    public File getSong(int song) {
        return fileList.get(song);
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        fileList.clear();
        playList.clear();

        findFiles(new java.io.File("/media/data/root"));
        this.filter = filter;

        UI.println("Filter items on " + filter);
        for (int i = 0; i < fileList.size(); i++) {
            File file = fileList.get(i);
            if (filter.toUpperCase().equals("ALL")) {
                playList.add(i);
                UI.println(file.getTitle() + " added to playlist");
            } else if (filter.toUpperCase().equals("<ALBUM>" + file.getAlbum().toUpperCase())) {
                playList.add(i);
                UI.println(file.getTitle() + " added to playlist (" + file.getAlbum() + ")");
            } else if (filter.toUpperCase().equals("<ARTIEST>" + file.getArtist().toUpperCase())) {
                playList.add(i);
                UI.println(file.getTitle() + " added to playlist (" + file.getArtist() + ")");
            } else if (filter.toUpperCase().equals("<GENRE>" + file.getGenre().toUpperCase())) {
                playList.add(i);
                UI.println(file.getTitle() + " added to playlist (" + file.getArtist() + ")");
            }
        }
    }

    public List<File> getFileList() {
        return fileList;
    }

    private void findFiles(java.io.File entry) {
        if (entry.isDirectory()) {
            try {
                //noinspection ConstantConditions
                for (java.io.File child : entry.listFiles()) {
                    findFiles(child);
                }
            } catch (NullPointerException ex) {
                UI.error("No list of file found", 11);
            }
        } else if (entry.getName().endsWith(".mp3")) {
            try {
                fileList.add(new File(entry));
                UI.println(entry.getName() + " Added to playlist");
            } catch (FileNotFoundException ex) {
                UI.error("File not found", 14);
            } catch (IOException ex) {
                UI.error("An MP3player.IO exeption has occurs", 1);
            }
        }
    }
}
