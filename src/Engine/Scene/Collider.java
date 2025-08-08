package Engine.Scene;

import Engine.Utils.Vector2i;

/**
 * Represents an axis-aligned bounding box (AABB) for an entity, used for collision detection.
 */
public class Collider {
    private final Entity entity;
    private Vector2i position;
    private final Vector2i size;
    private final boolean isStatic;
    private final boolean isTrigger;

    /**
     * Constructs a new Collider.
     *
     * @param entity   The entity this collider is attached to.
     * @param position The world position of the collider.
     * @param size     The width and height of the collider.
     * @param isStatic True if the collider is immovable.
     */
    public Collider(Entity entity, Vector2i position, Vector2i size, boolean isStatic, boolean isTrigger) {
        this.entity = entity;
        this.position = position;
        this.size = size;
        this.isStatic = isStatic;
        this.isTrigger = isTrigger;
    }

    /**
     * Checks for intersection with another collider using AABB logic.
     *
     * @param other The other collider to check against.
     * @return True if the colliders intersect, false otherwise.
     */
    public boolean intersects(Collider other) {
        if (other == null) return false;

        Vector2i thisMin = this.position;
        Vector2i thisMax = this.position.add(this.size);
        Vector2i otherMin = other.position;
        Vector2i otherMax = other.position.add(other.size);

        return thisMin.x() < otherMax.x() && thisMax.x() > otherMin.x() &&
               thisMin.y() < otherMax.y() && thisMax.y() > otherMin.y();
    }

    /**
     * Gets the position of this collider.
     *
     * @return The position vector.
     */
    public Vector2i getPosition() {
        return position;
    }

    /**
     * Sets the position of this collider.
     *
     * @param position The new position.
     */
    public void setPosition(Vector2i position) {
        this.position = position;
    }

    /**
     * Gets the size of this collider.
     *
     * @return The size vector.
     */
    public Vector2i getSize() {
        return size;
    }

    /**
     * Gets the entity this collider belongs to.
     *
     * @return The entity.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Checks if this collider is static.
     *
     * @return True if static, false otherwise.
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * Checks if this collider is a trigger.
     *
     * @return True if a trigger, false otherwise.
     */
    public boolean isTrigger() {
        return isTrigger;
    }
}
