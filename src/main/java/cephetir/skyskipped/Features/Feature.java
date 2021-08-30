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

import lombok.Getter;
import net.minecraft.client.Minecraft;

public abstract class Feature {

    public Minecraft mc = Minecraft.getMinecraft();

    @Getter
    private final String name;
    @Getter
    private final String category;
    @Getter
    private final String description;


    protected Feature(String name, String category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
    }
}