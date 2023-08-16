/*
 * SkySkipped - Hypixel Skyblock QOL mod
 * Copyright (C) 2023  Cephetir
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

package me.cephetir.skyskipped.transformers;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;

public class ModsData {
    @SerializedName("fileNames")
    private final LinkedList<String> fileNames = new LinkedList<>();

    @SerializedName("modNames")
    private final LinkedList<String> modNames = new LinkedList<>();

    @SerializedName("fileHashes")
    private final LinkedList<String> fileHashes = new LinkedList<>();

    public void addFilename(String name) {
        fileNames.add(name);
    }

    public void addModname(String modname) {
        modNames.add(modname);
    }

    public void addFileHashes(String hash) {
        fileHashes.add(hash);
    }
}
