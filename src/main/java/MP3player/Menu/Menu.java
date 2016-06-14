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

    /**
     * @param name    Name of menu, will show at the top of the display
     * @param display display to write to
     */
    Menu(String name, Display display) {
        this.name = name;
        this.display = display;
        subMenus = new ArrayList<Menu>();

        standardValue = "<<No menu>>";
    }

    /**
     * Get the standard value that is showed
     *
     * @return standard value
     */
    public String getStandardValue() {
        return standardValue;
    }

    /**
     * Set the standard value that is showed
     * @param standardValue standard value
     */
    void setStandardValue(String standardValue) {
        this.standardValue = standardValue;
    }

    /**
     * Open the selected menu
     * @return The menu that is selected. Null if there isn't a submenu
     */
    public Menu select() {
        //check if there exists more then 0 submenu
        if (subMenus.size() > 0) {
            //write name of selected submenu to the top of the display
            UI.println("Select: " + subMenus.get(selectedMenu).name);
            display.WriteNewLine(subMenus.get(selectedMenu).name, true);

            //check if submenu has at least 1 submenu
            if (subMenus.get(selectedMenu).subMenus.size() > 0) {
                //write name of first submenu (of the selected submenu) to the second line
                display.WriteNewLine(subMenus.get(selectedMenu).subMenus.get(0).name, false);
            } else {
                //write the standard value of the submenu
                display.WriteNewLine(subMenus.get(selectedMenu).standardValue, false);
            }

            //return the new menu
            return subMenus.get(selectedMenu);
        }

        return null;
    }

    /**
     * Get the display to write to
     * @return display
     */
    Display getDisplay() {
        return display;
    }

    /**
     * Add a submenu to the list
     * @param menu Menu to add to the list
     */
    void addSubMenu(Menu menu) {
        //add submenu
        subMenus.add(menu);

        //if first submenu, set the name of the submenu to standard value
        if (subMenus.size() == 1) {
            standardValue = subMenus.get(0).getName();
        }
    }

    /**
     * Select a submenu above the current one. If the current one is the first, it wil select
     * the last
     */
    public void up() {
        //get submenu above the current one and check if current submenu is the first
        if (++selectedMenu >= subMenus.size()) {
            selectedMenu = 0;
        }

        //write name of selected submenu to second line of display
        UI.println("Look: " + subMenus.get(selectedMenu).name);
        display.WriteNewLine(subMenus.get(selectedMenu).name, false);
    }

    /**
     * Select a submenu under the current one. If the current one is the last, it will select
     * the first
     */
    public void down() {
        //get submenu under the current one and check if current submenu is the first
        if (--selectedMenu < 0) {
            selectedMenu = subMenus.size() - 1;
        }

        //write name of selected submenu to second line of display
        UI.println("Look: " + subMenus.get(selectedMenu).name);
        display.WriteNewLine(subMenus.get(selectedMenu).name, false);
    }

    /**
     * Get the name of the menu
     * @return the naem of the menu
     */
    public String getName() {
        return name;
    }
}
