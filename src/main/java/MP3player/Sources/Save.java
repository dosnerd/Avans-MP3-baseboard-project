package MP3player.Sources;

import java.io.Serializable;

/**
 * Created by Acer on 9-6-2016.
 * <p/>
 *
 * This class represent the save file. All the info in this class
 * will be save to the save file. If info is set to an object of this class, it
 * will not anatomically save that info. This object need to be saved for saving
 * all the data in this object. This count also for loading the info.
 *
 * @author David de Prez
 * @version 1.0
 */
public class Save implements Serializable {
    private short volume;
    private boolean disco;
    private int timeToHide;
    private int scrollSpeed;
    private String filter;

    /**
     * Get the volume
     *
     * @return volume
     */
    public short getVolume() {
        //check if loaded volume is in out the limits
        if (volume >= 15) {
            return 15;
        }

        if (volume <= 0) {
            return 0;
        }

        //return the saved volume
        return volume;
    }

    /**
     * Set the volume. This will NOT automatically save it the file.
     *
     * @param volume Volume to be saved
     */
    public void setVolume(short volume) {
        //check if given volume is out of limits
        if (volume >= 15) {
            volume = 15;
        } else if (volume <= 0) {
            volume = 0;
        }

        //save the given volume
        this.volume = volume;
    }

    /**
     * Get if the disco mode is enable in the save file.
     * @return disco mode
     */
    public boolean isDisco() {
        return disco;
    }

    /**
     * Set the disco mode. This will NOT automatically save it the file.
     * @param disco disco mode
     */
    public void setDisco(boolean disco) {
        this.disco = disco;
    }

    /**
     * get the time before the standby mode.
     * @return standby time
     */
    public int getTimeToHide() {
        //check limits
        if (timeToHide <= 5000) {
            return 5000;
        }

        //return saved value
        return timeToHide;
    }

    /**
     * Set the time before standby mode enables. This will NOT automatically save it the file.
     * @param timeToHide time before standby mode
     */
    public void setTimeToHide(int timeToHide) {
        //check limits
        if (timeToHide <= 5000) {
            timeToHide = 5000;
        }

        //return saved value
        this.timeToHide = timeToHide;
    }

    /**
     * Get the speed that the text scroll through the display
     * @return scroll speed
     */
    public int getScrollSpeed() {
        return scrollSpeed;
    }

    /**
     * Set the speed that the text scrolls through the display
     * @param scrollSpeed scroll speed
     */
    public void setScrollSpeed(int scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    /**
     * Get the filter for the files
     * @return song filter
     */
    public String getFilter() {
        return filter;
    }

    /**
     * Set the filter for the files
     * @param filter song filter
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }
}
