package Engine;

import Engine.Graphics.Renderer;
import Engine.Scene.Scene;

/**
 * The main entry point and facade for the game engine.
 * It initializes and coordinates all the core engine components.
 */
public final class Engine {

    private final SceneManager sceneManager;
    private final Renderer renderer = new Renderer();
    private final Config config;
    private final GameLoop gameLoop;

    @Deprecated
    public Engine() {
        this.config = new Config();
        this.sceneManager = new SceneManager(config);
        this.gameLoop = new GameLoop(sceneManager, renderer, config);
    }

    public Engine(Config config) {
        this.config = config;
        this.sceneManager = new SceneManager(config);
        this.gameLoop = new GameLoop(sceneManager, renderer, config);
    }

    /**
     * Registers the initial scene to be loaded when the engine starts.
     *
     * @param initialScene The first scene to run.
     */
    public void register(Scene initialScene) {
        this.sceneManager.push(initialScene);
    }

    /**
     * Starts the engine and begins the main game loop.
     */
    public void run() {
        gameLoop.run();
    }
}
