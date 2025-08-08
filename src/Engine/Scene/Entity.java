package Engine.Scene;

import Engine.Graphics.Renderer;
import Engine.Input.InputAction;
import Engine.Utils.Vector2i;

/**
 * An abstract base class for all game objects that can exist in the world,
 * have a position, and potentially interact with other entities.
 * It implements MonoBehaviour and provides a foundation for objects like players, enemies, and items.
 */
public abstract class Entity implements MonoBehaviour {

    protected Vector2i position;
    protected Vector2i previousPosition;
    protected Collider collider;
    private final Scene scene;

    /**
     * Constructs a new Entity.
     *
     * @param scene    The scene this entity belongs to.
     * @param position The initial position of the entity.
     */
    public Entity(Scene scene, Vector2i position) {
        this.scene = scene;
        this.position = position;
        this.previousPosition = position;
        this.collider = null; // Collider is optional.
        this.scene.requestRender();
    }

    @Override
    public void onStart() {
        // The Default implementation is empty. Can be overridden by subclasses.
    }

    @Override
    public void update(double dt, InputAction ia) {
        // If the entity has a collider and its position has changed since the last frame,
        // update its position in the collision system.
        if (this.collider != null && !this.position.equals(this.previousPosition)) {
            // The entity's own `position` is the new position. The collider's internal
            // position is the old one. We pass the new position to the collision system,
            // which will handle the entire update operation.
            this.scene.collisions().updateColliderPosition(this.collider, this.position);
        }
    }

    @Override
    public abstract void render(Renderer r);

    /**
     * Called by the Scene when a collision is detected.
     * Override this to define custom collision behavior.
     *
     * @param other The other entity involved in the collision.
     */
    public void onCollide(Entity other) {
        // The Default implementation is empty.
    }

    /**
     * Called when another entity with a trigger collider enters this entity's trigger area.
     * Override this to define custom trigger behavior.
     *
     * @param other The other entity involved in the trigger event.
     */
    public void onTriggerEnter(Entity other) {
        // The Default implementation is empty.
    }

    /**
     * Gets the scene this entity belongs to.
     *
     * @return The scene instance.
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Sets the collider for this entity.
     *
     * @param size The size of the collider's bounding box.
     */
    protected void setCollider(Vector2i size) {
        setCollider(size, false); // Default to a dynamic collider.
    }

    /**
     * Sets the collider for this entity, specifying if it is static.
     *
     * @param size     The size of the collider's bounding box.
     * @param isStatic True if the collider should be static (immovable).
     */
    protected void setCollider(Vector2i size, boolean isStatic) {
        setCollider(size, isStatic, false); // Default to a non-trigger collider.
    }

    protected void setCollider(Vector2i size, boolean isStatic, boolean isTrigger) {
        if (this.collider != null) {
            this.scene.collisions().remove(this.collider);
        }
        if (size != null) {
            this.collider = new Collider(this, this.position, size, isStatic, isTrigger);
            this.scene.collisions().add(this.collider);
        } else {
            this.collider = null;
        }
        this.scene.requestRender();
    }

    /**
     * Gets the collider for this entity.
     *
     * @return The collider instance, or null if no collider is set.
     */
    public Collider getCollider() {
        return this.collider;
    }

    /**
     * Gets the current position of this entity.
     *
     * @return The position vector.
     */
    public Vector2i getPosition() {
        return position;
    }

    /**
     * Sets the position of this entity, updating its previous position and requesting a render.
     *
     * @param position The new position.
     */
    public void setPosition(Vector2i position) {
        this.previousPosition = this.position;
        this.position = position;
        this.scene.requestRender();
    }
}
