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
import me.cephetir.bladecore.config.settings.SettingManager
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.features.impl.discordrpc.RPC
import me.cephetir.skyskipped.features.impl.hacks.HotbarSaver
import me.cephetir.skyskipped.features.impl.macro.MacroManager
import me.cephetir.skyskipped.features.impl.macro.RemoteControlling
import me.cephetir.skyskipped.gui.impl.GuiItemSwap
import me.cephetir.skyskipped.utils.mc
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import java.io.File


object Config {
    val modDir = File(File(mc.mcDataDir, "config"), "skyskipped")
    val macroScriptsFolder = File(modDir, "scripts")
    private val gson = Gson()
    private val keybindsFile = File(modDir, "keybinds.json")
    private val hotbarFile = File(modDir, "hotbars.json")
    private val configFile = File(modDir, "config.json")
    val sm = SettingManager(configFile, "SkySkipped", ConfigSorting())

    fun load() {
        if (!configFile.exists()) {
            configFile.parentFile.mkdirs()
            configFile.createNewFile()
        }
        sm.loadConfig()
    }

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

    private class ConfigSorting : SettingManager.CategorySorting() {
        private val categories = listOf(
            "Dungeons",
            "Macro",
            "Failsafes (Legacy)",
            "Visual",
            "Optimization",
            "Movement",
            "Hacks",
            "Chat",
            "Slayers",
            "Discord",
            "Super Secret Settings",
            "Misc",
        )

        override fun getCategoryComparator(): Comparator<in SettingManager.Category> =
            Comparator.comparingInt { categories.indexOf(it.name) }
    }

    @JvmField
    val chestCloser = sm.booleanSetting("Chest Closer") {
        description = "Auto close chests in dungeons"
        category = "Dungeons"
        subCategory = "Chest Closer"
    }

    @JvmField
    val chatSwapper = sm.booleanSetting("Party chat swapper") {
        description = "Automatically swaps between party chat and global chat"
        category = "Chat"
    }

    @JvmField
    val DRPC = sm.booleanSetting("Discord RPC") {
        description = "Shows SkySkipped status in discord"
        category = "Discord"
        value = true
        listener = { RPC.reset(it) }
    }

    @JvmField
    val drpcDetail = sm.selectorSetting("RPC First Line") {
        category = "Discord"
        options = arrayOf("Location", "Username", "Server", "Item in hand", "Custom Text")
    }

    @JvmField
    val drpcState = sm.selectorSetting("RPC Second Line") {
        category = "Discord"
        options = arrayOf("Location", "Username", "Server", "Item in hand", "Custom Text")
        value = 3
    }

    @JvmField
    val drpcText = sm.textSetting("RPC Custom Text First Line") {
        category = "Discord"
        isHidden = { drpcDetail.value != 4 }
    }

    @JvmField
    val drpcText2 = sm.textSetting("RPC Custom Text Second Line") {
        category = "Discord"
        isHidden = { drpcState.value != 4 }
    }

    @JvmField
    val esp = sm.booleanSetting("ESP") {
        description = "Shows mobs through walls"
        category = "Dungeons"
        subCategory = "ESP"
    }

    @JvmField
    val espMode = sm.selectorSetting("ESP Mode") {
        description = "Type of esp"
        category = "Dungeons"
        subCategory = "ESP"
        options = arrayOf("Outline", "Box", "Chams")
    }

    @JvmField
    val playeresp = sm.booleanSetting("Player ESP") {
        description = "Shows players through walls"
        category = "Dungeons"
        subCategory = "ESP"
        isHidden = { !esp.value }
    }

    @JvmField
    val playerespR = sm.numberSetting("Player ESP Color Red") {
        description = "Red color for Player ESP"
        category = "Dungeons"
        subCategory = "ESP"
        value = 255.0
        min = 0.0
        max = 255.0
        isHidden = { !esp.value || !playeresp.value || playerespChroma.value }
    }

    @JvmField
    val playerespG = sm.numberSetting("Player ESP Color Green") {
        description = "Green color for Player ESP"
        category = "Dungeons"
        subCategory = "ESP"
        value = 175.0
        min = 0.0
        max = 255.0
        isHidden = { !esp.value || !playeresp.value || playerespChroma.value }
    }

    @JvmField
    val playerespB = sm.numberSetting("Player ESP Color Blue") {
        description = "Blue color for Player ESP"
        category = "Dungeons"
        subCategory = "ESP"
        value = 175.0
        min = 0.0
        max = 255.0
        isHidden = { !esp.value || !playeresp.value || playerespChroma.value }
    }

    @JvmField
    val playerespChroma = sm.booleanSetting("Player ESP Chroma Color") {
        description = "Chroma color for Player ESP"
        category = "Dungeons"
        subCategory = "ESP"
        isHidden = { !esp.value || !playeresp.value }
    }

    @JvmField
    val starredmobsesp = sm.booleanSetting("Starred Mobs ESP") {
        description = "Shows starred mobs through walls"
        category = "Dungeons"
        subCategory = "ESP"
        isHidden = { !esp.value }
    }

    @JvmField
    val mobsespR = sm.numberSetting("Starred mobs ESP Color Red") {
        description = "Red color for Starred mobs ESP"
        category = "Dungeons"
        subCategory = "ESP"
        value = 255.0
        min = 0.0
        max = 255.0
        isHidden = { !esp.value || !starredmobsesp.value || starredmobsespChroma.value }
    }

    @JvmField
    val mobsespG = sm.numberSetting("Starred mobs ESP Color Green") {
        description = "Green color for Starred mobs ESP"
        category = "Dungeons"
        subCategory = "ESP"
        value = 200.0
        min = 0.0
        max = 255.0
        isHidden = { !esp.value || !starredmobsesp.value || starredmobsespChroma.value }
    }

    @JvmField
    val mobsespB = sm.numberSetting("Starred mobs ESP Color Blue") {
        description = "Blue color for Starred mobs ESP"
        category = "Dungeons"
        subCategory = "ESP"
        value = 0.0
        min = 0.0
        max = 255.0
        isHidden = { !esp.value || !starredmobsesp.value || starredmobsespChroma.value }
    }

    @JvmField
    val starredmobsespChroma = sm.booleanSetting("Starred mobs ESP Chroma Color") {
        description = "Chroma color for Starred mobs ESP"
        category = "Dungeons"
        subCategory = "ESP"
        isHidden = { !esp.value || !starredmobsesp.value }
    }

    @JvmField
    val batsesp = sm.booleanSetting("Bats ESP") {
        description = "Shows bats through walls"
        category = "Dungeons"
        subCategory = "ESP"
        isHidden = { !esp.value }
    }

    @JvmField
    val batsespR = sm.numberSetting("Bats ESP Color Red") {
        description = "Red color for Bats ESP"
        category = "Dungeons"
        subCategory = "ESP"
        value = 0.0
        min = 0.0
        max = 255.0
        isHidden = { !esp.value || !batsesp.value || batsespChroma.value }
    }

    @JvmField
    val batsespG = sm.numberSetting("Bats ESP Color Green") {
        description = "Green color for Bats ESP"
        category = "Dungeons"
        subCategory = "ESP"
        value = 255.0
        min = 0.0
        max = 255.0
        isHidden = { !esp.value || !batsesp.value || batsespChroma.value }
    }

    @JvmField
    val batsespB = sm.numberSetting("Bats ESP Color Blue") {
        description = "Blue color for Bats ESP"
        category = "Dungeons"
        subCategory = "ESP"
        value = 0.0
        min = 0.0
        max = 255.0
        isHidden = { !esp.value || !batsesp.value || batsespChroma.value }
    }

    @JvmField
    val batsespChroma = sm.booleanSetting("Bats ESP Chroma Color") {
        description = "Chroma color for Bats ESP"
        category = "Dungeons"
        subCategory = "ESP"
        isHidden = { !esp.value || !batsesp.value }
    }

    @JvmField
    val keyesp = sm.booleanSetting("Key ESP") {
        description = "Shows key through walls"
        category = "Dungeons"
        subCategory = "ESP"
        isHidden = { !esp.value }
    }

    @JvmField
    val keyespR = sm.numberSetting("Key ESP Color Red") {
        description = "Red color for Key ESP"
        category = "Dungeons"
        subCategory = "ESP"
        value = 255.0
        min = 0.0
        max = 255.0
        isHidden = { !esp.value || !keyesp.value || keyespChroma.value }
    }

    @JvmField
    val keyespG = sm.numberSetting("Key ESP Color Green") {
        description = "Green color for Key ESP"
        category = "Dungeons"
        subCategory = "ESP"
        value = 0.0
        min = 0.0
        max = 255.0
        isHidden = { !esp.value || !keyesp.value || keyespChroma.value }
    }

    @JvmField
    val keyespB = sm.numberSetting("Key ESP Color Blue") {
        description = "Blue color for Key ESP"
        category = "Dungeons"
        subCategory = "ESP"
        value = 0.0
        min = 0.0
        max = 255.0
        isHidden = { !esp.value || !keyesp.value || keyespChroma.value }
    }

    @JvmField
    val keyespChroma = sm.booleanSetting("Key ESP Chroma Color") {
        description = "Chroma color for Key ESP"
        category = "Dungeons"
        subCategory = "ESP"
        isHidden = { !esp.value || !keyesp.value }
    }

    @JvmField
    val customesp = sm.booleanSetting("Custom ESP") {
        description = "Shows key through walls"
        category = "Dungeons"
        subCategory = "ESP"
        isHidden = { !esp.value }
    }

    @JvmField
    val customespR = sm.numberSetting("Custom ESP Color Red") {
        description = "Red color for Custom ESP"
        category = "Dungeons"
        subCategory = "ESP"
        value = 255.0
        min = 0.0
        max = 255.0
        isHidden = { !esp.value || !customesp.value || customespChroma.value }
    }

    @JvmField
    val customespG = sm.numberSetting("Custom ESP Color Green") {
        description = "Green color for Custom ESP"
        category = "Dungeons"
        subCategory = "ESP"
        value = 0.0
        min = 0.0
        max = 255.0
        isHidden = { !esp.value || !customesp.value || customespChroma.value }
    }

    @JvmField
    val customespB = sm.numberSetting("Custom ESP Color Blue") {
        description = "Blue color for Custom ESP"
        category = "Dungeons"
        subCategory = "ESP"
        value = 0.0
        min = 0.0
        max = 255.0
        isHidden = { !esp.value || !customesp.value || customespChroma.value }
    }

    @JvmField
    val customespChroma = sm.booleanSetting("Custom ESP Chroma Color") {
        category = "Dungeons"
        subCategory = "ESP"
        isHidden = { !esp.value || !customesp.value }
    }

    @JvmField
    val customespText = sm.textSetting("Custom ESP Mobs") {
        description = "Enter nametag name above mob.\nSplit with \", \""
        category = "Dungeons"
        subCategory = "ESP"
        value = "Enderman, Zombie"
        isHidden = { !esp.value || !customesp.value }
    }

    @JvmField
    val terminalEsp = sm.booleanSetting("Terminal ESP") {
        description = "Shows terminals on f7 boss fight through walls"
        category = "Dungeons"
        subCategory = "ESP"
    }

    @JvmField
    val witherDoorEsp = sm.booleanSetting("Wither Door ESP") {
        description = "Shows wither doors though walls in dungeons"
        category = "Dungeons"
        subCategory = "ESP"
    }

    @JvmField
    val block = sm.booleanSetting("Block ability") {
        category = "Hacks"
        subCategory = "Block ability"
    }

    @JvmField
    val blockList = sm.textSetting("Item list") {
        description = "Split with \", \""
        category = "Hacks"
        subCategory = "Block ability"
    }

    @JvmField
    val blockZombieSword = sm.booleanSetting("Block Useless Zombie Sword Charges") {
        category = "Hacks"
        subCategory = "Block ability"
    }

    @JvmField
    val boxKeybind = sm.keybindSetting("Jerry Box Opener Keybind") {
        category = "Hacks"
        subCategory = "Jerry Box Opener"
    }

    @JvmField
    val boxDelay = sm.numberSetting("Jerry Box Opener Delay") {
        category = "Hacks"
        subCategory = "Jerry Box Opener"
        value = 500.0
        max = 1000.0
        min = 100.0
        places = -1
    }

    @JvmField
    val hotbarSwapKey = sm.keybindSetting("Hotbar Swap Keybind") {
        category = "Hacks"
        subCategory = "Hotbar Swapper"
    }

    @JvmField
    val hotbarSwapDelay = sm.numberSetting("Hotbar Swap Delay") {
        description = "Set hotbar presets for Hotbar Swapper using command \"/sm hb\""
        category = "Hacks"
        subCategory = "Hotbar Swapper"
        value = 100.0
        places = -1
        max = 1000.0
        min = 10.0
    }

    @JvmField
    val lavaFishingEsp = sm.booleanSetting("Lava Fishing ESP") {
        category = "Hacks"
        description = "Shows lava fising spots through walls"
    }

    @JvmField
    val ping = sm.booleanSetting("Name ping") {
        category = "Chat"
        description = "Plays sound when someone says your name in chat"
    }

    @JvmField
    val EndLeave = sm.booleanSetting("Auto Leave Dungeon") {
        category = "Dungeons"
        subCategory = "Auto Leave"
        description = "Runs /leavedungeon command after run ends"
    }

    @JvmField
    val EndParty = sm.booleanSetting("Auto Party FragBot when Dungeon ends") {
        category = "Dungeons"
        subCategory = "Auto Leave"
        description = "Runs /fragrun command after run ends"
    }

    @JvmField
    val BotName = sm.textSetting("FragBot Name") {
        category = "Dungeons"
        subCategory = "Auto Leave"
        description = "FragBot IGN"
    }

    @JvmField
    val delay = sm.numberSetting("Delay For \"Leave Dungeon\"") {
        category = "Dungeons"
        subCategory = "Auto Leave"
        description = "Delay between going to lobby and to dungeon hub"
        value = 2000.0
        max = 10000.0
        min = 100.0
        places = -2
    }

    @JvmField
    val coinsToggle = sm.booleanSetting("Super Secret Money Exploit!") {
        category = "Super Secret Settings"
        description = "§kMAKES YOUR PURSE BLOW UP WITH BILLIONS OF COINS"
    }

    @JvmField
    val coins = sm.textSetting("Amount of Coins") {
        category = "Super Secret Settings"
        description = "Amount of Coins to add in purse"
        value = "10000000"
        maxTextSize = 9
        isHidden = { !coinsToggle.value }
    }

    @JvmField
    val mimic = sm.booleanSetting("Mimic Killed Message On Mimic Death") {
        category = "Dungeons"
        subCategory = "Ping"
        description = "Send mimic text on it's death"
    }

    @JvmField
    val mimicText = sm.textSetting("Mimic Message Text") {
        category = "Dungeons"
        subCategory = "Ping"
        description = "Text for mimic message."
        value = "Mimic Dead!"
        isHidden = { !mimic.value }
    }

    @JvmField
    val rabbitPing = sm.booleanSetting("Watcher Done Ping") {
        category = "Dungeons"
        subCategory = "Ping"
    }

    @JvmField
    val hidePetCandies = sm.booleanSetting("Hide Pet's Candies") {
        category = "Visual"
        description = "Hide pet's candies counter in tooltip."
    }

    @JvmField
    val petsOverlay = sm.booleanSetting("Pets Overlay") {
        category = "Visual"
        subCategory = "Pets Overlay"
        description = "§cDon't use with small window size"
    }

    @JvmField
    val petsBgBlur = sm.numberSetting("Pets Overlay Background Blur Strength") {
        category = "Visual"
        subCategory = "Pets Overlay"
        value = 12.5
        min = 5.0
        max = 25.0
        places = 1
        isHidden = { !petsOverlay.value }
    }

    @JvmField
    val petsBorderColorR = sm.numberSetting("Pets Overlay Border Color Red") {
        category = "Visual"
        subCategory = "Pets Overlay"
        description = "Red color for pets overlay border."
        min = 0.0
        max = 255.0
        places = 0
        value = 87.0
        isHidden = { !petsOverlay.value }
    }

    @JvmField
    val petsBorderColorG = sm.numberSetting("Pets Overlay Border Color Green") {
        category = "Visual"
        subCategory = "Pets Overlay"
        description = "Green color for pets overlay border."
        min = 0.0
        max = 255.0
        places = 0
        value = 0.0
        isHidden = { !petsOverlay.value }
    }

    @JvmField
    val petsBorderColorB = sm.numberSetting("Pets Overlay Border Color Blue") {
        category = "Visual"
        subCategory = "Pets Overlay"
        description = "Blue color for pets overlay border."
        min = 0.0
        max = 255.0
        places = 0
        value = 247.0
        isHidden = { !petsOverlay.value }
    }

    @JvmField
    val petsBorderWidth = sm.numberSetting("Pets Overlay Border Width") {
        category = "Visual"
        subCategory = "Pets Overlay"
        value = 2.0
        min = 1.0
        max = 6.0
        places = 1
        isHidden = { !petsOverlay.value }
    }

    @JvmField
    val presents = sm.booleanSetting("Highlight Presents in Jerry Workshop") {
        category = "Visual"
        subCategory = "Highlight Presents"
    }

    @JvmField
    val presentsColorR = sm.numberSetting("Highlight Presents Color Red") {
        category = "Visual"
        subCategory = "Highlight Presents"
        description = "Red Color for presents highlight"
        min = 0.0
        max = 255.0
        places = 0
        value = 0.0
        isHidden = { !presents.value }
    }

    @JvmField
    val presentsColorG = sm.numberSetting("Highlight Presents Color Green") {
        category = "Visual"
        subCategory = "Highlight Presents"
        description = "Green Color for presents highlight"
        min = 0.0
        max = 255.0
        places = 0
        value = 255.0
        isHidden = { !presents.value }
    }

    @JvmField
    val presentsColorB = sm.numberSetting("Highlight Presents Color Blue") {
        category = "Visual"
        subCategory = "Highlight Presents"
        description = "Blue Color for presents highlight"
        min = 0.0
        max = 255.0
        places = 0
        value = 0.0
        isHidden = { !presents.value }
    }

    @JvmField
    val autoGB = sm.booleanSetting("Auto Ghost Blocks") {
        category = "Dungeons"
        subCategory = "Auto Ghost Block"
    }

    @JvmField
    val autoGBMode = sm.selectorSetting("Auto Ghost Blocks Mode") {
        category = "Dungeons"
        subCategory = "Auto Ghost Block"
        options = arrayOf("On sneak", "On key")
        isHidden = { !autoGB.value }
    }

    @JvmField
    val autoGBKey = sm.keybindSetting("Auto Ghost Blocks Keybind") {
        category = "Dungeons"
        subCategory = "Auto Ghost Block"
        isHidden = { !autoGB.value || autoGBMode.value != 1 }
    }

    @JvmField
    val adminRoom = sm.booleanSetting("Admin Room Detection") {
        category = "Dungeons"
        description = "Scans dungeon for admin room"
    }

    @JvmField
    val betterPerspective = sm.booleanSetting("Perspective Toggle") {
        category = "Visual"
        subCategory = "Perspective Toggle"
        description = "Activates 3rd perspective on key press"
        value = true
    }

    @JvmField
    val betterPerspectiveMode = sm.selectorSetting("Perspective Toggle Mode") {
        category = "Visual"
        subCategory = "Perspective Toggle"
        options = arrayOf("Hold", "Toggle")
        isHidden = { !betterPerspective.value }
    }

    @JvmField
    val betterPerspectiveKey = sm.keybindSetting("Perspective Keybind") {
        category = "Visual"
        subCategory = "Perspective Toggle"
        isHidden = { !betterPerspective.value }
    }

    @JvmField
    val customSb = sm.booleanSetting("Custom Scoreboard") {
        category = "Visual"
        subCategory = "Scoreboard"
    }

    @JvmField
    val customSbLobby = sm.booleanSetting("Hide Server Number") {
        category = "Visual"
        subCategory = "Scoreboard"
        value = true
        isHidden = { !customSb.value }
    }

    @JvmField
    val customSbBlurT = sm.booleanSetting("Blur Scoreboard Background") {
        category = "Visual"
        subCategory = "Scoreboard"
        isHidden = { !customSb.value }
    }

    @JvmField
    val customSbBlur = sm.numberSetting("Custom Scoreboard Background Blur Strength") {
        category = "Visual"
        subCategory = "Scoreboard"
        value = 20.0
        min = 5.0
        max = 25.0
        places = 1
        isHidden = { !customSb.value }
    }

    @JvmField
    val customSbBg = sm.booleanSetting("Scoreboard Background") {
        category = "Visual"
        subCategory = "Scoreboard"
        value = true
        isHidden = { !customSb.value }
    }

    @JvmField
    val customSbBgColorR = sm.numberSetting("Scoreboard Background Color Red") {
        category = "Visual"
        subCategory = "Scoreboard"
        description = "Red Color for scoreboard background"
        min = 0.0
        max = 255.0
        places = 0
        value = 0.0
        isHidden = { !customSb.value || !customSbBg.value }
    }

    @JvmField
    val customSbBgColorG = sm.numberSetting("Scoreboard Background Color Green") {
        category = "Visual"
        subCategory = "Scoreboard"
        description = "Green Color for scoreboard background"
        min = 0.0
        max = 255.0
        places = 0
        value = 0.0
        isHidden = { !customSb.value || !customSbBg.value }
    }

    @JvmField
    val customSbBgColorB = sm.numberSetting("Scoreboard Background Color Blue") {
        category = "Visual"
        subCategory = "Scoreboard"
        description = "Blue Color for scoreboard background"
        min = 0.0
        max = 255.0
        places = 0
        value = 0.0
        isHidden = { !customSb.value || !customSbBg.value }
    }

    @JvmField
    val customSbBgColorA = sm.numberSetting("Scoreboard Background Color Alpha") {
        category = "Visual"
        subCategory = "Scoreboard"
        description = "Alpha for scoreboard background"
        min = 0.0
        max = 255.0
        places = 0
        value = 255.0
        isHidden = { !customSb.value || !customSbBg.value }
    }

    @JvmField
    val customSbOutline = sm.booleanSetting("Draw Scoreboard Outline") {
        category = "Visual"
        subCategory = "Scoreboard"
        isHidden = { !customSb.value }
    }

    @JvmField
    val customSbOutlineColorR = sm.numberSetting("Scoreboard Outline Color Red") {
        category = "Visual"
        subCategory = "Scoreboard"
        description = "Red Color for scoreboard outline"
        min = 0.0
        max = 255.0
        places = 0
        value = 87.0
        isHidden = { !customSb.value || !customSbOutline.value || customSbOutlineColorRainbow.value }
    }

    @JvmField
    val customSbOutlineColorG = sm.numberSetting("Scoreboard Outline Color Green") {
        category = "Visual"
        subCategory = "Scoreboard"
        description = "Green Color for scoreboard outline"
        min = 0.0
        max = 255.0
        places = 0
        value = 0.0
        isHidden = { !customSb.value || !customSbOutline.value || customSbOutlineColorRainbow.value }
    }

    @JvmField
    val customSbOutlineColorB = sm.numberSetting("Scoreboard Outline Color Blue") {
        category = "Visual"
        subCategory = "Scoreboard"
        description = "Blue Color for scoreboard outline"
        min = 0.0
        max = 255.0
        places = 0
        value = 247.0
        isHidden = { !customSb.value || !customSbOutline.value || customSbOutlineColorRainbow.value }
    }

    @JvmField
    val customSbOutlineColorRainbow = sm.booleanSetting("Scoreboard Outline Rainbow Color") {
        category = "Visual"
        subCategory = "Scoreboard"
        isHidden = { !customSb.value || !customSbOutline.value }
    }

    @JvmField
    val customSbShadow = sm.booleanSetting("Scoreboard Shadow") {
        category = "Visual"
        subCategory = "Scoreboard"
        value = true
        isHidden = { !customSb.value }
    }

    @JvmField
    val customSbShadowStr = sm.numberSetting("Scoreboard Shadow Strength") {
        category = "Visual"
        subCategory = "Scoreboard"
        value = 15.0
        min = 5.0
        max = 25.0
        places = 1
        isHidden = { !customSb.value || !customSbShadow.value }
    }

    @JvmField
    val customSbText = sm.textSetting("Custom Scoreboard Text") {
        category = "Visual"
        subCategory = "Scoreboard"
        description = "For color codes use \"&\""
        value = "SkySkipped"
        isHidden = { !customSb.value }
    }

    @JvmField
    val customSbNumbers = sm.booleanSetting("Remove Red Ugly Numbers") {
        category = "Visual"
        subCategory = "Scoreboard"
    }

    @JvmField
    val autoMaddox = sm.booleanSetting("Auto Open Maddox Phone") {
        category = "Slayers"
        description = "Clicks on Batphone and in chat on slayer kill"
    }

    @JvmField
    val highlightSlots = sm.booleanSetting("Delight Locked Gemstone Slots in AH") {
        category = "Visual"
        description = "Make items with locked gemstone slots dark in ah"
    }

    @JvmField
    val trail = sm.booleanSetting("Trail") {
        category = "Visual"
        subCategory = "Trail"
        description = "Render trail behind player when moving"
    }

    @JvmField
    val trailParticle = sm.textSetting("Trail Particles") {
        category = "Visual"
        subCategory = "Trail"
        description = "Get names from https://www.spigotmc.org/wiki/particle-list-1-8-8/"
        value = "DRIP_LAVA"
        isHidden = { !trail.value }
    }

    @JvmField
    val trailInterval = sm.numberSetting("Trail Interval") {
        category = "Visual"
        subCategory = "Trail"
        value = 100.0
        min = 0.0
        max = 5000.0
        places = -1
    }

    @JvmField
    val fastBreak = sm.booleanSetting("Fast Break") {
        category = "Hacks"
        subCategory = "Fast Break"
        description = "Works only on instantly breaking blocks!"
    }

    @JvmField
    val fastBreakNumber = sm.numberSetting("Fast Break Block Amount") {
        category = "Hacks"
        subCategory = "Fast Break"
        min = 0.0
        max = 3.0
        value = 3.0
        isHidden = { !fastBreak.value }
    }

    @JvmField
    val stopFly = sm.booleanSetting("Stop fly") {
        category = "Movement"
        description = "Stop flying on private island"
    }

    @JvmField
    val cookieClicker = sm.booleanSetting("Cookie Clicker") {
        category = "Misc"
    }

    @JvmField
    val macroType = sm.selectorSetting("Macro Type") {
        category = "Macro"
        options = arrayOf("Nether Wart (SShaped)", "Sugar Cane (Normal and SShaped)")
        value = 0
        listener = { MacroManager.current = MacroManager.macros[it] }
    }

    @JvmField
    val macroKeybind = sm.keybindSetting("Macro Keybind") {
        category = "Macro"
    }

    @JvmField
    val macroStopTime = sm.numberSetting("Schedule Disable") {
        category = "Macro"
        description = "Hours before macro will be auto disabled (0 for unlimited time)"
        min = 0.0
        max = 12.0
        places = 1
    }

    @JvmField
    val customYawToggle = sm.booleanSetting("Custom Yaw Toggle") {
        category = "Macro"
    }

    @JvmField
    val customYaw = sm.textSetting("Custom Yaw Value") {
        category = "Macro"
        isHidden = { !customYawToggle.value }
        value = "0"
    }

    @JvmField
    val customPitchToggle = sm.booleanSetting("Custom Pitch Toggle") {
        category = "Macro"
    }

    @JvmField
    val customPitch = sm.textSetting("Custom Pitch Value") {
        category = "Macro"
        isHidden = { !customPitchToggle.value }
        value = "0"
    }

    @JvmField
    val rotationDiff = sm.numberSetting("Rotation Difference") {
        category = "Macro"
        description = "Rotation difference needed for auto rotation rotate your head back"
        min = 0.0
        max = 10.0
        places = 1
        value = 0.2
    }

    @JvmField
    val autoPickSlot = sm.numberSetting("Auto Pick Slot With Hoe") {
        category = "Macro"
        min = 1.0
        max = 9.0
    }

    //        @Property(
//            type = PropertyType.SWITCH,
//            name = "Macro Randomization",
//            category = "Macro",
//            description = "Randomize certain actions to look more legit (may fix packet thottle and desync)."
//        )
    var macroRandomization = true

    @JvmField
    val macroScript = sm.textSetting("Custom Macro Script Name") {
        category = "Macro"
        value = "example.txt"
    }

    @JvmField
    val macroLagbackFix = sm.booleanSetting("Lagback Fix") {
        category = "Macro"
    }

    @JvmField
    val netherWartDirection = sm.selectorSetting("Farm Direction") {
        category = "Macro"
        subCategory = "Nether Wart Macro"
        description = "Set direction of your eyes (check it with f3)"
        options = arrayOf("North", "East", "West", "South")
    }

    @JvmField
    val netherWartType = sm.selectorSetting("Farm Type") {
        category = "Macro"
        subCategory = "Nether Wart Macro"
        description = "Type of your farm"
        options = arrayOf("Horizontal", "Vertical", "Ladders", "Dropdown")
    }

    @JvmField
    val netherWartSetSpawn = sm.booleanSetting("Auto Set Spawn") {
        category = "Macro"
        subCategory = "Nether Wart Macro"
        value = true
    }

    @JvmField
    val netherWartStuck = sm.booleanSetting("Unstuck Failsafe") {
        category = "Macro"
        subCategory = "Nether Wart Macro"
        value = true
    }

    @JvmField
    val netherWartDesync = sm.booleanSetting("Desync Failsafe") {
        category = "Macro"
        subCategory = "Nether Wart Macro"
        value = true
    }

    @JvmField
    val netherWartDesyncTime = sm.numberSetting("Desync Failsafe Timeout") {
        category = "Macro"
        subCategory = "Nether Wart Macro"
        value = 5.0
        min = 1.0
        max = 20.0
        isHidden = { !netherWartDesync.value }
    }

    @JvmField
    val netherWartJacob = sm.booleanSetting("Jacob Failsafe") {
        category = "Macro"
        subCategory = "Nether Wart Macro"
        value = true
    }

    @JvmField
    val netherWartJacobNumber = sm.numberSetting("Jacob Failsafe Stop At") {
        category = "Macro"
        subCategory = "Nether Wart Macro"
        value = 400000.0
        min = 0.0
        max = 1000000.0
        places = -3
        isHidden = { !netherWartJacob.value }
    }

    @JvmField
    val netherWartFullInv = sm.booleanSetting("Full Inventory Failsafe") {
        category = "Macro"
        subCategory = "Nether Wart Macro"
        value = true
    }

    @JvmField
    val netherWartBanWaveChecker = sm.booleanSetting("Ban Wave Checker") {
        category = "Macro"
        subCategory = "Nether Wart Macro"
        value = true
    }

    @JvmField
    val netherWartBanWaveCheckerDisable = sm.booleanSetting("Ban Wave Auto Macro Disable") {
        category = "Macro"
        subCategory = "Nether Wart Macro"
        value = true
        isHidden = { !netherWartBanWaveChecker.value }
    }

    @JvmField
    val netherWartBanWaveCheckerTimer = sm.numberSetting("Ban Wave Checker Timer") {
        category = "Macro"
        subCategory = "Nether Wart Macro"
        value = 5.0
        min = 0.1
        max = 30.0
        places = 1
    }

    @JvmField
    val netherWartCpuSaver = sm.booleanSetting("CPU Saver") {
        category = "Macro"
        subCategory = "Nether Wart Macro"
    }

    @JvmField
    val netherWartReconnect = sm.booleanSetting("Auto Reconnect") {
        category = "Macro"
        subCategory = "Nether Wart Macro"
    }

    @JvmField
    val sugarCaneDirection = sm.selectorSetting("Farm Direction (Only SShaped Type)") {
        category = "Macro"
        subCategory = "Sugar Cane Macro"
        description = "Set direction of your eyes (check it with f3)"
        options = arrayOf("North", "East", "West", "South")
    }

    @JvmField
    val sugarCaneDirectionNormal = sm.selectorSetting("Farm Direction (Only Normal Type)") {
        category = "Macro"
        subCategory = "Sugar Cane Macro"
        description = "Set yaw direction (check it with f3)"
        options = arrayOf("45", "-45")
    }

    @JvmField
    val sugarCaneType = sm.selectorSetting("Farm Type") {
        category = "Macro"
        subCategory = "Sugar Cane Macro"
        options = arrayOf("Normal", "SShaped", "SShaped Dropdown", "SShaped Ladders")
    }

    @JvmField
    val sugarCaneSetSpawn = sm.booleanSetting("Auto Set Spawn") {
        category = "Macro"
        subCategory = "Sugar Cane Macro"
        value = true
    }

    @JvmField
    val sugarCaneStuck = sm.booleanSetting("Unstuck Failsafe") {
        category = "Macro"
        subCategory = "Sugar Cane Macro"
        value = true
    }

    @JvmField
    val sugarCaneDesync = sm.booleanSetting("Desync Failsafe") {
        category = "Macro"
        subCategory = "Sugar Cane Macro"
        value = true
    }

    @JvmField
    val sugarCaneDesyncTime = sm.numberSetting("Desync Failsafe Timeout") {
        category = "Macro"
        subCategory = "Sugar Cane Macro"
        value = 5.0
        min = 1.0
        max = 20.0
        isHidden = { !sugarCaneDesync.value }
    }

    @JvmField
    val sugarCaneJacob = sm.booleanSetting("Jacob Failsafe") {
        category = "Macro"
        subCategory = "Sugar Cane Macro"
        value = true
    }

    @JvmField
    val sugarCaneJacobNumber = sm.numberSetting("Jacob Failsafe Stop At") {
        category = "Macro"
        subCategory = "Sugar Cane Macro"
        value = 400000.0
        min = 0.0
        max = 1000000.0
        places = -3
    }

    @JvmField
    val sugarCaneFullInv = sm.booleanSetting("Full Inventory Failsafe") {
        category = "Macro"
        subCategory = "Sugar Cane Macro"
        value = true
    }

    @JvmField
    val sugarCaneBanWaveChecker = sm.booleanSetting("Ban Wave Checker") {
        category = "Macro"
        subCategory = "Sugar Cane Macro"
        value = true
    }

    @JvmField
    val sugarCaneBanWaveCheckerDisable = sm.booleanSetting("Ban Wave Auto Macro Disable") {
        category = "Macro"
        subCategory = "Sugar Cane Macro"
        value = true
        isHidden = { !sugarCaneBanWaveChecker.value }
    }

    @JvmField
    val sugarCaneBanWaveCheckerTimer = sm.numberSetting("Ban Wave Checker Timer") {
        category = "Macro"
        subCategory = "Sugar Cane Macro"
        value = 5.0
        min = 0.1
        max = 30.0
        places = 1
        isHidden = { !sugarCaneBanWaveChecker.value }
    }

    @JvmField
    val sugarCaneCpuSaver = sm.booleanSetting("CPU Saver") {
        category = "Macro"
        subCategory = "Sugar Cane Macro"
    }

    @JvmField
    val sugarCaneReconnect = sm.booleanSetting("Auto Reconnect") {
        category = "Macro"
        subCategory = "Sugar Cane Macro"
    }

    @JvmField
    val webhook = sm.booleanSetting("Webhook Notifications") {
        category = "Macro"
        subCategory = "Notifications"
    }

    @JvmField
    val webhookUrl = sm.textSetting("Webhook URL") {
        category = "Macro"
        subCategory = "Notifications"
        maxTextSize = 100
        isHidden = { !webhook.value }
    }

    @JvmField
    val desktopNotifications = sm.booleanSetting("Desktop Notifications") {
        category = "Macro"
        subCategory = "Notifications"
        value = true
    }

    @JvmField
    val remoteControl = sm.booleanSetting("Remote Macro Controlling") {
        category = "Macro"
        subCategory = "Remote Controlling"
        description = "Read tutorial on discord server"
        listener = {
            if (it) RemoteControlling.setup()
            else RemoteControlling.stop()
        }
    }

    @JvmField
    val remoteControlUrl = sm.textSetting("Bot's Token") {
        category = "Macro"
        subCategory = "Remote Controlling"
        maxTextSize = 100
    }

    @JvmField
    val farmingHud = sm.booleanSetting("Farming HUD") {
        category = "Macro"
        subCategory = "Farming HUD"
    }

    @JvmField
    val farmingHudX = sm.numberSetting("Farming HUD Position X") {
        category = "Macro"
        subCategory = "Farming HUD"
        description = "Edit position with \"/sm hud\""
        min = 0.0
        max = 10000.0
        places = 1
        isHidden = { !farmingHud.value }
    }

    @JvmField
    val farmingHudY = sm.numberSetting("Farming HUD Position Y") {
        category = "Macro"
        subCategory = "Farming HUD"
        description = "Edit position with \"/sm hud\""
        min = 0.0
        max = 10000.0
        places = 1
        isHidden = { !farmingHud.value }
    }

    @JvmField
    val gyroRadius = sm.booleanSetting("Gyro Radius") {
        category = "Dungeons"
        subCategory = "Item Radius"
    }

    @JvmField
    val hypRadius = sm.booleanSetting("Hyperion Radius") {
        category = "Dungeons"
        subCategory = "Item Radius"
    }

    @JvmField
    val onlyDungeonRadius = sm.booleanSetting("Dungeons Only") {
        category = "Dungeons"
        subCategory = "Item Radius"
        value = true
        isHidden = { !gyroRadius.value && !hypRadius.value }
    }

    @JvmField
    val onlyThirdPersonRadius = sm.booleanSetting("In Third Person Only") {
        category = "Dungeons"
        subCategory = "Item Radius"
        isHidden = { !gyroRadius.value && !hypRadius.value }
    }

    @JvmField
    val radiusColorR = sm.numberSetting("Item Radius Color Red") {
        category = "Dungeons"
        subCategory = "Item Radius"
        description = "Red Color for item radius"
        min = 0.0
        max = 255.0
        places = 0
        value = 0.0
        isHidden = { (!gyroRadius.value && !hypRadius.value) || radiusColorChroma.value }
    }

    @JvmField
    val radiusColorG = sm.numberSetting("Item Radius Color Green") {
        category = "Dungeons"
        subCategory = "Item Radius"
        description = "Green Color for item radius"
        min = 0.0
        max = 255.0
        places = 0
        value = 255.0
        isHidden = { (!gyroRadius.value && !hypRadius.value) || radiusColorChroma.value }
    }

    @JvmField
    val radiusColorB = sm.numberSetting("Item Radius Color Blue") {
        category = "Dungeons"
        subCategory = "Item Radius"
        description = "Blue Color for item radius"
        min = 0.0
        max = 255.0
        places = 0
        value = 0.0
        isHidden = { (!gyroRadius.value && !hypRadius.value) || radiusColorChroma.value }
    }

    @JvmField
    val radiusColorChroma = sm.booleanSetting("Item Radius Chroma Color") {
        category = "Dungeons"
        subCategory = "Item Radius"
        isHidden = { !gyroRadius.value && !hypRadius.value }
    }

    @JvmField
    val zeroPingGui = sm.booleanSetting("Zero Ping Gui") {
        category = "Misc"
    }

    @JvmField
    val beansSize = sm.booleanSetting("Bigger Cocoa Beans Sizes") {
        category = "Hacks"
    }

    @JvmField
    val mushroomSize = sm.booleanSetting("Bigger Mushroom Sizes") {
        category = "Hacks"
        listener = {
            if (it) {
                Blocks.red_mushroom.setBlockBounds(0f, 0f, 0f, 1f, 1f, 1f)
                Blocks.brown_mushroom.setBlockBounds(0f, 0f, 0f, 1f, 1f, 1f)
            } else {
                Blocks.red_mushroom.setBlockBounds(0.3f, 0.0f, 0.3f, 0.7f, 0.4f, 0.7f)
                Blocks.brown_mushroom.setBlockBounds(0.3f, 0.0f, 0.3f, 0.7f, 0.4f, 0.7f)
            }
        }
    }

    @JvmField
    val cropSize = sm.booleanSetting("Bigger Potato and Carrot Sizes") {
        category = "Hacks"
        listener = {
            if (it) {
                Blocks.carrots.setBlockBounds(0f, 0f, 0f, 1f, 1f, 1f)
                Blocks.potatoes.setBlockBounds(0f, 0f, 0f, 1f, 1f, 1f)
            } else {
                Blocks.carrots.setBlockBounds(0.0f, 0.0f, 0.0f, 1f, 0.25f, 1f)
                Blocks.potatoes.setBlockBounds(0.0f, 0.0f, 0.0f, 1f, 0.25f, 1f)
            }
        }
    }

    @JvmField
    val caneSize = sm.booleanSetting("Bigger Sugar Cane Sizes") {
        category = "Hacks"
        listener = {
            if (it)
                Blocks.reeds.setBlockBounds(0f, 0f, 0f, 1f, 1f, 1f)
            else
                Blocks.reeds.setBlockBounds(0.125f, 0.0f, 0.125f, 0.875f, 1.0f, 0.875f)
        }
    }

    @JvmField
    val keepFocus = sm.booleanSetting("Keep Focus") {
        category = "Hacks"
    }

    @JvmField
    val antiEscrowAh = sm.booleanSetting("Anti Escrow AH") {
        category = "Misc"
        subCategory = "Anti Escrow"
        description = "Reopens ah if escrow happens"
    }

    @JvmField
    val antiEscrowAhBin = sm.booleanSetting("Anti Escrow AH Bin") {
        category = "Misc"
        subCategory = "Anti Escrow"
        description = "Reopens ah after you buy bin"
    }

    @JvmField
    val antiEscrowBz = sm.booleanSetting("Anti Escrow BZ") {
        category = "Misc"
        subCategory = "Anti Escrow"
        description = "Reopens bz if escrow happens"
    }

    @JvmField
    val autoSalvage = sm.booleanSetting("Auto Salvage") {
        category = "Hacks"
        description = "Works with both dungeon and lava fishing gear"
    }

    @JvmField
    val chatSearch = sm.booleanSetting("Chat Search") {
        category = "Chat"
        description = "Search text in chat by pressing Ctrl + F"
    }

    @JvmField
    val containerSearch = sm.booleanSetting("Container Search") {
        category = "Misc"
        description = "Search items in containers by pressing Ctrl + F"
    }

    @JvmField
    val stopBreaking = sm.booleanSetting("Stop Breaking Blocks") {
        category = "Hacks"
        subCategory = "Stop Breaking"
    }

    @JvmField
    val stopBreakingList = sm.textSetting("Stop Breaking Blocks List") {
        category = "Hacks"
        subCategory = "Stop Breaking"
        value = "dirt"
        isHidden = { !stopBreaking.value }
    }

    @JvmField
    val autoReply = sm.booleanSetting("Auto Reply") {
        category = "Chat"
        description = "Replys to \"wc\" and \"gg\" with \"\"wc\" - someonesIgn\""
    }

    @JvmField
    val autoReplyGuild = sm.booleanSetting("Auto Reply Guild Only") {
        category = "Chat"
        isHidden = { !autoReply.value }
    }

    @JvmField
    val fpsSpoof = sm.booleanSetting("Fps Spoofer") {
        category = "Misc"
        subCategory = "Fps Spoofer"
        description = "Big numbers are funni"
    }

    @JvmField
    val fpsSpoofNumber = sm.numberSetting("Fps Spoofer Number") {
        category = "Misc"
        subCategory = "Fps Spoofer"
        value = 69420.0
        max = Int.MAX_VALUE.toDouble()
        min = 0.0
        places = -1
        isHidden = { !fpsSpoof.value }
    }

    @JvmField
    val fireFreezePing = sm.booleanSetting("M3 Professor Fire Freeze Timer") {
        category = "Dungeons"
    }

    @JvmField
    val autoGo = sm.booleanSetting("Auto Going in Portal") {
        category = "Dungeons"
        description = "Sends \"going\" message when entering portal"
    }

    @JvmField
    val removeCarpets = sm.booleanSetting("Remove Carpet Hitboxes") {
        category = "Hacks"
        listener = {
            if (it) Block.getBlockFromName("carpet").setBlockBounds(0f, 0f, 0f, 1f, 0f, 1f)
            else Block.getBlockFromName("carpet").setBlockBounds(0f, 0f, 0f, 1f, 1 / 16f, 1f)
        }
    }

    @JvmField
    val redirectClicks = sm.booleanSetting("Auto Redirect Middle Clicks") {
        category = "Misc"
        description = "To not get kicked to limbo. Works with other mods!"
    }

    @JvmField
    val shinyBlocksEsp = sm.booleanSetting("Shiny Blocks Esp") {
        category = "Hacks"
        subCategory = "Shiny Blocks"
    }

    @JvmField
    val shinyBlocksAura = sm.booleanSetting("Shiny Blocks Nuker") {
        category = "Hacks"
        subCategory = "Shiny Blocks"
    }

    @JvmField
    val neuOptimize = sm.booleanSetting("Optimize NEU's equipment overlay") {
        category = "Optimization"
    }

    @JvmField
    val stopFallingBlocks = sm.booleanSetting("Stop Rendering Falling Blocks") {
        category = "Optimization"
    }

    @JvmField
    val hideDamageInBoss = sm.booleanSetting("Stop Rendering Damage in Boss") {
        category = "Optimization"
    }

    @JvmField
    val hideWitherCloak = sm.booleanSetting("Stop Rendering Wither Cloak Creepers") {
        category = "Optimization"
    }

    @JvmField
    val showDamage = sm.booleanSetting("Render Damage Always on Top") {
        category = "Misc"
        description = "Useful for dmg testing"
    }

    @JvmField
    val banDetector = sm.booleanSetting("Ban Detector") {
        category = "Chat"
        description = "Says who got banned"
        value = true
    }

    @JvmField
    val terminatorClicker = sm.booleanSetting("Terminator Clicker") {
        category = "Dungeons"
        subCategory = "Terminator Clicker"
    }

    @JvmField
    val terminatorClickerDelay = sm.numberSetting("Terminator Clicker Delay") {
        category = "Dungeons"
        subCategory = "Terminator Clicker"
        value = 30.0
        min = 10.0
        max = 100.0
        places = -1
        isHidden = { !terminatorClicker.value }
    }

    @JvmField
    val aotvDisplay = sm.booleanSetting("Display AOTV Tp Postion") {
        category = "Visual"
        subCategory = "AOTV Display"
    }

    @JvmField
    val aotvDisplayKey = sm.keybindSetting("Display AOTV On Key") {
        category = "Visual"
        subCategory = "AOTV Display"
        description = "Leave to NONE to disable"
        isHidden = { !aotvDisplay.value }
    }

    @JvmField
    val aotvDisplayTuners = sm.booleanSetting("Assume AOTV with Tuners") {
        category = "Visual"
        subCategory = "AOTV Display"
        value = true
        isHidden = { !aotvDisplay.value }
    }

    @JvmField
    val aotvDisplayDisableEther = sm.booleanSetting("Disable on Sneak") {
        category = "Visual"
        subCategory = "AOTV Display"
        isHidden = { !aotvDisplay.value }
    }

    @JvmField
    val grassEsp = sm.booleanSetting("Garden Grass ESP") {
        category = "Hacks"
    }
}