package core;

import Assets.Components.Theme;
import Engine.Config;

public class CustomConfig extends Config {
    public static final String GHOST_PATH = "Assets/images/ghost.png";
    public Theme theme;
    public int ghostNum = 5;

    public CustomConfig(int width,
                        int height,
                        int cellSize,
                        Theme theme,
                        int sw,
                        int sh) {
        super(width, height, sw, sh, cellSize);
        this.theme = theme;
    }
}