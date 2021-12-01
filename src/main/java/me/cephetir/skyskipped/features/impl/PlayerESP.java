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

import me.cephetir.skyskipped.config.Cache;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.event.events.RenderEntityModelEvent;
import me.cephetir.skyskipped.features.Feature;
import me.cephetir.skyskipped.utils.OutlineUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class PlayerESP extends Feature {
    private final ArrayList<Entity> highlightedEntities = new ArrayList<>();

    public PlayerESP() {
        super("Player ESP", "Dungeons", "Shows players through walls.");
    }

    @SubscribeEvent
    public void onEntityJoinWorld(final EntityJoinWorldEvent event) {
        if (!Cache.isInDungeon || !(event.entity instanceof EntityPlayer)) return;
        ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam) ((EntityPlayer) event.entity).getTeam();
        if (scoreplayerteam == null || scoreplayerteam.getNameTagVisibility() == Team.EnumVisible.NEVER) return;
        event.entity.setInvisible(false);
        highlightedEntities.add(event.entity);
    }

    @SubscribeEvent
    public void onRenderEntityModel(RenderEntityModelEvent event) {
        if (!Config.playerESP | !Cache.isInDungeon || highlightedEntities.isEmpty() || !highlightedEntities.contains(event.entity))
            return;
        OutlineUtils.outlineEntity(event);
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        highlightedEntities.clear();
    }
}
