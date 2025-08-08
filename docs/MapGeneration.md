# Map Generation (Demo)

Class: `demo/Assets/Map/MapGenerator.java`

Overview
- Generates a simple dungeon of rooms connected by corridors
- Adjustable constants for room sizes and attempts
- Ensures outer border walls and grows walls around floors
- Places a locked door in the final room's center (used as an exit)

Pipeline
1) Normalize world size to be odd (keeps 1-tile border)
2) Scatter non-overlapping rectangular rooms; optionally carve L-cutouts
3) Connect rooms sequentially with corridors (randomized horizontal/vertical order and 1–2 tiles width)
4) Grow walls around floors; add outer border walls
5) Place the exit door; record floor positions and solid tiles

Data provided to other systems
- `getSolidTiles()`: list of tiles that should be static colliders (e.g., walls)
- `getFloorPositions()`: all walkable tile coordinates (used to place the player and ghosts)
- `getExitPosition()`: the door/exit position

Theme integration
- Uses `CustomConfig.theme` to pick `WALL`, `FLOOR`, `NOTHING`, and `LOCKED_DOOR` tiles

Notes
- The generator is deterministic for a given seed
- If too few rooms are placed, it retries with `seed + 1`
