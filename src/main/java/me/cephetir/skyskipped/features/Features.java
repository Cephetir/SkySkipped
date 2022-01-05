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

package me.cephetir.skyskipped.features;

import lombok.Getter;
import me.cephetir.skyskipped.commands.fragrun.FragRun;
import me.cephetir.skyskipped.commands.fragrun.LeaveCommand;
import me.cephetir.skyskipped.commands.fragrun.PartyCommand;
import me.cephetir.skyskipped.features.impl.LastCrit;
import me.cephetir.skyskipped.features.impl.chat.ChatSwapper;
import me.cephetir.skyskipped.features.impl.chat.Nons;
import me.cephetir.skyskipped.features.impl.chat.Ping;
import me.cephetir.skyskipped.features.impl.dungeons.ChestCloser;
import me.cephetir.skyskipped.features.impl.dungeons.PizzaFailSafe;
import me.cephetir.skyskipped.features.impl.dungeons.PlayerESP;
import me.cephetir.skyskipped.features.impl.dungeons.ScoreCalculation;
import me.cephetir.skyskipped.features.impl.hacks.Blocker;
import me.cephetir.skyskipped.features.impl.visual.HidePetCandies;
import me.cephetir.skyskipped.features.impl.visual.PetsOverlay;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

public class Features {
    @Getter
    private final LeaveCommand leaveCommand = new LeaveCommand();
    @Getter
    private final PartyCommand partyCommand = new PartyCommand();
    @Getter
    private final ScoreCalculation scoreCalculation = new ScoreCalculation();
    @Getter
    private final PetsOverlay petsOverlay = new PetsOverlay();

    public List<Feature> features = new ArrayList<>();

    public void register() {
        features.add(new ChestCloser());
        features.add(new ChatSwapper());
        features.add(new PlayerESP());
        features.add(new LastCrit());
        features.add(new Nons());
        features.add(new Blocker());
        features.add(new Ping());
        features.add(new FragRun());
        features.add(getScoreCalculation());
        features.add(new PizzaFailSafe());
        features.add(new HidePetCandies());
        features.add(getPetsOverlay());
        features.forEach(MinecraftForge.EVENT_BUS::register);
    }
}
