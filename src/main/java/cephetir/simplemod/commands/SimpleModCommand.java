package cephetir.simplemod.commands;

import cephetir.simplemod.SimpleMod;
import club.sk1er.mods.core.ModCore;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

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
        return "/" + getCommandName();
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
        ModCore.getInstance().getGuiHandler().open(SimpleMod.config.gui());
    }
}