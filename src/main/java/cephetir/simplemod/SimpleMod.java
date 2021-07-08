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

package cephetir.simplemod;


import cephetir.simplemod.commands.SimpleModCommand;
import cephetir.simplemod.core.Config;
import cephetir.simplemod.tweaker.ModCoreInstaller;
import cephetir.simplemod.utils.ConfigColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.function.Supplier;

@Mod(modid = SimpleMod.MODID, name = SimpleMod.MOD_NAME, version = SimpleMod.VERSION, acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class SimpleMod {

    public static final String MODID = "simplemod";
    public static final String MOD_NAME = "SimpleMod";
    public static final String VERSION = "1.0";
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static final KeyBinding keySprint = new KeyBinding("Toggle Sprint", Keyboard.KEY_NONE, "SimpleMod");
    public static final KeyBinding keySneak = new KeyBinding("Toggle Sneak", Keyboard.KEY_NONE, "SimpleMod");
    public static Config config = new Config();
    public static boolean sprintHeld = false;
    public static boolean sneakHeld = false;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        ModCoreInstaller.initializeModCore(mc.mcDataDir);
        config.preload();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new SimpleModCommand());
        ClientRegistry.registerKeyBinding(keySprint);
        ClientRegistry.registerKeyBinding(keySneak);
    }

    @SubscribeEvent
    public void onInput(InputEvent event) {

        int sprint = mc.gameSettings.keyBindSprint.getKeyCode();
        int sneak = mc.gameSettings.keyBindSneak.getKeyCode();

        int sprintToggle = keySprint.getKeyCode();
        int sneakToggle = keySneak.getKeyCode();

        if (Config.keybindToggleSprint && sprintToggle > 0 ? Keyboard.isKeyDown(sprintToggle) : Mouse.isButtonDown(sprintToggle + 100)) {
            if (Config.enabledToggleSprint && !sprintHeld) {
                Config.toggleSprintState = !Config.toggleSprintState;
                config.markDirty();
            }
            sprintHeld = true;
        } else if (!Config.keybindToggleSprint && sprint > 0 ? Keyboard.isKeyDown(sprint) : Mouse.isButtonDown(sprint + 100)) {
            if (Config.enabledToggleSprint && !sprintHeld) {
                Config.toggleSprintState = !Config.toggleSprintState;
                config.markDirty();
            }
            sprintHeld = true;
        } else {
            sprintHeld = false;
        }

        if (Config.keybindToggleSneak && sneakToggle > 0 ? Keyboard.isKeyDown(sneakToggle) : Mouse.isButtonDown(sneakToggle + 100)) {
            if (Config.enabledToggleSneak && !sneakHeld) {
                Config.toggleSneakState = !Config.toggleSneakState;
                config.markDirty();
            }
            sneakHeld = true;
        } else if (!Config.keybindToggleSprint && sneak > 0 ? Keyboard.isKeyDown(sneak) : Mouse.isButtonDown(sneak + 100)) {
            if (Config.enabledToggleSneak && !sneakHeld) {
                Config.toggleSneakState = !Config.toggleSneakState;
                config.markDirty();
            }
            sneakHeld = true;
        } else {
            sneakHeld = false;
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR) return;
        if (Config.displayToggleState && (mc.currentScreen == null || Config.showInGui) && mc.thePlayer != null) {
            ScaledResolution sr = new ScaledResolution(mc);

            String active = DisplayState.getActiveDisplay();
            if (active == null) return;
            double x = sr.getScaledWidth_double() * (Config.displayStateX / 1000d);
            double y = sr.getScaledHeight_double() * (Config.displayStateY / 1000d);
            double scale = Config.displayStateScale / 1000f;

            double xOffset = (Config.displayStateAlignment != 0 ? (-mc.fontRendererObj.getStringWidth(active) / (float) Config.displayStateAlignment) : 0) * scale;

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, 0);
            GlStateManager.scale(scale, scale, 1d);

            mc.fontRendererObj.drawString(active, (float) xOffset, 0, ConfigColor.getColor(), Config.displayStateShadow);
            if (Config.displayBackground) {
                GlStateManager.translate(0, 0, -1);
                Gui.drawRect((int) xOffset - 2, -2, (int) xOffset + mc.fontRendererObj.getStringWidth(active) + 1, mc.fontRendererObj.FONT_HEIGHT + 1, ConfigColor.ColorEnum.BLACK);
                GlStateManager.translate(0, 0, 1);
            }

            GlStateManager.popMatrix();
        }
    }

    private enum DisplayState {
        DESCENDINGHELD("[Descending (key held)]", () -> mc.thePlayer.capabilities.isFlying && mc.thePlayer.isSneaking() && sneakHeld),
        DESCENDINGTOGGLED("[Descending (toggled)]", () -> mc.thePlayer.capabilities.isFlying && Config.enabledToggleSneak && Config.toggleSneakState),
        DESCENDING("[Descending (vanilla)]", () -> mc.thePlayer.capabilities.isFlying && mc.thePlayer.isSneaking()),
        FLYING("[Flying]", () -> mc.thePlayer.capabilities.isFlying),
        RIDING("[Riding]", () -> mc.thePlayer.isRiding()),
        SNEAKHELD("[Sneaking (key held)]", () -> mc.thePlayer.isSneaking() && sneakHeld),
        TOGGLESNEAK("[Sneaking (toggled)]", () -> Config.enabledToggleSneak && Config.toggleSneakState),
        SNEAKING("[Sneaking (vanilla)]", () -> mc.thePlayer.isSneaking()),
        SPRINTHELD("[Sprinting (key held)]", () -> mc.thePlayer.isSprinting() && sprintHeld),
        TOGGLESPRINT("[Sprinting (toggled)]", () -> Config.enabledToggleSprint && Config.toggleSprintState),
        SPRINTING("[Sprinting (vanilla)]", () -> mc.thePlayer.isSprinting());

        public final String displayText;
        public final Supplier<Boolean> displayCheck;

        DisplayState(String display, Supplier<Boolean> displayCheck) {
            this.displayText = display;
            this.displayCheck = displayCheck;
        }

        public static String getActiveDisplay() {
            if (mc.thePlayer == null) return null;
            for (DisplayState state : DisplayState.values()) {
                if (state.isActive()) return state.displayText;
            }
            return null;
        }

        public boolean isActive() {
            return this.displayCheck.get();
        }
    }

    public static boolean shouldSetSprint(KeyBinding keyBinding) {
        return keyBinding.isKeyDown() || (Config.enabledToggleSprint && Config.toggleSprintState && keyBinding == mc.gameSettings.keyBindSprint && mc.currentScreen == null);
    }
}