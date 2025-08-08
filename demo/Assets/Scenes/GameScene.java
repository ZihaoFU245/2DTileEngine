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
    private boolean paused = false;
    private boolean triggerWin = false;
    private boolean triggerLost = false;
    private boolean triggerBack = false;
    private String lastHoverDesc = ""; // cache to avoid redundant renders

    public GameScene(long seed) {
        /* Game Object should be put in onStart method as possible. */
        this.seed = seed;
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

        requestRender();
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
