package Engine.Scene;

import Engine.Utils.Vector2i;

/**
 * The Camera class defines a view into the game world.
 * It controls which part of the world is currently visible and rendered to the screen.
 * The camera can be set to follow a target, and its view is constrained within the world boundaries.
 */
public class Camera {

    private Vector2i position;
    private final int width;
    private final int height;

    /**
     * Constructs a new Camera with a specified viewport size.
     *
     * @param width  The width of the camera's viewport in tiles.
     * @param height The height of the camera's viewport in tiles.
     */
    public Camera(int width, int height) {
        this.width = width;
        this.height = height;
        this.position = new Vector2i(0, 0);
    }

    /**
     * Updates the camera's position to center on a target, clamping its view within the world boundaries.
     *
     * @param targetPosition The position of the target to follow (e.g., the player's position).
     * @param worldWidth     The total width of the game world in tiles.
     * @param worldHeight    The total height of the game world in tiles.
     */
    public void update(Vector2i targetPosition, int worldWidth, int worldHeight) {
        // Center the camera on the target
        int targetX = targetPosition.x() - width / 2;
        int targetY = targetPosition.y() - height / 2;

        // Clamp the camera's position to the world boundaries
        int clampedX = Math.max(0, Math.min(targetX, worldWidth - width));
        int clampedY = Math.max(0, Math.min(targetY, worldHeight - height));

        this.position = new Vector2i(clampedX, clampedY);
    }

    /**
     * Converts world coordinates to screen coordinates.
     * For tile-based rendering, this translates a world position into a position relative to the camera's view.
     *
     * @param worldPos The position in the world.
     * @return The corresponding position on the screen.
     */
    public Vector2i worldToScreenPoint(Vector2i worldPos) {
        return new Vector2i(worldPos.x() - this.position.x(), worldPos.y() - this.position.y());
    }

    /**
     * Converts screen coordinates to world coordinates.
     *
     * @param screenPos The position on the screen.
     * @return The corresponding position in the world.
     */
    public Vector2i screenToWorldPoint(Vector2i screenPos) {
        return new Vector2i(screenPos.x() + this.position.x(), screenPos.y() + this.position.y());
    }

    /**
     * @return The top-left x-coordinate of the camera's view in world space.
     */
    public int getX() {
        return position.x();
    }

    /**
     * @return The bottom-left y-coordinate of the camera's view in world space.
     */
    public int getY() {
        return position.y();
    }

    /**
     * @return The width of the camera's viewport.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return The height of the camera's viewport.
     */
    public int getHeight() {
        return height;
    }
}
