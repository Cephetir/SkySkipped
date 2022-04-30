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

import me.cephetir.skyskipped.event.events.UpdateWalkingPlayerEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityPlayerSP.class, priority = Integer.MAX_VALUE)
public abstract class MixinEntityPlayerSP {

    @Shadow
    private boolean serverSprintState;

    @Shadow
    private boolean serverSneakState;

    @Shadow
    protected abstract boolean isCurrentViewEntity();

    @Shadow
    private double lastReportedPosX;

    @Shadow
    private double lastReportedPosY;

    @Shadow
    private double lastReportedPosZ;

    @Shadow
    private float lastReportedYaw;

    @Shadow
    private float lastReportedPitch;

    @Shadow
    private int positionUpdateTicks;

    /**
     * @author Oringo
     */
    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    public void onUpdateWalkingPlayer(CallbackInfo ci) {
        EntityPlayerSP it = (EntityPlayerSP) (Object) this;
        UpdateWalkingPlayerEvent.Pre event = new UpdateWalkingPlayerEvent.Pre(it.posX, it.getEntityBoundingBox().minY, it.posZ, it.rotationYaw, it.rotationPitch, it.onGround, it.isSprinting(), it.isSneaking());

        if (!MinecraftForge.EVENT_BUS.post(event)) {
            ci.cancel();
            boolean flag = event.getSprinting();
            if (flag != this.serverSprintState) {
                if (flag)
                    it.sendQueue.addToSendQueue(new C0BPacketEntityAction(it, C0BPacketEntityAction.Action.START_SPRINTING));
                else
                    it.sendQueue.addToSendQueue(new C0BPacketEntityAction(it, C0BPacketEntityAction.Action.STOP_SPRINTING));

                this.serverSprintState = flag;
            }

            boolean flag1 = event.getSneaking();
            if (flag1 != this.serverSneakState) {
                if (flag1)
                    it.sendQueue.addToSendQueue(new C0BPacketEntityAction(it, C0BPacketEntityAction.Action.START_SNEAKING));
                else
                    it.sendQueue.addToSendQueue(new C0BPacketEntityAction(it, C0BPacketEntityAction.Action.STOP_SNEAKING));

                this.serverSneakState = flag1;
            }

            if (this.isCurrentViewEntity()) {
                double d0 = event.getX() - this.lastReportedPosX;
                double d1 = event.getY() - this.lastReportedPosY;
                double d2 = event.getZ() - this.lastReportedPosZ;
                double d3 = event.getYaw() - this.lastReportedYaw;
                double d4 = event.getPitch() - this.lastReportedPitch;
                boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4 || this.positionUpdateTicks >= 20;
                boolean flag3 = d3 != 0.0 || d4 != 0.0;
                if (it.ridingEntity == null)
                    if (flag2 && flag3)
                        it.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(event.getX(), event.getY(), event.getZ(), event.getYaw(), event.getPitch(), event.getOnGround()));
                    else if (flag2)
                        it.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(event.getX(), event.getY(), event.getZ(), event.getOnGround()));
                    else if (flag3)
                        it.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(event.getYaw(), event.getPitch(), event.getOnGround()));
                    else
                        it.sendQueue.addToSendQueue(new C03PacketPlayer(event.getOnGround()));
                else {
                    it.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(it.motionX, -999.0, it.motionZ, event.getYaw(), event.getPitch(), event.getOnGround()));
                    flag2 = false;
                }

                ++this.positionUpdateTicks;
                if (flag2) {
                    this.lastReportedPosX = event.getX();
                    this.lastReportedPosY = event.getY();
                    this.lastReportedPosZ = event.getZ();
                    this.positionUpdateTicks = 0;
                }

                if (flag3) {
                    this.lastReportedYaw = event.getYaw();
                    this.lastReportedPitch = event.getPitch();
                }
            }

            MinecraftForge.EVENT_BUS.post(new UpdateWalkingPlayerEvent.Post(event));
        }
    }
}
