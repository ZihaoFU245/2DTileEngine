# 2DTileEngine (Java)

A lightweight 2D, tile-based engine built on top of StdDraw (algs4). It features a scene stack, fixed-timestep game loop, camera/view-port rendering, spatial-hash collisions, input handling, and a demo game with themes, pathfinding enemies, and a heads-up command bar.

- Language: Java 23+
- Rendering: StdDraw via algs4.jar
- Entry point (demo): `core.Main`

## Features
- Fixed-timestep update loop with render-on-demand
- Scene stack with Push/Pop/Replace transitions
- Camera viewport and tile-buffered rendering
- TileMap layer with optional static colliders
- Spatial hash CollisionSystem (colliders + triggers)
- Entities with lifecycle hooks and optional colliders
- EventBus for decoupled collision/trigger events
- Input handling: queued key-typed + continuous keys + rate-limited movement
- HUD/TopBar command mode (":Q", ":B", ":T")
- Theme system and Theme Selection scene
- Example procedural dungeon generator with rooms/corridors

## Quick Start
Requirements:
- Java 17+
- algs4.jar is included under `lib/algs4.jar`

Compile and run (Windows PowerShell example):

1) From the repo root, compile all sources into `out` with algs4 on the classpath.
2) Run the demo entrypoint.

Example commands:
- Create output dir: `mkdir out`
- Compile: `javac -cp lib\algs4.jar -d out (Get-ChildItem -Recurse -Filter *.java | %% { $_.FullName })`
- Run: `java -cp "out;lib\algs4.jar" core.Main`

If you use another shell, adapt the classpath separator (`;` on Windows, `:` on macOS/Linux).

## Project Layout
- `src/Engine` – Core engine (loop, renderer, scene stack, camera, collisions, utils)
- `demo/` – Playable example game (scenes, entities, map gen, themes, entrypoint)
- `lib/` – External deps (algs4.jar for StdDraw)
- `docs/` – Documentation

## Core Concepts (where to look)
- Engine: `Engine.Engine`, `Engine.GameLoop`, `Engine.Config`
- Rendering: `Engine.Graphics.Renderer`, `Engine.Graphics.tileengine.*`
- Scenes/Layers: `Engine.Scene.Scene`, `Engine.Scene.SceneManager`, `Engine.Scene.Layer`, `Engine.Scene.SceneTransition`
- Entities/Colliders: `Engine.Scene.Entity`, `Engine.Scene.Collider`, `Engine.Scene.CollisionSystem`
- Camera: `Engine.Scene.Camera`
- Input: `Engine.Input.InputAction`
- Events: `Engine.Scene.EventBus`
- TileMap: `Engine.Scene.TileMap`
- Text: `Engine.Utils.TextUtils`, `Engine.Utils.Font`
- Demo entrypoint: `demo/core/Main.java`
- Demo content: `demo/Assets/*` (Scenes, Entities, Map, Components)

## Controls (demo)
- Main menu: N = New, L = Load (list + W/S navigate, Enter load, B back), T = Theme selection, Q = Quit
- Theme selection: A/D = Prev/Next, Enter = Confirm, B = Back
- In-game: Hold WASD to move (rate-limited). `:` enters command mode; then:
  - `:S` Save game, `:Q` Quit, `:B` Back to intro, `:T` Toggle ghost path overlay
- Final scene: R = Replay, Q = Quit
- Mouse hover shows tile description in the top bar

## Extending
- New Scene: extend `Engine.Scene.Scene`, add `Layer`s in `onStart`, override `update/render`, and return `SceneTransition` from `pollTransition` when needed.
- New Entity: extend `Engine.Scene.Entity`, implement `render`, optionally call `setCollider(size, isStatic, isTrigger)`.
- TileMap collisions: pass a list of solid tiles to `new TileMap(scene, tiles, solidTiles)`.
- Events: subscribe to `CollisionSystem.CollisionEvent` and `TriggerEvent` via `events().subscribe(...)`.
- Themes: implement `Assets.Components.Theme` and plug into `CustomConfig.theme`.

## Documentation
- docs/GettingStarted.md
- docs/Architecture.md
- docs/Controls.md
- docs/Extending.md
- docs/MapGeneration.md
- docs/Themes.md
 - Save/Load: Saves are written to `./saves/<timestamp>.json`. Loading is available from the Intro screen via `L`.

## Demo Game Play
<img width="2403" height="1401" alt="image" src="https://github.com/user-attachments/assets/72acfe77-01e3-46a5-90a5-c9b98de55026" />
Theme Selection !!!
<img width="2403" height="1401" alt="image" src="https://github.com/user-attachments/assets/69c95cfd-e8f3-4409-81ea-bc63690de2ca" />
Game Map
<img width="2403" height="1401" alt="image" src="https://github.com/user-attachments/assets/fb92ca98-9e82-444c-9edf-37c03bc0b668" />
About to Win
<img width="2403" height="1401" alt="image" src="https://github.com/user-attachments/assets/2e97c02e-760a-429d-938e-8e6d9f12848e" />
Main Page
<img width="2403" height="1401" alt="image" src="https://github.com/user-attachments/assets/cce26d4b-14fc-46a2-83cb-5846ee872594" />
Load / Save Game



