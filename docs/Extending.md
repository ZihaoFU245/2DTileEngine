# Extending the Engine

Create a Scene
- Extend `Engine.Scene.Scene`
- In `onStart()`, create and `addLayer(...)` layers (e.g., a `TileMap`, an entity `Layer`, and a HUD layer)
- Use `requestRender()` whenever the scene's visuals change
- Use `pollTransition()` to return `new SceneTransition.Push(...)`, `new SceneTransition.Pop()`, or `new SceneTransition.Replace(...)`

Create an Entity
- Extend `Engine.Scene.Entity`
- Implement `render(Renderer r)`
- Optionally call `setCollider(size)` or `setCollider(size, isStatic, isTrigger)` in the constructor
- Move by `setPosition(position.add(dir))`; base `update(...)` keeps the collider in sync
- Handle collisions by overriding `onCollide(Entity other)` and triggers via `onTriggerEnter(Entity other)`

Use the EventBus
- Subscribe in `Scene.onStart()`: `events().subscribe(CollisionSystem.CollisionEvent.class, this::onCollision)`
- Publish your own events to coordinate between systems

TileMap with Colliders
- `new TileMap(scene, tiles)` just renders
- `new TileMap(scene, tiles, List.of(Tileset.WALL, ...))` also spawns static colliders per matching tile

Camera
- Follow a target: `camera.update(playerPos, mapWidth, mapHeight)`; then draw using `camera.worldToScreenPoint(entityPos)`

Input
- Read queued text input via `InputAction.hasNextKeyTyped()/getNextKeyTyped()`
- Use continuous keys via `isWDown()/isADown()/...`
- For grid movement, use `pollMovement(intervalSec)` for repeat-limited deltas

Adding a Theme
- Implement `Assets.Components.Theme`
- Provide tiles for wall/floor/avatar/ghost/name
- Update the demo (e.g., `ThemeSelectionScene`) to include the new theme

Configuration
- Use `Engine.Config` for engine knobs, or extend as needed (see `demo/core/CustomConfig`)
