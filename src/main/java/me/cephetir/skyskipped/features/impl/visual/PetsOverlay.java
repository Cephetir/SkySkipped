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

import com.google.gson.JsonObject;
import io.github.moulberry.notenoughupdates.NotEnoughUpdates;
import io.github.moulberry.notenoughupdates.miscfeatures.PetInfoOverlay;
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
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;
import skytils.skytilsmod.features.impl.misc.PetFeatures;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PetsOverlay extends Feature {
    private GuiPetsOverlay petsOverlay = null;

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
        if (!containerName.equals("Pets")) return;
        petsOverlay = new GuiPetsOverlay(chest);
        new Thread(() -> {
            try {
                Thread.sleep(50L);
                petsOverlay.setSize(chest);
                petsOverlay.getPets();
            } catch (InterruptedException e) {
                e.printStackTrace();
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
        if (ticks == 20) {
            ticks = 0;
            petsOverlay.getPets();
        }
    }

    public class GuiPetsOverlay {
        private GuiChest chest;
        private List<Pet> pets = new ArrayList<>();
        private int width, height;
        private int j = 4;

        private Slot autopet;
        private Slot close;
        private Slot convert;
        private Slot hide;
        private Slot page;

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
                        if (width / 2 / 3 + 5 + k * 32 > width / 2 - width / 2 / 3) {
                            k = 0;
                            j++;
                        }
                        pets1.add(new Pet(width / 2 / 3 + 5 + k * 32, height / 2 / 4 + 5 + j * 32, displayName, slot, i, slot.getStack()));
                        i++;
                        k++;


                        Matcher m = PET_PATTERN.matcher(displayName);
                        if (m.matches())
                            pets1.get(i - 1).rarity = Objects.requireNonNull(RarityUtils.byBaseColor(m.group("color"))).getColorToRender().getRGB();

                        if (Loader.isModLoaded("skytils") && PetFeatures.Companion.getLastPet() != null && displayName.contains(PetFeatures.Companion.getLastPet()))
                            pets1.get(i - 1).last = true;
                        else if (Loader.isModLoaded("notenoughupdates") && NotEnoughUpdates.VERSION.equals("2.0.0-REL") && PetInfoOverlay.getCurrentPet() != null) {
                            JsonObject petItem = NotEnoughUpdates.INSTANCE.manager.getItemInformation().get(PetInfoOverlay.getCurrentPet().petType + ";" + PetInfoOverlay.getCurrentPet().rarity.petId);
                            ItemStack itemStack = NotEnoughUpdates.INSTANCE.manager.jsonToStack(petItem);
                            NBTTagCompound compoundd = itemStack.getTagCompound().getCompoundTag("display");
                            String name = compoundd.getString("Name");
                            if (displayName.equals(name)) pets1.get(i - 1).last = true;
                        }
                    } else if (displayName.toLowerCase().contains("autopet")) this.autopet = slot;
                    else if (displayName.toLowerCase().contains("close")) this.close = slot;
                    else if (displayName.toLowerCase().contains("convert pet")) this.convert = slot;
                    else if (displayName.toLowerCase().contains("hide pets")) this.hide = slot;
                    else if (displayName.toLowerCase().contains("page idk")) this.page = slot;
                }
            }

            pets = pets1;
            this.j = j;
        }

        public void onDrawScreen(int mouseX, int mouseY) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();

            int h = Math.max(height / 4 + 5 + j * 32 + 21, height - height / 3);
            Gui.drawRect(width / 4 - 20, height / 4, width - width / 4 + 20, h, new Color(0, 0, 0, 105).getRGB());

            GlStateManager.scale(2f, 2f, 2f);
            if (!pets.isEmpty()) for (Pet pet : pets) {
                renderItem(pet.itemStack, pet.x, pet.y);

                if (pet.last)
                    RoundUtils.drawRoundedRect(pet.x - 0.3f, pet.y - 0.3f, pet.x + 16.3f, pet.y + 16.3f, 6, pet.rarity);
                RoundUtils.drawRoundedOutline(pet.x - 0.5f, pet.y - 0.5f, pet.x + 16.5f, pet.y + 16.5f, 6, 2.5f, pet.rarity);

                GlStateManager.scale(0.25f, 0.25f, 0.25f);
                mc.fontRendererObj.drawStringWithShadow(pet.name, (pet.x + 8) * 2f * 2f - mc.fontRendererObj.getStringWidth(pet.name) / 2f, (pet.y + 18) * 2f * 2f, pet.rarity);
                GlStateManager.scale(4f, 4f, 4f);
            }

            GlStateManager.scale(0.5f, 0.5f, 0.5f);

            GlStateManager.resetColor();
            Gui.drawRect(width / 2 - 50 - 12 - 100, h - 25, width / 2 - 50 - 12, h - 25 + 20, new Color(255, 255, 255, 150).getRGB());
            mc.fontRendererObj.drawString("Auto Pet", width / 2 - 50 - 15 - 50 - mc.fontRendererObj.getStringWidth("Auto Pet") / 2, h - 25 + 10 - mc.fontRendererObj.FONT_HEIGHT / 2, new Color(0, 0, 0, 255).getRGB());

            Gui.drawRect(width / 2 - 50, h - 25, width / 2 + 50, h - 25 + 20, new Color(255, 255, 255, 150).getRGB());
            mc.fontRendererObj.drawString("Close", width / 2 - mc.fontRendererObj.getStringWidth("Close") / 2, h - 25 + 10 - mc.fontRendererObj.FONT_HEIGHT / 2, new Color(0, 0, 0, 255).getRGB());

            Gui.drawRect(width / 2 + 50 + 12, h - 25, width / 2 + 50 + 12 + 100, h - 25 + 20, new Color(255, 255, 255, 150).getRGB());
            mc.fontRendererObj.drawString("Convert Pet", width / 2 + 50 + 15 + 50 - mc.fontRendererObj.getStringWidth("Convert Pet") / 2, h - 25 + 10 - mc.fontRendererObj.FONT_HEIGHT / 2, new Color(0, 0, 0, 255).getRGB());

            Gui.drawRect(width / 2 + 50 + 12 + 100 + 12, h - 25, width / 2 + 50 + 12 + 100 + 12 + 100, h - 25 + 20, new Color(255, 255, 255, 150).getRGB());
            mc.fontRendererObj.drawString("Hide Pets", width / 2 + 50 + 15 + 100 + 15 + 50 - mc.fontRendererObj.getStringWidth("Hide Pets") / 2, h - 25 + 10 - mc.fontRendererObj.FONT_HEIGHT / 2, new Color(0, 0, 0, 255).getRGB());

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
            int h = Math.max(height / 4 + 5 + j * 32 + 21, height - height / 3);
            if (mouseX > width / 2f - 50 - 12 - 100 && mouseY > h - 25 && mouseX < width / 2f - 50 - 12 && mouseY < h - 25 + 20)
                container.handleMouseClick(autopet, autopet.slotNumber, button, 0);
            else if (mouseX > width / 2f - 50 && mouseY > h - 25 && mouseX < width / 2f + 50 && mouseY < h - 25 + 20)
                container.handleMouseClick(close, close.slotNumber, button, 0);
            else if (mouseX > width / 2f + 50 + 12 && mouseY > h - 25 && mouseX < width / 2f + 50 + 12 + 100 && mouseY < h - 25 + 20)
                container.handleMouseClick(convert, convert.slotNumber, button, 0);
            else if (mouseX > width / 2f + 50 + 12 + 100 + 12 && mouseY > h - 25 && mouseX < width / 2f + 50 + 12 + 100 + 12 + 100 && mouseY < h - 25 + 20)
                container.handleMouseClick(hide, hide.slotNumber, button, 0);
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
