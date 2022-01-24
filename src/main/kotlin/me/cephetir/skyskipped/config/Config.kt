/*
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * Version 2, December 2004
 *
 * Copyright (C) 2022 Cephetir
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

package me.cephetir.skyskipped.config

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.features.impl.discordrpc.RPC
import java.awt.Color
import java.io.File
import java.lang.reflect.Field

class Config : Vigilant(File("./config/skyskipped.toml"), "SkySkipped") {
    init {
        registerListener<Any>(
            "DRPC"
        ) {
            Thread {
                try {
                    Thread.sleep(100L)
                    RPC.reset()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }.start()
        }

        val list: MutableList<Field> = javaClass.fields.toMutableList()
        list.remove(javaClass.getField("DRPC"))
        list.forEach { registerListener<Any>(it) { SkySkipped.features.register() } }

        addDependency("espColor", "playerESP")
        addDependency("presentsColor", "presents")

        addDependency("petsBg", "petsOverlay")
        addDependency("petsBorderColor", "petsOverlay")
        addDependency("petsBorderWidth", "petsOverlay")

        addDependency("coins", "coinsToggle")
        addDependency("pingText", "scorePing")

        initialize()
    }

    companion object {
        @Property(
            type = PropertyType.SWITCH,
            name = "Chest Closer",
            category = "Dungeons",
            subcategory = "Chest Closer",
            description = "Chests in dungeon will close automatically."
        )
        var chestCloser = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Chest Closer in Crystal Hollows",
            category = "Dungeons",
            subcategory = "Chest Closer",
            description = "Chests in Crystal Hollows will close automatically."
        )
        var chestCloserCH = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Party chat swapper",
            category = "Chat",
            subcategory = "Chat",
            description = "Automatically swaps between party chat and global chat."
        )
        var chatSwapper = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Discord RPC",
            category = "Discord",
            subcategory = "Discord RPC",
            description = "Shows status in discord."
        )
        var DRPC = true

        @Property(
            type = PropertyType.SWITCH,
            name = "Player ESP",
            category = "Dungeons",
            subcategory = "Player ESP",
            description = "Shows players through walls."
        )
        var playerESP = false

        @Property(
            type = PropertyType.COLOR,
            name = "Player ESP Color",
            category = "Dungeons",
            subcategory = "Player ESP",
            description = "Outline color for Player ESP."
        )
        var espColor: Color = Color.GREEN

        @Property(
            type = PropertyType.SWITCH,
            name = "Pizza Fail Safe",
            category = "Hacks",
            subcategory = "Hacks",
            description = "Failsafe macros in Pizza client."
        )
        var failSafe = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Jump When Stuck",
            category = "Hacks",
            subcategory = "Hacks",
            description = "Jump in fail safe."
        )
        var failsafeJump = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Block GS ability",
            category = "Hacks",
            subcategory = "Hacks",
            description = "Blocks Giant's sword ability."
        )
        var gsBlock = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Name ping",
            category = "Chat",
            subcategory = "Chat",
            description = "Plays sound when someone says your name in chat."
        )
        var ping = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Leave Dungeon",
            category = "Dungeons",
            subcategory = "Auto Leave",
            description = "Runs /leavedungeon command after run ends."
        )
        var EndLeave = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Party FragBot when Dungeon ends",
            category = "Dungeons",
            subcategory = "Auto Leave",
            description = "Runs /fragrun command after run ends."
        )
        var EndParty = false

        @Property(
            type = PropertyType.TEXT,
            name = "FragBot Name",
            category = "Dungeons",
            subcategory = "Auto Leave",
            description = "FragBot IGN."
        )
        var BotName = ""

        @Property(
            type = PropertyType.NUMBER,
            name = "Delay For \"Leave Dungeon\"",
            category = "Dungeons",
            subcategory = "Auto Leave",
            description = "Delay between going to lobby and to dungeon hub.",
            increment = 10,
            max = 10000
        )
        var delay = 2000

        @Property(
            type = PropertyType.SWITCH,
            name = "§4!!VERY SECRET MONEY EXPLOIT!!",
            category = "SUPER SECRETS SETTINGS §4!(!DO NOT OPEN!)!",
            subcategory = "SUPER SECRETS SETTINGS §4!(!DO NOT ENABLE!)!",
            description = "§kMAKES YOUR PURSE BLOW UP WITH BILLIONS OF COINS"
        )
        var coinsToggle = false

        @Property(
            type = PropertyType.NUMBER,
            name = "AMOUNT OF COINS DO YOU WANT",
            category = "SUPER SECRETS SETTINGS §4!(!DO NOT OPEN!)!",
            subcategory = "§4SUPER SECRETS SETTINGS!",
            description = "§4AMOUNT OF COINS YOU WANT",
            max = Int.MAX_VALUE,
            increment = 10000000
        )
        var coins = 10000000

        @Property(
            type = PropertyType.SWITCH,
            name = "300 Score Ping",
            category = "Dungeons",
            subcategory = "Ping",
            description = "SBE like 300 score ping."
        )
        var scorePing = false

        @Property(
            type = PropertyType.TEXT,
            name = "300 Score Ping Text",
            category = "Dungeons",
            subcategory = "Ping",
            description = "Text to show when 300 score reached."
        )
        var pingText = "300 score reached! Btw sbe is cringe"

        @Property(
            type = PropertyType.SWITCH,
            name = "Rabbit hat Ping",
            category = "Dungeons",
            subcategory = "Ping",
            description = "Ping on Watcher cleared."
        )
        var rabbitPing = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Hide Pet Candies",
            category = "Visual",
            subcategory = "Visual",
            description = "Hide pet's candies counter in tooltip."
        )
        var hidePetCandies = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Pets Overlay",
            category = "Visual",
            subcategory = "Visual",
            description = "Nice looking overlay for pets menu.\n§cDon't use with small window size"
        )
        var petsOverlay = true

        @Property(
            type = PropertyType.COLOR,
            name = "Pets Overlay Background Color",
            category = "Visual",
            subcategory = "Visual",
            description = "Color for pets overlay background."
        )
        var petsBg: Color = Color(223, 223, 233, 155)

        @Property(
            type = PropertyType.COLOR,
            name = "Pets Overlay Border Color",
            category = "Visual",
            subcategory = "Visual",
            description = "Color for pets overlay border."
        )
        var petsBorderColor: Color = Color(87, 0, 247, 255)

        @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Pets Overlay Border Width",
            category = "Visual",
            subcategory = "Visual",
            description = "Width for pets overlay border.",
            minF = 1f,
            maxF = 6f,
            decimalPlaces = 1
        )
        var petsBorderWidth = 2f

        @Property(
            type = PropertyType.SWITCH,
            name = "Highlight Presents in Jerry Workshop",
            category = "Visual",
            subcategory = "Visual",
            description = "Highlights presents in Jerry Workshop."
        )
        var presents = true

        @Property(
            type = PropertyType.COLOR,
            name = "Highlight Presents Color",
            category = "Visual",
            subcategory = "Visual",
            description = "Color for presents highlight."
        )
        var presentsColor: Color = Color.GREEN
    }
}