/*
 * SkySkipped - Hypixel Skyblock QOL mod
 * Copyright (C) 2023  Cephetir
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.cephetir.skyskipped.config

import com.google.gson.*
import gg.essential.elementa.utils.withAlpha
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import gg.essential.vigilance.data.SortingBehavior
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.features.impl.discordrpc.RPC
import me.cephetir.skyskipped.features.impl.hacks.HotbarSaver
import me.cephetir.skyskipped.features.impl.macro.MacroManager
import me.cephetir.skyskipped.features.impl.macro.RemoteControlling
import me.cephetir.skyskipped.gui.impl.GuiItemSwap
import me.cephetir.skyskipped.utils.mc
import java.awt.Color
import java.io.File
import kotlin.reflect.jvm.javaField

class Config(configFile: File = File(modDir, "config.toml")) : Vigilant(configFile, "SkySkipped", sortingBehavior = ConfigSorting()) {
    init {
        if (!configFile.exists()) {
            configFile.parentFile.mkdirs()
            configFile.createNewFile()
        }

        registerListener<Boolean>(::DRPC.javaField!!) {
            RPC.reset(it)
        }

        registerListener<Boolean>(::remoteControl.javaField!!) {
            if (it) RemoteControlling.setup()
            else RemoteControlling.stop()
        }

        registerListener<Int>(::macroType.javaField!!) {
            MacroManager.current = MacroManager.macros[it]
        }


        addDependency("espMode", "esp")
        addDependency("playeresp", "esp")
        addDependency("starredmobsesp", "esp")
        addDependency("batsesp", "esp")
        addDependency("keyesp", "esp")
        addDependency("playersespColor", "playeresp")
        addDependency("playerespChroma", "playeresp")
        addDependency("mobsespColor", "starredmobsesp")
        addDependency("starredmobsespChroma", "starredmobsesp")
        addDependency("batsespColor", "batsesp")
        addDependency("batsespChroma", "batsesp")
        addDependency("keyespColor", "keyesp")
        addDependency("keyespChroma", "keyesp")
        addDependency("customespColor", "customesp")
        addDependency("customespChroma", "customesp")
        addDependency("customespText", "customesp")

        addDependency("drpcDetail", "DRPC")
        addDependency("drpcState", "DRPC")
        addDependency("drpcText", "DRPC")
        addDependency("drpcText2", "DRPC")

        addDependency("presentsColor", "presents")

        addDependency("autoGBMode", "autoGB")
        addDependency("betterPerspectiveItems", "betterPerspective")
        addDependency("betterPerspectiveMode", "betterPerspective")

        addDependency("customSbText", "customSb")
        addDependency("customSbLobby", "customSb")
        addDependency("customSbBlurT", "customSb")
        addDependency("customSbBg", "customSb")
        addDependency("customSbBgColor", "customSbBg")
        addDependency("customSbShadow", "customSb")
        addDependency("customSbShadowStr", "customSbShadow")
        addDependency("customSbBlur", "customSbBlurT")
        addDependency("customSbOutline", "customSb")
        addDependency("customSbOutlineColor", "customSbOutline")
        addDependency("customSbOutlineColorRainbow", "customSbOutline")

        addDependency("blockList", "block")
        addDependency("blockZombieSword", "block")

        addDependency("petsBgBlur", "petsOverlay")
        addDependency("petsBorderColor", "petsOverlay")
        addDependency("petsBorderWidth", "petsOverlay")

        addDependency("trailParticle", "trail")
        addDependency("trailInterval", "trail")

        addDependency("coins", "coinsToggle")
        addDependency("mimicText", "mimic")

        addDependency("customPitch", "customPitchToggle")
        addDependency("customYaw", "customYawToggle")
        addDependency("netherWartDesyncTime", "netherWartDesync")
        addDependency("netherWartJacobNumber", "netherWartJacob")
        addDependency("netherWartBanWaveCheckerDisable", "netherWartBanWaveChecker")
        addDependency("netherWartBanWaveCheckerTimer", "netherWartBanWaveChecker")
        addDependency("webhookUrl", "webhook")

        addDependency("farmingHudX", "farmingHud")
        addDependency("farmingHudY", "farmingHud")
        addDependency("farmingHudColor", "farmingHud")
        addDependency("farmingHudColorText", "farmingHud")

        addDependency("stopBreakingList", "stopBreaking")
        addDependency("autoReplyGuild", "autoReply")
        addDependency("fpsSpoofNumber", "fpsSpoof")

        setSubcategoryDescription("Hacks", "Item Swapper", "Set keybinds for Item Swapper in special gui \"/sm kb\"")
        setSubcategoryDescription("Hacks", "Hotbar Swapper", "Set hotbar presets for Hotbar Swapper using command \"/sm hb\"")

        initialize()
    }

    private val gson = Gson()
    private val keybindsFile = File(modDir, "keybinds.json")
    private val hotbarFile = File(modDir, "hotbars.json")

    fun loadKeybinds() {
        try {
            if (!keybindsFile.exists()) {
                keybindsFile.parentFile.mkdirs()
                keybindsFile.createNewFile()
            }

            keybindsFile.reader().use { reader ->
                SkySkipped.keybinds.clear()
                val `data` = (gson.fromJson(reader, JsonElement::class.java) ?: return@use) as JsonArray
                `data`.mapTo(SkySkipped.keybinds) {
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
                this.keybindsFile.writer().use { gson.toJson(JsonArray(), it) }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        SkySkipped.logger.info("Loaded keybinds!")
    }

    fun saveKeybinds() {
        try {
            if (!keybindsFile.exists()) {
                keybindsFile.parentFile.mkdirs()
                keybindsFile.createNewFile()
            }

            keybindsFile.writer().use { writer ->
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

    fun loadHotbars() {
        try {
            if (!hotbarFile.exists()) {
                hotbarFile.parentFile.mkdirs()
                hotbarFile.createNewFile()
            }

            hotbarFile.reader().use { reader ->
                HotbarSaver.presets.clear()
                val `data` = (gson.fromJson(reader, JsonElement::class.java) ?: return@use) as JsonArray
                `data`.mapTo(HotbarSaver.presets) {
                    it as JsonObject
                    HotbarSaver.HotbarPreset(
                        it["name"].asString,
                        it["items"].asJsonArray.map { element -> element.asString },
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                this.hotbarFile.writer().use { gson.toJson(JsonArray(), it) }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        SkySkipped.logger.info("Loaded hotbars!")
    }

    fun saveHotbars() {
        try {
            if (!hotbarFile.exists()) {
                hotbarFile.parentFile.mkdirs()
                hotbarFile.createNewFile()
            }

            hotbarFile.writer().use { writer ->
                val arr = JsonArray()
                for (s in HotbarSaver.presets) {
                    val obj = JsonObject()
                    obj.addProperty("name", s.name)
                    val array = JsonArray()
                    s.items.forEach { array.add(JsonPrimitive(it)) }
                    obj.add("items", array)
                    arr.add(obj)
                }
                gson.toJson(arr, writer)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        SkySkipped.logger.info("Saved hotbars!")
    }

    fun loadScripts() {
        try {
            if (!macroScriptsFolder.exists())
                macroScriptsFolder.mkdirs()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private class ConfigSorting : SortingBehavior() {
        private val categories = listOf(
            "Dungeons",
            "Macro",
            "Failsafes (Legacy)",
            "Visual",
            "Movement",
            "Hacks",
            "Chat",
            "Slayers",
            "Discord",
            "Super Secret Settings",
            "Misc",
        )

        override fun getCategoryComparator(): Comparator<in Category> =
            Comparator.comparingInt { category: Category -> categories.indexOf(category.name) }
    }

    companion object {
        val modDir = File(File(mc.mcDataDir, "config"), "skyskipped")
        val macroScriptsFolder = File(modDir, "scripts")

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
            type = PropertyType.SELECTOR,
            name = "RPC First Line",
            category = "Discord",
            description = "Shows status in discord.",
            options = ["Location", "Username", "Server", "Item in hand", "Custom Text"]
        )
        var drpcDetail = 2

        @Property(
            type = PropertyType.SELECTOR,
            name = "RPC Second Line",
            category = "Discord",
            description = "Shows status in discord.",
            options = ["Location", "Username", "Server", "Item in hand", "Custom Text"]
        )
        var drpcState = 3

        @Property(
            type = PropertyType.TEXT,
            name = "RPC Custom Text First Line",
            category = "Discord",
            description = "Shows status in discord."
        )
        var drpcText = ""

        @Property(
            type = PropertyType.TEXT,
            name = "RPC Custom Text Second Line",
            category = "Discord",
            description = "Shows status in discord."
        )
        var drpcText2 = ""

        @Property(
            type = PropertyType.SWITCH,
            name = "ESP",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Shows mobs through walls."
        )
        var esp = false

        @Property(
            type = PropertyType.SELECTOR,
            name = "ESP Mode",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Type of esp.",
            options = ["Outline", "Box", "Chams"]
        )
        var espMode = 0

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
            name = "Player ESP Color",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Color for ESP."
        )
        var playersespColor: Color = Color.PINK

        @Property(
            type = PropertyType.SWITCH,
            name = "Player ESP Chroma Color",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Chroma color for ESP."
        )
        var playerespChroma = false

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
            name = "Starred mobs ESP Color",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Color for ESP."
        )
        var mobsespColor: Color = Color.ORANGE

        @Property(
            type = PropertyType.SWITCH,
            name = "Starred mobs ESP Chroma Color",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Chroma color for ESP."
        )
        var starredmobsespChroma = false

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
            name = "Bats ESP Color",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Color for ESP."
        )
        var batsespColor: Color = Color.GREEN

        @Property(
            type = PropertyType.SWITCH,
            name = "Bats ESP Chroma Color",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Chroma color for ESP."
        )
        var batsespChroma = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Key ESP",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Shows keys through walls."
        )
        var keyesp = false

        @Property(
            type = PropertyType.COLOR,
            name = "Key ESP Color",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Color for ESP."
        )
        var keyespColor: Color = Color.RED

        @Property(
            type = PropertyType.SWITCH,
            name = "Key ESP Chroma Color",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Chroma color for ESP."
        )
        var keyespChroma = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Custom ESP",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Shows custom mobs through walls."
        )
        var customesp = false

        @Property(
            type = PropertyType.TEXT,
            name = "Custom ESP Mobs",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Enter nametag name above mob.\nSplit with \", \""
        )
        var customespText = "Enderman, Zombie"

        @Property(
            type = PropertyType.COLOR,
            name = "Custom ESP Color",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Color for ESP."
        )
        var customespColor: Color = Color.BLUE

        @Property(
            type = PropertyType.SWITCH,
            name = "Custom ESP Chroma Color",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Chroma color for ESP."
        )
        var customespChroma = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Terminal ESP",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Shows terminals on f7 boss fight through walls."
        )
        var terminalEsp = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Wither Door ESP",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Shows wither doors though walls in dungeons."
        )
        var witherDoorEsp = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Block ability",
            category = "Hacks",
            subcategory = "Block ability",
            description = "Blocks item ability."
        )
        var block = false

        @Property(
            type = PropertyType.TEXT,
            name = "Item list",
            category = "Hacks",
            subcategory = "Block ability",
            description = "List of items to block ability. Split with \", \"."
        )
        var blockList = ""

        @Property(
            type = PropertyType.SWITCH,
            name = "Block Useless Zombie Sword Charges",
            category = "Hacks",
            subcategory = "Block ability",
            description = "Block Useless Zombie Sword Charges."
        )
        var blockZombieSword = false

        @Property(
            type = PropertyType.SLIDER,
            name = "Item Swap Delay",
            category = "Hacks",
            subcategory = "Item Swapper",
            description = "Delay between items swapping.",
            increment = 10,
            max = 1000,
            min = 10
        )
        var swapDelay = 100

        @Property(
            type = PropertyType.SLIDER,
            name = "Jerry Box Open Delay",
            category = "Hacks",
            subcategory = "Jerry Box Opener",
            description = "Delay between jerry box openning.",
            increment = 10,
            max = 1000,
            min = 100
        )
        var boxDelay = 500

        @Property(
            type = PropertyType.SLIDER,
            name = "Hotbar Swap Delay",
            category = "Hacks",
            subcategory = "Hotbar Swapper",
            description = "Delay between items swapping.",
            increment = 10,
            max = 1000,
            min = 10
        )
        var hotbarSwapDelay = 100

        @Property(
            type = PropertyType.SWITCH,
            name = "Lava Fishing ESP",
            category = "Hacks",
            description = "Shows lava fising spots through walls."
        )
        var lavaFishingEsp = false

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
            max = 10_000_000_000.toInt(),
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
            name = "Admin Room Detection",
            category = "Dungeons",
            description = "Scans dungeon for admin room."
        )
        var adminRoom = false


        @Property(
            type = PropertyType.SWITCH,
            name = "Perspective Toggle",
            category = "Visual",
            subcategory = "Perspective Toggle",
            description = "Activates 3rd perspective on key."
        )
        var betterPerspective = true

        @Property(
            type = PropertyType.SELECTOR,
            name = "Perspective Toggle Mode",
            category = "Visual",
            subcategory = "Perspective Toggle",
            description = "Mode for perspective toggle.",
            options = ["Hold", "Toggle"]
        )
        var betterPerspectiveMode = 0

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
            name = "Scoreboard Background",
            category = "Visual",
            subcategory = "Scoreboard",
            description = "Scoreboard background."
        )
        var customSbBg = true

        @Property(
            type = PropertyType.COLOR,
            name = "Scoreboard Background Color",
            category = "Visual",
            subcategory = "Scoreboard",
            description = "Color for scoreboard background."
        )
        var customSbBgColor: Color = Color(0, 0, 0, 110)

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
            type = PropertyType.SWITCH,
            name = "Scoreboard Shadow",
            category = "Visual",
            subcategory = "Scoreboard",
            description = "Scoreboard Shadow."
        )
        var customSbShadow = true

        @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Scoreboard Shadow Strength",
            category = "Visual",
            subcategory = "Scoreboard",
            description = "Strength for scoreboard shadow.",
            minF = 5f,
            maxF = 25f,
            decimalPlaces = 1
        )
        var customSbShadowStr = 15f

        @Property(
            type = PropertyType.SWITCH,
            name = "Scoreboard Outline Rainbow Color",
            category = "Visual",
            subcategory = "Scoreboard",
            description = "Rainbow color for scoreboard outline."
        )
        var customSbOutlineColorRainbow = false

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
            name = "Trail",
            category = "Visual",
            subcategory = "Trail",
            description = "Render trail behind player when moving."
        )
        var trail = false

        @Property(
            type = PropertyType.TEXT,
            name = "Trail Particles",
            category = "Visual",
            subcategory = "Trail",
            description = "What particles will be rendered\nGet names from https://www.spigotmc.org/wiki/particle-list-1-8-8/"
        )
        var trailParticle = "DRIP_LAVA"

        @Property(
            type = PropertyType.SLIDER,
            name = "Trail Interval",
            category = "Visual",
            subcategory = "Trail",
            description = "Interval between particles in ms.",
            min = 0,
            max = 5000,
            increment = 10
        )
        var trailInterval = 100

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
            category = "Movement",
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

        @Property(
            type = PropertyType.SELECTOR,
            name = "Macro Type",
            category = "Macro",
            description = "Choose macro for keybind.",
            options = ["Nether Wart (SShaped)", "Sugar Cane (Normal and SShaped)"]
        )
        var macroType = 0

        @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Schedule Disable",
            category = "Macro",
            description = "Hours before macro will be auto disabled (0 for unlimited time).",
            minF = 0f,
            maxF = 12f,
            decimalPlaces = 1
        )
        var macroStopTime = 0f

        @Property(
            type = PropertyType.SWITCH,
            name = "Custom Pitch Toggle",
            category = "Macro",
            description = "Override default pitch."
        )
        var customYawToggle = false

        @Property(
            type = PropertyType.TEXT,
            name = "Custom Yaw Value",
            category = "Macro",
            description = "Override default yaw."
        )
        var customYaw = "0"

        @Property(
            type = PropertyType.SWITCH,
            name = "Custom Pitch Toggle",
            category = "Macro",
            description = "Override default pitch."
        )
        var customPitchToggle = false

        @Property(
            type = PropertyType.TEXT,
            name = "Custom Pitch Value",
            category = "Macro",
            description = "Override default pitch."
        )
        var customPitch = "0"

        @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Rotation Difference",
            category = "Macro",
            description = "Rotation difference needed for auto rotation rotate your head back.",
            minF = 0f,
            maxF = 50f,
            decimalPlaces = 1
        )
        var rotationDiff = 0.2f

        @Property(
            type = PropertyType.NUMBER,
            name = "Auto Pick Slot With Hoe",
            category = "Macro",
            description = "Auto picks slot when macro is started.",
            min = 1,
            max = 9,
            increment = 1
        )
        var autoPickSlot = 1

        //        @Property(
//            type = PropertyType.SWITCH,
//            name = "Macro Randomization",
//            category = "Macro",
//            description = "Randomize certain actions to look more legit (may fix packet thottle and desync)."
//        )
        var macroRandomization = true

        @Property(
            type = PropertyType.TEXT,
            name = "Custom Macro Script Name",
            category = "Macro",
            description = "Name of custom macro script which will be ran during macro.\nAll scripts should be placed in \"config/skyskipped/scripts\"."
        )
        var macroScript = "example.txt"

        @Property(
            type = PropertyType.SWITCH,
            name = "Lagback Fix",
            category = "Macro",
            description = "Enable this if youre experiencing lagbacks."
        )
        var macroLagbackFix = false

        @Property(
            type = PropertyType.SELECTOR,
            name = "Farm Direction",
            category = "Macro",
            subcategory = "Nether Wart Macro",
            description = "Set direction of your eyes (check it with f3).",
            options = ["North", "East", "West", "South"]
        )
        var netherWartDirection = 0

        @Property(
            type = PropertyType.SELECTOR,
            name = "Farm Type",
            category = "Macro",
            subcategory = "Nether Wart Macro",
            description = "Type of your farm.",
            options = ["Horizontal", "Vertical", "Ladders", "Dropdown"]
        )
        var netherWartType = 0

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Set Spawn",
            category = "Macro",
            subcategory = "Nether Wart Macro",
            description = "Set spawn on row switch."
        )
        var netherWartSetSpawn = true

        @Property(
            type = PropertyType.SWITCH,
            name = "Unstuck Failsafe",
            category = "Macro",
            subcategory = "Nether Wart Macro",
            description = "Prevent stacking in blocks."
        )
        var netherWartStuck = true

        @Property(
            type = PropertyType.SWITCH,
            name = "Desync Failsafe",
            category = "Macro",
            subcategory = "Nether Wart Macro",
            description = "Stops macro when hypixel decides to stop breaking crops."
        )
        var netherWartDesync = true

        @Property(
            type = PropertyType.SLIDER,
            name = "Desync Failsafe Timeout",
            category = "Macro",
            subcategory = "Nether Wart Macro",
            description = "Seconds to wait for failsafe to trigger.",
            min = 1,
            max = 20,
            increment = 1
        )
        var netherWartDesyncTime = 5

        @Property(
            type = PropertyType.SWITCH,
            name = "Jacob Failsafe",
            category = "Macro",
            subcategory = "Nether Wart Macro",
            description = "Stops macro on Jacob Event start."
        )
        var netherWartJacob = true

        @Property(
            type = PropertyType.SLIDER,
            name = "Jacob Failsafe Stop At",
            category = "Macro",
            subcategory = "Nether Wart Macro",
            description = "Amount of crops mined during Jacob after which macro will stop.",
            min = 0,
            max = 1000000,
            increment = 1000
        )
        var netherWartJacobNumber = 400000

        @Property(
            type = PropertyType.SWITCH,
            name = "Full Inventory Failsafe",
            category = "Macro",
            subcategory = "Nether Wart Macro",
            description = "Clears inventory if it fills up."
        )
        var netherWartFullInv = true

        @Property(
            type = PropertyType.SWITCH,
            name = "Ban Wave Checker",
            category = "Macro",
            subcategory = "Nether Wart Macro",
            description = "Checks if there's a ban wave happens right now."
        )
        var netherWartBanWaveChecker = true

        @Property(
            type = PropertyType.SWITCH,
            name = "Ban Wave Auto Macro Disable",
            category = "Macro",
            subcategory = "Nether Wart Macro",
            description = "Disable macro when ban wave happens."
        )
        var netherWartBanWaveCheckerDisable = true

        @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Ban Wave Checker Timer",
            category = "Macro",
            subcategory = "Nether Wart Macro",
            description = "Delay in minutes between ban wave checks.",
            minF = 0.1f,
            maxF = 30f,
            decimalPlaces = 1
        )
        var netherWartBanWaveCheckerTimer = 5f

        @Property(
            type = PropertyType.SWITCH,
            name = "CPU Saver",
            category = "Macro",
            subcategory = "Nether Wart Macro",
            description = "Limits cpu usage while macroing."
        )
        var netherWartCpuSaver = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Reconnect",
            category = "Macro",
            subcategory = "Nether Wart Macro",
            description = "Auto reconnects to server after getting disconnected."
        )
        var netherWartReconnect = false

        @Property(
            type = PropertyType.SELECTOR,
            name = "Farm Direction (Only SShaped Type)",
            category = "Macro",
            subcategory = "Sugar Cane Macro",
            description = "Set direction of your eyes (check it with f3).",
            options = ["North", "East", "West", "South"]
        )
        var sugarCaneDirection = 0

        @Property(
            type = PropertyType.SELECTOR,
            name = "Farm Direction (Only Normal Type)",
            category = "Macro",
            subcategory = "Sugar Cane Macro",
            description = "Set yaw direction (check it with f3).",
            options = ["45", "-45"]
        )
        var sugarCaneDirectionNormal = 0

        @Property(
            type = PropertyType.SELECTOR,
            name = "Farm Type",
            category = "Macro",
            subcategory = "Sugar Cane Macro",
            description = "Type of your farm.",
            options = ["Normal", "SShaped", "SShaped Dropdown", "SShaped Ladders"]
        )
        var sugarCaneType = 0

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Set Spawn",
            category = "Macro",
            subcategory = "Sugar Cane Macro",
            description = "Set spawn on row switch."
        )
        var sugarCaneSetSpawn = true

        @Property(
            type = PropertyType.SWITCH,
            name = "Unstuck Failsafe",
            category = "Macro",
            subcategory = "Sugar Cane Macro",
            description = "Prevent stacking in blocks."
        )
        var sugarCaneStuck = true

        @Property(
            type = PropertyType.SWITCH,
            name = "Desync Failsafe",
            category = "Macro",
            subcategory = "Sugar Cane Macro",
            description = "Stops macro when hypixel decides to stop breaking crops."
        )
        var sugarCaneDesync = true

        @Property(
            type = PropertyType.SLIDER,
            name = "Desync Failsafe Timeout",
            category = "Macro",
            subcategory = "Sugar Cane Macro",
            description = "Seconds to wait for failsafe to trigger.",
            min = 1,
            max = 20,
            increment = 1
        )
        var sugarCaneDesyncTime = 5

        @Property(
            type = PropertyType.SWITCH,
            name = "Jacob Failsafe",
            category = "Macro",
            subcategory = "Sugar Cane Macro",
            description = "Stops macro on Jacob Event start."
        )
        var sugarCaneJacob = true

        @Property(
            type = PropertyType.SLIDER,
            name = "Jacob Failsafe Stop At",
            category = "Macro",
            subcategory = "Sugar Cane Macro",
            description = "Amount of crops mined during Jacob after which macro will stop.",
            min = 0,
            max = 1000000,
            increment = 1000
        )
        var sugarCaneJacobNumber = 400000

        @Property(
            type = PropertyType.SWITCH,
            name = "Full Inventory Failsafe",
            category = "Macro",
            subcategory = "Sugar Cane Macro",
            description = "Clears inventory if it fills up."
        )
        var sugarCaneFullInv = true

        @Property(
            type = PropertyType.SWITCH,
            name = "Ban Wave Checker",
            category = "Macro",
            subcategory = "Sugar Cane Macro",
            description = "Checks if there's a ban wave happens right now."
        )
        var sugarCaneBanWaveChecker = true

        @Property(
            type = PropertyType.SWITCH,
            name = "Ban Wave Auto Macro Disable",
            category = "Macro",
            subcategory = "Sugar Cane Macro",
            description = "Disable macro when ban wave happens."
        )
        var sugarCaneBanWaveCheckerDisable = true

        @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Ban Wave Checker Timer",
            category = "Macro",
            subcategory = "Sugar Cane Macro",
            description = "Delay in minutes between ban wave checks.",
            minF = 0.1f,
            maxF = 30f,
            decimalPlaces = 1
        )
        var sugarCaneBanWaveCheckerTimer = 5f

        @Property(
            type = PropertyType.SWITCH,
            name = "CPU Saver",
            category = "Macro",
            subcategory = "Sugar Cane Macro",
            description = "Limits cpu usage while macroing."
        )
        var sugarCaneCpuSaver = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Reconnect",
            category = "Macro",
            subcategory = "Sugar Cane Macro",
            description = "Auto reconnects to server after getting disconnected."
        )
        var sugarCaneReconnect = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Webhook Notifications",
            category = "Macro",
            subcategory = "Notifications",
            description = "Send different notifications to webhook about something happening."
        )
        var webhook = false

        @Property(
            type = PropertyType.TEXT,
            name = "Webhook URL",
            category = "Macro",
            subcategory = "Notifications",
            description = "Webhook URL for notifications."
        )
        var webhookUrl = ""

        @Property(
            type = PropertyType.SWITCH,
            name = "Desktop Notifications",
            category = "Macro",
            subcategory = "Notifications",
            description = "Sends notifications just like webhook."
        )
        var desktopNotifications = true

        @Property(
            type = PropertyType.SWITCH,
            name = "Remote Macro Controlling",
            category = "Macro",
            subcategory = "Remote Controlling",
            description = "Control macro from anywhere with discord bot."
        )
        var remoteControl = false

        @Property(
            type = PropertyType.TEXT,
            name = "Bot's Token",
            category = "Macro",
            subcategory = "Remote Controlling",
            description = "Bot's token.\nRead tutorial on discord server."
        )
        var remoteControlUrl = ""

        @Property(
            type = PropertyType.SWITCH,
            name = "Farming HUD",
            category = "Macro",
            subcategory = "Farming HUD",
            description = "Render huds with some useful information."
        )
        var farmingHud = false

        @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Farming HUD Position X",
            category = "Macro",
            subcategory = "Farming HUD",
            description = "Edit position with \"/sm hud\".",
            minF = 0f,
            maxF = 10000f,
            decimalPlaces = 1
        )
        var farmingHudX = 0f

        @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Farming HUD Position Y",
            category = "Macro",
            subcategory = "Farming HUD",
            description = "Edit position with \"/sm hud\".",
            minF = 0f,
            maxF = 10000f,
            decimalPlaces = 1
        )
        var farmingHudY = 0f

        @Property(
            type = PropertyType.COLOR,
            name = "Farming HUD Background Color",
            category = "Macro",
            subcategory = "Farming HUD",
            description = "Background color of Farming HUD."
        )
        var farmingHudColor = Color.BLACK.withAlpha(110)

        @Property(
            type = PropertyType.COLOR,
            name = "Farming HUD Text Color",
            category = "Macro",
            subcategory = "Farming HUD",
            description = "Text color of Farming HUD."
        )
        var farmingHudColorText: Color = Color.RED.darker()

        @Property(
            type = PropertyType.SWITCH,
            name = "Gyro Radius",
            category = "Dungeons",
            subcategory = "Item Radius",
            description = "Renders a circle while holding Gyrokinetic Wand where mobs will be pulled in."
        )
        var gyroRadius = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Hyperion Radius",
            category = "Dungeons",
            subcategory = "Item Radius",
            description = "Renders a circle Hyperion dmg area."
        )
        var hypRadius = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Dungeons Only",
            category = "Dungeons",
            subcategory = "Item Radius",
            description = "Enable radiuses only in dungeons."
        )
        var onlyDungeonRadius = true

        @Property(
            type = PropertyType.SWITCH,
            name = "In Third Person Only",
            category = "Dungeons",
            subcategory = "Item Radius",
            description = "Enable radiuses only in third person view."
        )
        var onlyThirdPersonRadius = false

        @Property(
            type = PropertyType.COLOR,
            name = "Item Radius Color",
            category = "Dungeons",
            subcategory = "Item Radius",
            description = "Color for item radius."
        )
        var radiusColor: Color = Color.GREEN

        @Property(
            type = PropertyType.SWITCH,
            name = "Item Radius Chroma Color",
            category = "Dungeons",
            subcategory = "Item Radius",
            description = "Chroma color for item radius."
        )
        var radiusColorChroma = true

        @Property(
            type = PropertyType.SWITCH,
            name = "Zero Ping Gui",
            category = "Misc",
            description = "Use GUIs without lags."
        )
        var zeroPingGui = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Better Cocoa Beans Sizes",
            category = "Hacks",
            description = "Make hitbox of cocoa beans bigger and smaller if not grown."
        )
        var beansSize = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Keep Focus",
            category = "Hacks",
            description = "Always keep minecraft window in focus."
        )
        var keepFocus = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Anti Escrow AH",
            category = "Misc",
            subcategory = "Anti Escrow",
            description = "Reopens ah if escrow happens."
        )
        var antiEscrowAh = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Anti Escrow AH Bin",
            category = "Misc",
            subcategory = "Anti Escrow",
            description = "Reopens ah after you buy bin."
        )
        var antiEscrowAhBin = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Anti Escrow BZ",
            category = "Misc",
            subcategory = "Anti Escrow",
            description = "Reopens bz if escrow happens."
        )
        var antiEscrowBz = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Salvage",
            category = "Hacks",
            description = "Fixed version of auto salvage that works with both dungeon and lava fishing gear."
        )
        var autoSalvage = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Chat Search",
            category = "Chat",
            description = "Search text in chat by pressing Ctrl + F."
        )
        var chatSearch = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Container Search",
            category = "Misc",
            description = "Search items in containers by pressing Ctrl + F."
        )
        var containerSearch = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Stop Breaking Blocks",
            category = "Hacks",
            subcategory = "Stop Breaking",
            description = "Stops you from breaking certain blocks list."
        )
        var stopBreaking = false

        @Property(
            type = PropertyType.TEXT,
            name = "Stop Breaking Blocks List",
            category = "Hacks",
            subcategory = "Stop Breaking",
            description = "List of blocks to stop breaking."
        )
        var stopBreakingList = "dirt"

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Reply",
            category = "Chat",
            description = "Auto replys to \"wc\" and \"gg\" with \"\"wc\" - someonesIgn\"."
        )
        var autoReply = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Reply Guild Only",
            category = "Chat",
            description = "Auto Reply will work only in guild chat."
        )
        var autoReplyGuild = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Fps Spoofer",
            category = "Misc",
            subcategory = "Fps Spoofer",
            description = "Big numbers are funni."
        )
        var fpsSpoof = false

        @Property(
            type = PropertyType.SLIDER,
            name = "Fps Spoofer Number",
            category = "Misc",
            subcategory = "Fps Spoofer",
            description = "Big numbers are funni.",
            max = Int.MAX_VALUE,
            min = 0,
            increment = 10
        )
        var fpsSpoofNumber = 69420

        @Property(
            type = PropertyType.SWITCH,
            name = "M3 Professor Fire Freeze Timer",
            category = "Dungeons",
            description = "Timer until fire freeze use in M3 boss fight."
        )
        var fireFreezePing = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Going in Portal",
            category = "Dungeons",
            description = "Automatically sends \"going\" message in chat when entering portal."
        )
        var autoGo = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Remove Carpet Bounds",
            category = "Hacks",
            description = "Removes carpet hitboxes."
        )
        var removeCarpets = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Redirect Middle Clicks",
            category = "Misc",
            description = "Redirect middle clicks to left clicks to not get kicked to lobby.\nWorks with other mods!"
        )
        var redirectClicks = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Shiny Blocks Esp",
            category = "Hacks",
            subcategory = "Shiny Blocks",
            description = "Shiny Blocks Esp in the End"
        )
        var shinyBlocksEsp = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Shiny Blocks Aura",
            category = "Hacks",
            subcategory = "Shiny Blocks",
            description = "Auto mine Shiny Blocks in the End"
        )
        var shinyBlocksAura = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Optimize NEU's equipment overlay",
            category = "Misc",
            description = "Disable NEU's useless code which lags game hard."
        )
        var neuOptimize = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Stop Rendering Falling Blocks",
            category = "Misc",
            description = "Improve fps by not rendering useless dungeon shit."
        )
        var stopFallingBlocks = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Auction Buyer",
            category = "Misc",
            subcategory = "Auction Buyer",
            description = "Instantly buys auction and skips confirmation gui."
        )
        var auctionBuyer = false
    }
}