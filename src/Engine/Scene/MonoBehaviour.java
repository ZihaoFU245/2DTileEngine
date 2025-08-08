package Engine.Scene;

import Engine.Graphics.Renderer;
import Engine.Input.InputAction;

/**
 * An interface for all game objects that are managed by a Scene.
 * It defines the standard lifecycle methods that the engine will call.
 */
public interface MonoBehaviour {
    /**
     * Called once when the object is initialized.
     */
    void onStart();

    /**
     * Called every frame to update the object's state.
     *
     * @param dt The fixed time step for this frame.
     * @param ia The current input state.
     */
    void update(double dt, InputAction ia);

    /**
     * Called every frame to render the object.
     *
     * @param r The renderer to draw to.
     */
    void render(Renderer r);
}
