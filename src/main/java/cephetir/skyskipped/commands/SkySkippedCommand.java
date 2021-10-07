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

package cephetir.skyskipped.commands;

import cephetir.skyskipped.SkySkipped;
import cephetir.skyskipped.config.Cache;
import gg.essential.universal.UChat;
import gg.essential.universal.UScreen;
import gg.essential.universal.wrappers.message.UTextComponent;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.BlockPos;

import java.util.Collections;
import java.util.List;

public class SkySkippedCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "skyskipped";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("sm");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName() + " help";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "github", "crit", "help", "gui");
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            UScreen.displayScreen(SkySkipped.config.gui());
        } else if (args[0].equals("gui")) {
            UScreen.displayScreen(SkySkipped.config.gui());
        } else if (args[0].equals("github")) {
            UTextComponent text = new UTextComponent("§cSkySkipped §f:: §eGithub: §fhttps://github.com/Cephetir/SkySkipped");
            text.setHover(HoverEvent.Action.SHOW_TEXT, "§9Open URL in browser.");
            text.setClick(ClickEvent.Action.OPEN_URL, "https://github.com/Cephetir/SkySkipped");
            sender.addChatMessage(text);
        } else if (args[0].equals("crit")) {
            UTextComponent text = new UTextComponent("§cSkySkipped §f:: §eLast Crit: §f" + Cache.lastCrit);
            sender.addChatMessage(text);
        } else if (args[0].equals("help")) {
            UChat.chat("§cSkySkipped §f:: §eUsage:\n" +
                    "§cSkySkipped §f:: §e/sm §for§e /sm gui §f- §eOpens GUI\n" +
                    "§cSkySkipped §f:: §e/sm github §f- §eOpens official github page\n" +
                    "§cSkySkipped §f:: §e/sm crit §f- §eShows last critical hit\n" +
                    "§cSkySkipped §f:: §e/sm help §f- §eShow this message\n"
            );
        } else {
            UChat.chat("§cSkySkipped §f:: §eUsage:\n" +
                    "§cSkySkipped §f:: §e/sm §for§e /sm gui §f- §eOpens GUI\n" +
                    "§cSkySkipped §f:: §e/sm github §f- §eOpens official github page\n" +
                    "§cSkySkipped §f:: §e/sm crit §f- §eShows last critical hit\n" +
                    "§cSkySkipped §f:: §e/sm help §f- §eShows this message\n"
            );
        }
    }
}