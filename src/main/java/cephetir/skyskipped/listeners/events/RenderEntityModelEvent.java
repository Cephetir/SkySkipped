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

package cephetir.skyskipped.listeners.events;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderEntityModelEvent extends Event {
    public EntityLivingBase entity;
    public float limbSwing;
    public float limbSwingAmount;
    public float ageInTicks;
    public float headYaw;
    public float headPitch;
    public float scaleFactor;
    public ModelBase model;

    public RenderEntityModelEvent(final EntityLivingBase entity, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float headYaw, final float headPitch, final float scaleFactor, final ModelBase model) {
        this.entity = entity;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.headYaw = headYaw;
        this.headPitch = headPitch;
        this.scaleFactor = scaleFactor;
        this.model = model;
    }
}