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

package me.cephetir.skyskipped.event.events

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.Slot
import net.minecraftforge.fml.common.eventhandler.Event

abstract class DrawSlotEvent(open val gui: GuiContainer, open val slot: Slot) : Event() {
    data class Pre(override val gui: GuiContainer, override val slot: Slot) : DrawSlotEvent(gui, slot)

    data class Post(override val gui: GuiContainer, override val slot: Slot) : DrawSlotEvent(gui, slot)
}