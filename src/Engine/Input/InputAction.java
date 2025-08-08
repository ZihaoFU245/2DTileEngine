package Engine.Input;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Queue;
import Engine.Utils.Vector2i;

/**
 * Manages user input: queued key types, continuous key holds, mouse, and now
 * engine-level movement rate limiting for directional movement.
 */
public class InputAction {

    private final Queue<Character> typedKeys = new LinkedList<>();
    private long lastMoveNano = 0L; // for movement rate limiting

    /**
     * Capture new key-typed events.
     */
    public void updateInputState() {
        while (StdDraw.hasNextKeyTyped()) {
            typedKeys.add(StdDraw.nextKeyTyped());
        }
    }

    // Mouse helpers
    public int mouseTileX() { return (int) StdDraw.mouseX(); }
    public int mouseTileY() { return (int) StdDraw.mouseY(); }

    // Key typed queue
    public boolean hasNextKeyTyped() { return !typedKeys.isEmpty(); }
    public char getNextKeyTyped() { return typedKeys.poll(); }
    public char peekNextKey() { return typedKeys.peek(); }

    // Continuous key state
    public boolean isWDown() { return StdDraw.isKeyPressed(KeyEvent.VK_W); }
    public boolean isADown() { return StdDraw.isKeyPressed(KeyEvent.VK_A); }
    public boolean isSDown() { return StdDraw.isKeyPressed(KeyEvent.VK_S); }
    public boolean isDDown() { return StdDraw.isKeyPressed(KeyEvent.VK_D); }
    public boolean isUpArrowDown() { return StdDraw.isKeyPressed(KeyEvent.VK_UP); }
    public boolean isLeftArrowDown() { return StdDraw.isKeyPressed(KeyEvent.VK_LEFT); }
    public boolean isDownArrowDown() { return StdDraw.isKeyPressed(KeyEvent.VK_DOWN); }
    public boolean isRightArrowDown() { return StdDraw.isKeyPressed(KeyEvent.VK_RIGHT); }
    public boolean isShiftDown() { return StdDraw.isKeyPressed(KeyEvent.VK_SHIFT); }

    /**
     * Returns a movement vector at most once per interval while directional keys are held.
     * Priority order: W,S,A,D (can be adjusted). Holding a key yields repeated movement
     * spaced by intervalSec. If no key or interval not elapsed, returns Vector2i.ZERO.
     *
     * @param intervalSec minimum seconds between successive movement steps
     */
    public Vector2i pollMovement(double intervalSec) {
        Vector2i dir = Vector2i.ZERO;
        if (isWDown()) dir = Vector2i.UP;
        else if (isSDown()) dir = Vector2i.DOWN;
        else if (isADown()) dir = Vector2i.LEFT;
        else if (isDDown()) dir = Vector2i.RIGHT;
        if (dir.equals(Vector2i.ZERO)) return Vector2i.ZERO;
        long now = System.nanoTime();
        long needed = (long) (intervalSec * 1_000_000_000L);
        if (lastMoveNano == 0L || now - lastMoveNano >= needed) {
            lastMoveNano = now;
            return dir;
        }
        return Vector2i.ZERO;
    }

    /**
     * Resets movement rate limiter (e.g., when pausing/unpausing) so next key yields immediate step.
     */
    public void resetMovementTimer() { lastMoveNano = 0L; }
}
