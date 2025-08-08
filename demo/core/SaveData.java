package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Serializable snapshot of the game state used for save/load.
 */
public class SaveData {
    public String name;            // display name (e.g., timestamp)
    public long savedAtEpochMs;    // when saved

    // Engine/world config at time of save
    public int width;
    public int height;
    public int screenWidth;
    public int screenHeight;
    public int cellSize;

    // Map seed and theme
    public long seed;
    public String themeName;

    // Player position
    public int playerX;
    public int playerY;

    // Ghost positions
    public List<SavePos> ghosts = new ArrayList<>();

    /** Simple x/y pair for serialization. */
    public static class SavePos {
        public int x;
        public int y;

        public SavePos() {}
        public SavePos(int x, int y) { this.x = x; this.y = y; }
    }
}
