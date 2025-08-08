package Assets.Scenes;

import Assets.Components.TopBar;
import Assets.Entities.Door;
import Assets.Entities.Ghost;
import Assets.Entities.Player;
import Assets.Map.MapGenerator;
import Engine.Graphics.tileengine.TETile;
import Engine.Input.InputAction;
import Engine.Scene.*;
import Engine.Utils.Vector2i;
import core.CustomConfig;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameScene extends Scene {

    private Player player;
    private TopBar topBar;
    private TileMap mapLayer; // store reference for hover lookup
    private final long seed;
    // Cache for ghost references for saving/loading
    private final java.util.List<Ghost> ghostCache = new java.util.ArrayList<>();
    // Optional restore payload when loading from a save
    private core.SaveData pendingRestore = null;
    private boolean paused = false;
    private boolean triggerWin = false;
    private boolean triggerLost = false;
    private boolean triggerBack = false;
    private String lastHoverDesc = ""; // cache to avoid redundant renders

    public GameScene(long seed) {
        /* Game Object should be put in onStart method as possible. */
        this.seed = seed;
    }

    // Alternative constructor used when loading a save: seed + positions
    public GameScene(long seed, core.SaveData restore) {
        this.seed = seed;
        this.pendingRestore = restore;
    }

    @Override
    public void onStart() {
        super.onStart(); // basic registration
        int NUM_GHOSTS = ((CustomConfig) config).ghostNum;

        /* Game Map */
        TETile[][] map = MapGenerator.generateMap(config.WIDTH, config.HEIGHT, seed, (CustomConfig) config);
        // Tile Map Layer
        this.mapLayer = new TileMap(this, map, MapGenerator.getSolidTiles());
        addLayer(mapLayer);

        /* Entity Layer */
        Layer entityLayer = new Layer(this);

        List<Vector2i> floorPositions = MapGenerator.getFloorPositions();
        Collections.shuffle(floorPositions);

        // Add Player
        Vector2i playerPos = floorPositions.remove(0);
        player = new Player(this, playerPos);
        entityLayer.addObject(player);

        // Add Ghosts
        Set<Vector2i> walkable = new HashSet<>(MapGenerator.getFloorPositions());
        for (int i = 0; i < NUM_GHOSTS; i++) {
            if (floorPositions.isEmpty()) {
                break;
            }
            Vector2i ghostPos = floorPositions.remove(0);
            Ghost ghost = new Ghost(this, ghostPos);

            // ★ 新增依赖注入：可走格 + 目标（玩家） + 是否显示路径
            ghost.setWalkable(walkable);
            ghost.setTargetSupplier(this::getPlayerPosition);
            ghost.setShowPath(false); // TODO: Switching can be put in command input;

            entityLayer.addObject(ghost);
            ghostCache.add(ghost);
        }
        // Add Door
        Door door = new Door(this, MapGenerator.getExitPosition());
        entityLayer.addObject(door);
        addLayer(entityLayer);

        /* HUD */
        Layer HUDlayer = new Layer(this);
        topBar = new TopBar(this, false);
        HUDlayer.addObject(topBar);
        addLayer(HUDlayer);

        // If we're restoring from a save, apply positions now
        if (pendingRestore != null) {
            if (player != null) {
                player.setPosition(new Vector2i(pendingRestore.playerX, pendingRestore.playerY));
            }
            int n = Math.min(ghostCache.size(), pendingRestore.ghosts.size());
            for (int i = 0; i < n; i++) {
                core.SaveData.SavePos sp = pendingRestore.ghosts.get(i);
                ghostCache.get(i).setPosition(new Vector2i(sp.x, sp.y));
            }
        }

        requestRender();
    }

    // --- Save/Load helpers ---
    public long getSeed() {
        return seed;
    }

    @Override
    public void update(double dt, InputAction ia) {
        if (paused) { // command mode
            topBar.update(dt, ia);
            updateHoverText(ia);
            return;
        }
        // Consume ALL pending typed keys so none block later ':' presses.
        while (ia.hasNextKeyTyped()) {
            char c = ia.getNextKeyTyped();
            if (c == ':') {
                handOffFocus(); // pause & give focus
                topBar.beginCommandMode(); // seed ':' into buffer since we consumed it
                break; // stop processing further this frame (rest typed after focusing goes to TopBar)
            } else if (c == 't' || c == 'T') {
                Ghost.toggleGlobalShowPath();
                requestRender();
            } // ignore other keys (movement handled via isKeyDown in Player)
        }

        // Camera with dead zone: only shift when player nears borders
        if (player != null) {
            int margin = Math.max(0, getConfig().CAM_DEADZONE_MARGIN);
            Camera cam = getCamera();
            Vector2i screenPos = cam.worldToScreenPoint(player.getPosition());
            boolean needMove = screenPos.x() < margin
                    || screenPos.x() > cam.getWidth() - 1 - margin
                    || screenPos.y() < margin
                    || screenPos.y() > cam.getHeight() - 1 - margin;
            if (needMove) {
                cam.update(player.getPosition(), mapLayer.getWidth(), mapLayer.getHeight());
            }
        }

        super.update(dt, ia);
        updateHoverText(ia);
    }

    // Removed duplicate saveGame() earlier in file; keep single impl below

    private void updateHoverText(InputAction ia) {
        if (mapLayer == null)
            return;
        int screenX = ia.mouseTileX(); // screen-space tile coords (0..viewW-1)
        int screenY = ia.mouseTileY();
        if (screenX < 0 || screenX >= getCamera().getWidth() || screenY < 0 || screenY >= getCamera().getHeight()) {
            return; // outside visible viewport
        }
        // Translate to world coordinates using camera origin
        int worldX = getCamera().getX() + screenX;
        int worldY = getCamera().getY() + screenY;
        if (worldX < 0 || worldX >= mapLayer.getWidth() || worldY < 0 || worldY >= mapLayer.getHeight())
            return;
        TETile tile = mapLayer.getTile(worldX, worldY);
        if (tile == null)
            return;
        String desc = tile.description();
        if (!desc.equals(lastHoverDesc)) {
            lastHoverDesc = desc;
            topBar.setHoverText(desc);
        }
    }

    public void handOffFocus() {
        player.setHasFocus();
        topBar.setHasFocus();
        paused = !paused;
    }

    public Vector2i getPlayerPosition() {
        return (player != null) ? player.getPosition() : null;
    }

    public void triggerWin() {
        this.triggerWin = true;
    }

    public void triggerLost() {
        this.triggerLost = true;
    }

    public void triggerBack() {
        this.triggerBack = true;
    }

    // Exposed for TopBar :S command
    public void saveGame() {
        try {
            core.SaveData data = new core.SaveData();
            data.savedAtEpochMs = System.currentTimeMillis();
            data.name = core.SaveGameManager.makeDefaultSaveName();
            data.width = getConfig().WIDTH;
            data.height = getConfig().HEIGHT;
            data.screenWidth = getConfig().SCREEN_WIDTH;
            data.screenHeight = getConfig().SCREEN_HEIGHT;
            data.cellSize = getConfig().CELL_SIZE;
            data.seed = this.seed;
            if (getConfig() instanceof core.CustomConfig cc && cc.theme != null) {
                data.themeName = cc.theme.name();
            }
            if (player != null) {
                data.playerX = player.getPosition().x();
                data.playerY = player.getPosition().y();
            }
            for (Ghost g : ghostCache) {
                Vector2i p = g.getPosition();
                data.ghosts.add(new core.SaveData.SavePos(p.x(), p.y()));
            }
            core.SaveGameManager.write(data, data.name);
            System.out.println("Saved game: " + data.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SceneTransition pollTransition() {
        if (triggerWin) {
            return new SceneTransition.Replace(new FinalScene(FinalScene.STATE.WIN));
        }
        if (triggerLost) {
            return new SceneTransition.Replace(new FinalScene(FinalScene.STATE.LOST));
        }
        if (triggerBack) {
            return new SceneTransition.Replace(new IntroScene());
        }
        return null;
    }
}
