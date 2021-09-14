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

package cephetir.skyskipped.gui;

import cephetir.skyskipped.gui.hud.HUDManager;
import cephetir.skyskipped.gui.hud.IRenderConfig;
import cephetir.skyskipped.gui.hud.IRenderer;
import cephetir.skyskipped.gui.hud.ScreenPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Predicate;

public class EditLocations extends GuiScreen {
    private final HashMap<IRenderer, ScreenPosition> renderers = new HashMap<IRenderer, ScreenPosition>();
    private Optional<IRenderer> selectedRenderer = Optional.empty();

    private int prevX, prevY;

    public EditLocations() {
    }

    public EditLocations(HUDManager api) {
        Collection<IRenderer> registeredRenderers = api.getRegisteredRenderers();

        for (IRenderer ren : registeredRenderers) {

            ScreenPosition pos = ren.load();

            if (pos == null) {
                pos = ScreenPosition.fromRelativePosition(0.5, 0.5);
            }

            adjustBounds(ren, pos);

            this.renderers.put(ren, pos);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        float zBackup = this.zLevel;
        this.zLevel = 200;

        this.drawHollowRect(0, 0, this.width - 1, this.height - 1, 0xFFFF0000);

        for (IRenderer renderer : renderers.keySet()) {
            ScreenPosition pos = renderers.get(renderer);
            renderer.renderDummy(pos);

            this.drawHollowRect(pos.getAbsoluteX(), pos.getAbsoluteY(), renderer.getWidth(), renderer.getHeight(), 0xFF00FFFF);
        }
        this.zLevel = zBackup;
    }

    private void drawHollowRect(int x, int y, int w, int h, int c) {
        this.drawHorizontalLine(x, x + w, y, c);
        this.drawHorizontalLine(x, x + w, y + h, c);

        this.drawVerticalLine(x, y + h, y, c);
        this.drawVerticalLine(x + w, y + h, y, c);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            renderers.forEach(IRenderConfig::save);
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void mouseClickMove(int x, int y, int button, long time) {
        if (selectedRenderer.isPresent()) {
            moveSelectedRendererBy(x - prevX, y - prevY);
        }
        this.prevX = x;
        this.prevY = y;
    }

    private void moveSelectedRendererBy(int offsetX, int offsetY) {
        IRenderer renderer = selectedRenderer.get();
        ScreenPosition position = renderers.get(renderer);

        position.setAbsolute(position.getAbsoluteX() + offsetX, position.getAbsoluteY() + offsetY);

        adjustBounds(renderer, position);
    }

    @Override
    public void onGuiClosed() {
        for (IRenderer renderer : renderers.keySet()) {
            renderer.save(renderers.get(renderer));
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    private void adjustBounds(IRenderer renderer, ScreenPosition pos) {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

        int screenWidth = res.getScaledWidth();
        int screenHeight = res.getScaledHeight();

        int absoluteX = Math.max(0, Math.min(pos.getAbsoluteX(), Math.max(screenWidth - renderer.getWidth(), 0)));
        int absoluteY = Math.max(0, Math.min(pos.getAbsoluteY(), Math.max(screenHeight - renderer.getHeight(), 0)));

        pos.setAbsolute(absoluteX, absoluteY);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        this.prevX = x;
        this.prevY = y;

        loadMouseOver(x, y);
    }

    private void loadMouseOver(int x, int y) {
        this.selectedRenderer = renderers.keySet().stream()
                .filter(new MouseOverFinder(x, y))
                .findFirst();
    }

    private class MouseOverFinder implements Predicate<IRenderer> {

        private final int mouseX;
        private final int mouseY;

        public MouseOverFinder(int mouseX, int mouseY) {
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        @Override
        public boolean test(IRenderer renderer) {
            ScreenPosition pos = renderers.get(renderer);

            int absoluteX = pos.getAbsoluteX();
            int absoluteY = pos.getAbsoluteY();

            if (mouseX >= absoluteX && mouseX <= absoluteX + renderer.getWidth()) {
                return mouseY >= absoluteY && mouseY <= absoluteY + renderer.getHeight();
            }

            return false;
        }

    }
}
