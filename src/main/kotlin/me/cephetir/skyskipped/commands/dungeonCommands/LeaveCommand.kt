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
import me.cephetir.bladecore.utils.TextUtils.keepScoreboardCharacters
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.threading.BackgroundScope
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Features
import me.cephetir.skyskipped.utils.mc
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class LeaveCommand : CommandBase() {
    private var started = false

    override fun getCommandName(): String {
        return "leavedungeon"
    }

    override fun getCommandAliases(): List<String> {
        return listOf("ld")
    }

    override fun getCommandUsage(sender: ICommandSender): String? {
        return null
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        if (args.isEmpty())
            start(false)
    }

    private var party = false
    fun start(party: Boolean) {
        if (started || !EssentialAPI.getMinecraftUtil().isHypixel()) return
        started = true
        BladeEventBus.subscribe(this, true)
        this.party = party
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
                    mc.thePlayer.sendChatMessage("/lobby")
                    delay(Config.delay.toLong())
                    step++
                }
                1 -> {
                    mc.thePlayer.sendChatMessage("/play skyblock")
                    delay(Config.delay.toLong())
                    step++
                }
                2 -> {
                    val scoreboard = mc.thePlayer.worldScoreboard
                    val scoreObjective = scoreboard.getObjectiveInDisplaySlot(1)
                    val scores =
                        scoreboard.getSortedScores(scoreObjective)
                    var ok = false
                    for (sc in scores) {
                        val scorePlayerTeam = scoreboard.getPlayersTeam(sc.playerName)
                        val strippedLine: String =
                            ScorePlayerTeam.formatPlayerName(
                                scorePlayerTeam,
                                sc.playerName
                            ).stripColor().keepScoreboardCharacters().trim()
                        if (strippedLine.contains("Dungeon Hub")) {
                            ok = true
                        }
                    }
                    if (!ok) mc.thePlayer.sendChatMessage("/warp dungeon_hub")
                    if (Config.EndParty && Config.BotName != "" || party) Features.partyCommand.start()
                    BladeEventBus.unsubscribe(toStop, true)
                    started = false
                    step = 0
                }
            }
            startedd = false
        }
    }
}
