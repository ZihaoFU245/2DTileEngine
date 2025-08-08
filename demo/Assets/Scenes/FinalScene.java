package Assets.Scenes;

import Engine.Graphics.Renderer;
import Engine.Input.InputAction;
import Engine.Scene.Scene;
import Engine.Scene.SceneTransition;
import Engine.Utils.TextUtils;

import java.awt.Color;

public class FinalScene extends Scene {

    STATE currState;
    private boolean replayTriggered = false;

    public enum STATE {
        WIN,
        LOST
    }

    public FinalScene(STATE state) {
        currState = state;
    }

    @Override
    public void onStart() {
        super.onStart();
        requestRender();
    }

    @Override
    public void onResume() {
        super.onResume();
        replayTriggered = false;
        requestRender();
    }

    @Override
    public void update(double dt, InputAction ia) {
        while (ia.hasNextKeyTyped()) {
            char c = ia.getNextKeyTyped();
            handleInput(c);
            requestRender();
        }
    }

    private void handleInput(char c) {
        switch (Character.toUpperCase(c)) {
            case 'R':
                // Replay the game
                replayTriggered = true;
                break;
            case 'Q':
                // Quit the game
                System.exit(0);
                break;
        }
    }

    @Override
    public void render(Renderer r) {
        int w = r.getWidth();
        int h = r.getHeight();

        String title;
        Color titleColor;
        String message;

        if (currState == STATE.WIN) {
            title = "CONGRATULATIONS!";
            titleColor = Color.GREEN;
            message = "You found the exit and escaped!";
        } else {
            title = "GAME OVER";
            titleColor = Color.RED;
            message = "You were caught by the enemies!";
        }

        String replayOption = "(R) Replay";
        String quitOption = "(Q) Quit";

        // Draw title
        TextUtils.drawText(r, title, w / 2 - title.length() / 2, h * 3 / 4, titleColor);

        // Draw message
        TextUtils.drawText(r, message, w / 2 - message.length() / 2, h * 3 / 4 - 3);

        // Draw options
        TextUtils.drawText(r, replayOption, w / 2 - replayOption.length() / 2, h / 2);
        TextUtils.drawText(r, quitOption, w / 2 - quitOption.length() / 2, h / 2 - 2);
    }

    @Override
    public SceneTransition pollTransition() {
        if (replayTriggered) {
            return new SceneTransition.Replace(new IntroScene());
        }
        return null;
    }
}