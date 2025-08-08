package core;

import Assets.Components.CustomTiles;
import Assets.Components.Theme;

public final class ThemeUtils {
    private ThemeUtils() {}

    public static Theme forName(String name) {
        if (name == null) return new CustomTiles.RetroArcade();
        String n = name.trim();
        if (n.equalsIgnoreCase("Sci-Fi")) return new CustomTiles.SciFiTheme();
        if (n.equalsIgnoreCase("ARCANE DUNGEON")) return new CustomTiles.ArcaneDungeon();
        if (n.equalsIgnoreCase("NATURE GROVE")) return new CustomTiles.NatureGrove();
        if (n.equalsIgnoreCase("STEAM FACTORY")) return new CustomTiles.SteamFactory();
        if (n.equalsIgnoreCase("DESERT RUINS")) return new CustomTiles.DesertRuins();
        if (n.equalsIgnoreCase("RETRO ARCADE")) return new CustomTiles.RetroArcade();
        if (n.equalsIgnoreCase("CRYSTAL CAVE")) return new CustomTiles.CrystalCave();
        return new CustomTiles.RetroArcade();
    }
}
