package Engine.Graphics;

import Engine.Config;
import Engine.Graphics.tileengine.TERenderer;
import Engine.Graphics.tileengine.TETile;
import Engine.Graphics.tileengine.Tileset;

/**
 * Wraps the TERenderer to provide a simple, buffered interface for drawing tiles.
 * The game logic draws to an off-screen buffer, which is then rendered to the screen at once.
 */
public class Renderer {
    private TERenderer teRenderer;
    private int width, height;
    private TETile[][] frameBuffer;

    /**
     * Initializes the renderer and the frame buffer using engine configuration.
     *
     * @param config The engine configuration object.
     */
    public void initialize(Config config) {
        this.width = config.SCREEN_WIDTH;
        this.height = config.SCREEN_HEIGHT;
        this.teRenderer = new TERenderer();
        this.teRenderer.initialize(width, height);
        this.frameBuffer = new TETile[width][height];
        clearFrameBuffer();
    }

    /**
     * Clears the frame buffer, filling it with empty tiles.
     * This should be called at the beginning of each frame.
     */
    public void beginFrame() {
        clearFrameBuffer();
    }

    /**
     * Renders the completed frame buffer to the screen.
     */
    public void endFrame() {
        teRenderer.renderFrame(frameBuffer);
    }

    /**
     * Draws a single tile to the frame buffer at the specified coordinates.
     *
     * @param x    The x-coordinate.
     * @param y    The y-coordinate.
     * @param tile The tile to draw.
     */
    public void drawTile(int x, int y, TETile tile) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            frameBuffer[x][y] = tile;
        }
    }

    private void clearFrameBuffer() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                frameBuffer[x][y] = Tileset.NOTHING;
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
