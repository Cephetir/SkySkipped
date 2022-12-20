/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Cephetir
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
