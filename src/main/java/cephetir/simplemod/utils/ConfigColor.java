package cephetir.simplemod.utils;

import cephetir.simplemod.core.Config;

import java.awt.*;

public class ConfigColor {
    public static int getColor() {
        if (Config.isAdvanced == 1) return new Color(Config.displayStateRed, Config.displayStateGreen, Config.displayStateBlue).getRGB();
        switch (Config.simpleColor) {
            case 0:
                return ColorEnum.WHITE;
            case 1:
                return ColorEnum.LIGHT_GRAY;
            case 2:
                return ColorEnum.GRAY;
            case 3:
                return ColorEnum.DARK_GRAY;
            case 4:
                return ColorEnum.BLACK;
            case 5:
                return ColorEnum.RED;
            case 6:
                return ColorEnum.PINK;
            case 7:
                return ColorEnum.ORANGE;
            case 8:
                return ColorEnum.YELLOW;
            case 9:
                return ColorEnum.GREEN;
            case 10:
                return ColorEnum.MAGENTA;
            case 11:
                return ColorEnum.CYAN;
            case 12:
                return ColorEnum.BLUE;
            default:
                return Color.HSBtoRGB(System.currentTimeMillis() % 2000L / 2000.0F, 0.8F, 0.8F);
        }
    }
    public static class ColorEnum {
        public static final int WHITE = Color.WHITE.getRGB();
        public static final int LIGHT_GRAY = Color.LIGHT_GRAY.getRGB();
        public static final int GRAY = Color.GRAY.getRGB();
        public static final int DARK_GRAY = Color.DARK_GRAY.getRGB();
        public static final int BLACK = Color.BLACK.getRGB();
        public static final int RED = Color.RED.getRGB();
        public static final int PINK = Color.PINK.getRGB();
        public static final int ORANGE = Color.ORANGE.getRGB();
        public static final int YELLOW = Color.YELLOW.getRGB();
        public static final int GREEN = Color.GREEN.getRGB();
        public static final int MAGENTA = Color.MAGENTA.getRGB();
        public static final int CYAN = Color.CYAN.getRGB();
        public static final int BLUE = Color.BLUE.getRGB();
    }
}