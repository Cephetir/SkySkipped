package cephetir.simplemod;

import cephetir.simplemod.Features.ChatSwapper;
import cephetir.simplemod.Features.ChestCloser;
import cephetir.simplemod.commands.SimpleModCommand;
import cephetir.simplemod.config.Config;
import cephetir.simplemod.discordrpc.Client;
import cephetir.simplemod.listeners.InDungeon;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = SimpleMod.MODID, name = SimpleMod.MOD_NAME, version = SimpleMod.VERSION, acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class SimpleMod {
    public static final String MODID = "simplemod";
    public static final String MOD_NAME = "SimpleMod";
    public static final String VERSION = "1.3";
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static Config config = new Config();

    @Mod.Instance(MODID)
    public static SimpleMod Instance;

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
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new SimpleModCommand());
    }

    @Mod.EventHandler
    public void stop(FMLModDisabledEvent event) {
        Client.getINSTANCE().shutdown();
    }
}