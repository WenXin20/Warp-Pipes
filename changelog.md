## Update 3.3.1
- Improved disconnected pipe message
- Fixed pipe gui background being too dark
- Fixed pipe gui sliders not working with scrolling and click-dragging

Please report any bugs found to [GitHub](https://github.com/WenXin20/Warp-Pipes/issues)

## Update 3.3.0
- Ported changes from Marioverse:
- Added tooltips to warp pipes
- Changed water spouts to face any direction
- Changed water spouts to connect the model to make wider spouts
- Improved bucketing water spouts
- Added a config to require water near a connected pipe for water spouts to place. Default false.
- Removed the `bubbles` & `water_spout` blockstates from warp pipes
  - Saves to the block entity now
  - Reduces states from 192 to 48
- Added a config option to charge xp for teleporting with warp pipes. 0 by default
- Added a config option to charge an item for teleporting with warp pipes. 
  - The item tag `warp_fuel` includes ender pearls by default
- Added a config option to require pipes be connected to the destination pipe to teleport.
  - Disabled by default
  - Pipes connected to the destination pipe must be the same color
  - Pipe Junction blocks can be used to change the color of the pipes connected
- Added a config to disable teleporting using clear pipes
- Added the ability for mobs that can open doors to use warp pipes.
- Added the ability for warp pipes to teleport Aeronautics contraptions
- Added new linking and warping particles
- Added an enchant glow to wrenches when bound to a pipe
- Added smoke particles to pipes on cooldown. Configurable

## Update 3.2.1
- Added Pipe Junctions to pipe world gen
- Fixed a crash when the Warp Disruptor used on a player, and then the player stepped on a pipe
- Fixed wrench sounds

## Update 3.2.0
- Added new pipe textures
- Added built-in resource packs for the old models/textures
- Improved item and mob movement through clear pipes
- Improved Wrench tooltips, sounds, & subtitles
- Added Create Aeronautics support for:
  - Warp Pipe teleporting
  - Right-click to enter clear pipes & proper movement inside
- Added Warp Pipes to Framed Blocks whitelist tag

## Update 3.1.0
- Added Pipe Junction Blocks
  - Pipe textures connect to these
- Added shader support for Water Spouts
- Pipe textures now connect to polished stones and copper blocks
- Players will now face away from a sideways pipe after teleporting
- Vehicles now teleport with the rider
- Fixed pipe towers generating outside of mushroom islands if another mod adds a biome tagged as `is_mushroom`3