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

package me.cephetir.skyskipped.features.impl.cofl

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import gg.essential.universal.UChat
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.core.listeners.SkyblockIsland
import me.cephetir.bladecore.core.listeners.SkyblockListener
import me.cephetir.bladecore.utils.HttpUtils
import me.cephetir.bladecore.utils.TextUtils.keepScoreboardCharacters
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.minecraft.skyblock.ItemUtils.getExtraAttributes
import me.cephetir.bladecore.utils.minecraft.skyblock.ScoreboardUtils
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.threading.BackgroundScope
import me.cephetir.bladecore.utils.threading.safeListener
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.features.impl.macro.MacroManager
import me.cephetir.skyskipped.mixins.accessors.IMixinGuiEditSign
import me.cephetir.skyskipped.transformers.ModsData
import me.cephetir.skyskipped.utils.InventoryUtils
import me.cephetir.skyskipped.utils.InventoryUtils.getName
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.gui.inventory.GuiEditSign
import net.minecraft.event.ClickEvent
import net.minecraft.event.ClickEvent.Action
import net.minecraft.event.HoverEvent
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.Slot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockPos
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle
import net.minecraft.util.IChatComponent
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.util.*
import kotlin.math.roundToLong


object AutoFuckingCoflBuy : Feature() {
    val mainWebsocket = CoflWebsocket("1.5.2-Alpha")
    val fallbackWebsocket = CoflWebsocket("1.5.0-afclient")
    var websocket = mainWebsocket
    private var opening = false
    var forceMain = false

    val flipQueue = PriorityQueue<Flip>(compareByDescending { it.target })
    val sellQueue = PriorityQueue<Flip>(compareByDescending { it.target })

    class QueueThread : Thread("Queue Thread") {
        companion object {
            private val mutex = Mutex()
            private var instance: QueueThread? = null

            fun start() = runBlocking {
                mutex.withLock(instance) {
                    if (instance != null) instance!!.interrupt()
                    instance = QueueThread()
                    instance!!.start()
                }
            }

            fun stop() = runBlocking {
                mutex.withLock(instance) {
                    if (instance == null) return@runBlocking
                    instance!!.interrupt()
                    instance = null
                }
            }
        }

        override fun run() {
            while (Config.autoBuy.value && !interrupted()) {
                if (player == null || !SkyblockListener.onSkyblock || !websocket.isOpen) {
                    sleep(100L)
                    continue
                }

                try {
                    if (flipQueue.isEmpty() || buying != null || selling != null) continue
                    buying = flipQueue.poll()
                    if (buying == null) continue
                    if (mc.currentScreen != null) player!!.closeScreen()
                    player!!.sendChatMessage("/viewauction ${buying!!.id}")
                } catch (ex: Exception) {
                    SkySkipped.logger.error("Queue Thread Exception", ex)
                    UChat.chat("§cSkySkipped §f:: §4Error in Queue Thread! You can ignore it most of the time or send logs to Cephetir#0001")
                }
            }
        }
    }

    data class Flip(val id: String, val price: Long, val target: Long, var uid: String, val name: String, val finder: String)

    private val ahBoughtRegex = Regex("\\[Auction] \\w+ bought (?<item>.+) for [\\d,]+ coins CLICK")
    private val ahFlippedRegex = Regex("You purchased .+ for [\\d,]+ coins!")
    private val coflMessageRegex = Regex("Type: (?<type>.+) Item: (?<item>.+) Price: (?<price>.+) Target: (?<target>.+) Profit: (?<profit>.+)")

    private var lastFlip = 0L
    var selling: Flip? = null
    var buying: Flip? = null
    private var bedSpam = false
    private var flipsIn10 = false
    private var sellStep = 1
    private var sellingTimer = 0L
    private var sellingFallback = 0L
    private var sellingCount = 0

    private var keybindLastState = false
    private var ungrabbed = false

    private var purse = 0L
    private var bits = 0L
    private var updateTicks = 0
    private var collectChat = false
    private var chatRegex = Regex("nothing at all pls")
    private var lastBatch = 0L
    private var messages = mutableListOf<String>()

    private var warping = false
    private var lastPos = BlockPos.ORIGIN

    fun reset() {
        flipQueue.clear()
        sellQueue.clear()
        opening = false
        lastFlip = 0L
        selling = null
        buying = null
        bedSpam = false
        flipsIn10 = false
        sellStep = 1
        sellingTimer = 0L
        sellingFallback = 0L
        updateTicks = 0
        collectChat = false
        warping = false
    }

    private fun softReset() {
        flipQueue.clear()
        sellQueue.clear()
        lastFlip = 0L
        selling = null
        buying = null
        bedSpam = false
        flipsIn10 = false
        sellStep = 1
        sellingTimer = 0L
        sellingFallback = 0L
        collectChat = false
    }

    init {
        safeListener<ClientTickEvent> {
            if (Config.autoBuy.value && !websocket.isOpen && !websocket.isClosing && !opening && SkyblockListener.onSkyblock) {
                UChat.chat("§cSkySkipped §f:: §eStarting Cofl ${if (websocket == mainWebsocket) "main" else "fallback"} websocket...")

                opening = true
                softReset()
                if (websocket.isClosed) websocket.reconnect()
                else websocket.connect()
                BackgroundScope.launch {
                    delay(5000)
                    if (websocket.isOpen) {
                        websocket.send("{\"type\":\"set\",\"data\":\"\\\"fas true\\\"\"}")
                        websocket.send("{\"type\":\"set\",\"data\":\"\\\"2 modformat Type: {0} Item: {2} Price: {4} Target: {5} Profit: {6}\\\"\"}")
                        if (forceMain)
                            websocket.send("{\"type\":\"captcha\",\"data\":\"\\\"\\\"\"}")
                    }
                    opening = false
                }
            } else if (websocket.isOpen && !opening && (!Config.autoBuy.value || !SkyblockListener.onSkyblock))
                websocket.close()

            if (websocket.isOpen && ++updateTicks > 20) {
                updateTicks = 0
                val lines = ScoreboardUtils.sidebarLines.map { it.stripColor().keepScoreboardCharacters().lowercase() }
                for (line in lines) {
                    if (line.contains("purse") || line.contains("piggy")) {
                        var purseInScoreboard = 0L
                        try {
                            purseInScoreboard = line.split(" ")[1].replace(",", "").split("\\.")[0].toLong()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if (purse != purseInScoreboard) {
                            purse = purseInScoreboard
                            websocket.send("{\"type\":\"updatePurse\",\"data\":\"$purse\"}")
                        }
                    } else if (line.contains("bits")) {
                        var bitsInScoreboard = 0L
                        try {
                            bitsInScoreboard = line.split(" ")[1].replace(",", "").toLong()
                        } catch (e: NumberFormatException) {
                            e.printStackTrace()
                        }
                        if (bits != bitsInScoreboard) {
                            bits = bitsInScoreboard
                            websocket.send("{\"type\":\"updateBits\",\"data\":\"$bits\"}")
                        }
                    }
                }
            }

            val down = Config.ungrabKeybind.isKeyDown()
            if (down != keybindLastState) {
                keybindLastState = down
                if (down) {
                    if (ungrabbed) MacroManager.regrabMouse()
                    else MacroManager.ungrabMouse()
                    ungrabbed = !ungrabbed
                }
            }

            val pPos = BlockPos(player.posX, player.posY, player.posZ)
            if (Config.autoBuy.value && Config.autoWarpIsland.value && !warping && SkyblockListener.island != SkyblockIsland.PrivateIsland && lastPos != pPos) {
                UChat.chat("§cSkySkipped §f:: §eWarping back to island...")
                warping = true
                val job = BackgroundScope.launch {
                    delay(1000L)
                    while (isActive && Config.autoBuy.value && me.cephetir.bladecore.utils.player != null && warping && SkyblockListener.island != SkyblockIsland.PrivateIsland) {
                        if (me.cephetir.bladecore.utils.world == null) {
                            delay(250L)
                            continue
                        }
                        if (SkyblockListener.onSkyblock) {
                            player.sendChatMessage("/is")
                            delay(3000L)
                        } else {
                            player.sendChatMessage("/l")
                            delay(2000L)
                            player.sendChatMessage("/skyblock")
                            delay(5000L)
                        }
                    }
                    warping = false
                }
                BackgroundScope.launch {
                    val currTime = System.currentTimeMillis()
                    while (Config.autoBuy.value && warping && me.cephetir.bladecore.utils.player != null && System.currentTimeMillis() - currTime < 15_000)
                        delay(100L)
                    if (warping) {
                        UChat.chat("§cSkySkipped §f:: §4Auto warp timeout! Canceling...")
                        job.cancel()
                    }
                }
            }
            lastPos = pPos
        }

        listener<GuiScreenEvent.DrawScreenEvent.Pre> {
            if (!Config.autoBuy.value || it.gui !is GuiChest) return@listener
            val container = (it.gui as GuiChest).inventorySlots as ContainerChest
            val name = container.getName()
            if (!name.contains("BIN Auction View")) {
                if (!Config.useSkip.value && name.contains("Confirm"))
                    if (container.getSlot(11).hasStack)
                        slotClick(container.windowId, 11)
                return@listener
            }
            val slot = container.getSlot(31)
            if (!slot.hasStack) return@listener
            printdev("Clicking on slot")
            slotRender(container, slot)
            if (websocket == fallbackWebsocket && buying != null) {
                val item = container.getSlot(13)
                buying!!.uid = item.stack.getItemUUID()
            }
        }

        listener<ClientTickEvent> {
            if (it.phase != TickEvent.Phase.START || !Config.autoRelist.value) return@listener
            if (selling == null && !flipsIn10 && flipQueue.isEmpty() && System.currentTimeMillis() - lastFlip > 3000L && sellingCount < 14) {
                selling = sellQueue.poll()
                sellingFallback = System.currentTimeMillis()
            } else if (selling != null) {
                val currTime = System.currentTimeMillis()
                if (currTime - sellingFallback > 20_000L) {
                    UChat.chat("§cSkySkipped §f:: §eSomething went wrong while selling ${selling!!.name}! Canceling...")
                    if (mc.currentScreen != null && mc.currentScreen is GuiChest)
                        player!!.closeScreen()
                    sellStep = 1
                    selling = null
                    return@listener
                }
                if (currTime - sellingTimer < 1000) return@listener
                printdev("Sell step $sellStep")
                sellingTimer = currTime
                when (sellStep) {
                    1 -> {
                        player!!.sendChatMessage("/viewauction ${selling!!.id}")
                        sellStep = 2
                    }

                    2 -> {
                        if (mc.currentScreen != null) return@listener
                        player!!.sendChatMessage("/ah")
                        sellStep = 3
                    }

                    3 -> {
                        if (mc.currentScreen == null || mc.currentScreen !is GuiChest) return@listener
                        (mc.currentScreen as GuiChest).inventorySlots.inventorySlots.forEach { slot ->
                            if (slot.hasStack && slot.stack.getItemUUID().contains(selling!!.uid))
                                return@forEach slotLeftClick(player!!.openContainer.windowId, slot.slotNumber)
                        }
                        sellStep = 4
                    }

                    4 -> {
                        if (mc.currentScreen == null || mc.currentScreen !is GuiChest || InventoryUtils.getInventoryName()?.contains("Create BIN Auction") != true) return@listener
                        slotLeftClick(player!!.openContainer.windowId, 31)
                        sellStep = 5
                    }

                    5 -> {
                        if (mc.currentScreen == null || mc.currentScreen !is GuiEditSign) return@listener
                        val gui = (mc.currentScreen as GuiEditSign) as IMixinGuiEditSign
                        gui.tileSign.signText[0] = ChatComponentText(selling!!.target.toString())
                        mc.displayGuiScreen(null)
                        sellStep = 6
                    }

                    6 -> {
                        if (mc.currentScreen == null || mc.currentScreen !is GuiChest) return@listener
                        slotLeftClick(player!!.openContainer.windowId, 33)
                        sellStep = 7
                    }

                    7 -> {
                        if (mc.currentScreen == null || mc.currentScreen !is GuiChest) return@listener
                        slotLeftClick(player!!.openContainer.windowId, 10 + Config.customListingTime.value)
                        sellStep = 8
                    }

                    8 -> {
                        if (mc.currentScreen == null || mc.currentScreen !is GuiChest) return@listener
                        slotLeftClick(player!!.openContainer.windowId, 29)
                        sellStep = 9
                    }

                    9 -> {
                        if (mc.currentScreen == null || mc.currentScreen !is GuiChest || InventoryUtils.getInventoryName()?.contains("Confirm BIN Auction") != true) return@listener
                        slotLeftClick(player!!.openContainer.windowId, 11)
                        sellStep = 10
                    }

                    10 -> {
                        if (mc.currentScreen != null && mc.currentScreen is GuiChest)
                            player!!.closeScreen()
                        sellStep = 1
                        selling = null
                        sellingCount++
                    }
                }
            }
        }

        listener<ClientChatReceivedEvent> {
            if (!Config.autoBuy.value) return@listener

            val msg = it.message.formattedText.stripColor()
            var mr: MatchResult? = null
            if (Config.autoRelist.value && selling == null && ahBoughtRegex.matchEntire(msg).also { mr = it } != null) {
                player!!.sendChatMessage(it.message.chatStyle.chatClickEvent.value)
                sendWebhook("Sold an item!", "Someone bought your auction: ${mr!!.groupValues[1]}", false)
                sellingCount--
            } else if (buying != null && ahFlippedRegex.matches(msg)) {
                sendWebhook("Bought an item!", "Name: ${buying!!.name}\\nPrice: ${buying!!.price.formatProfit()}\\nTarget: ${buying!!.target.formatProfit()}", false)
                if (buying!!.finder != "USER" && buying!!.target > buying!!.price)
                    sellQueue.add(buying)
                buying = null
            } else if (buying != null && msg.contains("There was an error with the auction house") ||
                msg.contains("This auction wasn't found!") ||
                msg.contains("You didn't participate in this auction!") ||
                msg.contains("You don't have enough coins to afford this bid!")
            ) buying = null

            if (websocket.isOpen && collectChat) {
                val msg = it.message.unformattedText.replace("§", "")
                if (chatRegex.find(msg) == null)
                    return@listener
                messages.add(msg)

                if (System.currentTimeMillis() - lastBatch > 1000) {
                    lastBatch = System.currentTimeMillis()
                    val data = SkySkipped.gson.toJson(messages.toTypedArray()).replace("\"", "\\\"").replace("\\\\\"", "\\\\\\\"")
                    messages.clear()
                    BackgroundScope.launch {
                        websocket.send("{\"type\":\"chatBatch\",\"data\":\"$data\"}")
                    }
                }
            }
        }
    }

    private fun Long.formatProfit(): String {
        var ks = this / 1000
        var ms = ks / 1000
        ks -= ms * 1000
        val bs = ms / 1000
        ms -= bs * 1000
        val stringBuilder = StringBuilder()
        if (bs > 0) stringBuilder.append(bs).append("b ")
        if (ms > 0) stringBuilder.append(ms).append("m ")
        if (ks > 0) stringBuilder.append(ks).append("k ")
        val left = this - ks * 1000 - ms * 1000 * 1000 - bs * 1000 * 1000 * 1000
        if (left > 0) stringBuilder.append(left)
        return stringBuilder.toString().trim()
    }

    private fun ItemStack.getItemUUID(): String {
        val extraAttributes = this.getExtraAttributes() ?: return ""
        return extraAttributes.getString("uuid")
    }

    fun coflMessage(str: String) {
        if (!Config.autoBuy.value || !str.contains("\"type\":\"flip\"")) {
            if (str.contains("Flips in 10 seconds")) {
                UChat.chat("§cSkySkipped §f:: §eFlips in 10 seconds!")
                flipsIn10 = true
                BackgroundScope.launch {
                    delay(11_000)
                    flipsIn10 = false
                }
            } else if ((Config.printMessages.value || str.contains("\\\"onClick\\\":\\\"/cofl captcha")) && str.contains("{\"type\":\"chatMessage\"")) {
                if (str.contains("\\\"onClick\\\":\\\"/cofl captcha") && !forceMain) {
                    sendWebhook(
                        "Captcha!",
                        "You just got captcha. Switching to fallback websocket without captchas...\\nYou can do /skyskipped cofl captcha to switch back and solve the captcha.",
                        true
                    )
                    UChat.chat("§cSkySkipped §f:: §eYou just got captcha. Switching to fallback websocket without captchas... You can do /skyskipped cofl captcha to switch back and solve the captcha.")
                    websocket.close()
                    websocket = fallbackWebsocket
                }
                val received = SkySkipped.gson.fromJson(str, JsonObject::class.java)
                val data = SkySkipped.gson.fromJson(received.getAsJsonPrimitive("data").asString, JsonArray::class.java)
                val master = ChatComponentText("")
                for (comp in data) {
                    val component = getChatComponent(comp.asJsonObject)
                    if (component != null)
                        master.appendSibling(component)
                }
                player!!.addChatMessage(master)                                                                             // :troll:
            } else if (Config.printMessages.value && str.contains("{\"type\":\"writeToChat\"") && !str.contains("No inventory uploaded")) {
                if (str.contains("Thanks for confirming that you are a real user") && forceMain) {
                    forceMain = false
                    UChat.chat("§cSkySkipped §f:: §eYou successfully solved captcha!")
                }
                val received = SkySkipped.gson.fromJson(str, JsonObject::class.java)
                val data = SkySkipped.gson.fromJson(received.getAsJsonPrimitive("data").asString, JsonObject::class.java)
                val comp = getChatComponent(data.asJsonObject)
                if (comp != null)
                    player!!.addChatMessage(comp)
            } else if (str.contains("{\"type\":\"execute\"") && str.contains("fresponse")) {
                val received = SkySkipped.gson.fromJson(str, JsonObject::class.java)
                val data = received.getAsJsonPrimitive("data").asString
                val send = data.replace("\\\"", "").replace("\"", "").replace("/cofl fresponse ", "")
                websocket.send("{\"type\":\"fresponse\",\"data\":\"\\\"$send\\\"\"}")
            } else if (str.contains("{\"type\":\"getMods\"")) {
                val modData = ModsData()
                val modFolder = File(mc.mcDataDir, "mods")
                for (mod in modFolder.listFiles()!!) {
                    if (mod.name.contains("skyskipped", true)) continue
                    modData.addFilename(mod.name)
                    try {
                        modData.addFileHashes(getMD5Checksum(mod))
                    } catch (_: Exception) {
                    }
                }
                for (mod in Loader.instance().modList) {
                    if (mod.name.contains("skyskipped", true)) continue
                    modData.addModname(mod.name)
                    modData.addModname(mod.modId)
                }
                websocket.send("{\"type\":\"foundMods\",\"data\":\"${SkySkipped.gson.toJson(modData).replace("\"", "\\\"")}\"}")
            } else if (str.contains("{\"type\":\"privacySettings\"")) {
                val received = SkySkipped.gson.fromJson(str, JsonObject::class.java)
                val data = SkySkipped.gson.fromJson(received.getAsJsonPrimitive("data").asString, JsonObject::class.java)
                if (websocket == mainWebsocket) {
                    collectChat = data.getAsJsonPrimitive("collectChat").asBoolean
                    chatRegex = Regex(data.getAsJsonPrimitive("chatRegex").asString.replace("�", ""))
                } else {
                    collectChat = true
                    chatRegex = Regex(data.getAsJsonPrimitive("chatRegex").asString.replace("�", ""))
                }
            }
            return
        }

        try {
            if (websocket == fallbackWebsocket) {
                val received = SkySkipped.gson.fromJson(str, JsonObject::class.java)
                val data = SkySkipped.gson.fromJson(received.getAsJsonPrimitive("data").asString, JsonObject::class.java)
                val id = data.getAsJsonPrimitive("id").asString
                val price = data.getAsJsonPrimitive("startingBid").asLong
                val target = data.getAsJsonPrimitive("target").asLong
                val name = data.getAsJsonPrimitive("itemName").asString
                flipQueue.add(Flip(id, price, target, "", name, "UNKNOWN"))
                UChat.chat("§cSkySkipped §f:: §eNew flip: $name§r Price: $price§r Target: $target")
            } else {
                val received = SkySkipped.gson.fromJson(str, JsonObject::class.java)
                val data = SkySkipped.gson.fromJson(received.getAsJsonPrimitive("data").asString, JsonObject::class.java)
                val id = data.getAsJsonPrimitive("id").asString
                val auction = data.getAsJsonObject("auction")
                val name = auction.getAsJsonPrimitive("itemName").asString
                val price = auction.getAsJsonPrimitive("startingBid").asLong
                var target = data.getAsJsonPrimitive("target").asLong
                val uid = auction.getAsJsonObject("nbtData").getAsJsonObject("data").getAsJsonPrimitive("uid").asString
                val msg = data.getAsJsonArray("messages")[0].asJsonObject.getAsJsonPrimitive("text").asString
                var finder = "UNKNOWN"
                val mr = coflMessageRegex.find(msg)
                if (mr != null) {
                    finder = mr.groupValues[1]
                    target = mr.groupValues[4].parseNumber()
                }
                printdev("Adding flip to queue")
                flipQueue.add(Flip(id, price, target, uid, name, finder))
                UChat.chat(msg)
            }
        } catch (ex: Exception) {
            SkySkipped.logger.error("Cofl message error!", ex)
        }
    }

    private fun String.parseNumber(): Long {
        val num = this.lowercase()
        return if (num.endsWith("b") || num.endsWith("m") || num.endsWith("k")) when (num.last()) {
            'b' -> (num.removeSuffix("b").toDouble() * 1_000_000_000).roundToLong()
            'm' -> (num.removeSuffix("m").toDouble() * 1_000_000).roundToLong()
            'k' -> (num.removeSuffix("k").toDouble() * 1_000).roundToLong()
            else -> 0L
        } else num.replace(".", "").replace(",", "").toLong()
    }

    private fun slotRender(container: ContainerChest, slot: Slot) {
        val windowId = container.windowId
        when (slot.stack.item) {
            Items.gold_nugget, Item.getItemFromBlock(Blocks.gold_block) -> {
                slotClick(windowId, 31)
                if (Config.useSkip.value) {
                    slotClick(windowId + 1, 11)
                    player!!.closeScreen()
                }
                lastFlip = System.currentTimeMillis()
                UChat.chat("§cSkySkipped §f:: §eBought flip!")
            }

            Items.bed -> if (!bedSpam) {
                bedSpam = true
                BackgroundScope.launch {
                    delay(Config.bedInitDelay.value.toLong())
                    val fallback = System.currentTimeMillis() + 10_000
                    while (mc.currentScreen != null && mc.currentScreen is GuiChest && System.currentTimeMillis() < fallback) {
                        slotClick(windowId, 31)
                        if (Config.useSkip.value)
                            slotClick(windowId + 1, 11)
                        delay(Config.bedDelay.value.toLong())
                    }
                    lastFlip = System.currentTimeMillis()
                    bedSpam = false
                }
            }

            Items.potato, Items.poisonous_potato -> {
                player!!.closeScreen()
                lastFlip = System.currentTimeMillis()
                buying = null
                UChat.chat("§cSkySkipped §f:: §4Already bought!")
            }

            Items.feather -> printdev("Item not loaded")
        }
    }

    private fun slotClick(windowId: Int, slotId: Int) {
        mc.playerController.windowClick(windowId, slotId, 0, 3, player)
    }

    private fun slotLeftClick(windowId: Int, slotId: Int) {
        mc.playerController.windowClick(windowId, slotId, 0, 0, player)
    }

    private fun sendWebhook(title: String, message: String, ping: Boolean) {
        if (!Config.flipperWebhook.value) return
        val json =
            "{ \"content\": ${if (ping) "\"@everyone\"" else "null"}, \"embeds\":[ { \"title\": \"$title\", \"description\": \"Account: ${mc.session.username}\", \"color\": 8224125, \"fields\": [ { \"name\": \"Message:\", \"value\": \"$message\" } ], \"footer\": { \"text\": \"SkySkipped\", \"icon_url\": \"https://cdn.discordapp.com/icons/1088070084272066662/9458e6ab59f0aa2149834a6e604dab6e.png?size=4096\" } } ], \"username\": \"SkySkipped Macro\", \"avatar_url\": \"https://cdn.discordapp.com/icons/1088070084272066662/9458e6ab59f0aa2149834a6e604dab6e.png?size=4096\" }"
        try {
            HttpUtils.sendPost(Config.flipperWebhookUrl.value, json, mapOf("Content-Type" to "application/json"))
        } catch (ex: Exception) {
            SkySkipped.logger.error("Webhook send error", ex)
            UChat.chat("§cSkySkipped §f:: §4Failed to send to webhook! Wrong url?")
        }
    }

    private fun getChatComponent(obj: JsonObject): IChatComponent? {
        if (!obj.get("text").isJsonNull) {
            val comp = ChatComponentText(obj.getAsJsonPrimitive("text").asString)
            val style: ChatStyle
            if (!obj.get("onClick").isJsonNull) {
                val onClick = obj.getAsJsonPrimitive("onClick").asString
                style = if (onClick.startsWith("http")) ChatStyle().setChatClickEvent(ClickEvent(Action.OPEN_URL, onClick))
                else ChatStyle().setChatClickEvent(ClickEvent(Action.RUN_COMMAND, "/skyskipped cofl callback $onClick"))
                comp.setChatStyle(style)
            }
            if (!obj.get("hover").isJsonNull && obj.getAsJsonPrimitive("hover").asString.isNotEmpty()) {
                if (comp.chatStyle == null) comp.setChatStyle(ChatStyle())
                comp.chatStyle.setChatHoverEvent(
                    HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText(obj.getAsJsonPrimitive("hover").asString))
                )
            }
            return comp
        }
        return null
    }

    private fun getMD5Checksum(file: File): String {
        val b = createChecksum(file)
        val result = StringBuilder()
        for (i in b.indices)
            result.append(((b[i].toInt() and 0xff) + 0x100).toString(16).substring(1))
        return result.toString()
    }

    private fun createChecksum(file: File): ByteArray {
        val complete = MessageDigest.getInstance("MD5")
        FileInputStream(file).use {
            val buffer = ByteArray(1024)
            var numRead: Int
            do {
                numRead = it.read(buffer)
                if (numRead > 0) complete.update(buffer, 0, numRead)
            } while (numRead != -1)
        }
        return complete.digest()
    }
}
