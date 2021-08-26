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

    @SubscribeEvent
    public void onEntityRenderPre(RenderPlayerEvent.Pre event) {
        if (preCalled) return;
        if (!Config.playerESP) return;
        if (!Cache.isInDungeon) return;
        boolean ctue = false;
        if (event.entityPlayer instanceof EntityOtherPlayerMP) {
            ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam) event.entityPlayer.getTeam();
            if (scoreplayerteam != null && scoreplayerteam.getNameTagVisibility() != Team.EnumVisible.NEVER) {
                ctue = true;
            }
        }
        if (!ctue) return;
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
