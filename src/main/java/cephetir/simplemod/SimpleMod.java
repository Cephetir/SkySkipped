package cephetir.simplemod;


import cephetir.simplemod.Features.ChestCloser;
import cephetir.simplemod.commands.SimpleModCommand;
import cephetir.simplemod.core.Config;
import cephetir.simplemod.core.InDungeon;
import cephetir.simplemod.tweaker.ModCoreInstaller;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = SimpleMod.MODID, name = SimpleMod.MOD_NAME, version = SimpleMod.VERSION, acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class SimpleMod {

    public static final String MODID = "simplemod";
    public static final String MOD_NAME = "SimpleMod";
    public static final String VERSION = "1.0";
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static Config config = new Config();
    public static InDungeon Dungeon = new InDungeon();

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        ModCoreInstaller.initializeModCore(mc.mcDataDir);
        config.preload();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ChestCloser());
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new SimpleModCommand());
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        Dungeon.setIsInDungeon(false);
        Dungeon.inDungeon();
    }
}