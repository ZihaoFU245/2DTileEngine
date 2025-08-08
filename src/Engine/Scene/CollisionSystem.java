package Engine.Scene;

import Engine.Utils.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages collision detection for a scene using a spatial hash grid.
 * This system is optimized for tile-based games by partitioning the world into a grid.
 */
public class CollisionSystem {

    /**
     * Represents a collision event between two colliders.
     */
    public record CollisionEvent(Collider a, Collider b) {
    }

    /**
     * Represents a trigger event between two colliders.
     */
    public record TriggerEvent(Collider a, Collider b) {
    }

    private final Map<Integer, List<Collider>> grid = new HashMap<>();
    private final List<Collider> staticColliders = new ArrayList<>();
    private final List<Collider> dynamicColliders = new ArrayList<>();
    private final int cellSize;

    /**
     * Constructs a new CollisionSystem.
     *
     * @param cellSize The size of each cell in the spatial hash grid.
     *                 This should typically be larger than the average entity size.
     */
    public CollisionSystem(int cellSize) {
        this.cellSize = cellSize;
    }

    /**
     * Adds a collider to the system, classifying it as static or dynamic.
     *
     * @param collider The collider to add.
     */
    public void add(Collider collider) {
        if (collider.isStatic()) {
            staticColliders.add(collider);
        } else {
            dynamicColliders.add(collider);
        }
        addToGrid(collider);
    }

    /**
     * Removes a collider from the system.
     *
     * @param collider The collider to remove.
     */
    public void remove(Collider collider) {
        if (collider.isStatic()) {
            staticColliders.remove(collider);
        } else {
            dynamicColliders.remove(collider);
        }
        removeFromGrid(collider);
    }

    /**
     * Updates the position of a dynamic collider in the spatial hash grid.
     * This method is now responsible for the entire move operation: removing from the old
     * position, updating the collider's internal state, and adding to the new position.
     *
     * @param collider    The collider to move.
     * @param newPosition The new position for the collider.
     */
    public void updateColliderPosition(Collider collider, Vector2i newPosition) {
        if (collider.isStatic()) {
            return; // Static colliders do not move.
        }

        // First, remove the collider from its current position in the grid.
        removeFromGrid(collider);

        // Next, update the collider's internal position state.
        collider.setPosition(newPosition);

        // Finally, add the collider back to the grid at its new position.
        addToGrid(collider);
    }

    /**
     * Detects all collisions between dynamic and other colliders and publishes events.
     *
     * @param eventBus The event bus to publish collision events to.
     */
    public void checkCollisions(EventBus eventBus) {
        Set<CollisionEvent> reportedCollisions = new HashSet<>();
        Set<TriggerEvent> reportedTriggers = new HashSet<>();

        for (Collider dynamicCollider : dynamicColliders) {
            List<Collider> candidates = query(dynamicCollider);
            for (Collider candidate : candidates) {
                if (dynamicCollider != candidate && dynamicCollider.intersects(candidate)) {
                    if (dynamicCollider.isTrigger() || candidate.isTrigger()) {
                        TriggerEvent event = createTriggerEvent(dynamicCollider, candidate);
                        if (!reportedTriggers.contains(event)) {
                            eventBus.publish(event);
                            reportedTriggers.add(event);
                        }
                    } else {
                        CollisionEvent event = createCollisionEvent(dynamicCollider, candidate);
                        if (!reportedCollisions.contains(event)) {
                            eventBus.publish(event);
                            reportedCollisions.add(event);
                        }
                    }
                }
            }
        }
    }

    /**
     * Queries the grid to find potential collision candidates for a given collider.
     *
     * @param collider The collider to query for.
     * @return A list of nearby colliders.
     */
    public List<Collider> query(Collider collider) {
        List<Collider> candidates = new ArrayList<>();
        Set<Integer> visitedCells = new HashSet<>();

        Vector2i start = getCellCoords(collider.getPosition());
        Vector2i end = getCellCoords(collider.getPosition().add(collider.getSize()));

        for (int x = start.x() - 1; x <= end.x() + 1; x++) {
            for (int y = start.y() - 1; y <= end.y() + 1; y++) {
                int hash = hash(x, y);
                if (grid.containsKey(hash) && !visitedCells.contains(hash)) {
                    candidates.addAll(grid.get(hash));
                    visitedCells.add(hash);
                }
            }
        }
        return candidates;
    }

    private void addToGrid(Collider collider) {
        Set<Integer> hashes = getHashesForCollider(collider);
        for (int hash : hashes) {
            grid.computeIfAbsent(hash, k -> new ArrayList<>()).add(collider);
        }
    }

    private void removeFromGrid(Collider collider) {
        Set<Integer> hashes = getHashesForCollider(collider);
        for (int hash : hashes) {
            if (grid.containsKey(hash)) {
                grid.get(hash).remove(collider);
            }
        }
    }

    private Set<Integer> getHashesForCollider(Collider collider) {
        Set<Integer> hashes = new HashSet<>();
        Vector2i start = getCellCoords(collider.getPosition());
        Vector2i end = getCellCoords(collider.getPosition().add(collider.getSize()));

        for (int x = start.x(); x <= end.x(); x++) {
            for (int y = start.y(); y <= end.y(); y++) {
                hashes.add(hash(x, y));
            }
        }
        return hashes;
    }

    private Vector2i getCellCoords(Vector2i position) {
        return new Vector2i(position.x() / cellSize, position.y() / cellSize);
    }

    private int hash(int x, int y) {
        // Cantor pairing function ensures (x, y) maps to a unique integer.
        long A = (x >= 0 ? 2L * x : -2L * x - 1);
        long B = (y >= 0 ? 2L * y : -2L * y - 1);
        long C = (A >= B ? A * A + A + B : A + B * B) / 2;
        return (int) C;
    }

    private CollisionEvent createCollisionEvent(Collider a, Collider b) {
        // Sort by hashcode to avoid duplicate events (A, B) and (B, A).
        return (a.hashCode() < b.hashCode())
                ? new CollisionEvent(a, b)
                : new CollisionEvent(b, a);
    }

    private TriggerEvent createTriggerEvent(Collider a, Collider b) {
        // Sort by hashcode to avoid duplicate events (A, B) and (B, A).
        return (a.hashCode() < b.hashCode())
                ? new TriggerEvent(a, b)
                : new TriggerEvent(b, a);
    }
}
