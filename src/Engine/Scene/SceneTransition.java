package Engine.Scene;

/**
 * Represents a command to change the active scene.
 * A Scene can return a SceneTransition to the SceneManager to request a change.
 */
public sealed interface SceneTransition permits SceneTransition.Push, SceneTransition.Pop, SceneTransition.Replace {
    /**
     * Pushes a new scene onto the stack.
     */
    record Push(Scene next) implements SceneTransition {}

    /**
     * Pops the current scene from the stack.
     */
    record Pop() implements SceneTransition {}

    /**
     * Replaces the current scene with a new one.
     */
    record Replace(Scene next) implements SceneTransition {}
}