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

package me.cephetir.skyskipped.features.impl.visual;

import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.features.Feature;
import me.cephetir.skyskipped.mixins.IMixinGuiContainer;
import me.cephetir.skyskipped.utils.RarityUtils;
import me.cephetir.skyskipped.utils.RoundUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PetsOverlay extends Feature {
    private GuiPetsOverlay petsOverlay = null;
    public int auto = -1;

    public PetsOverlay() {
        super("PetsOverlay", "Visual", "Good looking overlay for pets menu");
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onGuiOpen(GuiOpenEvent event) {
        petsOverlay = null;
        if (!Config.petsOverlay) return;
        if (!(event.gui instanceof GuiChest)) return;
        GuiChest chest = (GuiChest) event.gui;
        ContainerChest container = (ContainerChest) chest.inventorySlots;
        String containerName = container.getLowerChestInventory().getDisplayName().getUnformattedText();
        if (!containerName.endsWith("Pets")) return;
        petsOverlay = new GuiPetsOverlay(chest);
        new Thread(() -> {
            try {
                Thread.sleep(50L);
                petsOverlay.setSize(chest);
                petsOverlay.getPets();
            } catch (Exception ignored) {
            }
        }).start();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (!Config.petsOverlay) return;
        if (petsOverlay == null) return;
        event.setCanceled(true);
        petsOverlay.setSize((GuiChest) event.gui);
        petsOverlay.onDrawScreen(event.mouseX, event.mouseY);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onActionPerformed(GuiScreenEvent.MouseInputEvent event) {
        if (!Config.petsOverlay) return;
        if (petsOverlay == null) return;
        event.setCanceled(true);
        int i = Mouse.getEventX() * event.gui.width / this.mc.displayWidth;
        int j = event.gui.height - Mouse.getEventY() * event.gui.height / this.mc.displayHeight - 1;
        petsOverlay.onMouseClicked(i, j, Mouse.getEventButton());
    }

    int ticks = 0;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (!Config.petsOverlay) return;
        if (petsOverlay == null) return;
        ticks++;
        if (ticks == 10) {
            ticks = 0;
            petsOverlay.getPets();
        }
    }

    public static Slot getPet(int index, GuiChest chest) {
        int i = 0;
        if (!chest.inventorySlots.inventorySlots.isEmpty()) {
            for (Slot slot : chest.inventorySlots.inventorySlots) {
                if (slot.slotNumber > 53) break;
                if (slot.getHasStack()) {
                    NBTTagCompound compound = slot.getStack().getTagCompound().getCompoundTag("display");
                    String displayName = compound.getString("Name");
                    if (displayName.toLowerCase().contains("[lvl ")) {
                        i++;
                        if (i == index) return slot;
                    }
                }
            }
        }
        return null;
    }

    public class GuiPetsOverlay {
        private GuiChest chest;
        private List<Pet> pets = new ArrayList<>();
        private int width, height;
        private int rectWidth;
        private int rectWidth1;
        private int rectHeight;
        private int rectHeight1;
        private int bottom;
        private int j = 0;

        private Slot autopet;
        private Slot close;
        private Slot convert;
        private Slot hide;
        private Slot nextPage;
        private Slot previousPage;

        private final Pattern PET_PATTERN = Pattern.compile("§7\\[Lvl \\d+] (?<color>§[0-9a-fk-or]).+");

        public GuiPetsOverlay(GuiChest chest) {
            this.chest = chest;
            this.width = chest.width;
            this.height = chest.height;
        }

        public void getPets() {
            List<Pet> pets1 = new ArrayList<>();
            int i = 0;
            int j = 0;
            int k = 0;
            if (!chest.inventorySlots.inventorySlots.isEmpty()) for (Slot slot : chest.inventorySlots.inventorySlots) {
                if (slot.slotNumber > 53) break;
                if (slot.getHasStack()) {
                    NBTTagCompound compound = slot.getStack().getTagCompound().getCompoundTag("display");
                    String displayName = compound.getString("Name");
                    if (displayName.toLowerCase().contains("[lvl ")) {
                        if (rectWidth1 + 5 + k * 32 > width / 2 - rectWidth1) {
                            k = 0;
                            j++;
                        }
                        pets1.add(new Pet(rectWidth1 + 5 + k * 32, rectHeight1 + 5 + j * 32, displayName, slot, i, slot.getStack()));
                        i++;
                        k++;

                        if(auto == i) {
                            IMixinGuiContainer container = (IMixinGuiContainer) chest;
                            container.handleMouseClick(slot, slot.slotNumber, 0, 0);
                            auto = -1;
                            mc.thePlayer.closeScreen();
                        }

                        for (String text : slot.getStack().getTooltip(mc.thePlayer, false)) {
                            if (text.contains("Click to summon")) {
                                pets1.get(i - 1).last = false;
                                break;
                            } else if (text.contains("Click to despawn")) {
                                pets1.get(i - 1).last = true;
                                break;
                            }
                        }


                        Matcher m = PET_PATTERN.matcher(displayName);
                        if (m.matches())
                            pets1.get(i - 1).rarity = Objects.requireNonNull(RarityUtils.byBaseColor(m.group("color"))).getColorToRender().getRGB();

                    } else if (displayName.toLowerCase().contains("autopet")) this.autopet = slot;
                    else if (displayName.toLowerCase().contains("close")) this.close = slot;
                    else if (displayName.toLowerCase().contains("convert pet")) this.convert = slot;
                    else if (displayName.toLowerCase().contains("hide pets")) this.hide = slot;
                    else if (displayName.toLowerCase().contains("next page")) this.nextPage = slot;
                    else if (displayName.toLowerCase().contains("previous page")) this.previousPage = slot;
                }
            }

            pets = pets1;
            this.j = j;
        }

        public void onDrawScreen(int mouseX, int mouseY) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();

            rectWidth = width / 5;
            rectWidth1 = width / 2 / 5;
            rectHeight = height / 5;
            rectHeight1 = height / 2 / 5;
            bottom = Math.max(height - height / 5, height / 5 + 5 + j * 32); // WHY NO WORK AAAAAAAAAA
            Gui.drawRect(rectWidth - 20, rectHeight, width - rectWidth + 20, bottom, new Color(0, 0, 0, 105).getRGB());

            GlStateManager.scale(1.5f, 1.5f, 1.5f);
            mc.fontRendererObj.drawString("PETS", (rectWidth - 20 + 3) / 1.5f, (rectHeight - 15) / 1.5f, -1, false);

            GlStateManager.scale(2f / 1.5f, 2f / 1.5f, 2f / 1.5f);
            if (!pets.isEmpty()) for (Pet pet : pets) {
                renderItem(pet.itemStack, pet.x, pet.y);

                if (pet.last) {
                    RoundUtils.drawRoundedRect(pet.x - 0.3f, pet.y - 0.3f, pet.x + 16.3f, pet.y + 16.3f, 6, pet.rarity);
                    RoundUtils.drawRoundedOutline(pet.x - 1f, pet.y - 1f, pet.x + 17f, pet.y + 17f, 6, 3f, new Color(23, 217, 7, 255).getRGB());
                }
                RoundUtils.drawRoundedOutline(pet.x - 0.5f, pet.y - 0.5f, pet.x + 16.5f, pet.y + 16.5f, 6, 2.5f, pet.rarity);

                GlStateManager.scale(0.25f, 0.25f, 0.25f);
                mc.fontRendererObj.drawStringWithShadow(pet.name, (pet.x + 8) * 2f * 2f - mc.fontRendererObj.getStringWidth(pet.name) / 2f, (pet.y + 18) * 2f * 2f, pet.rarity);
                GlStateManager.scale(4f, 4f, 4f);
            }

            GlStateManager.scale(0.5f, 0.5f, 0.5f);

            GlStateManager.resetColor();
            int h = height - rectHeight;

            if (nextPage != null) {
                Gui.drawRect(width / 2 - 50 - 12 - 100 - 12 - 100, h - 25, width / 2 - 50 - 12 - 100 - 12, h - 25 + 20, new Color(255, 255, 255, 150).getRGB());
                mc.fontRendererObj.drawStringWithShadow("NEXT", width / 2f - 50 - 12 - 100 - 12 - 50 - mc.fontRendererObj.getStringWidth("NEXT") / 2f, h - 25 - 3, -1);
                GlStateManager.scale(2f, 2f, 2f);
                renderItem(nextPage.getStack(), (width / 2 - 50 - 12 - 100 - 12 - 66) / 2, (h - 25 - 6) / 2);
                GlStateManager.scale(0.5f, 0.5f, 0.5f);
                if (previousPage != null) {
                    Gui.drawRect(width / 2 - 50 - 12 - 100 - 12 - 100, h - 25 - 30, width / 2 - 50 - 12 - 100 - 12, h - 25 - 30 + 20, new Color(255, 255, 255, 150).getRGB());
                    mc.fontRendererObj.drawStringWithShadow("PREVIOUS", width / 2f - 50 - 12 - 100 - 12 - 50 - mc.fontRendererObj.getStringWidth("PREVIOUS") / 2f, h - 25 - 30 - 3, -1);
                    GlStateManager.scale(2f, 2f, 2f);
                    renderItem(previousPage.getStack(), (width / 2 - 50 - 12 - 100 - 12 - 66) / 2, (h - 25 - 30 - 6) / 2);
                    GlStateManager.scale(0.5f, 0.5f, 0.5f);
                }
            } else if (previousPage != null) {
                Gui.drawRect(width / 2 - 50 - 12 - 100 - 12 - 100, h - 25, width / 2 - 50 - 12 - 100 - 12, h - 25 + 20, new Color(255, 255, 255, 150).getRGB());
                mc.fontRendererObj.drawStringWithShadow("PREVIOUS", width / 2f - 50 - 12 - 100 - 12 - 50 - mc.fontRendererObj.getStringWidth("PREVIOUS") / 2f, h - 25 - 3, -1);
                GlStateManager.scale(2f, 2f, 2f);
                renderItem(previousPage.getStack(), (width / 2 - 50 - 12 - 100 - 12 - 66) / 2, (h - 25 - 6) / 2);
                GlStateManager.scale(0.5f, 0.5f, 0.5f);
            }

            if (autopet != null) {
                Gui.drawRect(width / 2 - 50 - 12 - 100, h - 25, width / 2 - 50 - 12, h - 25 + 20, new Color(255, 255, 255, 150).getRGB());
                GlStateManager.scale(2f, 2f, 2f);
                renderItem(autopet.getStack(), (width / 2 - 50 - 12 - 66) / 2, (h - 25 - 6) / 2);
                GlStateManager.scale(0.5f, 0.5f, 0.5f);
            }

            if (close != null) {
                Gui.drawRect(width / 2 - 50, h - 25, width / 2 + 50, h - 25 + 20, new Color(255, 255, 255, 150).getRGB());
                GlStateManager.scale(2f, 2f, 2f);
                renderItem(close.getStack(), (width / 2 - 50 + 50 - 16) / 2, (h - 25 - 6) / 2);
                GlStateManager.scale(0.5f, 0.5f, 0.5f);
            }

            if (convert != null) {
                Gui.drawRect(width / 2 + 50 + 12, h - 25, width / 2 + 50 + 12 + 100, h - 25 + 20, new Color(255, 255, 255, 150).getRGB());
                GlStateManager.scale(2f, 2f, 2f);
                renderItem(convert.getStack(), (width / 2 + 50 + 12 + 50 - 16) / 2, (h - 25 - 6) / 2);
                GlStateManager.scale(0.5f, 0.5f, 0.5f);
            }

            if (hide != null) {
                Gui.drawRect(width / 2 + 50 + 12 + 100 + 12, h - 25, width / 2 + 50 + 12 + 100 + 12 + 100, h - 25 + 20, new Color(255, 255, 255, 150).getRGB());
                GlStateManager.scale(2f, 2f, 2f);
                renderItem(hide.getStack(), (width / 2 + 50 + 12 + 100 + 12 + 50 - 16) / 2, (h - 25 - 6) / 2);
                GlStateManager.scale(0.5f, 0.5f, 0.5f);
            }

            GlStateManager.resetColor();
            onHover(mouseX, mouseY);

            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();
        }

        public void onMouseClicked(double mouseX, double mouseY, int button) {
            if (button == -1 || !Mouse.isButtonDown(button)) return;
            mouseX /= 2;
            mouseY /= 2;
            IMixinGuiContainer container = (IMixinGuiContainer) chest;
            for (Pet pet : pets)
                if (mouseX > pet.x && mouseY > pet.y && mouseX < pet.x + 16 && mouseY < pet.y + 16) {
                    container.handleMouseClick(pet.slot, pet.slot.slotNumber, button, 0);
                    mc.thePlayer.closeScreen();
                    return;
                }

            mouseX *= 2;
            mouseY *= 2;
            int h = height - rectHeight;
            if (mouseX > width / 2f - 50 - 12 - 100 && mouseY > h - 25 && mouseX < width / 2f - 50 - 12 && mouseY < h - 25 + 20)
                container.handleMouseClick(autopet, autopet.slotNumber, button, 0);
            else if (mouseX > width / 2f - 50 && mouseY > h - 25 && mouseX < width / 2f + 50 && mouseY < h - 25 + 20)
                container.handleMouseClick(close, close.slotNumber, button, 0);
            else if (mouseX > width / 2f + 50 + 12 && mouseY > h - 25 && mouseX < width / 2f + 50 + 12 + 100 && mouseY < h - 25 + 20)
                container.handleMouseClick(convert, convert.slotNumber, button, 0);
            else if (mouseX > width / 2f + 50 + 12 + 100 + 12 && mouseY > h - 25 && mouseX < width / 2f + 50 + 12 + 100 + 12 + 100 && mouseY < h - 25 + 20)
                container.handleMouseClick(hide, hide.slotNumber, button, 0);
            else if (nextPage != null) {
                if (mouseX > width / 2f - 50 - 12 - 100 - 12 - 100 && mouseY > h - 25 && mouseX < width / 2f - 50 - 12 - 100 - 12 && mouseY < h - 25 + 20)
                    container.handleMouseClick(nextPage, nextPage.slotNumber, button, 0);
                if (previousPage != null) {
                    if (mouseX > width / 2f - 50 - 12 - 100 - 12 - 100 && mouseY > h - 25 - 30 && mouseX < width / 2f - 50 - 12 - 100 - 12 && mouseY < h - 25 - 30 + 20)
                        container.handleMouseClick(previousPage, previousPage.slotNumber, button, 0);
                }
            } else if (previousPage != null) {
                if (mouseX > width / 2f - 50 - 12 - 100 - 12 - 100 && mouseY > h - 25 && mouseX < width / 2f - 50 - 12 - 100 - 12 && mouseY < h - 25 + 20)
                    container.handleMouseClick(previousPage, previousPage.slotNumber, button, 0);
            }
        }

        public void onHover(int mouseX, int mouseY) {
            for (Pet pet : pets)
                if (mouseX / 2 > pet.x && mouseY / 2 > pet.y && mouseX / 2 < pet.x + 16 && mouseY / 2 < pet.y + 16) {
                    int i = 0;
                    int max = 0;
                    List<String> tooltip = pet.itemStack.getTooltip(mc.thePlayer, false);
                    GlStateManager.translate(0, 0, 150);
                    for (String text : tooltip) {
                        if (mc.fontRendererObj.getStringWidth(text) > max)
                            max = mc.fontRendererObj.getStringWidth(text);
                        mc.fontRendererObj.drawStringWithShadow(text, mouseX + 5 + 5, mouseY + 5 + i * 10 - 8, -1);
                        i++;
                    }
                    GlStateManager.translate(0, 0, -5);
                    Gui.drawRect(mouseX + 5, mouseY - 8, mouseX + 5 + max + 10, mouseY + tooltip.size() * 10 + 10 - 8, new Color(0, 0, 0, 150).getRGB());
                    GlStateManager.translate(0, 0, -145);
                }
        }

        public void renderItem(ItemStack itemStack, int x, int y) {
            RenderItem itemRender = mc.getRenderItem();
            RenderHelper.enableGUIStandardItemLighting();
            itemRender.zLevel = -145; //Negates the z-offset of the below method.
            itemRender.renderItemAndEffectIntoGUI(itemStack, x, y);
            itemRender.renderItemOverlayIntoGUI(mc.fontRendererObj, itemStack, x, y, null);
            itemRender.zLevel = 0;
            RenderHelper.disableStandardItemLighting();
        }

        public void setSize(GuiChest chest) {
            this.width = chest.width;
            this.height = chest.height;
            this.chest = chest;
        }

        public class Pet {
            public Pet(int x, int y, String name, Slot slot, int id, ItemStack itemStack) {
                this.x = x;
                this.y = y;
                this.name = name;
                this.slot = slot;
                this.id = id;
                this.itemStack = itemStack;
                this.rarity = -1;
                this.last = false;
            }

            public int x;
            public int y;
            public String name;
            public Slot slot;
            public int id;
            public ItemStack itemStack;
            public int rarity;
            public boolean last;
        }
    }
}
