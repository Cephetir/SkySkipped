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

import me.cephetir.skyskipped.SkySkipped.Companion.features
import me.cephetir.skyskipped.config.Config.Companion.petsOverlay
import me.cephetir.skyskipped.features.impl.visual.PetsOverlay
import me.cephetir.skyskipped.mixins.IMixinGuiContainer
import me.cephetir.skyskipped.utils.TextUtils.isNumeric
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class PetMacroCommand : CommandBase() {
    private var index = -1

    override fun getCommandName(): String {
        return "smpet"
    }

    override fun getCommandUsage(sender: ICommandSender): String {
        return "/smpet [pet index (don't count glass panes and other stuff)]"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        if (args.isNotEmpty() && args[0].isNumeric()) {
            val player = Minecraft.getMinecraft().thePlayer
            if (petsOverlay) features.petsOverlay.auto = args[0].toInt() else {
                MinecraftForge.EVENT_BUS.register(this)
                index = args[0].toInt()
            }
            player.sendChatMessage("/pets")
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onDrawScreen(event: DrawScreenEvent.Pre) {
        if (event.gui !is GuiChest) return
        val chest = event.gui as GuiChest
        val slot = PetsOverlay.getPet(index, chest) ?: return
        val container = chest as IMixinGuiContainer
        container.handleMouseClick(slot, slot.slotNumber, 0, 0)
        Minecraft.getMinecraft().thePlayer.closeScreen()
        MinecraftForge.EVENT_BUS.unregister(this)
    }
}
