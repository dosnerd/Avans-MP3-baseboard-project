package MP3player.Sources;

import MP3player.IO.UI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Acer on 2-6-2016.
 * <p/>
 * This class will search for music files and memorize those files. Out of the list of files,
 * it will create a playlist. The playlist can be filtered. To get the location of the file, use the
 * index of the complete list of files. To get another song, use the playlist to get the song.
 * <p/>
 * The index of the song, listed in the list of files, is called song UID <br />
 * The index of the song, listed in the playlist, is called song order
 *
 * @author David de Prez
 * @version 1.0
 */
public class FileSearch {
    private String filter;
    private List<File> fileList;
    private List<Integer> playList;
    private boolean shuffleMode = false;

    /**
     * Constructor. This will search imminently for song and set the filter to "All".
     */
    public FileSearch() {
        fileList = new ArrayList<File>();
        playList = new ArrayList<Integer>();
        setFilter("All");
    }

    public boolean isShuffleMode() {
        return shuffleMode;
    }

    public void setShuffleMode(boolean shuffleMode) {
        this.shuffleMode = shuffleMode;
        setFilter(filter);
    }

    /**
     * Get the playlist
     *
     * @return playlist
     */
    public List<Integer> getPlayList() {
        return playList;
    }

    /**
     * Get song order, the index of the song located in de playlist.
     *
     * @param songUID the index of song out of the list of files
     * @return Song order, the index of the song out of the playlist
     */
    public int getSongOrder(int songUID) {
        int songOrder = playList.indexOf(songUID);
        if (songOrder == -1) {
            return 0;
        }

        return songOrder;
    }

    /**
     * Get the song UID, the index of the song located in the list of files
     *
     * @param songOrder the index of the song out of the playlist
     * @return Song UID, the index of the song out of the  list of files
     */
    public int getSongUID(int songOrder) {
        return playList.get(songOrder);
    }

    /**
     * Get the file of the song.
     *
     * @param songUID index of the song located in the list of files
     * @return info about the song and the location to the file
     */
    public File getSong(int songUID) {
        return fileList.get(songUID);
    }

    /**
     * Get the current filter that is in use
     *
     * @return current filter
     */
    public String getFilter() {
        return filter;
    }

    /**
     * Set the filter. After the filter is set, it wil search for files in "/media/data/root".
     * All the files that has been found, will be saved in the file list. It will go through the
     * file list to search song that meet the requirements of the filter. Those will be added to
     * the playlist
     *
     * @param filter the search filter
     */
    public void setFilter(String filter) {
        //clear all lists
        fileList.clear();
        playList.clear();

        //find all files that probably can be read and set the filter
        findFiles(new java.io.File("/media/data/root"));
        this.filter = filter;

        //search through the file list
        UI.println("Filter items on " + filter);
        for (int i = 0; i < fileList.size(); i++) {
            //check if song meets the requirement
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

        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });

        //shuffle if shuffle mode is on
        if (shuffleMode) {
            for (int i = 0; i < playList.size(); i++) {
                //get random number that isn't been randomized
                int index = (int) (Math.random() * (playList.size() - i) + i);
                int song = playList.get(index);

                //move song to front
                playList.remove(index);
                playList.add(0, song);
            }
        }
    }

    /**
     * Get the file list
     *
     * @return file list
     */
    public List<File> getFileList() {
        return fileList;
    }

    /**
     * Find files that can probably be read by the VS1033. The only check is that the
     * name must and with .mp3. This method will search in the given directory and all
     * the directories under that (and under that, and so an)
     *
     * @param entry a file/directory to check
     */
    private void findFiles(java.io.File entry) {
        //check if given entry is a file or directory
        if (entry.isDirectory()) {
            try {
                //get all files and directories and check those
                //noinspection ConstantConditions
                for (java.io.File child : entry.listFiles()) {
                    findFiles(child);
                }
            } catch (NullPointerException ex) {
                UI.error("No list of file found", 11);
            }
        } else if (entry.getName().endsWith(".mp3")) {
            try {
                //add file to file list
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
