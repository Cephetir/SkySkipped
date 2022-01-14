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

package me.cephetir.skyskipped.commands

import gg.essential.api.EssentialAPI
import gg.essential.universal.UChat.chat
import gg.essential.universal.wrappers.message.UTextComponent
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Cache
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.BlockPos

class SkySkippedCommand : CommandBase() {
    override fun getCommandName(): String {
        return "skyskipped"
    }

    override fun getCommandAliases(): List<String> {
        return listOf("sm")
    }

    override fun getCommandUsage(sender: ICommandSender): String {
        return "/$commandName help"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun addTabCompletionOptions(sender: ICommandSender, args: Array<String>, pos: BlockPos): List<String>? {
        return if (args.size == 1) {
            getListOfStringsMatchingLastWord(args, "github", "crit", "help", "gui")
        } else null
    }

    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            EssentialAPI.getGuiUtil().openScreen(SkySkipped.config.gui())
        } else if (args[0] == "gui") {
            EssentialAPI.getGuiUtil().openScreen(SkySkipped.config.gui())
        } else if (args[0] == "github") {
            val text = UTextComponent("§cSkySkipped §f:: §eGithub: §fhttps://github.com/Cephetir/SkySkipped")
            text.setHover(HoverEvent.Action.SHOW_TEXT, "§9Open URL in browser.")
            text.setClick(ClickEvent.Action.OPEN_URL, "https://github.com/Cephetir/SkySkipped")
            sender.addChatMessage(text)
        } else if (args[0] == "crit") {
            val text = UTextComponent("§cSkySkipped §f:: §eLast Crit: §f" + Cache.lastCrit)
            sender.addChatMessage(text)
        } else if (args[0] == "help") {
            chat(
                """
                    §cSkySkipped §f:: §eUsage:
                    §cSkySkipped §f:: §e/sm §for§e /sm gui §f- §eOpens GUI
                    §cSkySkipped §f:: §e/sm github §f- §eOpens official github page
                    §cSkySkipped §f:: §e/sm crit §f- §eShows last critical hit
                    §cSkySkipped §f:: §e/sm help §f- §eShow this message
                    
                    """.trimIndent()
            )
        } else {
            chat(
                """
                    §cSkySkipped §f:: §eUsage:
                    §cSkySkipped §f:: §e/sm §for§e /sm gui §f- §eOpens GUI
                    §cSkySkipped §f:: §e/sm github §f- §eOpens official github page
                    §cSkySkipped §f:: §e/sm crit §f- §eShows last critical hit
                    §cSkySkipped §f:: §e/sm help §f- §eShows this message
                    
                    """.trimIndent()
            )
        }
    }
}