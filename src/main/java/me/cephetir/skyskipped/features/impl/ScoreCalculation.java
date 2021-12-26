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

import gg.essential.universal.ChatColor;
import kr.syeyoung.dungeonsguide.features.FeatureRegistry;
import kr.syeyoung.dungeonsguide.features.impl.dungeon.FeatureDungeonScore;
import me.cephetir.skyskipped.config.Cache;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.features.Feature;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import skytils.skytilsmod.Skytils;

//skid
public class ScoreCalculation extends Feature {
    private int timer = 0;

    public ScoreCalculation() {
        super("ScoreCalculation", "Dungeons", "skid score from other mods");
    }

    public void score() {
        if (!Cache.isInDungeon) return;
        if (Loader.isModLoaded("skyblock_dungeons_guide")) {
            FeatureDungeonScore.ScoreCalculation score = FeatureRegistry.DUNGEON_SCORE.calculateScore();
            Cache.totalScore = score.getTime() + score.getSkill() + score.getExplorer() + score.getBonus();
        } else if (Loader.isModLoaded("skytils") && checkVersion()) {
            skytils.skytilsmod.features.impl.dungeons.ScoreCalculation scoreCalculation = skytils.skytilsmod.features.impl.dungeons.ScoreCalculation.INSTANCE;
            Cache.totalScore = scoreCalculation.getSpeedScore() + scoreCalculation.getSkillScore() + scoreCalculation.getDiscoveryScore() + scoreCalculation.getBonusScore();
        }
        if (Cache.totalScore >= 300 && !Cache.was) {
            timer = 60;
            mc.thePlayer.sendChatMessage("300 score reached! btw sbe is cringe");
            Cache.was = true;
        }
    }

    @SubscribeEvent
    public void draw(RenderGameOverlayEvent event) {
        if (!Config.scorePing || !Cache.isInDungeon || timer == 0) return;
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.5f, 1.5f, 1f);
        mc.fontRendererObj.drawStringWithShadow(ChatColor.DARK_RED + "300 Score Reached!",
                event.resolution.getScaledWidth() / 1.5f / 2f - mc.fontRendererObj.getStringWidth("300 Score Reached!") / 2f,
                event.resolution.getScaledHeight() / 1.5f / 2f - 6.75f, -1);
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (timer == 0 || event.phase != TickEvent.Phase.START) return;
        timer--;
    }

    private boolean checkVersion() {
        return Skytils.VERSION.equals("1.0.9-RC2");
    }
}
