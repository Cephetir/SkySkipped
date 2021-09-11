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

package cephetir.skyskipped.config;

import cephetir.skyskipped.SkySkipped;
import cephetir.skyskipped.gui.hud.ScreenPosition;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class HUDConfig {
    private final File dir;

    public HUDConfig(File dir) {
        this.dir = new File(dir, "skyskipped");
    }

    public void saveToFile() {
        try {
            File file = new File(dir, "hud.ceph");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            SkySkipped.hudManager.getRegisteredRenderers().forEach(iRenderer -> {
                try {
                    writer.write(iRenderer.getClass().getSimpleName().toLowerCase() + ":" + iRenderer.load().getAbsoluteX() + ":" + iRenderer.load().getAbsoluteY());
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile() {
        try {
            File file = new File(dir, "hud.ceph");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            for (String line : Objects.requireNonNull(getLines(file))) {
                String[] separator = line.split(":");

                SkySkipped.hudManager.getRegisteredRenderers().forEach(iRenderer -> {
                    if (separator[0].equals(iRenderer.getClass().getSimpleName().toLowerCase())) {
                        iRenderer.save(ScreenPosition.fromAbsolute(Integer.parseInt(separator[1]), Integer.parseInt(separator[2])));
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getLines(File file) {
        try {
            Scanner scanner = new Scanner(file);
            ArrayList<String> lines = new ArrayList<>();

            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }

            scanner.close();
            return lines;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
