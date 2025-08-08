package Engine.Scene;

import Engine.Config;
import Engine.Graphics.Renderer;
import Engine.Input.InputAction;

import java.util.ArrayList;
import java.util.List;

/**
 * A Scene encapsulates a self-contained part of the game, such as a level, menu, or cutscene.
 * It owns and manages all the layers, entities, and subsystems for that part of the game.
 * The Engine interacts with the current Scene, which directs the flow of logic and rendering.
 */
public abstract class Scene {

    private final List<Layer> layers = new ArrayList<>();
    private Camera camera;
    private CollisionSystem collisionSystem;
    private EventBus eventBus;
    private boolean renderRequested = true; // Initial render is always requested.
    protected Config config;

    /**
     * Called by the SceneManager to provide the engine's configuration to the scene.
     *
     * @param config The engine configuration.
     */
    public void setConfig(Config config) {
        this.config = config;
    }

    public Config getConfig() {
        return this.config;
    }

    /**
     * Called once by the Engine after the Scene is created but before the first frame.
     * Use this to initialize layers, entities, and event subscriptions.
     */
    public void onStart() {
        events().subscribe(CollisionSystem.CollisionEvent.class, this::onCollision);
        events().subscribe(CollisionSystem.TriggerEvent.class, this::onTrigger);
        for (Layer layer : layers) {
            layer.onStart();
        }
    }

    /**
     * Called by the GameLoop at a fixed rate. It drives all game logic and simulation.
     *
     * @param dt The fixed time step, in seconds, for this update.
     * @param ia The current input state.
     */
    public void update(double dt, InputAction ia) {
        for (Layer layer : layers) {
            layer.update(dt, ia);
        }
        if (this.collisionSystem != null) {
            this.collisionSystem.checkCollisions(this.eventBus);
        }
    }

    /**
     * Called by the GameLoop when a render is requested. It draws all visible objects.
     *
     * @param r The Renderer to use for drawing.
     */
    public void render(Renderer r) {
        for (Layer layer : layers) {
            layer.render(r);
        }
    }

    /**
     * Optional: Called when another Scene is pushed on top of this one (e.g., a pause menu).
     */
    public void onPause() {
    }

    /**
     * Optional: Called when this Scene resumes after a higher-level Scene is popped.
     */
    public void onResume() {
    }

    /**
     * Called once when the Scene is about to be destroyed and removed from the game.
     */
    public void onDestroy() {
    }

    /**
     * Polled by the SceneManager to check if this Scene wants to transition to another.
     *
     * @return A SceneTransition object (Push, Pop, Replace) or null for no transition.
     */
    public SceneTransition pollTransition() {
        return null;
    }

    //–- Render-on-demand API –-//

    /**
     * Signals to the GameLoop that a redraw is needed because the visual state has changed.
     */
    public final void requestRender() {
        this.renderRequested = true;
    }

    /**
     * Checked by the GameLoop to determine if a render is necessary.
     *
     * @return True if a render has been requested.
     */
    public final boolean isRenderRequested() {
        return this.renderRequested;
    }

    /**
     * Called by the GameLoop after a frame is rendered to reset the request flag.
     */
    public final void resetRenderRequest() {
        this.renderRequested = false;
    }

    //–- Game-Facing Helpers –-//

    /**
     * Adds a Layer to this Scene. Layers are rendered in the order they are added.
     *
     * @param l The Layer to add.
     * @return The added Layer.
     */
    protected final Layer addLayer(Layer l) {
        this.layers.add(l);
        return l;
    }

    /**
     * Provides access to the scene-local EventBus for decoupled communication.
     *
     * @return The singleton EventBus for this scene.
     */
    protected final EventBus events() {
        if (this.eventBus == null) {
            this.eventBus = new EventBus();
        }
        return this.eventBus;
    }

    /**
     * Provides public access to the scene's Camera.
     *
     * @return The Camera instance for this scene.
     */
    public final Camera getCamera() {
        return camera();
    }

    /**
     * Lazily initializes and returns the Camera for this scene.
     *
     * @return The singleton Camera for this scene.
     */
    protected final Camera camera() {
        if (this.camera == null) {
            this.camera = new Camera(config.SCREEN_WIDTH, config.SCREEN_HEIGHT);
        }
        return this.camera;
    }

    /**
     * Lazily initializes and returns the CollisionSystem for this scene.
     *
     * @return The singleton CollisionSystem for this scene.
     */
    protected final CollisionSystem collisions() {
        if (this.collisionSystem == null) {
            this.collisionSystem = new CollisionSystem(config.CELL_SIZE);
        }
        return this.collisionSystem;
    }

    /**
     * Handles collision events from the EventBus, performs automatic resolution,
     * and dispatches gameplay-level collision events to the entities involved.
     *
     * @param event The collision event.
     */
    private void onCollision(CollisionSystem.CollisionEvent event) {
        Collider colliderA = event.a();
        Collider colliderB = event.b();
        Entity entityA = colliderA.getEntity();
        Entity entityB = colliderB.getEntity();

        // Automatic collision resolution:
        // If a dynamic entity collides with a static one, revert the dynamic entity's position.
        if (!colliderA.isStatic() && colliderB.isStatic()) { // A is dynamic, B is static
            entityA.setPosition(entityA.previousPosition); // Revert game state position
            // CRITICAL: Update the collision grid to reflect the reverted position.
            collisions().updateColliderPosition(colliderA, entityA.position);
        } else if (colliderA.isStatic() && !colliderB.isStatic()) { // A is static, B is dynamic
            entityB.setPosition(entityB.previousPosition); // Revert game state position
            // CRITICAL: Update the collision grid to reflect the reverted position.
            collisions().updateColliderPosition(colliderB, entityB.position);
        }

        // Notify entities for gameplay logic AFTER resolution
        if (entityA != null) {
            entityA.onCollide(entityB);
        }
        if (entityB != null) {
            entityB.onCollide(entityA);
        }
    }

    private void onTrigger(CollisionSystem.TriggerEvent event) {
        Entity entityA = event.a().getEntity();
        Entity entityB = event.b().getEntity();

        if (entityA != null) {
            entityA.onTriggerEnter(entityB);
        }
        if (entityB != null) {
            entityB.onTriggerEnter(entityA);
        }
    }
}