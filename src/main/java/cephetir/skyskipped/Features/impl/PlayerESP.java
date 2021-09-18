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

package cephetir.skyskipped.Features.impl;

import cephetir.skyskipped.Features.Feature;
import cephetir.skyskipped.config.Cache;
import cephetir.skyskipped.config.Config;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class PlayerESP extends Feature {
    private boolean preCalled = false;

    public PlayerESP() {
        super("Player ESP", "Dungeons", "Shows players through walls.");
    }

    @SuppressWarnings("all")
    @SubscribeEvent
    public void onEntityRenderPre(RenderPlayerEvent.Pre event) {
        if (preCalled && !Config.playerESP && !Cache.isInDungeon) return;
        if (event.entityPlayer instanceof EntityOtherPlayerMP) return;
        ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam) event.entityPlayer.getTeam();
        if (scoreplayerteam != null && scoreplayerteam.getNameTagVisibility() != Team.EnumVisible.NEVER) ;
        else return;
        preCalled = true;

        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClearStencil(0);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

        GL11.glStencilMask(0xFF);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_REPLACE, GL11.GL_REPLACE);

        EntityPlayer entity = event.entityPlayer;
        InventoryPlayer inv = entity.inventory;
        ItemStack[] armor = inv.armorInventory;
        inv.armorInventory = new ItemStack[4];
        ItemStack[] hand = inv.mainInventory;
        inv.mainInventory = new ItemStack[36];

        float f = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * event.partialRenderTick;
        try {
            event.renderer.doRender((AbstractClientPlayer) event.entityPlayer, event.x, event.y, event.z, f, event.partialRenderTick);
        } catch (Throwable ignored) {
        }

        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xff);
        GL11.glDepthMask(false);
        GL11.glDepthFunc(GL11.GL_GEQUAL);

        GlStateManager.pushMatrix();
        GlStateManager.translate(event.x, event.y + 0.9, event.z);
        GlStateManager.scale(1.2f, 1.1f, 1.2f);
        event.renderer.setRenderOutlines(true);
        try {
            event.renderer.doRender((AbstractClientPlayer) event.entityPlayer, 0, -0.9, 0, f, event.partialRenderTick);
        } catch (Throwable ignored) {
        }

        event.renderer.setRenderOutlines(false);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GlStateManager.popMatrix();

        GL11.glDisable(GL11.GL_STENCIL_TEST); // Turn this shit off!

        inv.armorInventory = armor;
        inv.mainInventory = hand;

        preCalled = false;
    }
}
