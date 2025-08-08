# Controls (Demo)

Intro Scene
- N: New Game
- L: Load (placeholder)
- T: Theme selection
- Q: Quit

Theme Selection Scene
- A / D: Previous / Next theme
- Enter: Confirm theme
- B: Back to Intro

Game Scene
- Hold W/A/S/D to move (movement is rate-limited via Config.PLAYER_MOVE_INTERVAL_SEC)
- ':' Enter command mode; then type and press Enter:
  - :Q Quit
  - :B Back to Intro
  - :T Toggle ghost path overlay (global)
- Mouse hover shows the description of the tile under the cursor on the Top Bar

Final Scene
- R: Replay (back to Intro)
- Q: Quit
