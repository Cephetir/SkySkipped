package mynameisjeff.templatemod;


import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = TemplateMod.MODID, name = TemplateMod.MOD_NAME, version = TemplateMod.VERSION, acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class TemplateMod {
    public static final String MODID = "templatemod";
    public static final String MOD_NAME = "TemplateMod";
    public static final String VERSION = "0.0.1";
    public static final Minecraft mc = Minecraft.getMinecraft();

}