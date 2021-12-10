/*
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * Version 2, December 2004
 *
 * Copyright (C) 2021 Cephetir
 *
 * Everyone is permitted to copy and distribute verbatim or modified
 * copies of this license document, and changing it is allowed as long
 * as the name is changed.
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  0. You just DO WHAT THE FUCK YOU WANT TO.
 */

package me.cephetir.skyskipped;

import me.cephetir.skyskipped.commands.SkySkippedCommand;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.event.Listener;
import me.cephetir.skyskipped.features.Features;
import me.cephetir.skyskipped.features.impl.discordrpc.RPC;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = SkySkipped.MODID, name = SkySkipped.MOD_NAME, version = SkySkipped.VERSION, acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class SkySkipped {
    public static final String MODID = "skyskipped";
    public static final String MOD_NAME = "SkySkipped";
    public static final String VERSION = "2.5";
    public static Config config = new Config();
    public static final Features features = new Features();

    private static Logger logger = LogManager.getLogger("SkySkipped");

    @SuppressWarnings("unused")
    @Mod.Instance(MODID)
    public static SkySkipped Instance;

    @Mod.EventHandler()
    public void onPreInit(FMLPreInitializationEvent event) {
        SkySkipped.logger.info("Starting SkySkipped...");
        config.preload();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(RPC.getINSTANCE());
        MinecraftForge.EVENT_BUS.register(new Listener());
        features.register();
        RPC.getINSTANCE().init();
        ClientCommandHandler.instance.registerCommand(new SkySkippedCommand());
        ClientCommandHandler.instance.registerCommand(features.getLeaveCommand());
        ClientCommandHandler.instance.registerCommand(features.getPartyCommand());
    }

    @Mod.EventHandler
    public void stop(FMLModDisabledEvent event) {
        RPC.getINSTANCE().shutdown();
    }

    public static Logger getLogger() {
        if(logger == null) logger = LogManager.getLogger("SkySkipped");
        return logger;
    }
}