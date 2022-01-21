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

package me.cephetir.skyskipped.config;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;
import me.cephetir.skyskipped.features.impl.discordrpc.RPC;

import java.awt.*;
import java.io.File;

public class Config extends Vigilant {

    @Property(
            type = PropertyType.SWITCH,
            name = "Chest Closer",
            category = "Dungeons", subcategory = "Chest Closer",
            description = "Chests in dungeon will close automatically."
    )
    public static boolean chestCloser = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Chest Closer in Crystal Hollows",
            category = "Dungeons", subcategory = "Chest Closer",
            description = "Chests in Crystal Hollows will close automatically."
    )
    public static boolean chestCloserCH = false;

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
    public static boolean DRPC = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Player ESP",
            category = "Dungeons", subcategory = "Player ESP",
            description = "Shows players through walls."
    )
    public static boolean playerESP = false;

    @Property(
            type = PropertyType.COLOR,
            name = "Player ESP Color",
            category = "Dungeons", subcategory = "Player ESP",
            description = "Outline color for Player ESP."
    )
    public static Color espColor = Color.GREEN;

    @Property(
            type = PropertyType.SWITCH,
            name = "Pizza Fail Safe",
            category = "Hacks", subcategory = "Hacks",
            description = "Failsafe macros in Pizza client."
    )
    public static boolean failSafe = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Jump When Stuck",
            category = "Hacks", subcategory = "Hacks",
            description = "Jump in fail safe."
    )
    public static boolean failsafeJump = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Block GS ability",
            category = "Hacks", subcategory = "Hacks",
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
            category = "Dungeons", subcategory = "Auto Leave",
            description = "Runs /leavedungeon command after run ends."
    )
    public static boolean EndLeave = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Auto Party FragBot when Dungeon ends",
            category = "Dungeons", subcategory = "Auto Leave",
            description = "Runs /fragrun command after run ends."
    )
    public static boolean EndParty = false;

    @Property(
            type = PropertyType.TEXT,
            name = "FragBot Name",
            category = "Dungeons", subcategory = "Auto Leave",
            description = "FragBot IGN."
    )
    public static String BotName = "";

    @Property(
            type = PropertyType.NUMBER,
            name = "Delay For \"Leave Dungeon\"",
            category = "Dungeons", subcategory = "Auto Leave",
            description = "Delay between going to lobby and to dungeon hub.",
            increment = 10,
            max = 10000
    )
    public static int delay = 2000;

    @Property(
            type = PropertyType.SWITCH,
            name = "§4!!VERY SECRET MONEY EXPLOIT!!",
            category = "SUPER SECRETS SETTINGS §4!(!DO NOT OPEN!)!", subcategory = "SUPER SECRETS SETTINGS §4!(!DO NOT ENABLE!)!",
            description = "§kMAKES YOUR PURSE BLOW UP WITH BILLIONS OF COINS"
    )
    public static boolean coinsToggle = false;

    @Property(
            type = PropertyType.NUMBER,
            name = "AMOUNT OF COINS DO YOU WANT",
            category = "SUPER SECRETS SETTINGS §4!(!DO NOT OPEN!)!", subcategory = "§4SUPER SECRETS SETTINGS!",
            description = "§4AMOUNT OF COINS YOU WANT",
            max = Integer.MAX_VALUE,
            increment = 10000000
    )
    public static int coins = 10000000;

    @Property(
            type = PropertyType.SWITCH,
            name = "300 Score Ping",
            category = "Visual", subcategory = "Visual",
            description = "SBE like 300 score ping."
    )
    public static boolean scorePing = false;

    @Property(
            type = PropertyType.TEXT,
            name = "300 Score Ping Text",
            category = "Visual", subcategory = "Visual",
            description = "Text to show when 300 score reached."
    )
    public static String pingText = "300 score reached! Btw sbe is cringe";

    @Property(
            type = PropertyType.SWITCH,
            name = "Rabbit hat Ping",
            category = "Visual", subcategory = "Visual",
            description = "Ping on Watcher cleared."
    )
    public static boolean rabbitPing = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide Pet Candies",
            category = "Visual", subcategory = "Visual",
            description = "Hide pet's candies counter in tooltip."
    )
    public static boolean hidePetCandies = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Pets Overlay",
            category = "Visual", subcategory = "Visual",
            description = "Good looking overlay for pets menu.\n§cDon't use with small window size"
    )
    public static boolean petsOverlay = true;

    public Config() {
        super(new File("./config/skyskipped.toml"), "SkySkipped");

        registerListener("DRPC", aBoolean -> new Thread(() -> {
            try {
                Thread.sleep(100L);
                RPC.reset();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start());

        addDependency("espColor", "playerESP");
        addDependency("coins", "coinsToggle");
        addDependency("pingText", "scorePing");

        initialize();
    }
}