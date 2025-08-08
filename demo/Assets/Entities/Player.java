package Assets.Entities;

import Assets.Scenes.GameScene;
import Engine.Graphics.Renderer;
import Engine.Graphics.tileengine.TETile;
import Engine.Input.InputAction;
import Engine.Scene.Entity;
import Engine.Scene.Scene;
import Engine.Utils.Vector2i;
import core.CustomConfig;

public class Player extends Entity {
    private final TETile playerTile;
    private boolean hasFocus;

    public Player(Scene scene, Vector2i position) {
        super(scene, position);
        playerTile = ((CustomConfig) scene.getConfig()).theme.avatar();
        hasFocus = true;
        setCollider(new Vector2i(1, 1), false, false);
    }

    @Override public void onStart() { }

    @Override
    public void update(double dt, InputAction ia) {
        if (!hasFocus) { super.update(dt, ia); return; }
        Vector2i move = ia.pollMovement(getScene().getConfig().PLAYER_MOVE_INTERVAL_SEC);
        if (!move.equals(Vector2i.ZERO)) {
            setPosition(position.add(move));
            getScene().requestRender();
        }
        super.update(dt, ia);
    }

    @Override
    public void render(Renderer r) {
        Vector2i screen = getScene().getCamera().worldToScreenPoint(position);
        r.drawTile(screen.x(), screen.y(), playerTile);
    }

    @Override
    public void onTriggerEnter(Entity other) {
        if (other instanceof Door) {
            ((GameScene) getScene()).triggerWin();
            System.out.println("WIN");
        }
    }

    @Override
    public void onCollide(Entity other) {
        if (other instanceof Ghost) {
            ((GameScene) getScene()).triggerLost();
            System.out.println("LOST");
        }
    }

    public void setHasFocus() { this.hasFocus = !this.hasFocus; }
    public Vector2i getPosition() { return this.position; }
}
