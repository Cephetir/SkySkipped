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

package cephetir.skyskipped;

import cephetir.skyskipped.Features.*;
import cephetir.skyskipped.commands.SkySkippedCommand;
import cephetir.skyskipped.config.Config;
import cephetir.skyskipped.discordrpc.Client;
import cephetir.skyskipped.listeners.Status;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;

@Mod(modid = SkySkipped.MODID, name = SkySkipped.MOD_NAME, version = SkySkipped.VERSION, acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class SkySkipped {
    public static final String MODID = "skyskipped";
    public static final String MOD_NAME = "SkySkipped";
    public static final String VERSION = "1.6";
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static Config config = new Config();

    @Mod.Instance(MODID)
    public static SkySkipped Instance;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config.preload();
        MinecraftForge.EVENT_BUS.register(new Client());
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new Status());
        MinecraftForge.EVENT_BUS.register(new ChatSwapper());
        MinecraftForge.EVENT_BUS.register(new ChestCloser());
        MinecraftForge.EVENT_BUS.register(new PlayerESP());
        MinecraftForge.EVENT_BUS.register(new LastCrit());
        MinecraftForge.EVENT_BUS.register(new Nons());
        Client.getINSTANCE().init();
        ClientCommandHandler.instance.registerCommand(new SkySkippedCommand());
    }

    @Mod.EventHandler
    public void stop(FMLModDisabledEvent event) {
        Client.getINSTANCE().shutdown();
    }
}