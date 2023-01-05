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

package me.cephetir.skyskipped.mixins;

import gg.essential.lib.mixinextras.injector.ModifyReturnValue;
import me.cephetir.bladecore.utils.TextUtils;
import me.cephetir.skyskipped.config.Cache;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.features.impl.hacks.ShinyBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Timer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Shadow
    public GameSettings gameSettings;

    @Shadow
    public boolean inGameHasFocus;

    @Shadow
    public MovingObjectPosition objectMouseOver;

    @Shadow
    public PlayerControllerMP playerController;

    @Shadow
    public WorldClient theWorld;

    @Shadow
    public EntityPlayerSP thePlayer;

    @Shadow
    public EntityRenderer entityRenderer;

    @Shadow
    private Timer timer;

    @Shadow
    private static int debugFPS;

    @Inject(method = "sendClickBlockToController", at = @At("HEAD"), cancellable = true)
    private void sendClickBlockToControllerHead(CallbackInfo ci) {
        if (ShinyBlocks.Companion.getShouldBreak()) {
            ShinyBlocks.Companion.mine();
            ci.cancel();
            return;
        }

        if (!Config.Companion.getStopBreaking() || Config.Companion.getStopBreakingList().isEmpty() || this.objectMouseOver == null || this.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
            return;

        String[] blocks = Config.Companion.getStopBreakingList().split(", ");
        BlockPos blockpos = this.objectMouseOver.getBlockPos();
        Block block = this.theWorld.getBlockState(blockpos).getBlock();

        if (TextUtils.INSTANCE.containsAny(block.getUnlocalizedName(), blocks))
            ci.cancel();
    }

    @Inject(method = "sendClickBlockToController", at = @At("RETURN"))
    private void sendClickBlockToController(CallbackInfo ci) {
        if (!Config.Companion.getFastBreak() || !Cache.INSTANCE.getOnIsland()) return;

        int extraClicks = Config.Companion.getFastBreakNumber();
        boolean shouldClick = Minecraft.getMinecraft().currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() && this.inGameHasFocus;
        if (this.objectMouseOver != null && extraClicks > 0 && shouldClick)
            for (int i = 0; i < extraClicks; i++) {
                BlockPos prevBlockPos = this.objectMouseOver.getBlockPos();
                BlockPos blockpos;
                this.entityRenderer.getMouseOver(this.timer.renderPartialTicks);
                if (this.objectMouseOver == null
                        || (blockpos = this.objectMouseOver.getBlockPos()) == null
                        || this.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK
                        || blockpos.equals(prevBlockPos)
                        || this.theWorld.getBlockState(blockpos).getBlock().getMaterial() == Material.air
                        || TextUtils.INSTANCE.containsAny(this.theWorld.getBlockState(blockpos).getBlock().getUnlocalizedName(), Config.Companion.getStopBreakingList())
                ) break;
                this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
                if (i % 3 == 0) this.thePlayer.swingItem();
            }
    }

    @ModifyReturnValue(method = "getDebugFPS", at = @At("RETURN"))
    private static int getDebugFPSRedirect(int original) {
        return Config.Companion.getFpsSpoof() ? original + Config.Companion.getFpsSpoofNumber() : original;
    }

    @Redirect(method = "runGameLoop", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;debugFPS:I", opcode = Opcodes.GETSTATIC, ordinal = 0))
    private int runGameLoopRedirect() {
        return Config.Companion.getFpsSpoof() ? debugFPS + Config.Companion.getFpsSpoofNumber() : debugFPS;
    }
}
