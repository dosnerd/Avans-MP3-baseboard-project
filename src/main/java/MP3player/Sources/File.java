package MP3player.Sources;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Acer on 24-5-2016.
 * <p/>
 * In here the basic info about the song will be saved. This includes the path to the file, artiest, album, genre and
 * title. Some information can not be loaded because the file doesn't have a tag the can be recognise by this
 * class.
 *
 * @author David de Prez
 * @version 1.0
 */
public class File {
    private final String[] albums = new String[]{"Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "Alt Rock", "Bass", "Soul", "Punk", "Space", "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American", "Cabaret", "New Wave", "Psychedelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk", "Folk/Rock", "National Folk", "Swing", "Fast Fusion", "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Bass", "Primus", "Porn Groove", "Satire/Parody", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo", "Acapella", "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie", "BritPop", "Negerpunk", "Polsk Punk", "Beat", "Christian Gangsta", "Heavy Metal", "Black Metal", "Crossover", "Contemporary Chr", "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime", "JPop", "Synthpop"};
    private String path;
    private String tag;

    /**
     * Constructor. The give file will be check if it can be found. It also search for
     * the tag in the file. If the tag can't be found, the Artist, album and genre is not
     * available. The title will be de name of the file
     *
     * @param file File where the song is saved in
     * @throws IOException This, or a subclass of it, will be throw if the file if not found, if can read
     *                     the file or if it can not close the file
     */
    File(java.io.File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        path = file.getAbsolutePath();
        byte[] buffer = new byte[128];

        RandomAccessFile rdr = new RandomAccessFile(path, "r");
        if (rdr.length() - 128 > 0) {
            rdr.seek(rdr.length() - 128);
        }
        rdr.read(buffer);
        tag = new String(buffer);

        rdr.close();
    }

    /**
     * This will filter some characters out of the given data. This is needed
     * because the tag will give some characters that aren't readable for
     * humans (normally). <br />
     * This filter will filters null characters (\0) and double (and more) spaces.
     *
     * @param data data to be filtered
     * @return filtered data
     */
    private String filter(String data) {
        //filter double and more spaced
        data = data.replaceAll("\\s+", " ");

        //filter null character
        data = data.replaceAll("\\00+", " ");

        return data;
    }

    /**
     * Get is there is a tag in the file
     *
     * @return true if tag exists, false if tag isn't found.
     */
    public boolean hasTag() {
        return tag.startsWith("TAG");
    }

    /**
     * Get the title of the song if the tag exists. Else it will give the name
     * of the file. If the name of the file is to hard to get, is will return "No title"
     * @return title of song or name of file
     */
    public String getTitle() {
        //check if tag exists
        if (hasTag()) {
            //return filtered title out of tag
            return filter(tag.substring(3, 32));
        }

        //try getting file name
        if (path.lastIndexOf("/") >= 0 && !path.endsWith("/")) {
            return path.substring(path.lastIndexOf("/") + 1);
        } else {
            //can not get the name of file easily, so return "No title".
            return "No title";
        }
    }

    /**
     * Get the artist of the song. If the tag can't be found, it will return "No artist"
     * @return artist of song or No artist
     */
    public String getArtist() {
        if (hasTag()) {
            return filter(tag.substring(33, 62));
        }

        return "No artist";
    }

    /**
     * Get the genre of the song. If the tag can't be found, it will return "No genre"
     * @return genre of song or No genre
     */
    public String getGenre() {
        if (hasTag()) {
            return albums[(int) tag.substring(127).getBytes()[0]];
        }

        return "No genre";
    }

    /**
     * Get the album of the song. If the tag can't be found, it will return "No album"
     * @return album of song or No album
     */
    public String getAlbum() {
        if (hasTag()) {
            return filter(tag.substring(63, 92));
        }

        return "no album";
    }

    /**
     * Get the path to the file
     * @return path of file
     */
    public String getPath() {
        return path;
    }
}
