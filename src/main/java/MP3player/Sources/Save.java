package MP3player.Sources;

import java.io.Serializable;

/**
 * Created by Acer on 9-6-2016.
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

    public short getVolume() {
        if (volume >= 15) {
            return 15;
        }

        if (volume <= 0) {
            return 0;
        }
        return volume;
    }

    public void setVolume(short volume) {
        if (volume >= 15) {
            volume = 15;
        } else if (volume <= 0) {
            volume = 0;
        }

        this.volume = volume;
    }

    public boolean isDisco() {
        return disco;
    }

    public void setDisco(boolean disco) {
        this.disco = disco;
    }

    public int getTimeToHide() {
        if (timeToHide <= 5000) {
            return 5000;
        }
        return timeToHide;
    }

    public void setTimeToHide(int timeToHide) {
        if (timeToHide <= 5000) {
            timeToHide = 5000;
        }

        this.timeToHide = timeToHide;
    }

    public int getScrollSpeed() {
        return scrollSpeed;
    }

    public void setScrollSpeed(int scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
