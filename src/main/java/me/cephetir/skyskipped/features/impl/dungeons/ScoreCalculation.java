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

package me.cephetir.skyskipped.features.impl.dungeons;

import kr.syeyoung.dungeonsguide.features.FeatureRegistry;
import kr.syeyoung.dungeonsguide.features.impl.dungeon.FeatureDungeonScore;
import me.cephetir.skyskipped.config.Cache;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.features.Feature;
import me.cephetir.skyskipped.utils.PingUtils;
import me.cephetir.skyskipped.utils.TextUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ScoreCalculation extends Feature {
    public boolean isDGLoaded = false;
    public boolean isSTLoaded = false;

    public boolean bloodCleared = false;
    private int ticks = 0;

    public ScoreCalculation() {
        super("ScoreCalculation", "Dungeons", "skid score from other mods");
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        ticks++;
        if (!Cache.isInDungeon || event.phase != TickEvent.Phase.START) return;
        if(ticks < 20) return;
        ticks = 0;
        try {
            if (isDGLoaded) {
                FeatureDungeonScore.ScoreCalculation score = FeatureRegistry.DUNGEON_SCORE.calculateScore();
                Cache.totalScore = score.getSkill() + score.getExplorer() + score.getTime() + score.getBonus();
            } else if (isSTLoaded) {
                skytils.skytilsmod.features.impl.dungeons.ScoreCalculation scoreCalculation = skytils.skytilsmod.features.impl.dungeons.ScoreCalculation.INSTANCE;
                Cache.totalScore = scoreCalculation.getSkillScore().get() + scoreCalculation.getDiscoveryScore().get() + scoreCalculation.getSpeedScore().get() + scoreCalculation.getBonusScore().get();
            }

            if (Cache.totalScore >= 300 && !Cache.was && Config.scorePing) {
                new PingUtils(100, "300 score reached!");
                mc.thePlayer.sendChatMessage(Config.pingText);
                Cache.was = true;
            }
            if (bloodCleared && !Cache.was2 && Config.rabbitPing) {
                new PingUtils(100, "Rabbit Hat!");
                Cache.was2 = true;
            }

        } catch (NullPointerException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!Cache.isInDungeon || mc.theWorld == null) return;
        String unformatted = TextUtils.stripColor(event.message.getUnformattedText());
        if (unformatted.contains("You have proven yourself. You may pass")) bloodCleared = true;
    }
}
