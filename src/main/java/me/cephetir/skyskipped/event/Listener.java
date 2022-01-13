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

package me.cephetir.skyskipped.event;

import gg.essential.api.EssentialAPI;
import me.cephetir.skyskipped.SkySkipped;
import me.cephetir.skyskipped.config.Cache;
import me.cephetir.skyskipped.utils.ScoreboardUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

import static me.cephetir.skyskipped.utils.TextUtils.keepScoreboardCharacters;
import static me.cephetir.skyskipped.utils.TextUtils.stripColor;

public class Listener {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void update(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().getNetHandler() != null && EssentialAPI.getMinecraftUtil().isHypixel() && Minecraft.getMinecraft().thePlayer.getWorldScoreboard() != null) {
            try {
                boolean foundDungeon = false;
                boolean foundSkyblock = false;
                int percentage = 0;
                String dungeonName = "";
                String itemheld = "Nothing";

                ScoreObjective scoreObjective = Minecraft.getMinecraft().thePlayer.getWorldScoreboard().getObjectiveInDisplaySlot(1);
                List<String> scores = ScoreboardUtils.fetchScoreboardLines();

                if (stripColor(scoreObjective.getDisplayName()).startsWith("SKYBLOCK")) foundSkyblock = true;

                if (foundSkyblock) {
                    for (String text : scores) {
                        String strippedLine = keepScoreboardCharacters(stripColor(text)).trim();
                        if (strippedLine.contains("Dungeon Cleared: ")) {
                            foundDungeon = true;
                            percentage = Integer.parseInt(strippedLine.substring(17));
                        } else if (text.startsWith(" §7⏣")) dungeonName = strippedLine.trim();
                    }
                    if (Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem() != null)
                        itemheld = keepScoreboardCharacters(stripColor(Minecraft.getMinecraft().thePlayer.getHeldItem().getDisplayName())).trim();
                }

                Cache.inSkyblock = foundSkyblock;
                Cache.isInDungeon = foundDungeon;
                Cache.dungeonPercentage = percentage;
                Cache.dungeonName = dungeonName;
                Cache.itemheld = itemheld;
            } catch (NullPointerException ignored) {
            }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        Cache.inSkyblock = false;
        Cache.isInDungeon = false;
        Cache.was = false;
        Cache.was2 = false;
        SkySkipped.features.getScoreCalculation().bloodCleared = false;
    }
}
