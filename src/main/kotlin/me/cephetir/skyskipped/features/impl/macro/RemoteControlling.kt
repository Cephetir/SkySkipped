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

package me.cephetir.skyskipped.features.impl.macro

import gg.essential.api.EssentialAPI
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.cephetir.bladecore.core.event.BladeEventBus
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.TextUtils.isNumeric
import me.cephetir.bladecore.utils.threading.BackgroundScope
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.utils.ScreenshotUtils
import me.cephetir.skyskipped.utils.mc
import me.cephetir.skyskipped.utils.skyblock.Queues
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.multiplayer.GuiConnecting
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.util.ChatComponentText
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent


object RemoteControlling {
    private lateinit var jda: JDA

    @Volatile
    private var connect = false

    fun setup() {
        SkySkipped.logger.info("Starting JDA...")
        if (!Loader.isModLoaded("skyskippedjdaaddon")) return SkySkipped.logger.error("Failed to start JDA! SkySkipped JDA Addon mod not loaded!")

        try {
            jda = JDABuilder.createLight(Config.remoteControlUrl)
                .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE, CacheFlag.ACTIVITY)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setBulkDeleteSplittingEnabled(false)
                .setActivity(Activity.watching("over skyskipped macro"))
                .addEventListeners(EventListener())
                .build()

            BladeEventBus.subscribe(RemoteControlling)
        } catch (e: Exception) {
            if (mc.fontRendererObj != null) EssentialAPI.getNotifications().push("Failed to start JDA", "Invalid discord bot token!")
            SkySkipped.logger.error("Failed to start JDA! Invalid discord bot token!")
            e.printStackTrace()
        }
    }

    fun stop() {
        if (this::jda.isInitialized) {
            SkySkipped.logger.info("Stopping JDA...")
            jda.shutdown()
            BladeEventBus.unsubscribe(RemoteControlling)
        }
    }

    init {
        listener<ClientTickEvent> {
            if (connect) mc.displayGuiScreen(
                GuiConnecting(
                    GuiMultiplayer(GuiMainMenu()),
                    mc,
                    ServerData(Cache.prevName, Cache.prevIP, Cache.prevIsLan)
                )
            )
        }
    }

    class EventListener : ListenerAdapter() {
        override fun onMessageReceived(event: MessageReceivedEvent) {
            var message = event.message.contentStripped
            if (!message.startsWith("!")) return
            message = message.removePrefix("!").trim()
            println("Received command $message")

            if (mc.thePlayer == null || mc.theWorld == null) {
                if (message.startsWith("connect") || message.startsWith("c")) {
                    connect = true
                    event.message.reply("Connecting...").queue()
                } else event.message.reply("Not connected to server!").queue()
                return
            }

            when (message) {
                "info", "i" -> {
                    if (!MacroManager.current.enabled) event.message.reply("No macro currently active!").queue()
                    else event.message.reply(MacroManager.current.info()).queue()
                }

                "toggle", "t" -> {
                    if (!MacroManager.current.enabled) event.message.reply("Started macro!").queue()
                    else event.message.reply("Stopped macro!").queue()
                    MacroManager.current.toggle()
                }

                "screenshot", "ss" -> {
                    val ss = ScreenshotUtils.takeScreenshot()
                    val channel = event.message.channel
                    val embed = EmbedBuilder()
                    embed.setImage("attachment://ss.png")
                    channel.sendFiles(FileUpload.fromData(ss, "ss.png")).setEmbeds(embed.build()).queue()
                }

                "disconnect", "dc" -> {
                    mc.netHandler.networkManager.closeChannel(ChatComponentText("Disconnected using discord bot"))
                    event.message.reply("Successfully disconnected!").queue()
                }

                "inventory", "inv" -> BackgroundScope.launch {
                    MacroManager.current.stopAndOpenInv()
                    delay(100L)

                    val ss = ScreenshotUtils.takeScreenshot()
                    val channel = event.message.channel
                    val embed = EmbedBuilder()
                    embed.setImage("attachment://ss.png")
                    channel.sendFiles(FileUpload.fromData(ss, "ss.png")).setEmbeds(embed.build()).queue()

                    MacroManager.current.closeInvAndReturn()
                }

                else -> {
                    if (message.startsWith("run")) {
                        val command = message.removePrefix("run ")
                        Queues.sendCommand(command)
                        event.message.reply("Successfully sent $command").queue()
                    } else if (message.startsWith("switch")) {
                        val name = message.removePrefix("switch ").replace(" ", "")
                        val macro = MacroManager.macros.find { it.name.equals(name, true) }
                        if (macro == null) {
                            event.message.reply("Invalid macro name!").queue()
                            return
                        }
                        Config.macroType = MacroManager.macros.indexOf(macro).coerceAtLeast(0)
                        event.message.reply("Switched macro to ${MacroManager.current.name}!").queue()
                    } else if (message.startsWith("slot") || message.startsWith("s")) {
                        val slot = message.removePrefix("slot ").removePrefix("s ").trim()
                        if (!slot.isNumeric() || slot.toInt() !in 0 until 9) {
                            event.message.reply("Invalid slot number!").queue()
                            return
                        }
                        mc.thePlayer.inventory.currentItem = slot.toInt()
                    } else event.message.reply("Unknown command \"$message\"!").queue()
                }
            }
        }
    }
}