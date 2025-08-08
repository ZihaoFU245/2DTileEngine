# Getting Started

This repository contains a small 2D tile-based engine and a demo game. The engine uses StdDraw from algs4.jar for rendering.

Requirements
- Java 17+
- The included `lib/algs4.jar`

Build and Run (Windows PowerShell)
- Create output directory: `mkdir out`
- Compile: `javac -cp lib\algs4.jar -d out (Get-ChildItem -Recurse -Filter *.java | %% { $_.FullName })`
- Run the demo: `java -cp "out;lib\algs4.jar" core.Main`

Entry Point
- Demo entry point is `demo/core/Main.java` (package `core`)
- It constructs a `CustomConfig`, creates `Engine`, registers the `IntroScene`, and calls `engine.run()`

Config knobs (see `Engine.Config` / `demo/core/CustomConfig`)
- WIDTH/HEIGHT: World size in tiles
- SCREEN_WIDTH/SCREEN_HEIGHT: Viewport size in tiles
- CELL_SIZE: Spatial-hash grid size for collisions (usually 16)
- PLAYER_MOVE_INTERVAL_SEC: Movement repeat interval when holding WASD
- CAM_DEADZONE_MARGIN: Deadzone around the player before camera shifts
- MAX_CATCHUP_STEPS: Logic catch-up limit per frame
- CustomConfig.theme: current visual theme
- CustomConfig.ghostNum: number of ghost enemies in the demo

