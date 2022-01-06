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

import gg.essential.universal.ChatColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PingUtils {
    private final Minecraft mc = Minecraft.getMinecraft();
    private int ticks;
    private final String text;

    public PingUtils(int ticks, String text) {
        this.ticks = ticks;
        this.text = text;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (ticks <= 0) MinecraftForge.EVENT_BUS.unregister(this);
        ticks--;
    }

    @SubscribeEvent
    public void draw(RenderGameOverlayEvent.Text event) {
        if(ticks % 5 == 0) mc.thePlayer.playSound("random.orb", 1f, 1f);
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.5f, 1.5f, 1.5f);
        mc.fontRendererObj.drawStringWithShadow(ChatColor.DARK_RED + text,
                event.resolution.getScaledWidth() / 1.5f / 2f - mc.fontRendererObj.getStringWidth(text) / 2f,
                event.resolution.getScaledHeight() / 1.5f / 2f - 6.75f, -1);
        GlStateManager.popMatrix();
    }
}
