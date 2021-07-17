package cephetir.simplemod;

import cephetir.simplemod.Features.ChatSwapper;
import cephetir.simplemod.commands.SimpleModCommand;
import cephetir.simplemod.config.Config;
import cephetir.simplemod.listeners.InDungeon;
import cephetir.simplemod.utils.CommandQueue;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

@Mod(modid = SimpleMod.MODID, name = SimpleMod.MOD_NAME, version = SimpleMod.VERSION, acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class SimpleMod {
    public static final String MODID = "simplemod";
    public static final String MOD_NAME = "SimpleMod";
    public static final String VERSION = "1.2";
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static Config config = new Config();

    @Mod.Instance(MODID)
    public static SimpleMod Instance;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config.preload();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new InDungeon());
        MinecraftForge.EVENT_BUS.register(new ChatSwapper());
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new SimpleModCommand());
    }

    public CommandQueue getCommandQueue() {
        return new CommandQueue();
    }
}