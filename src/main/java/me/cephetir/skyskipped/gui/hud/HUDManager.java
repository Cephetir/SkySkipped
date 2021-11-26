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

package me.cephetir.skyskipped.gui.hud;

import com.google.common.collect.Sets;
import me.cephetir.skyskipped.gui.EditLocations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class HUDManager {
    private static HUDManager INSTANCE = null;
    private final Set<IRenderer> registerRenderers = Sets.newHashSet();
    private final Minecraft mc = Minecraft.getMinecraft();

    public HUDManager() {
    }

    public static HUDManager getINSTANCE() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new HUDManager();
        MinecraftForge.EVENT_BUS.register(INSTANCE);

        return INSTANCE;
    }

    public void openConfigScreen() {
        mc.displayGuiScreen(new EditLocations(this));
    }

    public void register(IRenderer... renderers) {
        this.registerRenderers.addAll(Arrays.asList(renderers));
    }

    public void unregister(IRenderer... renderers) {
        for (IRenderer renderer : renderers) {
            this.registerRenderers.remove(renderer);
        }
    }

    public Collection<IRenderer> getRegisteredRenderers() {
        return Sets.newHashSet(registerRenderers);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        if (mc.currentScreen == null || mc.currentScreen instanceof GuiContainer || mc.currentScreen instanceof GuiChat) {
            for (IRenderer renderer : registerRenderers) {
                callRenderer(renderer);
            }
        }
    }

    private void callRenderer(IRenderer renderer) {
        ScreenPosition position = renderer.load();

        if (position == null) {
            position = ScreenPosition.fromRelativePosition(0.5, 0.5);
        }

        renderer.render(position);
    }
}
