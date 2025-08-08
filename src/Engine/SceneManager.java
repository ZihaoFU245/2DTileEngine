package Engine;

import Engine.Scene.Scene;
import Engine.Scene.SceneTransition;

import java.util.Stack;

/**
 * Manages the stack of scenes, handling transitions and lifecycle events.
 */
public class SceneManager {
    private final Stack<Scene> scenes = new Stack<>();
    private final Config config;

    public SceneManager(Config config) {
        this.config = config;
    }

    /**
     * Pushes a new scene onto the stack, pausing the previous one.
     *
     * @param scene The new scene to add.
     */
    void push(Scene scene) {
        if (!scenes.isEmpty()) {
            scenes.peek().onPause();
        }
        scenes.push(scene);
        scene.setConfig(this.config);
        scene.onStart();
    }

    /**
     * Pops the current scene from the stack, destroying it and resuming the next one.
     */
    void pop() {
        if (!scenes.isEmpty()) {
            scenes.pop().onDestroy();
        }
        if (!scenes.isEmpty()) {
            scenes.peek().onResume();
        }
    }

    /**
     * Replaces the current scene with a new one.
     *
     * @param scene The new scene.
     */
    void replace(Scene scene) {
        if (!scenes.isEmpty()) {
            scenes.peek().onDestroy();
            scenes.pop();
        }
        scenes.push(scene);
        scene.setConfig(this.config);
        scene.onStart();
    }

    /**
     * Gets the currently active scene.
     *
     * @return The current scene, or null if no scenes are active.
     */
    public Scene getCurrentScene() {
        return scenes.isEmpty() ? null : scenes.peek();
    }

    /**
     * Checks for and handles scene transitions requested by the current scene.
     */
    public void handleTransition() {
        if (scenes.isEmpty()) {
            return;
        }
        SceneTransition transition = scenes.peek().pollTransition();
        if (transition != null) {
            switch (transition) {
                case SceneTransition.Push push -> push(push.next());
                case SceneTransition.Pop pop -> pop();
                case SceneTransition.Replace replace -> replace(replace.next());
            }
        }
    }

    /**
     * Checks if there are any scenes on the stack.
     *
     * @return True if there is at least one scene.
     */
    public boolean hasScene() {
        return !scenes.isEmpty();
    }
}
