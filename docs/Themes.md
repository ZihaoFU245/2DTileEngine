# Themes

Interface: `Assets.Components.Theme`
- Provide tiles for wall(), floor(), avatar(), ghost(), and a display name via name()
- Optional: override `lockedDoor()` and `nothing()` if needed

Built-in themes (demo)
- Sci-Fi
- ARCANE DUNGEON
- NATURE GROVE
- STEAM FACTORY
- DESERT RUINS
- RETRO ARCADE
- CRYSTAL CAVE

Where used
- `demo/core/CustomConfig.theme` stores the active theme
- `ThemeSelectionScene` lets the player preview and choose a theme; it updates `CustomConfig.theme`
- `MapGenerator` pulls tiles from the active theme when producing the world
- Entities such as `Player` and `Ghost` read tiles from the theme for rendering

Add your own
- Implement `Theme` in `Assets.Components`
- Optionally define extra decorative tiles for your maps
- Add your theme to the list in `ThemeSelectionScene`
