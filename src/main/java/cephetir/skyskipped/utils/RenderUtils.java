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

import cephetir.skyskipped.config.Cache;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static cephetir.skyskipped.SkySkipped.mc;

public class RenderUtils {
    private String title = null;
    private int titleDisplayTicks = 0;

    public void createTitle(String title, int ticks) {
        mc.thePlayer.playSound("random.orb", 0.8f, 1f);
        this.titleDisplayTicks = ticks;
        this.title = title;
    }

    /**
     * Adapted from Skytils under AGPL-3.0 license
     *
     * @link https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
     */
    private void renderTitle(ScaledResolution sc) {
        if ((mc.theWorld == null || mc.thePlayer == null || !Cache.inSkyblock) && title == null) return;
        int scaledWidth = sc.getScaledWidth();
        int scaledHeight = sc.getScaledHeight();
        float stringWidth = mc.fontRendererObj.getStringWidth(title);
        float scale = 4F;
        if (stringWidth * scale > scaledWidth * 0.9f) {
            scale = scaledWidth * 0.9f / stringWidth;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(scaledWidth / 2F, scaledHeight / 2F, 0.0f);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        mc.fontRendererObj.drawString(title, (-mc.fontRendererObj.getStringWidth(title) / 2F), -20.0f, 0xFF0000, true);
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (titleDisplayTicks > 0) {
            titleDisplayTicks--;
        } else if (titleDisplayTicks == 0) {
            titleDisplayTicks = -1;
            title = null;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderHUD(RenderGameOverlayEvent event) {
        renderTitle(event.resolution);
    }
}
