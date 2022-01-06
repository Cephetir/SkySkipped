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

package me.cephetir.skyskipped.commands;

import me.cephetir.skyskipped.SkySkipped;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.features.impl.visual.PetsOverlay;
import me.cephetir.skyskipped.mixins.IMixinGuiContainer;
import me.cephetir.skyskipped.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PetMacroCommand extends CommandBase {
    private int index = -1;

    @Override
    public String getCommandName() {
        return "smpet";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/smpet [pet index (don't count glass panes and other stuff)]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0 && TextUtils.isNumeric(args[0])) {
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            if(Config.petsOverlay) SkySkipped.features.getPetsOverlay().auto = Integer.parseInt(args[0]);
            else {
                MinecraftForge.EVENT_BUS.register(this);
                index = Integer.parseInt(args[0]);
            }
            player.sendChatMessage("/pets");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if(!(event.gui instanceof GuiChest)) return;
        GuiChest chest = (GuiChest) event.gui;
        Slot slot = PetsOverlay.getPet(index, chest);
        if(slot == null) return;
        IMixinGuiContainer container = (IMixinGuiContainer) chest;
        container.handleMouseClick(slot, slot.slotNumber, 0, 0);
        Minecraft.getMinecraft().thePlayer.closeScreen();
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
