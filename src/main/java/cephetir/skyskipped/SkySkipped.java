package cephetir.skyskipped;

import cephetir.skyskipped.Features.ChatSwapper;
import cephetir.skyskipped.Features.ChestCloser;
import cephetir.skyskipped.Features.PlayerESP;
import cephetir.skyskipped.commands.SkySkippedCommand;
import cephetir.skyskipped.config.Config;
import cephetir.skyskipped.discordrpc.Client;
import cephetir.skyskipped.listeners.InDungeon;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = SkySkipped.MODID, name = SkySkipped.MOD_NAME, version = SkySkipped.VERSION, acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class SkySkipped {
    public static final String MODID = "skyskipped";
    public static final String MOD_NAME = "SkySkipped";
    public static final String VERSION = "1.4";
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static Config config = new Config();

    @Mod.Instance(MODID)
    public static SkySkipped Instance;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        Client.getINSTANCE().init();
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config.preload();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new InDungeon());
        MinecraftForge.EVENT_BUS.register(new ChatSwapper());
        MinecraftForge.EVENT_BUS.register(new ChestCloser());
        MinecraftForge.EVENT_BUS.register(new PlayerESP());
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new SkySkippedCommand());
    }

    @Mod.EventHandler
    public void stop(FMLModDisabledEvent event) {
        Client.getINSTANCE().shutdown();
    }
}