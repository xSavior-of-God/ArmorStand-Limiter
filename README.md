# ArmorStand-Limiter

![Java CI with Maven](https://github.com/xSavior-of-God/ArmorStand-Limiter/workflows/Java%20CI%20with%20Maven/badge.svg?branch=main)

allows you to limit armor stands, so you can fix TPS drops caused by a high number of ArmorStands in your server!
Can be used in all modalities and is fully and easily configurable thanks to its intuitive configuration file.

This plugin was created with the express purpose of fixing LAG problems caused by other plugins that generate and do not
remove custom Armor Stands within a single block!
Later we also thought of adding other functions to avoid lagging machines caused by Armor Stands, like this one in the
picture

![Image of lag macchine](https://media.giphy.com/media/tEMxrfDqlsOxr3Ft8N/giphy.gif)

## Feature

* The most important function is that of removing CUSTOM Armor Stands ( created by other plugins ) that exceed the
  preset limit in a single block or chunk!
* Set Armor Stand limit in a Block and/or a Chunk
* Check the world name, armor stand type and its name to avoid unwanted removal
* Timer that checks every X minutes in the loaded chunks the number of Armor Stands inside the chunk or in the single
  block
* Check TPS status and if too low check and remove the Armor Stands in a sigle block or chunk ( this check is also
  performed only on loaded chunks )
* Ability to limit players to placing a maximum of Armor Stands per chunk
* Ability to disable that the Armor Stand can be moved by the pistons or blocks connected to it
* Ability to disable that Armor Stand can move in the water
* Ability to disable that Armor Stand can be spawned through dispensers
* Customized notification ( inGame, Discord, Telegram and Console ) of when Armor Stands are removed due to limit
  violation
* No Dependency!
* Open Source!

| With ArmorStand Limiter                                                | Without ArmorStand Limiter                                                |
|------------------------------------------------------------------------|---------------------------------------------------------------------------|
| ![with 1](https://media.giphy.com/media/KIAEOaU7DQ0zMV3tNG/giphy.gif)  | ![without 1](https://media.giphy.com/media/tEMxrfDqlsOxr3Ft8N/giphy.gif)  |
| ![with 2](https://media.giphy.com/media/cWRZo5KXvXQqSZsYro/giphy.gif)  | ![without 2](https://media.giphy.com/media/8fI4hdymXqI8S5yjKX/giphy.gif)  |
| ![with 3](https://media.giphy.com/media/Gic4t11kRXLkDZnyxL/giphy.gif)  | ![without 3](https://media.giphy.com/media/zLbGnlhX0eZJ9KzrEh/giphy.gif)  |
| ![with 4](https://media1.giphy.com/media/iwZOd2zAndB8WrBWaG/giphy.gif) | ![without 4](https://media1.giphy.com/media/piOLmBeN2Ew9ZwMA1Z/giphy.gif) |

## Commands

* **/asl** - *Simple reload of the config file (Perms: `armostandlimiter.reload`)*
* **/asl check <chunk>** - *Simple command to control how many Armor Stands there are in a chunk or location in the
  world (Perms: `armostandlimiter.check`)*
* **/asl test** - *Simple command to test the notifications (Perms: `armostandlimiter.test`)*

# Support

[![support image](https://www.heroxwar.com/discordLogo.png)](https://discord.gg/5GqJbRw)

**[Link: https://discord.gg/5GqJbRw](https://discord.gg/5GqJbRw)**

# Download

[![Spigotmc](https://static.spigotmc.org/img/spigot.png)](https://www.spigotmc.org/resources/armorstand-limiter.86706/)          [![mc-market](https://pbs.twimg.com/profile_images/1557308606579556352/suzgxeGs_200x200.jpg)](https://builtbybit.com/resources/18303/)
