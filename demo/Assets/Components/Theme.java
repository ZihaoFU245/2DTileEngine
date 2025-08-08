package Assets.Components;

import Engine.Graphics.tileengine.TETile;
import Engine.Graphics.tileengine.Tileset;

public interface Theme {
    TETile wall();

    TETile floor();

    TETile avatar();

    TETile ghost();

    String name();

    default TETile lockedDoor() {
        return Tileset.LOCKED_DOOR;
    }

    /**
     * Nothing tile default is a pure black tile.
     */
    default TETile nothing() {
        return Tileset.NOTHING;
    }
}
