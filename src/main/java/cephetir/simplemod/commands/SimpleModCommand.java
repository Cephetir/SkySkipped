package cephetir.simplemod.commands;

import cephetir.simplemod.SimpleMod;
import gg.essential.api.EssentialAPI;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.Collections;
import java.util.List;

public class SimpleModCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "simplemod";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("sm");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "§cSimpleMod§7: §c/sm §7- §fOpens configuration gui";
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
            EssentialAPI.getGuiUtil().openScreen(SimpleMod.config.gui());
        } else if(args[0].equalsIgnoreCase("github")) {
            sender.addChatMessage(new ChatComponentText("§cSimpleMod§7: §cGithub: §fhttps://github.com/Cephetir/SimpleMod/"));
        } else {
            sender.addChatMessage(new ChatComponentText("§cSimpleMod§7: §c/sm §7- §fOpens configuration gui"));
            sender.addChatMessage(new ChatComponentText("§cSimpleMod§7: §c/sm github §7- §fOpens mod's github page"));
        }
    }
}