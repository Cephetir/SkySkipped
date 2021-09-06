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

package cephetir.skyskipped.Features;

import cephetir.skyskipped.Features.impl.*;
import cephetir.skyskipped.Features.impl.fragrun.FragRun;
import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

public class Features {
    @Getter
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
        for (Feature feature : features) {
            MinecraftForge.EVENT_BUS.register(feature);
        }
    }
}
