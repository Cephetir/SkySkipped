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

package me.cephetir.skyskipped.features.impl.dungeons;

import kotlin.ranges.RangesKt;
import kr.syeyoung.dungeonsguide.features.FeatureRegistry;
import kr.syeyoung.dungeonsguide.features.impl.dungeon.FeatureDungeonScore;
import me.cephetir.skyskipped.config.Cache;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.features.Feature;
import me.cephetir.skyskipped.utils.PingUtils;
import me.cephetir.skyskipped.utils.Scoreboardutils;
import me.cephetir.skyskipped.utils.TabListUtils;
import me.cephetir.skyskipped.utils.TextUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import skytils.skytilsmod.features.impl.handlers.MayorInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreCalculation extends Feature {
    public boolean isDGLoaded = false;
    public boolean isSTLoaded = false;

    private int totalScore = 0;
    public boolean bloodOpened = false;
    public boolean bloodCleared = false;

    private int ticks = 0;
    private final Pattern deathsTabPattern = Pattern.compile("§r§a§lDeaths: §r§f\\((?<deaths>\\d+)\\)§r");
    private final Pattern missingPuzzlePattern = Pattern.compile("§r (?<puzzle>.+): §r§7\\[§r§6§l✦§r§7] ?§r");
    private final Pattern failedPuzzlePattern = Pattern.compile("§r (?<puzzle>.+): §r§7\\[§r§c§l✖§r§7] §r§f(?:\\((?:§r(?<player>.+))?§r§f\\)|\\(§r§f})§r");
    private final Pattern secretsFoundPattern = Pattern.compile("§r Secrets Found: §r§b(?<secrets>\\d+)§r");
    private final Pattern secretsFoundPercentagePattern = Pattern.compile("§r Secrets Found: §r§[ae](?<percentage>[\\d.]+)%§r");
    private final Pattern cryptsPattern = Pattern.compile("§r Crypts: §r§6(?<crypts>\\d+)§r");
    private final Pattern dungeonClearedPattern = Pattern.compile("Dungeon Cleared: (?<percentage>\\d+)%");
    private final Pattern timeElapsedPattern = Pattern.compile("Time Elapsed: (?:(?<hrs>\\d+)h )?(?:(?<min>\\d+)m )?(?:(?<sec>\\d+)s)?");
    private final Pattern roomCompletedPattern = Pattern.compile("§r Completed Rooms: §r§d(?<count>\\d+)§r");

    public Map<String, FloorRequirement> floorRequirements = new HashMap<>();

    int deaths = 0;
    int missingPuzzles = 0;
    int failedPuzzles = 0;
    int foundSecrets = 0;
    int totalSecrets = 0;
    int crypts = 0;
    public boolean mimicKilled = false;
    public boolean firstDeathHadSpirit = false;
    int clearedPercentage = 0;
    double secondsElapsed = 0.0;
    boolean isPaul = false;
    int skillScore = 0;
    double percentageSecretsFound = 0.0;
    double totalSecretsNeeded = 0.0;
    int discoveryScore = 0;
    int speedScore = 0;
    int bonusScore = 0;
    public double perRoomPercentage = 0.0;
    int completedRooms = 0;
    int totalRooms = 0;

    public FloorRequirement floorReq;

    public ScoreCalculation() {
        super("ScoreCalculation", "Dungeons", "skid score from other mods");
        floorRequirements.put("E", new FloorRequirement(.3, 10 * 60));
        floorRequirements.put("F1", new FloorRequirement(.3, 10 * 60));
        floorRequirements.put("F2", new FloorRequirement(.4, 10 * 60));
        floorRequirements.put("F3", new FloorRequirement(.5, 10 * 60));
        floorRequirements.put("F4", new FloorRequirement(.6, 12 * 60));
        floorRequirements.put("F5", new FloorRequirement(.7, 10 * 60));
        floorRequirements.put("F6", new FloorRequirement(.85, 12 * 60));
        floorRequirements.put("F7", new FloorRequirement(1.0, 14 * 60));
        floorRequirements.put("M1", new FloorRequirement(1.0, 8 * 60));
        floorRequirements.put("M2", new FloorRequirement(1.0, 8 * 60));
        floorRequirements.put("M3", new FloorRequirement(1.0, 8 * 60));
        floorRequirements.put("M4", new FloorRequirement(1.0, 8 * 60));
        floorRequirements.put("M5", new FloorRequirement(1.0, 8 * 60));
        floorRequirements.put("M6", new FloorRequirement(1.0, 8 * 60));
        floorRequirements.put("M7", new FloorRequirement(1.0, 8 * 60));
        floorRequirements.put("default", new FloorRequirement(1.0, 10 * 60));
        floorReq = floorRequirements.get("default");
    }

    public void score() {
        if (!Cache.isInDungeon) return;
        try {
            if (isDGLoaded) {
                FeatureDungeonScore.ScoreCalculation score = FeatureRegistry.DUNGEON_SCORE.calculateScore();
                Cache.totalScore = score.getTime() + score.getSkill() + score.getExplorer() + score.getBonus();
            } else {
                Cache.totalScore = totalScore;
            }
            if (Cache.totalScore >= 300 && !Cache.was) {
                new PingUtils(100, "300 score reached!");
                mc.thePlayer.sendChatMessage(Config.pingText);
                Cache.was = true;
            }
            if (bloodCleared && !Cache.was2) {
                new PingUtils(100, "Rabbit Hat!");
                Cache.was2 = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @SubscribeEvent
//    public void draw(RenderGameOverlayEvent.Text event) {
//        if (!Cache.isInDungeon) return;
//        mc.fontRendererObj.drawStringWithShadow(ChatColor.DARK_RED + "Total Score: " + totalScore,
//                event.resolution.getScaledWidth() / 5f - mc.fontRendererObj.getStringWidth(Config.pingText) / 2f,
//                event.resolution.getScaledHeight() / 2f, -1);
//    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (isDGLoaded) return;
        ticks++;
        if (mc.thePlayer == null || mc.theWorld == null || !Cache.isInDungeon) return;
        if (ticks % 5 == 0) {
            missingPuzzles = 0;
            failedPuzzles = 0;
            for (String line : Scoreboardutils.fetchScoreboardLines()) {
                if (line.startsWith("Dungeon Cleared:")) {
                    Matcher matcher = dungeonClearedPattern.matcher(line);
                    if (matcher.find()) {
                        clearedPercentage = Integer.parseInt(matcher.group("percentage"));
                        continue;
                    }
                }
                if (line.startsWith("Time Elapsed:")) {
                    Matcher matcher = timeElapsedPattern.matcher(line);
                    if (matcher.find()) {
                        double hours = matcher.group("hrs") != null && TextUtils.isNumeric(matcher.group("hrs")) ? Double.parseDouble(matcher.group("hrs")) : 0;
                        double minutes = matcher.group("min") != null && TextUtils.isNumeric(matcher.group("min")) ? Double.parseDouble(matcher.group("min")) : 0;
                        double seconds = matcher.group("sec") != null && TextUtils.isNumeric(matcher.group("sec")) ? Double.parseDouble(matcher.group("sec")) : 0;
                        secondsElapsed = (hours * 3600 + minutes * 60 + seconds);
                    }
                }
            }
            for (NetworkPlayerInfo npi : TabListUtils.fetchTabEntires()) {
                String name = mc.ingameGUI.getTabList().getPlayerName(npi);
                if (name.contains("Deaths:")) {
                    Matcher matcher = deathsTabPattern.matcher(name);
                    if (matcher.matches())
                        deaths = matcher.group("deaths") != null && TextUtils.isNumeric(matcher.group("deaths")) ? Integer.parseInt(matcher.group("deaths")) : 0;
                }
                if (name.contains("✦") && missingPuzzlePattern.matcher(name).matches()) missingPuzzles++;
                if (name.contains("✖") && failedPuzzlePattern.matcher(name).matches()) failedPuzzles++;
                if (name.contains("Secrets Found:")) {
                    if (name.contains("%")) {
                        Matcher matcher = secretsFoundPercentagePattern.matcher(name);
                        if (matcher.matches()) {
                            double percentagePer = (TextUtils.isNumeric(matcher.group("percentage")) ? Double.parseDouble(matcher.group("percentage")) : 0.0) / foundSecrets;
                            totalSecrets = percentagePer > 0.0 ? (int) (100 / percentagePer) : 0;
                        }
                    } else {
                        Matcher matcher = secretsFoundPattern.matcher(name);
                        if (matcher.matches()) foundSecrets = Integer.parseInt(matcher.group("secrets"));
                    }
                }
                if (name.contains("Crypts:")) {
                    Matcher matcher = cryptsPattern.matcher(name);
                    if (matcher.matches()) crypts = Integer.parseInt(matcher.group("crypts"));
                }
                if (name.contains("Completed Rooms")) {
                    Matcher matcher = roomCompletedPattern.matcher(name);
                    if (matcher.matches()) completedRooms = Integer.parseInt(matcher.group("count"));
                    if (completedRooms > 0) {
                        perRoomPercentage = ((double) clearedPercentage / (double) completedRooms);
                        totalRooms = (int) (100 / perRoomPercentage);
                    }
                }
            }
            int calcingCompletedRooms = completedRooms + (bloodOpened ? 2 : 0);
            double calcingClearedPercentage = RangesKt.coerceAtMost(perRoomPercentage * calcingCompletedRooms, 100.0);
            if (isSTLoaded)
                isPaul = (Objects.equals(MayorInfo.INSTANCE.getCurrentMayor(), "Paul") && MayorInfo.INSTANCE.getMayorPerks().contains("EZPZ")) || (MayorInfo.INSTANCE.getJerryMayor() != null && MayorInfo.INSTANCE.getJerryMayor().getName().equals("Paul"));
            skillScore = RangesKt.coerceIn(100 - (2 * deaths - (firstDeathHadSpirit ? 1 : 0)) - 10 * (missingPuzzles + failedPuzzles) - 4 * (totalRooms - calcingCompletedRooms), 0, 100);
            totalSecretsNeeded = (totalSecrets * floorReq.secretPercentage);
            percentageSecretsFound = foundSecrets / totalSecretsNeeded;
            discoveryScore = (int) (Math.floor(
                    RangesKt.coerceIn(60 * (calcingClearedPercentage / 100f), 0.0, 60.0)
            ) + ((totalSecrets <= 0) ? 0.0 : Math.floor(
                    RangesKt.coerceIn(40f * percentageSecretsFound, 0.0, 40.0)
            )));
            bonusScore = ((mimicKilled) ? 2 : 0) + RangesKt.coerceAtMost(crypts, 5) + (isPaul ? 10 : 0);
            speedScore = (int) (100 - RangesKt.coerceIn((secondsElapsed - floorReq.speed) / 3f, 0.0, 100.0));

            totalScore = (skillScore + discoveryScore + speedScore + bonusScore);
//            System.out.println("NEW -------------------- NEW");
//            System.out.println("deaths: " + deaths);
//            System.out.println("missing puzz: " + missingPuzzles);
//            System.out.println("failed puzz: " + failedPuzzles);
//            System.out.println("found secrets: " + foundSecrets);
//            System.out.println("total secrests: " + totalSecretsNeeded);
//            System.out.println("crypts: " + crypts);
//            System.out.println("mimic: " + mimicKilled);
//            System.out.println("skill score: " + skillScore);
//            System.out.println("discovery score: " + discoveryScore);
//            System.out.println("speed score: " + speedScore);
//            System.out.println("bonus score: " + bonusScore);
//            System.out.println("total score: " + totalScore);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!Cache.isInDungeon || mc.thePlayer == null) return;
        String unformatted = TextUtils.stripColor(event.message.getUnformattedText());
        if (unformatted.startsWith("Party > ")) {
            if (unformatted.contains("$SKYTILS-DUNGEON-SCORE-ROOM$")) event.setCanceled(true);
            if (unformatted.contains("$SKYTILS-DUNGEON-SCORE-MIMIC$") || unformatted.toLowerCase().contains("mimic dead") || unformatted.toLowerCase().contains("mimic killed"))
                mimicKilled = true;
        } else if (unformatted.equalsIgnoreCase("the blood door has been opened!")) bloodOpened = true;
        else if (unformatted.contains("You have proven yourself. You may pass")) bloodCleared = true;
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if (!Cache.isInDungeon) return;
        if (event.entity instanceof EntityZombie) {
            EntityZombie entity = (EntityZombie) event.entity;
            if (entity.isChild() && entity.getCurrentArmor(0) == null && entity.getCurrentArmor(1) == null && entity.getCurrentArmor(
                    2
            ) == null && entity.getCurrentArmor(3) == null
            ) {
                if (!mimicKilled) mimicKilled = true;
            }
        }
    }

    public static class FloorRequirement {
        double secretPercentage;
        int speed;

        public FloorRequirement(double secretPercentage, int speed) {
            this.secretPercentage = secretPercentage;
            this.speed = speed;
        }
    }
}
