package core;

import Assets.Components.CustomTiles;
import Assets.Components.Theme;
import Assets.Scenes.IntroScene;
import Engine.Engine;
import Engine.Config;

public class Main {
    public static void main(String[] args) {

        // 100 * 60 is a good choice
        int WIDTH = 200;
        int HEIGHT = 110;
        int CELL_SIZE = 16; // For the collision system's spatial hash grid
        int SCREEN_WIDTH = 100;
        int SCREEN_HEIGHT = 55;

        Theme theme = new CustomTiles.DesertRuins();

        Config config = new CustomConfig(WIDTH, HEIGHT, CELL_SIZE, theme, SCREEN_WIDTH, SCREEN_HEIGHT);
        Engine engine = new Engine(config);

        engine.register(new IntroScene());

        engine.run();
    }
}
