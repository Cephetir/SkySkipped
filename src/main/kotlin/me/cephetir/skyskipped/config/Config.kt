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

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.features.impl.discordrpc.RPC
import me.cephetir.skyskipped.gui.impl.GuiItemSwap
import net.minecraft.client.Minecraft
import java.awt.Color
import java.io.File

class Config : Vigilant(File(this.modDir, "config.toml"), "SkySkipped") {
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

        addDependency("playeresp", "esp")
        addDependency("starredmobsesp", "esp")
        addDependency("batsesp", "esp")
        addDependency("chromaMode", "esp")
        addDependency("playersespColor", "playeresp")
        addDependency("mobsespColor", "starredmobsesp")
        addDependency("batsespColor", "batsesp")

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
        addDependency("fastBreakNumber", "fastBreak")
        addDependency("failSafeDesyncTime", "failSafeDesync")
        addDependency("failSafeDesyncMode", "failSafeDesync")
        addDependency("failSafeIslandDelay", "failSafeIsland")
        addDependency("blockList", "block")

        addDependency("petsBgBlur", "petsOverlay")
        addDependency("petsBorderColor", "petsOverlay")
        addDependency("petsBorderWidth", "petsOverlay")

        addDependency("coins", "coinsToggle")
        addDependency("mimicText", "mimic")

        initialize()
    }

    private val gson = Gson()
    private val file = File(modDir, "keybinds.json")
    fun loadKeybinds() {
        try {
            if (!file.exists()) {
                file.parentFile.mkdirs()
                file.createNewFile()
            }

            file.reader().use { reader ->
                SkySkipped.keybinds.clear()
                val data = gson.fromJson(reader, JsonElement::class.java) as JsonArray
                data.mapTo(SkySkipped.keybinds) {
                    it as JsonObject
                    GuiItemSwap.Keybind(
                        it["message"].asString,
                        it["keyCode"].asInt,
                        GuiItemSwap.Modifiers.fromBitfield(it["modifiers"].asInt)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                this.file.writer().use { gson.toJson(JsonArray(), it) }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        SkySkipped.logger.info("Loaded keybinds!")
    }

    fun saveKeybinds() {
        try {
            if (!file.exists()) {
                file.parentFile.mkdirs()
                file.createNewFile()
            }

            file.writer().use { writer ->
                val arr = JsonArray()
                for (s in SkySkipped.keybinds) {
                    val obj = JsonObject()
                    obj.addProperty("message", s.message)
                    obj.addProperty("keyCode", s.keyCode)
                    obj.addProperty("modifiers", s.modifiers)
                    arr.add(obj)
                }
                gson.toJson(arr, writer)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        SkySkipped.logger.info("Saved keybinds!")
    }

    companion object {
        @JvmStatic
        val modDir = File(File(Minecraft.getMinecraft().mcDataDir, "config"), "skyskipped")

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
            description = "Shows status in discord."
        )
        var DRPC = true

        @Property(
            type = PropertyType.SWITCH,
            name = "ESP",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Shows mobs through walls."
        )
        var esp = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Player ESP",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Shows players through walls."
        )
        var playeresp = false

        @Property(
            type = PropertyType.COLOR,
            name = "ESP Players Color",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Box color for ESP."
        )
        var playersespColor: Color = Color.PINK

        @Property(
            type = PropertyType.SWITCH,
            name = "Starred Mobs ESP",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Shows starred mobs through walls."
        )
        var starredmobsesp = false

        @Property(
            type = PropertyType.COLOR,
            name = "ESP Starred mobs Color",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Box color for ESP."
        )
        var mobsespColor: Color = Color.ORANGE

        @Property(
            type = PropertyType.SWITCH,
            name = "Bats ESP",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Shows bats through walls."
        )
        var batsesp = false

        @Property(
            type = PropertyType.COLOR,
            name = "ESP Bats Color",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Box color for ESP."
        )
        var batsespColor: Color = Color.GREEN

        @Property(
            type = PropertyType.SWITCH,
            name = "Rainbow ESP Color",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Make all ESP colors rainbow."
        )
        var chromaMode = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Unstuck Failsafe",
            category = "Hacks",
            subcategory = "Failsafes",
            description = "Prevent stucking in blocks for Pizza and Cheeto Client."
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
            name = "Jacob Failsafe",
            category = "Hacks",
            subcategory = "Failsafes",
            description = "Stops Pizza and Cheeto Client's macros on jacob event start."
        )
        var failSafeJacob = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Desync Failsafe",
            category = "Hacks",
            subcategory = "Failsafes",
            description = "Stops Pizza and Cheeto Client's macros when hypixel decides to stop breaking crops."
        )
        var failSafeDesync = false

        @Property(
            type = PropertyType.SLIDER,
            name = "Desync Failsafe Timeout",
            category = "Hacks",
            subcategory = "Failsafes",
            description = "Seconds to wait for failsafe to trigger.",
            min = 5,
            max = 240,
            increment = 1
        )
        var failSafeDesyncTime = 40

        @Property(
            type = PropertyType.SELECTOR,
            name = "Desync Failsafe Mode",
            category = "Hacks",
            subcategory = "Failsafes",
            description = "Mode for desync failsafe.",
            options = ["Hoe counter (old)", "Block (new)"]
        )
        var failSafeDesyncMode = 0

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Set Spawn",
            category = "Hacks",
            subcategory = "Failsafes",
            description = "Automatically sets home on layer switch when Pizza's or Cheeto Client's macro enabled."
        )
        var failSafeSpawn = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Warp Back To Island Failsafe",
            category = "Hacks",
            subcategory = "Failsafes",
            description = "Automatically warps you to island."
        )
        var failSafeIsland = false

        @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Auto Warp Back Failsafe Delay",
            category = "Hacks",
            subcategory = "Failsafes",
            description = "Delay in seconds between warps. Set more if you have bad ping (1s ~ 100 ping, 5s ~ 300ping)",
            minF = 0.5f,
            maxF = 10f,
            decimalPlaces = 1
        )
        var failSafeIslandDelay = 4f

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
            type = PropertyType.SLIDER,
            name = "Item Swap Delay",
            category = "Hacks",
            description = "Delay between items swapping.",
            increment = 10,
            max = 1000,
            min = 10
        )
        var swapDelay = 100

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
            type = PropertyType.SLIDER,
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
            name = "Super Secret Money Exploit!",
            category = "Super Secret Settings",
            description = "§kMAKES YOUR PURSE BLOW UP WITH BILLIONS OF COINS"
        )
        var coinsToggle = false

        @Property(
            type = PropertyType.SLIDER,
            name = "Amount of Coins",
            category = "Super Secret Settings",
            description = "Amount of Coins to add in purse",
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
            name = "Fast Break",
            category = "Hacks",
            subcategory = "Fast Break",
            description = "Break extra blocks behind."
        )
        var fastBreak = false

        @Property(
            type = PropertyType.NUMBER,
            name = "Fast Break Block Number",
            category = "Hacks",
            subcategory = "Fast Break",
            description = "How many extra blocks to break.",
            min = 0,
            max = 3,
            increment = 1
        )
        var fastBreakNumber = 3

        @Property(
            type = PropertyType.SWITCH,
            name = "Stop fly",
            category = "Misc",
            description = "Stop flying on private island."
        )
        var stopFly = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Cookie Clicker",
            category = "Misc",
            description = "Auto clicks in cookie clicker."
        )
        var cookieClicker = false
    }
}