package MP3player.Menu;

import MP3player.IO.Display;
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
    private Display display;
    private String standardValue;

    Menu(String name, Display display) {
        this.name = name;
        this.display = display;
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
            display.WriteNewLine(subMenus.get(selectedMenu).name, true);

            if (subMenus.get(selectedMenu).subMenus.size() > 0) {
                display.WriteNewLine(subMenus.get(selectedMenu).subMenus.get(0).name, false);
            } else {
                display.WriteNewLine(subMenus.get(selectedMenu).standardValue, false);
            }

            return subMenus.get(selectedMenu);
        }
        return null;
    }

    Display getDisplay() {
        return display;
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

        display.WriteNewLine(subMenus.get(selectedMenu).name, false);
    }

    public void down() {
        if (--selectedMenu < 0) {
            selectedMenu = subMenus.size() - 1;
        }

        UI.println("Look: " + subMenus.get(selectedMenu).name);

        display.WriteNewLine(subMenus.get(selectedMenu).name, false);
    }

    public String getName() {
        return name;
    }
}
