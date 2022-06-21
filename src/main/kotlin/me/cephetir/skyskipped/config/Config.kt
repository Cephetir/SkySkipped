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
import gg.essential.elementa.utils.withAlpha
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import gg.essential.vigilance.data.SortingBehavior
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.features.impl.discordrpc.RPC
import me.cephetir.skyskipped.features.impl.macro.RemoteControlling
import me.cephetir.skyskipped.gui.impl.GuiItemSwap
import net.minecraft.client.Minecraft
import java.awt.Color
import java.io.File

class Config : Vigilant(File(this.modDir, "config.toml"), "SkySkipped", sortingBehavior = ConfigSorting()) {
    init {
        registerListener<Boolean>("DRPC") {
            Thread {
                try {
                    Thread.sleep(100L)
                    RPC.reset()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }.start()
        }

        registerListener<Boolean>("remoteControl") {
            Thread {
                try {
                    Thread.sleep(100L)
                    if (it) RemoteControlling.setup()
                    else RemoteControlling.stop()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }.start()
        }

        addDependency("playeresp", "esp")
        addDependency("starredmobsesp", "esp")
        addDependency("batsesp", "esp")
        addDependency("chromaMode", "esp")
        addDependency("espTracers", "esp")
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
        addDependency("customSbOutlineColorRainbow", "customSbOutline")

        addDependency("failsafeJump", "failSafe")
        addDependency("fastBreakNumber", "fastBreak")
        addDependency("failSafeDesyncTime", "failSafeDesync")
        addDependency("failSafeIslandDelay", "failSafeIsland")
        addDependency("failSafeJacobNumber", "failSafeJacob")
        addDependency("failSafeChangeYawRandom", "failSafeChangeYaw")
        addDependency("failSafeChangeYawSpeed", "failSafeChangeYaw")
        addDependency("failSafeBanWaveTimer", "failSafeBanWave")
        addDependency("failSafeBanWaveDisable", "failSafeBanWave")
        addDependency("failSafeInvConfigTime", "failSafeInvConfig")
        addDependency("blockList", "block")

        addDependency("petsBgBlur", "petsOverlay")
        addDependency("petsBorderColor", "petsOverlay")
        addDependency("petsBorderWidth", "petsOverlay")

        addDependency("trailParticle", "trail")
        addDependency("trailInterval", "trail")

        addDependency("coins", "coinsToggle")
        addDependency("mimicText", "mimic")

        addDependency("netherWartDesyncTime", "netherWartDesync")
        addDependency("netherWartJacobNumber", "netherWartJacob")
        addDependency("netherWartBanWaveCheckerDisable", "netherWartBanWaveChecker")
        addDependency("netherWartBanWaveCheckerTimer", "netherWartBanWaveChecker")
        addDependency("webhookUrl", "webhook")

        addDependency("farmingHudX", "farmingHud")
        addDependency("farmingHudY", "farmingHud")
        addDependency("farmingHudColor", "farmingHud")
        addDependency("farmingHudColorText", "farmingHud")

        setSubcategoryDescription("Hacks", "Item Swapper", "Set keybinds for Item Swapper in special gui \"/sm kb\"")

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

    private class ConfigSorting : SortingBehavior() {
        private val categories = listOf(
            "Dungeons",
            "Macro",
            "Failsafes (Legacy)",
            "Visual",
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
            name = "Tracers",
            category = "Dungeons",
            subcategory = "ESP",
            description = "Draw tracers on esp mobs."
        )
        var espTracers = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Unstuck Failsafe",
            category = "Failsafes (Legacy)",
            subcategory = "Unstuck",
            description = "Prevent stacking in blocks for Pizza and Cheeto Client."
        )
        var failSafe = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Jump When Stuck",
            category = "Failsafes (Legacy)",
            subcategory = "Unstuck",
            description = "Jump when stuck."
        )
        var failsafeJump = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Jacob Failsafe",
            category = "Failsafes (Legacy)",
            subcategory = "Jacob",
            description = "Stops Pizza and Cheeto Client's macros on jacob event start."
        )
        var failSafeJacob = false

        @Property(
            type = PropertyType.SLIDER,
            name = "Jacob Failsafe Stop At",
            category = "Failsafes (Legacy)",
            subcategory = "Jacob",
            description = "Amount of crops mined for Jacob failsafe to stop.",
            min = 100000,
            max = 1000000,
            increment = 1
        )
        var failSafeJacobNumber = 400000

        @Property(
            type = PropertyType.SWITCH,
            name = "Desync Failsafe",
            category = "Failsafes (Legacy)",
            subcategory = "Desync",
            description = "Stops Pizza and Cheeto Client's macros when hypixel decides to stop breaking crops."
        )
        var failSafeDesync = false

        @Property(
            type = PropertyType.SLIDER,
            name = "Desync Failsafe Timeout",
            category = "Failsafes (Legacy)",
            subcategory = "Desync",
            description = "Seconds to wait for failsafe to trigger.",
            min = 1,
            max = 20,
            increment = 1
        )
        var failSafeDesyncTime = 5

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Set Spawn",
            category = "Failsafes (Legacy)",
            subcategory = "Auto Set Spawn",
            description = "Automatically sets home on layer switch when Pizza's or Cheeto Client's macro enabled."
        )
        var failSafeSpawn = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Warp Back To Island Failsafe",
            category = "Failsafes (Legacy)",
            subcategory = "Auto Warp Back",
            description = "Automatically warps you to island."
        )
        var failSafeIsland = false

        @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Auto Warp Back Failsafe Delay",
            category = "Failsafes (Legacy)",
            subcategory = "Auto Warp Back",
            description = "Delay in seconds between warps. Set more if you have bad ping (1s ~ 100 ping, 5s ~ 300ping)",
            minF = 0.5f,
            maxF = 10f,
            decimalPlaces = 1
        )
        var failSafeIslandDelay = 4f

        @Property(
            type = PropertyType.SWITCH,
            name = "Full Inventory Failsafe",
            category = "Failsafes (Legacy)",
            subcategory = "Inventory",
            description = "Inventory cleaning when full."
        )
        var failSafeInv = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Rotation",
            category = "Failsafes (Legacy)",
            subcategory = "Auto Rotation",
            description = "Rotates you 180 deg on Y change for vertical farms."
        )
        var failSafeChangeYaw = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Auto Rotation Randomness",
            category = "Failsafes (Legacy)",
            subcategory = "Auto Rotation",
            description = "Will make rotation yaw random from -2.5 to 2.5."
        )
        var failSafeChangeYawRandom = true

        @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Auto Rotation Speed",
            category = "Failsafes (Legacy)",
            subcategory = "Auto Rotation",
            description = "Speed in seconds rotation takes.",
            minF = 0.3f,
            maxF = 3f,
            decimalPlaces = 1
        )
        var failSafeChangeYawSpeed = 1.5f

        @Property(
            type = PropertyType.SWITCH,
            name = "Ban Wave Checker",
            category = "Failsafes (Legacy)",
            subcategory = "Ban Wave",
            description = "Checks if there's a ban wave happens right now."
        )
        var failSafeBanWave = false

        @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Ban Wave Checker Timer",
            category = "Failsafes (Legacy)",
            subcategory = "Ban Wave",
            description = "Delay in minutes between banwave checks.",
            minF = 0.1f,
            maxF = 30f,
            decimalPlaces = 1
        )
        var failSafeBanWaveTimer = 5f

        @Property(
            type = PropertyType.SWITCH,
            name = "Ban Wave Auto Macro Disable",
            category = "Failsafes (Legacy)",
            subcategory = "Ban Wave",
            description = "Disable macro when ban wave happens."
        )
        var failSafeBanWaveDisable = false

        @Property(
            type = PropertyType.SWITCH,
            name = "Configurable Inventory Failsafe",
            category = "Failsafes (Legacy)",
            subcategory = "Inventory",
            description = "Inventory cleaning on specific crops amount."
        )
        var failSafeInvConfig = false

        @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Inventory Failsafe Timer",
            category = "Failsafes (Legacy)",
            subcategory = "Inventory",
            description = "Time in minutes between inventory cleaning.",
            minF = 30f,
            maxF = 360f,
            decimalPlaces = 1
        )
        var failSafeInvConfigTime = 120f

        @Property(
            type = PropertyType.SLIDER,
            name = "Global Extra Delay for Failsafes",
            category = "Failsafes (Legacy)",
            description = "Time in ms between actions (Use only if high ping!).",
            min = 0,
            max = 10000,
            increment = 10
        )
        var failSafeGlobalTime = 0

        @Property(
            type = PropertyType.SWITCH,
            name = "Force Macro Mode",
            category = "Failsafes (Legacy)",
            description = "Forcely enable all failsafes.\n! WARNING: It will brake most failsafes!"
        )
        var failSafeForce = false

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
            name = "Ability list",
            category = "Hacks",
            subcategory = "Block ability",
            description = "List of items to block ability. Split with \", \"."
        )
        var blockList = ""

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

        @Property(
            type = PropertyType.SWITCH,
            name = "Advanced Custom Names",
            category = "Misc",
            description = "Redraws text in all menus and guis.\nCan make performance issues!"
        )
        var advancedCustomNames = false

        @Property(
            type = PropertyType.SELECTOR,
            name = "Macro Type",
            category = "Macro",
            description = "Choose macro for keybind.",
            options = ["Nether Wart (SShaped)", "Sugar Cane (Normal and SShaped)"]
        )
        var macroType = 0

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
        var farmingHudColorText = Color.RED.darker()
    }
}