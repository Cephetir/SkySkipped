package cephetir.skyskipped.commands;

import cephetir.skyskipped.SkySkipped;
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
        } else if(args[0].equals("github")) {
            UChat.chat("§cSkySkipped §f:: §eGithub: §fhttps://github.com/Cephetir/SkySkipped");
            UTextComponent text = new UTextComponent("§cSkySkipped §f:: §eGithub: §fhttps://github.com/Cephetir/SkySkipped");
            text.setHover(HoverEvent.Action.SHOW_TEXT, "§9Open URL in browser.");
            text.setClick(ClickEvent.Action.OPEN_URL, "https://github.com/Cephetir/SkySkipped");
            sender.addChatMessage(text);
        } else {
            UChat.chat("§cSkySkipped §f:: §eUsage:\n" +
                            "             §f:: §e/sm §f- §eOpens GUI\n"
                            //"             §f:: §e/sm github §f- §eOpens official github page"
            );
        }
    }
}