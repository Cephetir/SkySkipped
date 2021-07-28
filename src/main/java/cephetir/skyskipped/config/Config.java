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

//    @Property(
//            type = PropertyType.SWITCH,
//            name = "Auto going",
//            category = "Dungeons", subcategory = "Dungeons",
//            description = "Automatically says GOING! in chat when go throw the portal"
//    )
    public static boolean autoGoing = false;

    public Config() {
        super(new File("./config/skyskipped.toml"));
        initialize();

        addDependency("onlyPlayers", "playerESP");
    }
}
