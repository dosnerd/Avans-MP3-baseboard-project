package MP3player.Menu;

import MP3player.IO.Dislay;
import MP3player.IO.UI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acer on 2-6-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class Menu {
    private String name;
    private List<Menu> subMenus;
    private int selectedMenu;
    private Dislay dislay;
    private String standardValue;

    Menu(String name, Dislay dislay) {
        this.name = name;
        this.dislay = dislay;
        subMenus = new ArrayList<Menu>();

        standardValue = "<<No menu>>";
    }

    public String getStandardValue() {
        return standardValue;
    }

    void setStandardValue(String standardValue) {
        this.standardValue = standardValue;
    }

    public Menu select() {
        if (subMenus.size() > 0) {
            UI.println("Select: " + subMenus.get(selectedMenu).name);
            dislay.WriteNewLine(subMenus.get(selectedMenu).name, true);

            if (subMenus.get(selectedMenu).subMenus.size() > 0) {
                dislay.WriteNewLine(subMenus.get(selectedMenu).subMenus.get(0).name, false);
            } else {
                dislay.WriteNewLine(subMenus.get(selectedMenu).standardValue, false);
            }

            return subMenus.get(selectedMenu);
        }
        return null;
    }

    Dislay getDislay() {
        return dislay;
    }

    void addSubMenu(Menu menu) {
        subMenus.add(menu);
        if (subMenus.size() == 1) {
            standardValue = subMenus.get(0).getName();
        }
    }

    public void up() {
        if (++selectedMenu >= subMenus.size()) {
            selectedMenu = 0;
        }

        UI.println("Look: " + subMenus.get(selectedMenu).name);

        dislay.WriteNewLine(subMenus.get(selectedMenu).name, false);
    }

    public void down() {
        if (--selectedMenu < 0) {
            selectedMenu = subMenus.size() - 1;
        }

        UI.println("Look: " + subMenus.get(selectedMenu).name);

        dislay.WriteNewLine(subMenus.get(selectedMenu).name, false);
    }

    public String getName() {
        return name;
    }
}
