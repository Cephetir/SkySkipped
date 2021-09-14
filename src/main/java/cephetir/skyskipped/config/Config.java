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

package cephetir.skyskipped.config;

import cephetir.skyskipped.Features.impl.discordrpc.RPC;
import cephetir.skyskipped.SkySkipped;
import cephetir.skyskipped.gui.hud.ScreenPosition;
import cephetir.skyskipped.gui.hud.impl.FPSHud;
import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;

import java.io.File;
import java.util.function.Consumer;

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
            category = "Chat", subcategory = "Chat",
            description = "Automatically swaps between party chat and global chat."
    )
    public static boolean chatSwapper = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "[NON] Rank",
            category = "Chat", subcategory = "Chat",
            description = "Adds the [NON] rank, given to people without a rank."
    )
    public static boolean nons = false;

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
            name = "Block GS ability",
            category = "Slayers", subcategory = "Slayers",
            description = "Blocks Giant's sword ability."
    )
    public static boolean gsBlock = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Name ping",
            category = "Chat", subcategory = "Chat",
            description = "Plays sound when someone says your name in chat."
    )
    public static boolean ping = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Auto Leave Dungeon",
            category = "Dungeons", subcategory = "Dungeons",
            description = "Runs /leavedungeon command after run ends."
    )
    public static boolean EndLeave = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Auto Party FragBot when Dungeon ends",
            category = "Dungeons", subcategory = "Dungeons",
            description = "Runs /fragrun command after run ends."
    )
    public static boolean EndParty = false;

    @Property(
            type = PropertyType.TEXT,
            name = "FragBot Name",
            category = "Dungeons", subcategory = "Dungeons",
            description = "FragBot IGN."
    )
    public static String BotName = "";

    @Property(
            type = PropertyType.SWITCH,
            name = "FPS",
            category = "HUD", subcategory = "HUD",
            description = "Shows your fps."
    )
    public static boolean fpsHud = false;

    public static final FPSHud fpsHudClass = new FPSHud();
    @Property(
            type = PropertyType.NUMBER,
            name = "FPS Pos X",
            category = "HUD", subcategory = "HUD",
            description = "Where on screen hud will be."
    )
    public static int fpsPosX = 1;

    private long timer = 0;
    @Property(
            type = PropertyType.NUMBER,
            name = "FPS Pos Y",
            category = "HUD", subcategory = "HUD",
            description = "Where on screen hud will be."
    )
    public static int fpsPosY = 1;

    public Config() {
        super(new File("./config/skyskipped.toml"));
        registerListener("DRPC", (Consumer<Boolean>) aBoolean -> {
            if (aBoolean && (!RPC.getINSTANCE().getDiscordRPCManager().isActive())) {
                if (System.currentTimeMillis() - timer < 4000) return;
                timer = System.currentTimeMillis();
                RPC.getINSTANCE().getDiscordRPCManager().start();
            } else if (!aBoolean && RPC.getINSTANCE().getDiscordRPCManager().isActive()) {
                RPC.getINSTANCE().getDiscordRPCManager().stop();
            }
        });
        registerListener("fpsHud", (Consumer<Boolean>) aBoolean -> {
            if (aBoolean) {
                fpsHudClass.save(ScreenPosition.fromAbsolute(fpsPosX, fpsPosY));
                SkySkipped.hudManager.register(fpsHudClass);
            } else {
                SkySkipped.hudManager.unregister(fpsHudClass);
            }
        });
        initialize();
    }
}
