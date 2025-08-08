package Assets.Map;

import Assets.Components.Theme;
import Engine.Graphics.tileengine.TETile;
import Engine.Utils.Vector2i;
import core.CustomConfig;

import java.util.*;

/**
 * A Utility class for map generation
 *
 * @note Go to demo read the map generator class
 */
public class MapGenerator {
    private static final int MIN_ROOM_WIDTH = 6;
    private static final int MIN_ROOM_HEIGHT = 4;
    private static final int MAX_ROOM_WIDTH = 12;
    private static final int MAX_ROOM_HEIGHT = 9;
    private static final int ROOM_ATTEMPTS = 80;

    /* Themes */
    private static TETile WALL;
    private static TETile FLOOR;
    private static TETile NOTHING;
    private static TETile LOCKED_DOOR;

    static List<TETile> solidTiles = new ArrayList<>();
    static List<Vector2i> floorPositions = new ArrayList<>();
    private static Vector2i lockedDoorPos;

    private static class Rect {
        int x, y, w, h;//left a bottom corner and size

        Rect(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        boolean intersects(Rect o) {
            return x < o.x + o.w + 1 && x + w + 1 > o.x &&
                    y < o.y + o.h + 1 && y + h + 1 > o.y;
        }

        int cx() {
            return x + w / 2;
        }

        int cy() {
            return y + h / 2;
        }
    }

    /**
     * Generate a map for TileMap Layer {@link Engine.Scene.TileMap}
     *
     * @param width  The width of the map.
     * @param height The height of the map.
     * @return A 2D TETile array representing the generated map.
     */
    public static TETile[][] generateMap(int width, int height, long seed, CustomConfig config) {

        Theme theme = config.theme;
        WALL = theme.wall();
        FLOOR = theme.floor();
        NOTHING = theme.nothing();
        LOCKED_DOOR = theme.lockedDoor();

        /* ① 统一转奇数，确保左右各留 1 格墙 */
        if (width % 2 == 0) width--;
        if (height % 2 == 0) height--;

        TETile[][] world = new TETile[width][height];
        for (int x = 0; x < width; x++) Arrays.fill(world[x], NOTHING);

        Random rng = new Random(seed);
        List<Rect> rooms = new ArrayList<>();

        /* ② 随机投点放房间（含 L 型概率） */
        for (int i = 0; i < ROOM_ATTEMPTS; i++) {
            int rw = rng.nextInt(MAX_ROOM_WIDTH - MIN_ROOM_WIDTH + 1) + MIN_ROOM_WIDTH;
            int rh = rng.nextInt(MAX_ROOM_HEIGHT - MIN_ROOM_HEIGHT + 1) + MIN_ROOM_HEIGHT;
            int rx = rng.nextInt(width - rw - 4) + 2;   // 四周 ≥2 墙
            int ry = rng.nextInt(height - rh - 4) + 2;

            Rect cand = new Rect(rx, ry, rw, rh);
            boolean overlap = false;
            for (Rect r : rooms)
                if (cand.intersects(r)) {
                    overlap = true;
                    break;
                }
            if (overlap) continue;

            carveRoom(world, cand);
            if (rng.nextInt(6) == 0) carveL(world, cand, rng);
            rooms.add(cand);
        }

        if (rooms.size() < 2) return generateMap(width, height, seed + 1, config); // 极小概率回滚

        /* ③ 依次把房间用 L 形走廊连起来，宽度随机 1/2 */
        for (int i = 1; i < rooms.size(); i++) {
            Rect a = rooms.get(i - 1);
            Rect b = rooms.get(i);
            carveCorridor(world, a.cx(), a.cy(), b.cx(), b.cy(), rng);
        }

        /* ④ 生成墙体 & 外框 */
        growWalls(world);
        for (int x = 0; x < width; x++) {
            world[x][0] = WALL;
            world[x][height - 1] = WALL;
        }
        for (int y = 0; y < height; y++) {
            world[0][y] = WALL;
            world[width - 1][y] = WALL;
        }

        /* ⑤ 终点门放在最后一个房间中心 */
        Rect last = rooms.get(rooms.size() - 1);
        world[last.cx()][last.cy()] = LOCKED_DOOR;

        lockedDoorPos = new Vector2i(last.cx(), last.cy());

        /* ⑥ 更新实心表 */
        solidTiles.clear();
        solidTiles.add(WALL);

        /* ⑦ Populate floor positions from the generated map */
        floorPositions.clear();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (world[x][y] == FLOOR) {
                    floorPositions.add(new Vector2i(x, y));
                }
            }
        }

        return world;
    }

    private static void carveRoom(TETile[][] w, Rect r) {
        for (int x = r.x; x < r.x + r.w; x++)
            for (int y = r.y; y < r.y + r.h; y++) {
                w[x][y] = FLOOR;
            }
    }

    private static void carveL(TETile[][] w, Rect base, Random rng) {
        int lx = base.x + rng.nextInt(base.w / 2);
        int ly = base.y + rng.nextInt(base.h / 2);
        int lw = base.w / 2, lh = base.h / 2;
        for (int x = lx; x < lx + lw; x++)
            for (int y = ly; y < ly + lh; y++) {
                if (w[x][y] == FLOOR) {
                    w[x][y] = NOTHING;
                }
            }
    }

    private static void carveCorridor(TETile[][] w, int x1, int y1, int x2, int y2, Random rng) {
        boolean horizFirst = rng.nextBoolean();
        int cw = rng.nextBoolean() ? 1 : 2;
        if (horizFirst) {
            carveH(w, x1, x2, y1, cw);
            carveV(w, y1, y2, x2, cw);
        } else {
            carveV(w, y1, y2, x1, cw);
            carveH(w, x1, x2, y2, cw);
        }
    }

    private static void carveH(TETile[][] w, int xs, int xe, int y, int cw) {
        if (xs > xe) {
            int t = xs;
            xs = xe;
            xe = t;
        }
        for (int x = xs; x <= xe; x++)
            for (int dy = 0; dy < cw; dy++) {
                w[x][y + dy] = FLOOR;
            }
    }

    private static void carveV(TETile[][] w, int ys, int ye, int x, int cw) {
        if (ys > ye) {
            int t = ys;
            ys = ye;
            ye = t;
        }
        for (int y = ys; y <= ye; y++)
            for (int dx = 0; dx < cw; dx++) {
                w[x + dx][y] = FLOOR;
            }
    }

    /* 将 NOTHING 且邻接 FLOOR 的格子变为 WALL */
    private static void growWalls(TETile[][] w) {
        int W = w.length, H = w[0].length;
        for (int x = 0; x < W; x++)
            for (int y = 0; y < H; y++)
                if (w[x][y] == NOTHING && hasAdjFloor(w, x, y))
                    w[x][y] = WALL;
    }

    private static boolean hasAdjFloor(TETile[][] w, int x, int y) {
        int W = w.length, H = w[0].length;
        int[][] d = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dxy : d) {
            int nx = x + dxy[0], ny = y + dxy[1];
            if (nx >= 0 && nx < W && ny >= 0 && ny < H && w[nx][ny] == FLOOR) return true;
        }
        return false;
    }


    public static List<TETile> getSolidTiles() {
        return solidTiles;
    }

    public static List<Vector2i> getFloorPositions() {
        return floorPositions;
    }

    public static Vector2i getExitPosition() {
        return lockedDoorPos;
    }
}