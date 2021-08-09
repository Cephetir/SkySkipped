/*
 * SkySkipped - Hypixel Skyblock mod
 * Copyright (C) 2021  Cephetir
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

package cephetir.skyskipped.commands;

import cephetir.skyskipped.SkySkipped;
import cephetir.skyskipped.config.Cache;
import gg.essential.api.EssentialAPI;
import gg.essential.universal.UChat;
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
        return null;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(args.length == 0) {
            EssentialAPI.getGuiUtil().openScreen(SkySkipped.config.gui());
        } else if (args[0].equals("github")) {
            UTextComponent text = new UTextComponent("§cSkySkipped §f:: §eGithub: §fhttps://github.com/Cephetir/SkySkipped");
            text.setHover(HoverEvent.Action.SHOW_TEXT, "§9Open URL in browser.");
            text.setClick(ClickEvent.Action.OPEN_URL, "https://github.com/Cephetir/SkySkipped");
            sender.addChatMessage(text);
        } else if (args[0].equals("crit")) {
            UTextComponent text = new UTextComponent("§cSkySkipped §f:: §eLast Crit: §f" + Cache.lastCrit);
            sender.addChatMessage(text);
        } else {
            UChat.chat("§cSkySkipped §f:: §eUsage:\n" +
                    "§cSkySkipped §f:: §e/sm §f- §eOpens GUI\n" +
                    "§cSkySkipped §f:: §e/sm github §f- §eOpens official github page\n" +
                    "§cSkySkipped §f:: §e/sm crit §f- §eShows last critical hit\n"
            );
        }
    }
}