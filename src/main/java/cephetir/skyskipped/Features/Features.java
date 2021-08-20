/*
 * SkySkipped - Hypixel Skyblock mod
 * Copyright (C) 2021  Cephetir
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cephetir.skyskipped.Features;

import cephetir.skyskipped.Features.impl.*;
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
        for (Feature feature : features) {
            MinecraftForge.EVENT_BUS.register(feature);
        }
    }
}
