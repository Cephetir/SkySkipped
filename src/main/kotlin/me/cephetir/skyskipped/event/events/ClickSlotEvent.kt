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

import me.cephetir.bladecore.core.event.Cancellable
import me.cephetir.bladecore.core.event.Event
import me.cephetir.bladecore.core.event.ICancellable
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.Slot

class ClickSlotEvent(val gui: GuiContainer, val slot: Slot?, val button: Int) : Event, ICancellable by Cancellable()

class ClickSlotControllerEvent(val windowId: Int, var slot: Int, var button: Int, var mode: Int) : Event