package Engine.Scene;

import Engine.Graphics.Renderer;
import Engine.Input.InputAction;
import java.util.ArrayList;
import java.util.List;

/**
 * A Layer is a container for a collection of MonoBehaviour objects.
 * Layers are used to organize and render objects in a specific order.
 */
public class Layer {
    protected final List<MonoBehaviour> objects = new ArrayList<>();
    protected Scene scene; // made protected for subclass access

    public Layer() {
        this.scene = null;
    }

    public Layer(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return this.scene;
    }

    /**
     * Called when the layer is started.
     */
    public void onStart() {
        for (MonoBehaviour obj : objects)
            obj.onStart();
    }

    /**
     * Called every frame to update the objects in this layer.
     *
     * @param dt The fixed time step for this frame.
     * @param ia The current input state.
     */
    public void update(double dt, InputAction ia) {
        for (MonoBehaviour obj : objects)
            obj.update(dt, ia);
    }

    /**
     * Called every frame to render the objects in this layer.
     *
     * @param r The renderer to draw to.
     */
    public void render(Renderer r) {
        for (MonoBehaviour obj : objects)
            obj.render(r);
    }

    /**
     * Adds a MonoBehaviour to this layer.
     *
     * @param obj The object to add.
     */
    public void addObject(MonoBehaviour obj) {
        objects.add(obj);
    }

    /**
     * Removes a MonoBehaviour from this layer.
     *
     * @param obj The object to remove.
     */
    public void removeObject(MonoBehaviour obj) {
        objects.remove(obj);
    }

    public List<MonoBehaviour> getObjects() {
        return objects;
    }
}