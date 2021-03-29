/*
      SimpleToggleSprint
      Copyright (C) 2021  My-Name-Is-Jeff

      This program is free software: you can redistribute it and/or modify
      it under the terms of the GNU Affero General Public License as published by
      the Free Software Foundation, either version 3 of the License, or
      (at your option) any later version.

      This program is distributed in the hope that it will be useful,
      but WITHOUT ANY WARRANTY; without even the implied warranty of
      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
      GNU Affero General Public License for more details.

      You should have received a copy of the GNU Affero General Public License
      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package mynameisjeff.simpletogglesprint.mixins;

import club.sk1er.mods.core.ModCore;
import mynameisjeff.simpletogglesprint.SimpleToggleSprint;
import mynameisjeff.simpletogglesprint.core.Config;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameMenu.class)
public class MixinGuiIngameMenu extends GuiScreen {

    @Unique
    private GuiButton configButton;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void addGuiButtons(CallbackInfo ci) {
        if (Config.showConfigOnEscape) {
            this.buttonList.add(configButton = new GuiButton(-69420, 2, this.height - 82, 100, 20, "ToggleSprint"));
        }
    }

    @Inject(method = "actionPerformed", at = @At("TAIL"))
    private void onButtonPress(GuiButton button, CallbackInfo ci) {
        if (button == configButton) {
            ModCore.getInstance().getGuiHandler().open(SimpleToggleSprint.config.gui());
        }
    }

}
