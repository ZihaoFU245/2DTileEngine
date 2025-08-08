package Assets.Scenes;

import Engine.Graphics.Renderer;
import Engine.Input.InputAction;
import Engine.Scene.Scene;
import Engine.Scene.SceneTransition;
import Engine.Utils.TextUtils; // Use the new TextUtils

import java.awt.Color;

public class IntroScene extends Scene {

    // To manage the scene's state
    private enum State {
        MAIN_MENU,
        SEED_INPUT
    }

    private State currentState = State.MAIN_MENU;
    private final StringBuilder seedBuffer = new StringBuilder();
    private boolean readyToStart = false;
    private boolean themeSelectionTriggered = false;

    @Override
    public void onStart() {
        super.onStart();
        requestRender();
    }

    @Override
    public void onResume() {
        super.onResume();
        themeSelectionTriggered = false;
        requestRender();
    }

    @Override
    public void update(double dt, InputAction ia) {
        while (ia.hasNextKeyTyped()) {
            char c = ia.getNextKeyTyped();

            if (currentState == State.MAIN_MENU) {
                handleMainMenuInput(c);
            } else if (currentState == State.SEED_INPUT) {
                handleSeedInput(c);
            }

            requestRender();
        }
    }

    private void handleMainMenuInput(char c) {
        switch (Character.toUpperCase(c)) {
            case 'N':
                // Switch to seed input mode
                currentState = State.SEED_INPUT;
                break;
            case 'L':
                // Load game functionality (not implemented)
                // For now, we can just ignore it or add a placeholder message
                break;
            case 'Q':
                // Quit the game
                System.exit(0);
                break;
            case 'T':
                themeSelectionTriggered = true;
                break;
        }
    }

    private void handleSeedInput(char c) {
        // Backspace
        if (c == '\b' && !seedBuffer.isEmpty()) {
            seedBuffer.deleteCharAt(seedBuffer.length() - 1);
        }
        // Enter / Return
        else if (c == '\n' || c == '\r') {
            if (!seedBuffer.isEmpty()) {
                readyToStart = true;
            }
        }
        // go back to the main menu
        else if (c == 'b' || c == 'B') {
            seedBuffer.setLength(0);
            readyToStart = false;
            currentState = State.MAIN_MENU;
        }
        // Digits only
        else if (Character.isDigit(c)) {
            seedBuffer.append(c);
        }
    }

    @Override
    public void render(Renderer r) {
        int w = r.getWidth();
        int h = r.getHeight();

        if (currentState == State.MAIN_MENU) {
            renderMainMenu(r, w, h);
        } else if (currentState == State.SEED_INPUT) {
            renderSeedInput(r, w, h);
        }
    }

    private void renderMainMenu(Renderer r, int w, int h) {
        String title = "BYOW – Maze Adventure";
        String desc1 = "You are trapped. Find the exit to win.";
        String desc2 = "Enemies are hunting you. Don't get caught!";
        String newGame = "(N) New Game";
        String loadGame = "(L) Load Game";
        String quitGame = "(Q) Quit Game";
        String themeSelection = "(T) Theme Selection!!! Try it out!";

        TextUtils.drawText(r, title, w / 2 - title.length() / 2, h * 3 / 4, Color.CYAN);
        TextUtils.drawText(r, desc1, w / 2 - desc1.length() / 2, h * 3 / 4 - 2);
        TextUtils.drawText(r, desc2, w / 2 - desc2.length() / 2, h * 3 / 4 - 4);

        TextUtils.drawText(r, newGame, w / 2 - newGame.length() / 2, h / 2);
        TextUtils.drawText(r, loadGame, w / 2 - loadGame.length() / 2, h / 2 - 2);
        TextUtils.drawText(r, quitGame, w / 2 - quitGame.length() / 2, h / 2 - 4);
        TextUtils.drawText(r, themeSelection, w / 2 - themeSelection.length() / 2, h / 2 - 10, Color.ORANGE);
    }

    private void renderSeedInput(Renderer r, int w, int h) {
        String title = "BYOW – Maze Adventure";
        String prompt = "Enter a seed for map generation, then press Enter.";
        String input = "> " + seedBuffer;
        String goBackPrompt = "(B) Back";

        TextUtils.drawText(r, title, w / 2 - title.length() / 2, h * 3 / 4, Color.CYAN);
        TextUtils.drawText(r, prompt, w / 2 - prompt.length() / 2, h / 2 + 3);
        TextUtils.drawText(r, input, w / 2 - input.length() / 2, h / 2, Color.YELLOW);
        TextUtils.drawText(r, goBackPrompt, w / 2 - goBackPrompt.length() / 2, h / 4);
    }


    @Override
    public SceneTransition pollTransition() {
        if (readyToStart) {
            long seed = Long.parseLong(seedBuffer.toString());
            return new SceneTransition.Replace(new GameScene(seed));
        } else if (themeSelectionTriggered) {
            return new SceneTransition.Push(new ThemeSelectionScene());
        }
        return null;
    }
}