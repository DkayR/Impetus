# Impetus
WIP
## Requirements
Impetus requires MariaDB to function currently

Tested on Spigot 1.8.x
## Features
### Commands
#### /prac (username) (all)
Creates a checkpoint in the world and gives the player a "practice tool" that allows them to return to it. Players may have multiple checkpoints at once and are able to cycle through them. Also can optionally copy another player's current practice checkpoint or copy all of a player's practice checkpoints.
- **Right Click**: Returns to the current practice checkpoint
- **Left Click**: Cycles to the next practice checkpoint
- **Drop**: Creates a new practice checkpoint (quick practice)

#### /unprac (all)
Removes the current practice checkpoints or optionally all practice checkpoints.

#### /pktool
Gives the player and item that allows them to create checkpoint plates. Gives a checkpoint setter if you right click a pressure plate with the pktool. After using the checkpoint setter the pressure plate will then give players the checkpoint associated with the location that the setter was used.
