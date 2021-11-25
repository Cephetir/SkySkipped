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

package cephetir.skyskipped.utils;

import cephetir.skyskipped.config.Config;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.lang.reflect.Field;

public class PacketUtils extends ChannelDuplexHandler {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Packet && msg.getClass().getName().endsWith("S08PacketPlayerPosLook")) {
            if(!Config.noRotate) {
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
                if (player != null) {
                    ItemStack item = player.getHeldItem();
                    if (item != null && item.getDisplayName().contains("Hyperion") || item != null && item.getDisplayName().contains("Aspect of the End")) {
                        S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) msg;
                        Field packetYaw = packet.getClass().getDeclaredField("field_148936_d");
                        Field packetPitch = packet.getClass().getDeclaredField("field_148937_e");
                        packetYaw.setAccessible(true);
                        packetPitch.setAccessible(true);
                        packetYaw.setFloat(packet, player.rotationYaw);
                        packetPitch.setFloat(packet, player.rotationPitch);
                        msg = packet;
                    }
                }
            }
        }

        super.channelRead(ctx, msg);
    }
}
