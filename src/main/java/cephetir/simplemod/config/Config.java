package cephetir.simplemod.config;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;

import java.io.File;

public class Config extends Vigilant {
    @Property(
            type = PropertyType.SWITCH,
            name = "ChestCloser",
            category = "Dungeons", subcategory = "Dungeons",
            description = "Chests in dungeon will close automatically."
    )
    public static boolean chestCloser = false;

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

    public Config() {
        super(new File("./config/simplemod.toml"));
        initialize();
    }
}
