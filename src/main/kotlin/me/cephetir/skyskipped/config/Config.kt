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
import me.cephetir.skyskipped.features.impl.discordrpc.RPC
import java.awt.Color
import java.io.File

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

        addDependency("espColor", "playerESP")
        addDependency("presentsColor", "presents")

        addDependency("autoGBMode", "autoGB")
        addDependency("betterPerspectiveItems", "betterPerspective")

        addDependency("customSbText", "customSb")
        addDependency("customSbLobby", "customSb")
        addDependency("customSbBlurT", "customSb")
        addDependency("customSbBlur", "customSbBlurT")
        addDependency("customSbOutline", "customSb")
        addDependency("customSbOutlineColor", "customSbOutline")

        addDependency("failsafeJump", "failSafe")
        addDependency("blockList", "block")

        addDependency("armorFirst", "armorSwap")
        addDependency("armorSecond", "armorSwap")

        addDependency("petsBgBlur", "petsOverlay")
        addDependency("petsBorderColor", "petsOverlay")
        addDependency("petsBorderWidth", "petsOverlay")

        addDependency("coins", "coinsToggle")
        addDependency("mimicText", "mimic")

        initialize()
    }

    companion object {
        @Property(
            type = PropertyType.SWITCH,
            name = "Chest Closer",
            category = "Dungeons",
            subcategory = "Chest Closer",
            description = "Auto close chests in dungeons."
        )
        var chestCloser = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Chest Closer in Crystal Hollows",
            category = "Dungeons",
            subcategory = "Chest Closer",
            description = "Auto close chests in Crystal Hollows."
        )
        var chestCloserCH = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Party chat swapper",
            category = "Chat",
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
            name = "Stuck Falisafe",
            category = "Hacks",
            subcategory = "Failsafes",
            description = "Unstuck for Pizza and Cheeto Clients."
        )
        var failSafe = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Jump When Stuck",
            category = "Hacks",
            subcategory = "Failsafes",
            description = "Jump when stuck."
        )
        var failsafeJump = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Jacob Falisafe",
            category = "Hacks",
            subcategory = "Failsafes",
            description = "Stop Pizza and Cheeto Clients' macros on jacob event start."
        )
        var failSafeJacob = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Block ability",
            category = "Hacks",
            description = "Blocks item ability."
        )
        var block = false

        @Property(
            type = PropertyType.TEXT,
            name = "Ability list",
            category = "Hacks",
            description = "List of items to block ability. Split with \", \"."
        )
        var blockList = ""

        @Property(
            type = PropertyType.SWITCH,
            name = "Name ping",
            category = "Chat",
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
            name = "Mimic Killed Message On Mimic Death",
            category = "Dungeons",
            subcategory = "Ping",
            description = "Send mimic text on it's death."
        )
        var mimic = false

        @Property(
            type = PropertyType.TEXT,
            name = "Mimic Message Text",
            category = "Dungeons",
            subcategory = "Ping",
            description = "Text for mimic message."
        )
        var mimicText = "Mimic Killed!"

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
            name = "Hide Pet's Candies",
            category = "Visual",
            description = "Hide pet's candies counter in tooltip."
        )
        var hidePetCandies = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Pets Overlay",
            category = "Visual",
            subcategory = "Pets Overlay",
            description = "Good-looking overlay for pets menu.\n§cDon't use with small window size"
        )
        var petsOverlay = false

        @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Pets Overlay Background Blur Strength",
            category = "Visual",
            subcategory = "Pets Overlay",
            description = "Strength for pets overlay background blur.",
            minF = 5f,
            maxF = 25f,
            decimalPlaces = 1
        )
        var petsBgBlur = 12.5f

        @Property(
            type = PropertyType.COLOR,
            name = "Pets Overlay Border Color",
            category = "Visual",
            subcategory = "Pets Overlay",
            description = "Color for pets overlay border."
        )
        var petsBorderColor: Color = Color(87, 0, 247, 255)

        @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Pets Overlay Border Width",
            category = "Visual",
            subcategory = "Pets Overlay",
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
            subcategory = "Highlight Presents",
            description = "Highlights presents in Jerry Workshop."
        )
        var presents = false

        @Property(
            type = PropertyType.COLOR,
            name = "Highlight Presents Color",
            category = "Visual",
            subcategory = "Highlight Presents",
            description = "Color for presents highlight."
        )
        var presentsColor: Color = Color.GREEN

        @Property(
            type = PropertyType.SWITCH,
            name = "Terminals Display",
            category = "Dungeons",
            subcategory = "Visual",
            description = "Display called terminals in player's nametag."
        )
        var terms = true

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Ghost Block",
            category = "Dungeons",
            subcategory = "Auto Ghost Block",
            description = "Automatically make ghost block on stairs, upside down stairs, skulls, etc."
        )
        var autoGB = false

        @Property(
            type = PropertyType.SELECTOR,
            name = "Auto Ghost Block Mode",
            category = "Dungeons",
            subcategory = "Auto Ghost Block",
            description = "Automatically make ghost block when you're sneaking on stairs, upside down stairs, skulls, etc.",
            options = ["On sneak", "On key"]
        )
        var autoGBMode = 0

        @Property(
            type = PropertyType.SWITCH,
            name = "Perspective Toggle",
            category = "Visual",
            subcategory = "Perspective Toggle",
            description = "Activates 3rd perspective on key."
        )
        var betterPerspective = true

        @Property(
            type = PropertyType.TEXT,
            name = "Perspective Item",
            category = "Visual",
            subcategory = "Perspective Toggle",
            description = "On what item Perspective Toggle will work. Split with \", \".\nLeave blank for toggle to work with any item."
        )
        var betterPerspectiveItems = ""

        @Property(
            type = PropertyType.SWITCH,
            name = "Custom Scoreboard",
            category = "Visual",
            subcategory = "Scoreboard",
            description = "Draw custom scoreboard."
        )
        var customSb = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Hide Server Number",
            category = "Visual",
            subcategory = "Scoreboard",
            description = "Hide server number from scoreboard."
        )
        var customSbLobby = true

        @Property(
            type = PropertyType.SWITCH,
            name = "Blur Scoreboard Background",
            category = "Visual",
            subcategory = "Scoreboard",
            description = "Blur the background of scoreboard."
        )
        var customSbBlurT = false

        @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Custom Scoreboard Background Blur Strength",
            category = "Visual",
            subcategory = "Scoreboard",
            description = "Strength for scoreboard background blur.",
            minF = 5f,
            maxF = 25f,
            decimalPlaces = 1
        )
        var customSbBlur = 20f

        @Property(
            type = PropertyType.SWITCH,
            name = "Draw Scoreboard Outline",
            category = "Visual",
            subcategory = "Scoreboard",
            description = "Draw scoreboard outline."
        )
        var customSbOutline = false

        @Property(
            type = PropertyType.COLOR,
            name = "Scoreboard Outline Color",
            category = "Visual",
            subcategory = "Scoreboard",
            description = "Color for scoreboard outline."
        )
        var customSbOutlineColor: Color = Color(87, 0, 247, 255)

        @Property(
            type = PropertyType.TEXT,
            name = "Custom Scoreboard Text",
            category = "Visual",
            subcategory = "Scoreboard",
            description = "Name text to display on scoreboard\nFor color codes use \"&\"."
        )
        var customSbText = "SkySkipped"

        @Property(
            type = PropertyType.SWITCH,
            name = "Remove Red Ugly Numbers",
            category = "Visual",
            subcategory = "Scoreboard",
            description = "Remove red ugly numbers."
        )
        var customSbNumbers = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Open Maddox Phone",
            category = "Slayers",
            description = "Clicks on Batphone and in chat on slayer kill."
        )
        var autoMaddox = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Delight Locked Gemstone Slots in AH",
            category = "Visual",
            description = "Make items with locked gemstone slots dark in ah."
        )
        var highlightSlots = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Enable Item Swap Keybind",
            category = "Hacks",
            subcategory = "Item Swap",
            description = "Keybind will only work if this enabled."
        )
        var armorSwap = false

        @Property(
            type = PropertyType.TEXT,
            name = "First Item to Swap",
            category = "Hacks",
            subcategory = "Item Swap",
            description = "First item that will be swapped."
        )
        var armorFirst = ""

        @Property(
            type = PropertyType.TEXT,
            name = "Second Item to Swap",
            category = "Hacks",
            subcategory = "Item Swap",
            description = "Second item that will be swapped."
        )
        var armorSecond = ""
    }
}