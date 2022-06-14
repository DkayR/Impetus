# Impetus
## Requirements
Impetus requires MariaDB to function currently.

Tested on Spigot 1.8.x.
## Commands
### /prac (username)
Creates a checkpoint in the world and gives the player a "practice tool" that allows them to return to it. Players can also copy another player's current practice checkpoint regardless of them being online or offline.
- **Right Click**: Returns to the current practice checkpoint.
- **Drop**: Creates a new practice checkpoint. (subject to removal in future build)

### /unprac
Removes the current practice checkpoint and associated data.

### /pktool
Gives the player and item that allows them to create checkpoint plates. On right click of a valid activator block (currently pressure plates) gives the player a persistent checkpoint setter. Once the checkpoint setter has been used, the pressure plate will then give any player that steps on the plate the associated location.

### /timer
Default usage of the command displays the usage prompt
#### /timer toggle
Hides the current practice timer whilst still recording data such as time elapsed and attempts.

#### /timer reset
Resets a timer's associated time elapsed and attempts to their default values.

#### /timer pause
Pauses or unpauses both the time elapsed and attempts function of the timer depending on the current state.