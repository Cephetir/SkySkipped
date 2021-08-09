/*
 * SkySkipped - Hypixel Skyblock mod
 * Copyright (C) 2021  Cephetir
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cephetir.skyskipped.config;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;

import java.io.File;

public class Config extends Vigilant {
    @Property(
            type = PropertyType.SWITCH,
            name = "Chest closer",
            category = "Dungeons", subcategory = "Dungeons",
            description = "Chests in dungeon will close automatically."
    )
    public static boolean chestCloser = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Party chat swapper",
            category = "Chat", subcategory = "Swapper",
            description = "Automatically swaps between party chat and global chat."
    )
    public static boolean chatSwapper = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Discord RPC",
            category = "Discord", subcategory = "Discord RPC",
            description = "Shows status in discord."
    )
    public static boolean DRPC = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Player ESP",
            category = "Dungeons", subcategory = "Dungeons",
            description = "Shows players through walls."
    )
    public static boolean playerESP = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Players Only",
            category = "Dungeons", subcategory = "Dungeons",
            description = "Shows only players through walls."
    )
    public static boolean onlyPlayers = false;

    public Config() {
        super(new File("./config/skyskipped.toml"));
        initialize();

        addDependency("onlyPlayers", "playerESP");
    }
}
