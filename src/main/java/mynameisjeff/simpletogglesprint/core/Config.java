/*
      SimpleToggleSprint
      Copyright (C) 2021  My-Name-Is-Jeff

      This program is free software: you can redistribute it and/or modify
      it under the terms of the GNU Affero General Public License as published by
      the Free Software Foundation, either version 3 of the License, or
      (at your option) any later version.

      This program is distributed in the hope that it will be useful,
      but WITHOUT ANY WARRANTY; without even the implied warranty of
      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
      GNU Affero General Public License for more details.

      You should have received a copy of the GNU Affero General Public License
      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package mynameisjeff.simpletogglesprint.core;

import club.sk1er.vigilance.Vigilant;
import club.sk1er.vigilance.data.Property;
import club.sk1er.vigilance.data.PropertyType;

import java.io.File;

public class Config extends Vigilant {

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Config on Menu",
            category = "General", subcategory = "Options",
            description = "Adds the option to configure to the escape menu.\nYou can also access this menu with /simpletogglesprint."
    )
    public static boolean showConfigOnEscape = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Enabled",
            category = "General", subcategory = "Toggle Sprint",
            description = "Enables the toggle sprint functionality."
    )
    public static boolean enabledToggleSprint = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "State",
            category = "General", subcategory = "Toggle Sprint",
            description = "Saves the sprint state to use on launch.",
            hidden = true
    )
    public static boolean toggleSprintState = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Enabled",
            category = "General", subcategory = "Toggle Sneak",
            description = "Enables the toggle sneak functionality.\nThis does not function while in a menu."
    )
    public static boolean enabledToggleSneak = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "State",
            category = "General", subcategory = "Toggle Sneak",
            description = "Saves the sneak state to use on launch.",
            hidden = true
    )
    public static boolean toggleSneakState = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Enabled",
            category = "Display",
            description = "Displays the toggle states on your HUD."
    )
    public static boolean displayToggleState = true;

    @Property(
            type = PropertyType.SLIDER,
            name = "Red",
            category = "Display", subcategory = "Color",
            description = "Change the red value for the color of the display.",
            max = 255
    )
    public static int displayStateRed = 255;

    @Property(
            type = PropertyType.SLIDER,
            name = "Green",
            category = "Display", subcategory = "Color",
            description = "Change the green value for the color of the display.",
            max = 255
    )
    public static int displayStateGreen = 255;

    @Property(
            type = PropertyType.SLIDER,
            name = "Blue",
            category = "Display", subcategory = "Color",
            description = "Change the blue value for the color of the display.",
            max = 255
    )
    public static int displayStateBlue = 255;

    @Property(
            type = PropertyType.SLIDER,
            name = "X",
            category = "Display", subcategory = "Position",
            description = "Change the X value for the state display. Based on a percentage of your screen.",
            max = 1000
    )
    public static int displayStateX = 2;

    @Property(
            type = PropertyType.SLIDER,
            name = "Y",
            category = "Display", subcategory = "Position",
            description = "Change the Y value for the state display. Based on a percentage of your screen.",
            max = 1000
    )
    public static int displayStateY = 974;

    @Property(
            type = PropertyType.SLIDER,
            name = "Scale",
            category = "Display", subcategory = "Options",
            description = "Change the scale for the state display, this is a percentage.",
            max = 5000
    )
    public static int displayStateScale = 1085;

    @Property(
            type = PropertyType.SWITCH,
            name = "Text-Shadow",
            category = "Display", subcategory = "Options",
            description = "Change whether or not the display has text-shadow."
    )
    public static boolean displayStateShadow = true;

    @Property(
            type = PropertyType.SELECTOR,
            name = "Text-Alignment",
            category = "Display", subcategory = "Options",
            description = "Changes the text-alignment settings for the state display.",
            options = {
                    "Left-Right",
                    "Center",
                    "Right-Left"
            }
    )
    public static int displayStateAlignment = 0;

    public Config() {
        super(new File("./config/simpletogglesprint.toml"));
        initialize();
    }
}
