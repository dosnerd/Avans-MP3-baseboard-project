package MP3player.Sources;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Acer on 24-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class File {
    private final String[] albums = new String[]{"Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "Alt Rock", "Bass", "Soul", "Punk", "Space", "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American", "Cabaret", "New Wave", "Psychedelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk", "Folk/Rock", "National Folk", "Swing", "Fast Fusion", "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Bass", "Primus", "Porn Groove", "Satire/Parody", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo", "Acapella", "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie", "BritPop", "Negerpunk", "Polsk Punk", "Beat", "Christian Gangsta", "Heavy Metal", "Black Metal", "Crossover", "Contemporary Chr", "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime", "JPop", "Synthpop"};
    private String path;
    private String tag;

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

    private String filter(String data) {
        data = data.replaceAll("\\s+", " ");
        data = data.replaceAll("\\00+", " ");

        return data;
    }

    public boolean hasTag() {
        return tag.startsWith("TAG");
    }

    public String getTitle() {
        if (hasTag()) {
            return filter(tag.substring(3, 32));
        }

        if (path.lastIndexOf("/") >= 0 && !path.endsWith("/"))
            return path.substring(path.lastIndexOf("/") + 1);
        else
            return "No title";
    }

    public String getArtist() {
        if (hasTag()) {
            return filter(tag.substring(33, 62));
        }

        return "No artist";
    }

    public String getGenre() {
        if (hasTag()) {
            return albums[(int) tag.substring(127).getBytes()[0]];
        }

        return "No genre";
    }

    public String getAlbum() {
        if (hasTag()) {
            return filter(tag.substring(63, 92));
        }

        return "no album";
    }

    public String getPath() {
        return path;
    }
}
