package Engine;

import Engine.Graphics.Renderer;
import Engine.Input.InputAction;

/**
 * The core of the engine. It manages the main game loop, which is responsible
 * for
 * updating game logic, handling input, and rendering frames.
 * This implementation uses a fixed-timestep for logic updates and event-driven
 * rendering.
 */
public class GameLoop {
    private final SceneManager sceneManager;
    private final Renderer renderer;
    private boolean isRunning = false;
    private final Config config;

    public GameLoop(SceneManager sceneManager, Renderer renderer, Config config) {
        this.sceneManager = sceneManager;
        this.renderer = renderer;
        this.config = config;
    }

    /**
     * Starts and runs the main game loop.
     */
    public void run() {
        renderer.initialize(config);
        this.isRunning = true;

        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1_000_000_000 / amountOfTicks;
        double delta = 0;
        double fixedDt = 1.0 / amountOfTicks;

        InputAction ia = new InputAction();

        while (isRunning) {
            if (!sceneManager.hasScene()) {
                stop();
                break;
            }

            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            ia.updateInputState();

            int catchUps = 0;
            // Process game logic in fixed-step updates to ensure a consistent simulation
            // speed.
            while (delta >= 1) {
                sceneManager.getCurrentScene().update(fixedDt, ia);
                delta--;
                catchUps++;
                if (catchUps >= config.MAX_CATCHUP_STEPS) { // avoid spiral-of-death
                    delta = 0; // drop leftover to keep frame responsive
                    break;
                }
            }

            // Rendering is decoupled and only occurs when the scene requests it.
            if (isRunning && sceneManager.getCurrentScene().isRenderRequested()) {
                renderer.beginFrame();
                sceneManager.getCurrentScene().render(renderer);
                renderer.endFrame();
                sceneManager.getCurrentScene().resetRenderRequest();
            }

            sceneManager.handleTransition();
        }
    }

    /**
     * Stops the game loop.
     */
    public void stop() {
        this.isRunning = false;
    }
}
