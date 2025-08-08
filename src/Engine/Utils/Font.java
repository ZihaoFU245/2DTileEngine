package Engine.Utils;

import Engine.Graphics.tileengine.TETile;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * A factory and cache for font character tiles.
 * This class lazily creates and caches TETile objects for character-color combinations
 * to avoid creating new tile objects every frame.
 */
public class Font {
    // A two-level cache: Character -> { Color -> TETile }
    private static final Map<Character, Map<Color, TETile>> FONT_CACHE = new HashMap<>();

    /**
     * Retrieves a TETile for a given character and color.
     * If the tile for this specific character-color combination does not exist,
     * it is created and cached for future use.
     *
     * @param character The character for the tile.
     * @param color     The desired text color.
     * @return A cached or newly created TETile.
     */
    public static TETile getTile(char character, Color color) {
        // Check if the character is cached. If not, create a new map for it.
        FONT_CACHE.computeIfAbsent(character, k -> new HashMap<>());

        // Get the color map for the character.
        Map<Color, TETile> colorMap = FONT_CACHE.get(character);

        // Check if the color is cached for this character. If not, create and cache it.
        // The background is assumed to be black and the description is "char".
        return colorMap.computeIfAbsent(color, k -> new TETile(character, color, Color.BLACK, "char", -1));
    }
}
