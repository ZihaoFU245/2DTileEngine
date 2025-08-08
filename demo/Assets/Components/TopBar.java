package Assets.Components;

import Assets.Scenes.GameScene;
import Engine.Graphics.Renderer;
import Engine.Graphics.tileengine.Tileset;
import Engine.Input.InputAction;
import Engine.Scene.MonoBehaviour;
import Engine.Scene.Scene;
import Engine.Utils.TextUtils;

/**
 * The top bar of HUD:
 * The Top Bar should now only contain "Commands>" part,
 * which displayed in the middle of the HUD top bar.
 * In update method it should Check if ia has ":" pressed, if it is pressed then
 * check if it is one of the valid commands entered.
 * <p>
 * When the ":" key is pressed, the game will wait for further input to
 * determine if it is a valid command. The focus will remain on this input
 * field until the "enter" key is pressed, at which point the command will
 * be processed.
 *
 * @note If ":"is pressed, the keyboard should have the entire focus,
 *       until "enter" is pressed
 */
public class TopBar implements MonoBehaviour {
    private boolean hasFocus;
    private final Scene currScene;
    private final StringBuilder inputBuffer = new StringBuilder();
    private String hoverText = ""; // tile description under mouse

    public TopBar(Scene curr, boolean hasF) {
        hasFocus = hasF;
        currScene = curr;
    }

    public void setHasFocus() {
        this.hasFocus = !this.hasFocus;
    }

    // Called by GameScene when hover tile changes
    public void setHoverText(String text) {
        this.hoverText = text == null ? "" : text;
        currScene.requestRender();
    }

    // Called when switching into command mode AFTER ':' was consumed by outer
    // scene.
    public void beginCommandMode() {
        inputBuffer.setLength(0);
        inputBuffer.append(':');
        currScene.requestRender();
    }

    @Override
    public void onStart() {
        // Initialization in here
    }

    @Override
    public void update(double dt, InputAction ia) {
        if (!hasFocus) {
            return;
        }

        while (ia.hasNextKeyTyped()) {
            char key = ia.getNextKeyTyped();

            if (key == '\n') { // Enter key
                processCommand();
                inputBuffer.setLength(0);
                // handOffFocus is in GameScene, which will toggle focus
                ((GameScene) currScene).handOffFocus();

            } else if (key == '\b') { // Backspace
                if (inputBuffer.length() > 1) { // Keep the ':'
                    inputBuffer.deleteCharAt(inputBuffer.length() - 1);
                }
            } else {
                inputBuffer.append(key);
            }
            currScene.requestRender();
        }
    }

    private void processCommand() {
        String command = inputBuffer.toString().trim().toUpperCase();
        if (command.equals(":Q")) {
            // TODO: Saving logic goes here
            System.exit(0);
        }
        if (command.equals(":S")) {
            // Save game
            if (currScene instanceof GameScene gs) {
                gs.saveGame();
            }
            currScene.requestRender();
        }
        if (command.equals(":B")) {
            // Go back to an intro scene
            ((GameScene) currScene).triggerBack();
        }
        if (command.equals(":T")) { // allow :t toggling path while in command mode
            GhostToggleHelper.toggle();
            currScene.requestRender();
        }
    }

    @Override
    public void render(Renderer r) {
        String beginText = "Commands > ";
        String textToDraw = inputBuffer.toString();

        for (int i = 0; i < r.getWidth(); i++) {
            r.drawTile(i, r.getHeight() - 1, Tileset.NOTHING);
        }

        int y = r.getHeight() - 1;
        // Left side: hover tile description (truncate if too long)
        String hover = hoverText == null ? "" : hoverText;
        int maxHoverLen = Math.max(0, (r.getWidth() / 3));
        if (hover.length() > maxHoverLen) {
            hover = hover.substring(0, Math.max(0, maxHoverLen - 3)) + "...";
        }
        TextUtils.drawText(r, hover, 1, y);

        int x = (r.getWidth() - beginText.length()) / 2;
        TextUtils.drawText(r, beginText + textToDraw, x, y);
    }

    // Helper static to avoid circular import (Ghost referenced here logically)
    private static class GhostToggleHelper {
        static void toggle() {
            Assets.Entities.Ghost.toggleGlobalShowPath();
        }
    }
}
