# Architecture

High level
- Engine facade `Engine.Engine` wires together the `SceneManager`, `Renderer`, and `GameLoop`.
- `GameLoop` runs a fixed-timestep update (60 Hz) and render-on-demand frames.
- `SceneManager` maintains a stack of `Scene` objects and handles `SceneTransition`s (Push/Pop/Replace).
- `Renderer` wraps `TERenderer` and buffers tiles in a `TETile[][]` frame buffer.
- `Camera` defines the viewport and is used by `TileMap` and Entities to convert world to screen.
- `CollisionSystem` uses a spatial hash grid to query nearby colliders and emits Collision/Trigger events.
- `EventBus` enables decoupled event handling within a scene.

Rendering
- `Engine.Graphics.tileengine.TERenderer` uses StdDraw (from algs4).
- `Renderer` owns a back buffer `TETile[][]` and exposes `drawTile(x,y,tile)`.
- Scenes call `requestRender()` whenever visuals change so the loop knows to present a frame.

Scenes and Layers
- `Scene` owns a list of `Layer`s. Override `onStart`, `update`, `render`, `pollTransition`.
- `Layer` manages `MonoBehaviour` objects. In the demo: TileMap layer, entity layer, HUD layer.

Entities and Colliders
- `Entity` tracks `position` and optional `Collider` (AABB). The default `update` syncs collider to position.
- `Collider` has flags: `isStatic` (immovable) and `isTrigger` (non-blocking, event-only).
- `CollisionSystem` reports `CollisionEvent` and `TriggerEvent` via `EventBus`.
- `Scene` auto-resolves dynamic-vs-static collisions by reverting the dynamic to its previous position and syncing the grid.

Input
- `InputAction` buffers key-typed events and exposes continuous key state (WASD, arrows, Shift, mouse position).
- `pollMovement(intervalSec)` provides rate-limited movement vectors while a key is held.

TileMap
- `TileMap` renders a `TETile[][]` using the scene camera and can create static colliders for given solid tiles.
- Out-of-bounds map queries return `Tileset.VOID` (different from `Tileset.NOTHING`).

Demo add-ons
- Pathfinding enemy `Assets.Entities.Ghost` uses A* with a small recompute interval and greedy fallback.
- HUD `TopBar` supports a command mode (":Q", ":B", ":T").
- `ThemeSelectionScene` previews multiple `Theme`s and writes back to `CustomConfig.theme`.

Key Classes
- Engine: `Engine`, `GameLoop`, `Config`
- Graphics: `Renderer`, `TERenderer`, `TETile`, `Tileset`
- Scene System: `Scene`, `SceneManager`, `SceneTransition`, `Layer`, `Camera`
- Collision: `Collider`, `CollisionSystem`, `EventBus`
- Utils: `Vector2i`, `TextUtils`, `Font`
- Demo: `IntroScene`, `GameScene`, `FinalScene`, `ThemeSelectionScene`, `MapGenerator`, `Player`, `Ghost`, `Door`, `TopBar`
