package Assets.Scenes;

import Engine.Graphics.Renderer;
import Engine.Input.InputAction;
import Engine.Scene.Scene;
import Engine.Scene.SceneTransition;
import Engine.Utils.TextUtils; // Use the new TextUtils

import core.CustomConfig;
import core.SaveData;
import core.SaveGameManager;
import core.ThemeUtils;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IntroScene extends Scene {

    // Scene state machine
    private enum State {
        MAIN_MENU,
        SEED_INPUT,
        LOAD_MENU,
        CONFIRM_DELETE
    }

    private State currentState = State.MAIN_MENU;
    private final StringBuilder seedBuffer = new StringBuilder();
    private boolean readyToStart = false;
    private boolean themeSelectionTriggered = false;

    // Load menu state
    private List<File> saves = new ArrayList<>();
    private int selectedSave = 0;
    private SaveData pendingLoad = null;
    private String loadMenuMessage = null; // status/info message for load menu

    @Override
    public void onStart() {
        requestRender();
    }

    @Override
    public void onResume() {
        requestRender();
    }

    @Override
    public void update(double dt, InputAction ia) {
        while (ia.hasNextKeyTyped()) {
            char c = ia.getNextKeyTyped();

            if (currentState == State.MAIN_MENU) {
                handleMainMenuInput(c);
            } else if (currentState == State.LOAD_MENU) {
                handleLoadMenuInput(c);
            } else if (currentState == State.CONFIRM_DELETE) {
                handleConfirmDeleteInput(c);
            } else if (currentState == State.SEED_INPUT) {
                handleSeedInput(c);
            }

            requestRender();
        }
    }

    private void handleMainMenuInput(char c) {
        switch (Character.toUpperCase(c)) {
            case 'N':
                currentState = State.SEED_INPUT;
                break;
            case 'L':
                refreshSaves();
                currentState = State.LOAD_MENU;
                break;
            case 'Q':
                System.exit(0);
                break;
            case 'T':
                themeSelectionTriggered = true;
                break;
        }
    }

    private void handleSeedInput(char c) {
        if (c == '\b' && !seedBuffer.isEmpty()) {
            seedBuffer.deleteCharAt(seedBuffer.length() - 1);
        } else if (c == '\n' || c == '\r') { // Enter
            if (!seedBuffer.isEmpty()) {
                readyToStart = true;
            }
        } else if (c == 'b' || c == 'B') { // Back
            seedBuffer.setLength(0);
            readyToStart = false;
            currentState = State.MAIN_MENU;
        } else if (Character.isDigit(c)) { // digits only
            seedBuffer.append(c);
        }
    }

    private void handleLoadMenuInput(char c) {
        switch (Character.toUpperCase(c)) {
            case 'W':
                if (!saves.isEmpty()) selectedSave = (selectedSave - 1 + saves.size()) % saves.size();
                loadMenuMessage = null;
                break;
            case 'S':
                if (!saves.isEmpty()) selectedSave = (selectedSave + 1) % saves.size();
                loadMenuMessage = null;
                break;
            case 'B':
                currentState = State.MAIN_MENU;
                break;
            case 'D':
                // Go to confirmation screen before deleting
                if (!saves.isEmpty()) {
                    currentState = State.CONFIRM_DELETE;
                }
                break;
            case '\n':
            case '\r':
                if (!saves.isEmpty()) {
                    try {
                        pendingLoad = SaveGameManager.read(saves.get(selectedSave));
                        readyToStart = true;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                break;
        }
    }

    private void handleConfirmDeleteInput(char c) {
        char u = Character.toUpperCase(c);
        if (u == 'Y' || u == '\n' || u == '\r') {
            // Confirm delete
            if (!saves.isEmpty() && selectedSave >= 0 && selectedSave < saves.size()) {
                File toDelete = saves.get(selectedSave);
                String name = toDelete.getName();
                boolean ok = false;
                try {
                    ok = toDelete.delete();
                } catch (Exception ex) {
                    // keep ok = false
                }
                refreshSaves();
                if (!saves.isEmpty()) {
                    selectedSave = Math.min(selectedSave, saves.size() - 1);
                } else {
                    selectedSave = 0;
                }
                loadMenuMessage = ok ? ("Deleted: " + name) : ("Failed to delete: " + name);
            }
            currentState = State.LOAD_MENU;
        } else if (u == 'N' || u == 'B' || c == 27 /* ESC */) {
            // Cancel
            currentState = State.LOAD_MENU;
            loadMenuMessage = "Deletion cancelled";
        }
    }

    private void refreshSaves() {
        saves = SaveGameManager.listSaves();
        selectedSave = 0;
    }

    @Override
    public void render(Renderer r) {
        int w = r.getWidth();
        int h = r.getHeight();

        if (currentState == State.MAIN_MENU) {
            renderMainMenu(r, w, h);
        } else if (currentState == State.SEED_INPUT) {
            renderSeedInput(r, w, h);
        } else if (currentState == State.LOAD_MENU) {
            renderLoadMenu(r, w, h);
        } else if (currentState == State.CONFIRM_DELETE) {
            renderConfirmDelete(r, w, h);
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

    private void renderLoadMenu(Renderer r, int w, int h) {
        String title = "Load Game";
        String instructions = "W/S: Navigate, Enter: Load, D: Delete, B: Back";
        TextUtils.drawText(r, title, w / 2 - title.length() / 2, h * 3 / 4, Color.CYAN);
        TextUtils.drawText(r, instructions, w / 2 - instructions.length() / 2, h * 3 / 4 - 2, Color.GRAY);
        if (loadMenuMessage != null && !loadMenuMessage.isEmpty()) {
            TextUtils.drawText(r, loadMenuMessage, w / 2 - Math.min(loadMenuMessage.length(), w - 4) / 2, h * 3 / 4 - 4, Color.PINK);
        }

        int listTop = h * 3 / 4 - 5;
        int maxToShow = Math.min(10, Math.max(1, h / 2));
        int start = saves.isEmpty() ? 0 : Math.max(0, Math.min(selectedSave - maxToShow / 2, Math.max(0, saves.size() - maxToShow)));
        int end = Math.min(saves.size(), start + maxToShow);
        for (int i = start; i < end; i++) {
            String name = saves.get(i).getName();
            String line = (i == selectedSave ? "> " : "  ") + name;
            Color c = (i == selectedSave) ? Color.YELLOW : Color.WHITE;
            TextUtils.drawText(r, line, w / 2 - Math.min(line.length(), w - 4) / 2, listTop - (i - start), c);
        }
        if (saves.isEmpty()) {
            String none = "No saves in ./" + core.SaveGameManager.SAVE_DIR;
            TextUtils.drawText(r, none, w / 2 - none.length() / 2, h / 2, Color.LIGHT_GRAY);
        }
    }

    private void renderConfirmDelete(Renderer r, int w, int h) {
        String title = "Confirm Delete";
        String fileName = (!saves.isEmpty() && selectedSave >= 0 && selectedSave < saves.size())
                ? saves.get(selectedSave).getName()
                : "(none)";
        String prompt = "Delete '" + fileName + "'?";
        String actions = "(Y) Yes  (N) No";

        TextUtils.drawText(r, title, w / 2 - title.length() / 2, h / 2 + 4, Color.RED);
        TextUtils.drawText(r, prompt, w / 2 - prompt.length() / 2, h / 2 + 2, Color.ORANGE);
        TextUtils.drawText(r, actions, w / 2 - actions.length() / 2, h / 2, Color.GRAY);
    }

    @Override
    public SceneTransition pollTransition() {
        if (readyToStart) {
            if (pendingLoad != null) {
                // Apply theme and ghost count from save before starting
                if (getConfig() instanceof CustomConfig cc) {
                    cc.theme = ThemeUtils.forName(pendingLoad.themeName);
                    cc.ghostNum = Math.max(0, pendingLoad.ghosts.size());
                }
                return new SceneTransition.Replace(new GameScene(pendingLoad.seed, pendingLoad));
            } else if (!seedBuffer.isEmpty()) {
                long seed = Long.parseLong(seedBuffer.toString());
                return new SceneTransition.Replace(new GameScene(seed));
            }
        } else if (themeSelectionTriggered) {
            // Reset the trigger so we don't immediately re-push after the theme scene pops.
            themeSelectionTriggered = false;
            return new SceneTransition.Push(new ThemeSelectionScene());
        }
        return null;
    }
}