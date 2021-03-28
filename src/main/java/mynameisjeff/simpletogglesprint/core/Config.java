/**
 *     SimpleToggleSprint
 *     Copyright (C) 2021  My-Name-Is-Jeff
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
            description = "Adds the option to configure to the escape menu.\nYou can also access this menu with /simpletogglesprint"
    )
    public static boolean showConfigOnEscape = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Enabled",
            category = "General", subcategory = "Toggle Sprint",
            description = "Enables the toggle sprint functionality."
    )
    public static boolean enabledToggleSprint = false;

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

    public Config() {
        super(new File("./config/simpletogglesprint.toml"));
        initialize();
    }
}
