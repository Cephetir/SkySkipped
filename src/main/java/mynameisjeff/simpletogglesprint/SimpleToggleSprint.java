/**
 *     SimpleToggleSprint
 *     Copyright (C) 2021  My-Name-Is-Jeff
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package mynameisjeff.simpletogglesprint;


import mynameisjeff.simpletogglesprint.commands.SimpleToggleSprintCommand;
import mynameisjeff.simpletogglesprint.core.Config;
import mynameisjeff.simpletogglesprint.tweaker.ModCoreInstaller;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

@Mod(modid = SimpleToggleSprint.MODID, name = SimpleToggleSprint.MOD_NAME, version = SimpleToggleSprint.VERSION, acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class SimpleToggleSprint {
    public static final String MODID = "simpletogglesprint";
    public static final String MOD_NAME = "SimpleToggleSprint";
    public static final String VERSION = "1.0";
    public static final Minecraft mc = Minecraft.getMinecraft();

    public static Config config = new Config();


    public static boolean sprintToggled = false;
    public static boolean sneakToggled = false;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        ModCoreInstaller.initializeModCore(mc.mcDataDir);
        config.preload();

        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new SimpleToggleSprintCommand());
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        sprintToggled = Config.toggleSprintState;
        sneakToggled = Config.toggleSneakState;
    }

    @SubscribeEvent
    public void onInput(InputEvent event) {
        if (Config.enabledToggleSprint && mc.gameSettings.keyBindSprint.isPressed()) {
            Config.toggleSprintState = sprintToggled = !sprintToggled;
            config.markDirty();
            config.writeData();
        }
        if (Config.enabledToggleSneak && mc.gameSettings.keyBindSneak.isPressed()) {
            Config.toggleSneakState = sneakToggled = !sneakToggled;
            config.markDirty();
            config.writeData();
        }
    }
}