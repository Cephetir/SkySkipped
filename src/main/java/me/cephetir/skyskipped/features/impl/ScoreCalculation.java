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

package me.cephetir.skyskipped.features.impl;

import kr.syeyoung.dungeonsguide.features.FeatureRegistry;
import kr.syeyoung.dungeonsguide.features.impl.dungeon.FeatureDungeonScore;
import me.cephetir.skyskipped.config.Cache;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.features.Feature;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import skytils.skytilsmod.Skytils;

//skid
public class ScoreCalculation extends Feature {
    private int timer = 0;

    public ScoreCalculation() {
        super("ScoreCalculation", "Dungeons", "skid score from other mods");
    }

    public void score() {
        if (!Cache.isInDungeon || !Config.scorePing) return;
        if (Loader.isModLoaded("skytils") && checkVersion()) {
            skytils.skytilsmod.features.impl.dungeons.ScoreCalculation scoreCalculation = skytils.skytilsmod.features.impl.dungeons.ScoreCalculation.INSTANCE;
            Cache.totalScore = scoreCalculation.getSpeedScore() + scoreCalculation.getSkillScore() + scoreCalculation.getDiscoveryScore() + scoreCalculation.getBonusScore();
        } else if (Loader.isModLoaded("skyblock_dungeons_guide")) {
            FeatureDungeonScore.ScoreCalculation score = FeatureRegistry.DUNGEON_SCORE.calculateScore();
            Cache.totalScore = score.getTime() + score.getSkill() + score.getExplorer() + score.getBonus();
        }
        if (Cache.totalScore >= 300) {
            if(Cache.was) return;
            timer = 60;
            mc.thePlayer.sendChatMessage("300 score reached! btw sbe is cringe");
            Cache.was = true;
        }
    }

    @SubscribeEvent
    public void draw(RenderGameOverlayEvent event) {
        if (!Config.scorePing) return;
        if (!Cache.isInDungeon || (!Loader.isModLoaded("skytils") && !Loader.isModLoaded("skyblock_dungeons_guide")))
            return;
        if (timer == 0) return;
        timer--;
        mc.fontRendererObj.drawStringWithShadow("Score: " + Cache.totalScore,
                event.resolution.getScaledWidth() / 2f - mc.fontRendererObj.getStringWidth("Score: " + Cache.totalScore) / 2f,
                event.resolution.getScaledHeight() / 2f - 9 / 2f, -1);
    }

    private boolean checkVersion() {
        return Skytils.VERSION.equals("1.0.9-RC2");
    }
}
