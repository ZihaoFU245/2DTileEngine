package Assets.Scenes;

import Assets.Components.CustomTiles;
import Assets.Components.Theme;
import Assets.Map.MapGenerator;
import Assets.Map.ThemeArt;
import Engine.Graphics.Renderer;
import Engine.Graphics.tileengine.TETile;
import Engine.Input.InputAction;
import Engine.Scene.Layer;
import Engine.Scene.Scene;
import Engine.Scene.SceneTransition;
import Engine.Scene.TileMap;
import Engine.Utils.TextUtils;
import core.CustomConfig;

import java.awt.*;
import java.util.List;

/**
 * A scene for selecting a game theme.
 * <p>
 * This class is designed as an ui and for user to select theme
 * Read {@link Assets.Components.CustomTiles} to see how many themes we currently have
 * The idea is make each theme with an ASCII ART: Like Below
 * <p>
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║  █████╗ ██████╗  ██████╗  █████╗ ███╗  ██╗███████╗    ██████╗ ██╗   ██╗███╗   ██╗ ██████╗ ███████╗███╗   ██╗  ║
 * ║ ██╔══██╗██╔══██╗██╔════╝ ██╔══██╗████╗ ██║██╔════╝    ██╔══██╗██║   ██║████╗  ██║██╔════╝ ██╔════╝████╗  ██║  ║
 * ║ ███████║██████╔╝██║      ███████║██╔██╗██║█████╗      ██║  ██║██║   ██║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║  ║
 * ║ ██╔══██║██╔══██╗██║      ██╔══██║██║╚████║██╔══╝      ██║  ██║██║   ██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║  ║
 * ║ ██║  ██║██║  ██║╚██████╗ ██║  ██║██║ ╚███║███████╗    ██████╔╝╚██████╔╝██║ ╚████║╚██████╔╝███████╗██║ ╚████║  ║
 * ║ ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚══╝╚══════╝    ╚═════╝  ╚═════╝ ╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝  ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 * </P>
 * But the screen may not fit in this box, so it is essential to split it into two lines, but must in one box!
 * Like the one above Arcane Dungeon does not fit, there fore display:
 * ╔═════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                       █████╗ ██████╗  ██████╗  █████╗ ███╗  ██╗███████╗                         ║
 * ║                      ██╔══██╗██╔══██╗██╔════╝ ██╔══██╗████╗ ██║██╔════╝                         ║
 * ║                      ███████║██████╔╝██║      ███████║██╔██╗██║█████╗                           ║
 * ║                      ██╔══██║██╔══██╗██║      ██╔══██║██║╚████║██╔══╝                           ║
 * ║                      ██║  ██║██║  ██║╚██████╗ ██║  ██║██║ ╚███║███████╗                         ║
 * ║                      ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚══╝╚══════╝                         ║
 * ║                                                                                                 ║
 * ║                     ██████╗ ██╗   ██╗███╗   ██╗ ██████╗ ███████╗███╗   ██╗                      ║
 * ║                     ██╔══██╗██║   ██║████╗  ██║██╔════╝ ██╔════╝████╗  ██║                      ║
 * ║                     ██║  ██║██║   ██║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║                      ║
 * ║                     ██║  ██║██║   ██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║                      ║
 * ║                     ██████╔╝╚██████╔╝██║ ╚████║╚██████╔╝███████╗██║ ╚████║                      ║
 * ║                     ╚═════╝  ╚═════╝ ╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝                      ║
 * ╚═════════════════════════════════════════════════════════════════════════════════════════════════╝
 * <p>
 * The User can use A / D to select theme, and at the top we have, e.g., 3 / 7 to display with which
 * theme it is currently selected. And for backgrounds, use {@link Assets.Map.MapGenerator} to generate an example scene
 * Use {@link Engine.Scene.Layer} for convenience and let the ASCII ART render on top it.
 * </p>
 * <p>
 * User can press B to go back to the {@link IntroScene} and Enter to confirm.
 * </p>
 */
public class ThemeSelectionScene extends Scene {

    private final List<Theme> themes = List.of(
            new CustomTiles.SciFiTheme(),
            new CustomTiles.ArcaneDungeon(),
            new CustomTiles.NatureGrove(),
            new CustomTiles.SteamFactory(),
            new CustomTiles.DesertRuins(),
            new CustomTiles.RetroArcade(),
            new CustomTiles.CrystalCave()
    );
    private int currentThemeIndex = 0;
    private Theme originalTheme;
    private boolean goBack = false;
    private boolean confirmed = false;
    private TileMap backgroundLayer;

    @Override
    public void onStart() {
        super.onStart();
        if (config instanceof CustomConfig customConfig) {
            originalTheme = customConfig.theme;
        }
        TETile[][] initialTiles = new TETile[config.WIDTH][config.HEIGHT];
        backgroundLayer = new TileMap(this, initialTiles);
        addLayer(backgroundLayer);
        addLayer(new UILayer(this));
        generateBackground();
    }

    @Override
    public void update(double dt, InputAction ia) {
        if (ia.hasNextKeyTyped()) {
            char c = Character.toUpperCase(ia.getNextKeyTyped());
            boolean themeChanged = false;
            if (c == 'A') {
                currentThemeIndex = (currentThemeIndex - 1 + themes.size()) % themes.size();
                themeChanged = true;
            } else if (c == 'D') {
                currentThemeIndex = (currentThemeIndex + 1) % themes.size();
                themeChanged = true;
            } else if (c == 'B') {
                goBack = true;
            } else if (c == '\n' || c == '\r') {
                confirmed = true;
            }

            if (themeChanged) {
                generateBackground();
            }
            requestRender();
        }
    }

    private void generateBackground() {
        Theme currentTheme = themes.get(currentThemeIndex);
        if (config instanceof CustomConfig customConfig) {
            customConfig.theme = currentTheme;
            long seed = System.currentTimeMillis();
            TETile[][] newTiles = MapGenerator.generateMap(customConfig.SCREEN_WIDTH, customConfig.SCREEN_HEIGHT, seed, customConfig);
            for (int x = 0; x < newTiles.length; x++) {
                for (int y = 0; y < newTiles[0].length; y++) {
                    backgroundLayer.setTile(x, y, newTiles[x][y]);
                }
            }
        }
    }

    @Override
    public SceneTransition pollTransition() {
        if (goBack) {
            if (config instanceof CustomConfig customConfig) {
                customConfig.theme = originalTheme;
            }
            return new SceneTransition.Pop();
        }
        if (confirmed) {
            if (config instanceof CustomConfig) {
                ((CustomConfig) config).theme = themes.get(currentThemeIndex);
            }
            return new SceneTransition.Pop();
        }
        return null;
    }

    public Theme getCurrentTheme() {
        return themes.get(currentThemeIndex);
    }

    public String getCounter() {
        return String.format("%d / %d", currentThemeIndex + 1, themes.size());
    }

    public static class UILayer extends Layer {
        private final ThemeSelectionScene themeSelectionScene;

        public UILayer(ThemeSelectionScene themeSelectionScene) {
            this.themeSelectionScene = themeSelectionScene;
        }

        @Override
        public void render(Renderer r) {
            Theme currentTheme = themeSelectionScene.getCurrentTheme();
            String themeName = currentTheme.name();
            String counter = themeSelectionScene.getCounter();

            int width = r.getWidth();
            int height = r.getHeight();

            // Draw ASCII Art
            String[] art = ThemeArt.THEME_ARTS.get(themeName);
            if (art != null) {
                int artHeight = art.length;
                int artWidth = art[0].length();
                int startX = (width - artWidth) / 2;
                int startY = (height - artHeight) / 2 + 5;
                for (int i = 0; i < artHeight; i++) {
                    TextUtils.drawText(r, art[i], startX, startY + artHeight - 1 - i,
                            ThemeArt.THEME_COLORS.get(themeName));
                }
            }

            // Draw counter
            TextUtils.drawText(r, counter, width / 2 - counter.length() / 2, height - 4, Color.YELLOW);

            // Draw instructions
            String instructions = "[A] Prev | [D] Next | [Enter] Confirm | [B] Back";
            String border = "╔" + "═".repeat(instructions.length() + 2) + "╗";
            String content = "║ " + instructions + " ║";
            String bottomBorder = "╚" + "═".repeat(instructions.length() + 2) + "╝";

            TextUtils.drawText(r, border, width / 2 - (instructions.length() + 4) / 2, 3, Color.WHITE);
            TextUtils.drawText(r, content, width / 2 - (instructions.length() + 4) / 2, 2, Color.WHITE);
            TextUtils.drawText(r, bottomBorder, width / 2 - (instructions.length() + 4) / 2, 1, Color.WHITE);
        }
    }
}
