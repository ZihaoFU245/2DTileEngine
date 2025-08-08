package Engine.Scene;

import Engine.Graphics.Renderer;
import Engine.Graphics.tileengine.TETile;
import Engine.Graphics.tileengine.Tileset;
import Engine.Utils.Vector2i;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A Layer that renders a 2D array of {@link TETile} tiles. Optionally, specific
 * tile types can generate static colliders so that the map participates in the
 * scene's collision system.
 */
public class TileMap extends Layer {

    private final TETile[][] tiles;
    private final int width;
    private final int height;

    /**
     * Creates a TileMap that simply renders the provided tiles.
     *
     * @param scene the owning scene
     * @param tiles 2D array of tiles to render
     */
    public TileMap(Scene scene, TETile[][] tiles) {
        this(scene, tiles, List.of());
    }

    /**
     * Creates a TileMap and generates static colliders for any tiles that match
     * the provided list of solid tile types.
     *
     * @param scene      the owning scene
     * @param tiles      2D array of tiles to render
     * @param solidTiles tiles that should produce static colliders
     */
    public TileMap(Scene scene, TETile[][] tiles, List<TETile> solidTiles) {
        super(scene);
        this.tiles = tiles;
        this.width = tiles.length;
        this.height = tiles[0].length;

        if (solidTiles != null && !solidTiles.isEmpty()) {
            Set<TETile> solids = new HashSet<>(solidTiles);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (solids.contains(tiles[x][y])) {
                        addObject(new StaticTile(scene, new Vector2i(x, y)));
                    }
                }
            }
        }
    }

    /**
     * @return map width in tiles
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return map height in tiles
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the tile at the specified coordinates or {@link Tileset#VOID} if
     * the coordinates lie outside the map.
     */
    public TETile getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return Tileset.VOID;
        }
        return tiles[x][y];
    }

    /**
     * Sets the tile at the specified coordinates and requests a redraw.
     */
    public void setTile(int x, int y, TETile tile) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return;
        }
        tiles[x][y] = tile;
        scene.requestRender();
    }

    @Override
    public void render(Renderer r) {
        // Camera-aware rendering: only draw the visible window to improve performance
        // and correctness.
        Camera cam = scene.getCamera();
        int camX = cam.getX();
        int camY = cam.getY();
        int viewW = cam.getWidth();
        int viewH = cam.getHeight();

        int startX = Math.max(0, camX);
        int endX = Math.min(width - 1, camX + viewW - 1);
        int startY = Math.max(0, camY);
        int endY = Math.min(height - 1, camY + viewH - 1);

        for (int x = startX; x <= endX; x++) {
            int screenX = x - camX;
            for (int y = startY; y <= endY; y++) {
                int screenY = y - camY;
                TETile tile = tiles[x][y];
                if (tile != null) {
                    r.drawTile(screenX, screenY, tile);
                }
            }
        }
        // Children (e.g., static collider entities) render after.
        super.render(r);
    }

    /**
     * Internal entity used to generate static colliders for solid tiles. It
     * does not render anything.
     */
    private static class StaticTile extends Entity {
        StaticTile(Scene scene, Vector2i position) {
            super(scene, position);
            setCollider(new Vector2i(1, 1), true);
        }

        @Override
        public void render(Renderer r) {
            // TileMap already renders the visual tile.
        }
    }
}
