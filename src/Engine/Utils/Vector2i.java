package Engine.Utils;

/**
 * An immutable 2D integer vector for representing grid-based positions and directions.
 */
public record Vector2i(int x, int y) {

    public static final Vector2i ZERO = new Vector2i(0, 0);
    public static final Vector2i ONE = new Vector2i(1, 1);
    public static final Vector2i UP = new Vector2i(0, 1);
    public static final Vector2i DOWN = new Vector2i(0, -1);
    public static final Vector2i LEFT = new Vector2i(-1, 0);
    public static final Vector2i RIGHT = new Vector2i(1, 0);

    /**
     * Adds another vector to this vector.
     *
     * @param other The vector to add.
     * @return A new Vector2i representing the sum.
     */
    public Vector2i add(Vector2i other) {
        return new Vector2i(this.x + other.x, this.y + other.y);
    }

    /**
     * Subtracts another vector from this vector.
     *
     * @param other The vector to subtract.
     * @return A new Vector2i representing the difference.
     */
    public Vector2i subtract(Vector2i other) {
        return new Vector2i(this.x - other.x, this.y - other.y);
    }

    /**
     * Scales this vector by a scalar value.
     *
     * @param scalar The scalar to multiply by.
     * @return A new Vector2i representing the scaled vector.
     */
    public Vector2i scale(int scalar) {
        return new Vector2i(this.x * scalar, this.y * scalar);
    }
}
