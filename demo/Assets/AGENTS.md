For this game, everything under /Assets/*
needs to follow

## Feature: Theme Selection Screen

### 1. User Request Summary

The goal is to implement a theme selection screen that appears when the user selects "(S) Select Theme" from the main menu.

*   **Core UI:** The screen should present a list of available themes as selectable "boxes" or areas.
*   **Interaction:**
    *   The player should be able to navigate between the theme choices using keyboard input (e.g., W/A/S/D or arrow keys).
    *   The currently selected theme should be clearly highlighted.
    *   Pressing a confirmation key (e.g., 'E') should select the theme, update the game's configuration, and return to the main menu.
    *   Pressing a back key (e.g., 'B') should return to the main menu without changing the theme.
*   **Technical Approach (User Feedback):**
    *   The selection screen should be a new, dedicated `Scene` (`ThemeSelectionScene`) that is pushed onto the scene stack.
    *   This scene should feature a simple, interactive map where each theme is represented.
    *   A movable `Entity` should act as the player's cursor or selector.
    *   The `Camera` should follow this selector entity to create a smooth navigation experience.
    *   When selection is complete (or cancelled), the scene should be popped from the stack, returning control to the `IntroScene`.

### 2. Implementation Plan

To meet these requirements, we will follow the engine's established architecture.

1.  **Create a `ThemeSelector` Entity:** A new, simple entity (`src/Assets/Entities/ThemeSelector.java`) will be created to act as the movable cursor on the selection map.
2.  **Create `ThemeSelectionScene`:** A new scene (`src/Assets/Scenes/ThemeSelectionScene.java`) will be built.
    *   It will use `Layers` to manage a background `TileMap` and a foreground for the `ThemeSelector` entity.
    *   It will construct a small "gallery" map where each theme has a representative tile on a pedestal.
    *   It will handle player input (A/D) to move the `ThemeSelector` between the theme pedestals.
    *   The scene's `update` loop will correctly call `camera().update(selector.getPosition(), ...)` to ensure the camera follows the selector.
    *   UI text for instructions and the name of the highlighted theme will be rendered on screen.
3.  **Refactor `IntroScene`:** The `IntroScene` will be modified to push the `ThemeSelectionScene` when the user presses 'S'.
4.  **Update `Theme` Components:** The `Theme` interface and `CustomTiles` class will be updated with helper methods (`getRepresentativeTile()`, `getAvailableThemes()`) to support the gallery UI.

### 3. Implementation Log

*   **[TODO]** Add `getThemeName()` and `getRepresentativeTile()` to the `Theme` interface.
*   **[TODO]** Implement the new methods in all themes within `CustomTiles.java` and add `getAvailableThemes()`.
*   **[TODO]** Create the `ThemeSelector.java` entity file.
*   **[TODO]** Create the `ThemeSelectionScene.java` file.
*   **[TODO]** Implement the map, entity, camera, and UI logic within `ThemeSelectionScene`.
*   **[TODO]** Modify `IntroScene.java` to push `ThemeSelectionScene` on user input.
*   **[TODO]** Verify the complete workflow.

## Feature: Bug Fix - Player/Door Collision

### 1. User Request Summary

The user reported a critical bug where the player's collision with the `Door` entity was not being detected. The `onCollide` method in the `Player` class was never triggered for the door, preventing the game's win condition from being met.

### 2. Investigation and Root Cause Analysis

Initial investigation focused on the `CollisionSystem` and the colliders on the `Player` and `Door` entities. Several attempts were made to fix the issue by modifying the collision logic, including introducing a new `TriggerEvent` system. However, these changes did not resolve the problem.

The breakthrough came from adding debug logs to the `CollisionSystem`. The logs revealed the true nature of the problem: the `Player` was not colliding with the `Door` entity at all. Instead, it was colliding with a `StaticTile` from the `TileMap` layer at the door's location.

The root cause was identified in `src/Assets/Map/MapGenerator.java`. The `LOCKED_DOOR` tile was being incorrectly added to the `solidTiles` list. This caused the `TileMap` to treat the door's position as a solid wall, effectively blocking the `Player` from ever reaching the `Door` entity's collider.

### 3. Implementation of the Fix

To resolve this bug, a series of targeted changes were made to both the map generation logic and the collision system to properly support triggers.

1.  **`src/Assets/Map/MapGenerator.java`:**
    *   The line `solidTiles.add(LOCKED_DOOR);` was removed. This was the most critical change, ensuring the door tile is no longer a solid, impassable barrier on the `TileMap`.

2.  **`src/Engine/Scene/Collider.java`:**
    *   A new boolean field, `isTrigger`, was added to the `Collider` class.
    *   The constructor was updated to accept the `isTrigger` flag.
    *   A public getter, `isTrigger()`, was added.

3.  **`src/Engine/Scene/Entity.java`:**
    *   A new method, `onTriggerEnter(Entity other)`, was added to the `Entity` class to handle trigger-specific logic, separating it from solid collision handling (`onCollide`).
    *   The `setCollider` methods were overloaded to accept the `isTrigger` flag, ensuring it is correctly passed to the `Collider` constructor.

4.  **`src/Engine/Scene/CollisionSystem.java`:**
    *   To create a clear distinction between collision types, a new `TriggerEvent` record was created, separate from the existing `CollisionEvent`.
    *   The `checkCollisions` method was updated to check if either collider in an intersection is a trigger. If so, it now publishes a `TriggerEvent` to the event bus instead of a `CollisionEvent`.

5.  **`src/Engine/Scene/Scene.java`:**
    *   The `Scene` class was updated to subscribe to the new `TriggerEvent`.
    *   A new handler method, `onTrigger(TriggerEvent event)`, was implemented to process these events and call the `onTriggerEnter` method on the involved entities.

6.  **`src/Assets/Entities/Player.java` & `Door.java`:**
    *   The win condition logic in `Player.java` was moved from `onCollide` to the new `onTriggerEnter` method.
    *   The `setCollider` call in `Door.java` was updated to pass `true` for the `isTrigger` flag, officially designating it as a trigger.

### 4. Outcome

With these changes, the `Player` can now move onto the `Door`'s tile. The `CollisionSystem` correctly identifies this as a trigger event, and the `Scene` dispatches it to the `Player`'s `onTriggerEnter` method, successfully triggering the win condition and printing "WIN" to the console.

## Feature: Tile Hover Name Display

Goal: When the player moves the mouse over tiles (or after a key press, if you choose the gated variant), the name/description of the tile under the cursor is displayed in the HUD (re-using the existing `TopBar`). Requirement check: Hovering over at least three distinct tiles must yield three distinct displayed names.

---
## 1. UX / Behaviour

Two acceptable interaction modes (choose one):

1. Live Hover (recommended, already implemented in sample below):
   - Moving the mouse updates the displayed tile description instantly (render-on-demand).
2. Key-Gated Hover (fallback):
   - Player presses a key (e.g. `H`) to sample the current mouse position and update the text (satisfies: "OK if a key press is needed").

Display location: Left side of the existing top HUD bar (`TopBar`), while command input (centered) continues to function unchanged.

Text source: `TETile.description()` (already stored with every tile). This guarantees distinct descriptions for different tile types (e.g. "crystal wall", "crystal floor", "spelunker", etc.).

---
## 2. Minimal File Changes

| File | Change | Purpose |
|------|--------|---------|
| `Assets/Components/TopBar.java` | Add `hoverText` field + `setHoverText(String)` + render left side | Render tile name in HUD |
| `Assets/Scenes/GameScene.java` | Keep a reference to the `TileMap`; in `update()` compute mouse tile coords and fetch description; call `topBar.setHoverText(...)` when it changes | Drive hover updates |
| `Engine/Input/InputAction.java` (Optional) | Add helper methods `mouseTileX()`, `mouseTileY()` that wrap `StdDraw.mouseX()/mouseY()` | Encapsulate raw input (cleaner engine API) |
| (No change) `TileMap` | Already exposes `getTile(x,y)` | Used for lookup |
| (No change) `TETile` | Already holds `description` | Source string |

---
## 3. Data Flow

```
StdDraw mouse (double) -> (int) tile coords -> bounds check vs map -> TETile -> description String -> TopBar.setHoverText -> Scene.requestRender -> Renderer -> HUD shows text
```

Change detection: Cache last description and only request a render if it differs (avoids unnecessary redraws / flicker).

---
## 4. Edge Cases & Rules

1. Mouse outside world bounds: Ignore (keep last valid hover text or blank).
2. Null tile safeguard: Skip if `getTile` returns null (should not in valid map).
3. Long descriptions: Truncate to available width segment (e.g., left 1/3 of bar) and append `...`.
4. Paused / Command Mode: Still update hover text (or optionally freeze — current implementation keeps it updating for user feedback consistency).
5. Performance: O(1) per frame; only triggers render when text changes.

---
## 5. Example Pseudocode (No Full Code Here)

### TopBar additions
```
class TopBar {
  String hoverText = "";
  void setHoverText(String t) { hoverText = t == null ? "" : t; currScene.requestRender(); }
  render():
     clear row
     draw hoverText at x=1
     draw existing centered command text
}
```

### GameScene hover update
```
private String lastHoverDesc = "";
private TileMap mapLayer;

private void updateHoverText(InputAction ia) {
  int mx = ia.mouseTileX(); // or (int) StdDraw.mouseX()
  int my = ia.mouseTileY();
  if (inside map bounds) {
     TETile t = mapLayer.getTile(mx,my);
     String desc = t.description();
     if (!desc.equals(lastHoverDesc)) { lastHoverDesc = desc; topBar.setHoverText(desc); }
  }
}
```

Call `updateHoverText(ia)` at end of `update()` (and also while paused if you want continuous feedback).

### Optional Key-Gated Variant
Add in `Player.update()` or `GameScene.update()`:
```
if (ia.hasNextKeyTyped() && ia.peekNextKey() == 'h') updateHoverText(ia);
```
Remove the per-frame call for continuous updates.

---
## 6. Testing Checklist

| Test | Steps | Expected |
|------|-------|----------|
| Three distinct tiles | Move mouse over wall, then floor, then avatar | Top bar shows three distinct strings (e.g., "crystal wall", then "crystal floor", then "spelunker") |
| Bounds safety | Move mouse outside world | Hover text remains last valid or blank, no crash |
| Pause / command mode | Press ':' enter command mode and move mouse | (If live mode) text still updates; no command buffer corruption |
| Performance | Wiggle mouse rapidly | No noticeable lag; renders only when description changes |

---
## 7. Possible Enhancements (Future)

1. Color-code hover text by tile category (wall vs floor vs entity).
2. Show coordinates alongside description (e.g., `crystal wall (34,12)`).
3. Tooltip window instead of HUD bar injection.
4. Debounce updates to ~30Hz if ever needed for performance on large maps.

---
## 8. Summary Implementation Diff (Conceptual)

(Do not copy blindly; illustrative only.)
```
// TopBar.java
+ private String hoverText = "";
+ public void setHoverText(String t) { hoverText = (t==null?"":t); currScene.requestRender(); }
~ render(): draw hoverText at left before centered commands

// GameScene.java
+ private TileMap mapLayer;
+ private String lastHoverDesc = "";
~ onStart(): store mapLayer reference
+ updateHoverText(ia): logic above
~ update(): call updateHoverText(ia) each frame

// InputAction.java (optional)
+ int mouseTileX() { return (int) StdDraw.mouseX(); }
+ int mouseTileY() { return (int) StdDraw.mouseY(); }
```

---
## 9. Current Status

The described implementation has been integrated (live hover mode) with:
- Cached description comparison
- HUD rendering on change
- Non-intrusive to existing command input.

If you prefer the key-gated approach, remove the unconditional `updateHoverText` calls and gate them behind a chosen key.

---
## 10. Rollback / Isolation Plan

All changes are confined to three classes. To disable the feature quickly:
1. Remove calls to `updateHoverText` in `GameScene`.
2. Remove `hoverText` logic from `TopBar` (or keep dormant; harmless).
3. (If added) Remove mouse helpers from `InputAction`.

---
Happy building – this satisfies the requirement: Inspecting three different tiles yields three distinct names displayed in the HUD.
