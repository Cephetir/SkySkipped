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

package me.cephetir.skyskipped.mixins.render;

import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.features.impl.optimization.HideDamageInBoss;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Render.class)
public abstract class MixinRender {
    @Shadow
    @Final
    protected RenderManager renderManager;

    @Shadow
    public abstract FontRenderer getFontRendererFromRenderManager();

    @Inject(method = "renderLivingLabel", at = @At("HEAD"), cancellable = true)
    private void damageDepthOverride(Entity entityIn, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
        if (!Config.showDamage.getValue() || !HideDamageInBoss.Companion.isDamageText(entityIn)) return;
        ci.cancel();

        double d = entityIn.getDistanceSqToEntity(this.renderManager.livingPlayer);
        if (d > (double) (maxDistance * maxDistance)) return;
        FontRenderer fontRenderer = this.getFontRendererFromRenderManager();
        float f = 1.6f;
        float g = 0.016666668f * f;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.0f, (float) y + entityIn.height + 0.5f, (float) z);
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-g, -g, g);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        int j = fontRenderer.getStringWidth(str) / 2;
        GlStateManager.disableTexture2D();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(-j - 1, -1, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
        worldRenderer.pos(-j - 1, 8, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
        worldRenderer.pos(j + 1, 8, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
        worldRenderer.pos(j + 1, -1, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        fontRenderer.drawString(str, -fontRenderer.getStringWidth(str) / 2, 0, 0x20FFFFFF);
        fontRenderer.drawString(str, -fontRenderer.getStringWidth(str) / 2, 0, -1);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
}
