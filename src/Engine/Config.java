package Engine;

/**
 * A simple configuration class for the engine.
 * This can be extended to hold more engine-wide settings.
 */
public class Config {
    public int WIDTH = 80;
    public int HEIGHT = 45;
    public int SCREEN_WIDTH;
    public int SCREEN_HEIGHT;
    public int CELL_SIZE = 16; // For the collision system's spatial hash grid

    // Max player move frequency (seconds per tile). Acts as speed limiter for
    // continuous key hold.
    public double PLAYER_MOVE_INTERVAL_SEC = 0.12; // default ~8.3 moves per second

    // Camera dead zone margin (tiles). Player can move within this margin before
    // camera shifts.
    public int CAM_DEADZONE_MARGIN = 3;

    // Cap logic catch-up per frame to avoid stutter on slow frames
    public int MAX_CATCHUP_STEPS = 5;

    public Config() {
        this.SCREEN_HEIGHT = HEIGHT;
        this.SCREEN_WIDTH = WIDTH;
    }

    public Config(int width, int height, int cellSize) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.CELL_SIZE = cellSize;
        this.SCREEN_HEIGHT = height;
        this.SCREEN_WIDTH = width;
    }

    public Config(int width, int height, int screenWidth, int screenHeight, int cellSize) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.CELL_SIZE = cellSize;
        this.SCREEN_HEIGHT = screenHeight;
        this.SCREEN_WIDTH = screenWidth;
    }

}
