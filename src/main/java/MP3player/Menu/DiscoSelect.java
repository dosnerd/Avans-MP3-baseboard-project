package MP3player.Menu;

import MP3player.MP3;

/**
 * Created by Acer on 3-6-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
class DiscoSelect extends Menu {
    private MP3 mp3;

    DiscoSelect(MP3 mp3) {
        super("Disco light", mp3.getDisplay());
        this.mp3 = mp3;
        setStandardValue(mp3.isDisco() ? "On" : "Off");
    }

    @Override
    public Menu select() {
        return null;
    }

    @Override
    public void up() {
        mp3.setDisco(!mp3.isDisco());
        mp3.getDisplay().WriteNewLine(mp3.isDisco() ? "On" : "Off", false);
    }

    @Override
    public void down() {
        up();
    }
}
