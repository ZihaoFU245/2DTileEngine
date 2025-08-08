package Engine.Utils;

import Engine.Graphics.Renderer;
import Engine.Graphics.tileengine.TETile;

import java.awt.Color;

/**
 * A utility class to render text on the screen, one character at a time.
 */
public class TextUtils {

    /**
     * Draws a string of text at the specified (x, y) position.
     *
     * @param r     The Renderer to use.
     * @param text  The string to draw.
     * @param x     The starting x-coordinate.
     * @param y     The starting y-coordinate.
     * @param color The color of the text.
     */
    public static void drawText(Renderer r, String text, int x, int y, Color color) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            TETile tile = Font.getTile(c, color);
            r.drawTile(x + i, y, tile);
        }
    }

    /**
     * Draws a string of text at the specified (x, y) position with a default color (white).
     *
     * @param r    The Renderer to use.
     * @param text The string to draw.
     * @param x    The starting x-coordinate.
     * @param y    The starting y-coordinate.
     */
    public static void drawText(Renderer r, String text, int x, int y) {
        drawText(r, text, x, y, Color.WHITE);
    }
}
