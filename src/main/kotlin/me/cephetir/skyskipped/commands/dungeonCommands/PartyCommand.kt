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

package me.cephetir.skyskipped.commands.dungeonCommands

import gg.essential.api.EssentialAPI
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.cephetir.bladecore.core.event.BladeEventBus
import me.cephetir.bladecore.utils.threading.BackgroundScope
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Features
import me.cephetir.skyskipped.utils.mc
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class PartyCommand : CommandBase() {
    private var started = false

    override fun getCommandName(): String {
        return "fragrun"
    }

    override fun getCommandAliases(): List<String> {
        return listOf("frag")
    }

    override fun getCommandUsage(sender: ICommandSender): String? {
        return null
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        Features.leaveCommand.start(true)
    }

    fun start() {
        if (started || !EssentialAPI.getMinecraftUtil().isHypixel()) return
        started = true
        BladeEventBus.subscribe(this)
    }

    private var step = 0
    private var startedd = false

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (startedd) return
        val toStop = this
        BackgroundScope.launch {
            startedd = true
            when (step) {
                0 -> {
                    mc.thePlayer.sendChatMessage("/p leave")
                    delay(200L)
                    step++
                }
                1 -> {
                    mc.thePlayer.sendChatMessage("/p " + Config.BotName)
                    BladeEventBus.unsubscribe(toStop, true)
                    started = false
                    step = 0
                }
            }
            startedd = false
        }
    }
}
