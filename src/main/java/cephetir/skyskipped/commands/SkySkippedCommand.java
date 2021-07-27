package cephetir.skyskipped.commands;

import cephetir.skyskipped.SkySkipped;
import gg.essential.api.EssentialAPI;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
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
        return "§SkySkipped§7: §c/sm §7- §fOpens configuration gui";
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
        }
    }
}