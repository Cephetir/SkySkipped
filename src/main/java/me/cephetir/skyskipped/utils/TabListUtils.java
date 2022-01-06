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

package me.cephetir.skyskipped.utils;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.world.WorldSettings;

import java.util.Collections;
import java.util.List;

public class TabListUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static List<NetworkPlayerInfo> fetchTabEntires() {
        if(mc.thePlayer == null) return Collections.emptyList();
        else return playerInfoOrdering.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap());
    }

    private static final Ordering<NetworkPlayerInfo> playerInfoOrdering = new Ordering<NetworkPlayerInfo>() {
        public int compare(NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_) {
            ScorePlayerTeam scorePlayerTeam = p_compare_1_ != null ? p_compare_1_.getPlayerTeam() : null;
            ScorePlayerTeam scorePlayerTeam1 = p_compare_2_ != null ? p_compare_2_.getPlayerTeam() : null;
            if (p_compare_1_ == null) {
                return -1;
            } else if (p_compare_2_ == null) {
                return 0;
            } else {
                ComparisonChain var10000 = ComparisonChain.start().compareTrueFirst(p_compare_1_.getGameType() != WorldSettings.GameType.SPECTATOR, p_compare_2_.getGameType() != WorldSettings.GameType.SPECTATOR).compare(scorePlayerTeam != null ? scorePlayerTeam.getRegisteredName() : "", scorePlayerTeam1 != null ? scorePlayerTeam1.getRegisteredName() : "");
                GameProfile var10001 = p_compare_1_.getGameProfile();
                Intrinsics.checkNotNullExpressionValue(var10001, "p_compare_1_.gameProfile");
                Comparable<String> var5 = var10001.getName();
                GameProfile var10002 = p_compare_2_.getGameProfile();
                Intrinsics.checkNotNullExpressionValue(var10002, "p_compare_2_.gameProfile");
                return var10000.compare(var5, var10002.getName()).result();
            }
        }
    };
}
