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
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Features
import me.cephetir.skyskipped.utils.TextUtils.keepScoreboardCharacters
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import net.minecraft.client.Minecraft
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraftforge.common.MinecraftForge
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
        if (args.isEmpty()) {
            start(false)
        }
    }

    private var party = false
    fun start(party: Boolean) {
        if (started || !EssentialAPI.getMinecraftUtil().isHypixel()) return
        started = true
        MinecraftForge.EVENT_BUS.register(this)
        this.party = party
    }

    private var step = 0
    private var startedd = false

    @SubscribeEvent
    fun onTick(event: ClientTickEvent?) {
        if (startedd) return
        Thread {
            startedd = true
            when (step) {
                0 -> {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/lobby")
                    timer(Config.delay.toLong())
                }
                1 -> {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/play skyblock")
                    timer(Config.delay.toLong())
                }
                2 -> {
                    val scoreboard = Minecraft.getMinecraft().thePlayer.worldScoreboard
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
                    if (!ok) {
                        Minecraft.getMinecraft().thePlayer.sendChatMessage("/warp dungeon_hub")
                    }
                    if (Config.EndParty && Config.BotName != "" || party) Features.partyCommand
                        .start()
                    MinecraftForge.EVENT_BUS.unregister(this)
                    started = false
                    step = 0
                }
            }
            startedd = false
        }.start()
    }

    private fun timer(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        step++
    }
}
