package Assets.Components;

import Engine.Graphics.tileengine.TETile;
import core.CustomConfig;

import java.awt.*;

public class CustomTiles {

    public static class SciFiTheme implements Theme {
        public static final TETile WALL = new TETile('▓', Color.cyan, new Color(10, 20, 40), "energy barrier", 21);
        public static final TETile FLOOR = new TETile('░', Color.lightGray, Color.black, "holo floor", 22);
        public static final TETile AVATAR = new TETile('⧫', Color.green, Color.black, "cyber explorer", 23);
        public static final TETile NEW_GHOST = new TETile(' ', Color.WHITE, Color.black, "glitchy apparition", CustomConfig.GHOST_PATH, 24);

        @Override
        public TETile wall() {
            return WALL;
        }

        @Override
        public TETile floor() {
            return FLOOR;
        }

        @Override
        public TETile avatar() {
            return AVATAR;
        }

        @Override
        public TETile ghost() {
            return NEW_GHOST;
        }

        @Override
        public String name() {
            return "Sci-Fi";
        }
    }

    public static class ArcaneDungeon implements Theme {
        public static final TETile WALL = new TETile('▒', new Color(100, 50, 160), new Color(30, 0, 50), "enchanted wall", 41);
        public static final TETile FLOOR = new TETile('·', new Color(180, 170, 220), new Color(30, 0, 60), "rune floor", 42);
        public static final TETile AVATAR = new TETile('☽', Color.magenta, Color.black, "mystic", 43);
        public static final TETile CRYSTAL = new TETile('✶', Color.cyan, Color.blue, "magic crystal", 44);
        public static final TETile ALTAR = new TETile('⌑', Color.white, new Color(100, 0, 100), "altar", 45);
        public static final TETile NEW_GHOST = new TETile(' ', Color.WHITE, Color.black, "wailing spirit", CustomConfig.GHOST_PATH, 46);

        @Override
        public TETile wall() {
            return WALL;
        }

        @Override
        public TETile floor() {
            return FLOOR;
        }

        @Override
        public TETile avatar() {
            return AVATAR;
        }

        @Override
        public TETile ghost() {
            return NEW_GHOST;
        }

        @Override
        public String name() {
            return "ARCANE DUNGEON";
        }
    }

    public static class NatureGrove implements Theme {
        public static final TETile WALL = new TETile('♠', new Color(30, 120, 30), new Color(10, 50, 10), "ancient tree", 51);
        public static final TETile FLOOR = new TETile('"', new Color(120, 220, 120), Color.black, "soft grass", 52);
        public static final TETile AVATAR = new TETile('⚘', new Color(220, 180, 70), Color.black, "forest wanderer", 53);
        public static final TETile FLOWER = new TETile('❀', Color.pink, Color.green, "flower", 54);
        public static final TETile STONE = new TETile('⬟', Color.gray, Color.green, "mossy stone", 55);
        public static final TETile NEW_GHOST = new TETile(' ', Color.WHITE, Color.black, "forest phantom", CustomConfig.GHOST_PATH, 56);

        @Override
        public TETile wall() {
            return WALL;
        }

        @Override
        public TETile floor() {
            return FLOOR;
        }

        @Override
        public TETile avatar() {
            return AVATAR;
        }

        @Override
        public TETile ghost() {
            return NEW_GHOST;
        }

        @Override
        public String name() {
            return "NATURE GROVE";
        }
    }

    public static class SteamFactory implements Theme {
        public static final TETile WALL = new TETile('▤', new Color(150, 100, 40), new Color(70, 50, 20), "brass wall", 61);
        public static final TETile FLOOR = new TETile('·', new Color(200, 180, 120), new Color(80, 60, 30), "metal grating", 62);
        public static final TETile AVATAR = new TETile('⚙', Color.orange, Color.black, "engineer", 63);
        public static final TETile PIPE = new TETile('═', Color.gray, Color.darkGray, "steam pipe", 64);
        public static final TETile GEAR = new TETile('✶', Color.yellow, Color.darkGray, "gear", 65);
        public static final TETile NEW_GHOST = new TETile(' ', Color.WHITE, Color.black, "steam wraith", CustomConfig.GHOST_PATH, 66);

        @Override
        public TETile wall() {
            return WALL;
        }

        @Override
        public TETile floor() {
            return FLOOR;
        }

        @Override
        public TETile avatar() {
            return AVATAR;
        }

        @Override
        public TETile ghost() {
            return NEW_GHOST;
        }

        @Override
        public String name() {
            return "STEAM FACTORY";
        }
    }

    public static class DesertRuins implements Theme {
        public static final TETile WALL = new TETile('▦', new Color(230, 200, 120), new Color(140, 110, 50), "sandstone", 71);
        public static final TETile FLOOR = new TETile('▒', new Color(240, 220, 150), new Color(160, 140, 80), "sunbaked floor", 72);
        public static final TETile AVATAR = new TETile('☥', Color.yellow, Color.black, "adventurer", 73);
        public static final TETile RELIC = new TETile('⌆', Color.red, Color.yellow, "ancient relic", 74);
        public static final TETile TRAP = new TETile('▲', Color.red, new Color(139, 69, 19), "trap", 75);
        public static final TETile NEW_GHOST = new TETile(' ', Color.WHITE, Color.black, "sand spirit", CustomConfig.GHOST_PATH, 76);

        @Override
        public TETile wall() {
            return WALL;
        }

        @Override
        public TETile floor() {
            return FLOOR;
        }

        @Override
        public TETile avatar() {
            return AVATAR;
        }

        @Override
        public TETile ghost() {
            return NEW_GHOST;
        }

        @Override
        public String name() {
            return "DESERT RUINS";
        }
    }

    public static class RetroArcade implements Theme {
        public static final TETile WALL = new TETile('#', Color.white, Color.black, "retro wall", 81);
        public static final TETile FLOOR = new TETile('.', Color.lightGray, Color.black, "retro floor", 82);
        public static final TETile AVATAR = new TETile('@', Color.green, Color.black, "player", 83);
        public static final TETile ENEMY = new TETile('X', Color.red, Color.black, "enemy", 84);
        public static final TETile DOT = new TETile('•', Color.white, Color.black, "collectible", 85);
        public static final TETile NEW_GHOST = new TETile(' ', Color.WHITE, Color.black, "8-bit ghost", CustomConfig.GHOST_PATH, 86);

        @Override
        public TETile wall() {
            return WALL;
        }

        @Override
        public TETile floor() {
            return FLOOR;
        }

        @Override
        public TETile avatar() {
            return AVATAR;
        }

        @Override
        public TETile ghost() {
            return NEW_GHOST;
        }

        @Override
        public String name() {
            return "RETRO ARCADE";
        }
    }

    public static class CrystalCave implements Theme {
        public static final TETile WALL = new TETile('▲', new Color(120, 160, 255), new Color(30, 30, 60), "crystal wall", 91);
        public static final TETile FLOOR = new TETile('·', new Color(160, 200, 255), Color.black, "crystal floor", 92);
        public static final TETile AVATAR = new TETile('◆', Color.cyan, Color.black, "spelunker", 93);
        public static final TETile GEM = new TETile('✧', Color.white, Color.blue, "glowing gem", 94);
        public static final TETile PIT = new TETile('⏚', Color.gray, Color.black, "bottomless pit", 95);
        public static final TETile NEW_GHOST = new TETile(' ', Color.WHITE, Color.black, "crystal echo", CustomConfig.GHOST_PATH, 96);

        @Override
        public TETile wall() {
            return WALL;
        }

        @Override
        public TETile floor() {
            return FLOOR;
        }

        @Override
        public TETile avatar() {
            return AVATAR;
        }

        @Override
        public TETile ghost() {
            return NEW_GHOST;
        }

        @Override
        public String name() {
            return "CRYSTAL CAVE";
        }
    }

}