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

package me.cephetir.skyskipped.mixins;

import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPlayerSP.class)
public interface IMixinEntityPlayerSP {
    @Accessor("lastReportedYaw")
    float getLastReportedYaw();

    @Accessor("lastReportedPitch")
    float getLastReportedPitch();

    @Accessor("lastReportedYaw")
    void setLastReportedYaw(float yaw);

    @Accessor("lastReportedPitch")
    void setLastReportedPitch(float pitch);

    @Accessor("lastReportedPosX")
    double getLastReportedPosX();

    @Accessor("lastReportedPosX")
    void setLastReportedPosX(double x);

    @Accessor("lastReportedPosY")
    double getLastReportedPosY();

    @Accessor("lastReportedPosY")
    void setLastReportedPosY(double y);

    @Accessor("lastReportedPosZ")
    double getLastReportedPosZ();

    @Accessor("lastReportedPosZ")
    void setLastReportedPosZ(double z);
}
