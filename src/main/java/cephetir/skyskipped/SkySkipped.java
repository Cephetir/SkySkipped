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

package cephetir.skyskipped;

import cephetir.skyskipped.Features.Features;
import cephetir.skyskipped.Features.impl.discordrpc.RPC;
import cephetir.skyskipped.commands.SkySkippedCommand;
import cephetir.skyskipped.config.Config;
import cephetir.skyskipped.listeners.Status;
import cephetir.skyskipped.utils.PacketUtils;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

@Mod(modid = SkySkipped.MODID, name = SkySkipped.MOD_NAME, version = SkySkipped.VERSION, acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class SkySkipped {
    public static final String MODID = "skyskipped";
    public static final String MOD_NAME = "SkySkipped";
    public static final String VERSION = "2.3";
    public static Config config = new Config();
    public static final Features features = new Features();

    @SuppressWarnings("unused")
    @Mod.Instance(MODID)
    public static SkySkipped Instance;

    @Mod.EventHandler()
    public void onPreInit(FMLPreInitializationEvent event) {
        System.out.println("Starting SkySkipped...");
        config.preload();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(RPC.getINSTANCE());
        MinecraftForge.EVENT_BUS.register(new Status());
        MinecraftForge.EVENT_BUS.register(new PacketUtils());
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

    @SubscribeEvent
    public void onServerConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        event.manager.channel().pipeline().addBefore("packet_handler", "danker_packet_handler", new PacketUtils());
        System.out.println("Added packet handler to channel pipeline.");
    }
}