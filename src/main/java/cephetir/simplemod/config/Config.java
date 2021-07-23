package cephetir.simplemod.config;

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

//    @Property(
//            type = PropertyType.SWITCH,
//            name = "isInDungeon",
//            category = "Dungeons", subcategory = "Cache",
//            description = "DO NOT EDIT THIS!!!"
//    )
    public static boolean isInDungeon = false;

//    @Property(
//            type = PropertyType.NUMBER,
//            name = "dungeonPercentage",
//            category = "Dungeons", subcategory = "Cache",
//            description = "DO NOT EDIT THIS!!!"
//    )
    public static int dungeonPercentage = 0;

//    @Property(
//            type = PropertyType.TEXT,
//            name = "dungeonName",
//            category = "Dungeons", subcategory = "Cache",
//            description = "DO NOT EDIT THIS!!!"
//    )
    public static String dungeonName = "dungeonName";

    @Property(
            type = PropertyType.SWITCH,
            name = "Discord RPC",
            category = "Discord", subcategory = "Discord RPC",
            description = "Shows status in discord."
    )
    public static boolean DRPC = true;

    public Config() {
        super(new File("./config/simplemod.toml"));
        initialize();
    }
}
