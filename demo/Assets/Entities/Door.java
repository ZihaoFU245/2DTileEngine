package Assets.Entities;

import Engine.Graphics.Renderer;
import Engine.Scene.Entity;
import Engine.Utils.Vector2i;
import Engine.Scene.Scene;

public class Door extends Entity {
    public Door(Scene scene, Vector2i position) {
        super(scene, position);
        setCollider(new Vector2i(1, 1), true, true); // isTrigger = true
    }

    @Override
    public void render(Renderer r) {

    }
}

