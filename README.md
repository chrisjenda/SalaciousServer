# SalaciousServer an Enhanced Zomboid Server


## Salacious Server Alpha Release:
This package is used on our current private server to add Crazy new features.

**Current Features:**  
1. Built-in web server, so you can administer your server from a web GUI!
2. completely new Built-in Discord Bot that replaces the old stale vanilla bot with features that only external Python bots can dream of.
3. Custom Rcon Commands

And much more to come!  The possibilities are endless. Stay tuned and expect frequent updates!

### ( A Modification of Original PZHook by Olipro )
This is a tool for both end-users and developers for creating Java-based mods. The library makes use
of [Interceptify](https://github.com/Olipro/Interceptify) to achieve the runtime hooking.  

**Thanks to Olipro for showing me how to get started with his awesome PZHook and Interceptify Frameworks!**  
The basic premise of it is that you write your code, zip it up into a JAR file and throw it in a `java` folder with the
rest of your mod which can then be uploaded to the Steam Workshop (or run locally from your local `Zomboid/Workshop`
folder in your user directory)

## End-Users

### Manual installation
**Note: the default configuration puts the `Zomboid Data/Configuration` folder inside the server folder**
If you would like to change the location, make sure to update the `-Duser.home=` var in the `PZHook_server` file

1. Follow the Zomboid Guide on Setting up the Default Discord Server (IE: Get a token and setup the varables in the server ini file)
2. Unzip the `PZHook_server.tar.gz` archive from Releases to the server directory (where start-server.sh is located)
3. Modify the `PZHook_server` file to change user.home(Zomboid folder location) servername and steam values:  
 `-Duser.home=${PZ_HOME}` `-Dzomboid.steam=1`
5. Unzip the `ZomboidMod.tar.gz` archive from Releases to the `Zomboid\mods` Directory (Where all your server mods are located)
6. Start the server with `PZHook_server` instead of `start-server.sh`
7. Once the server is started check the logs for a Discord Invite Link and invite the bot to the discord
8. Use !commands in discord to get a list of commands

### Docker Server Install Extra Steps
Follow the steps above but in your entry point you will need to navigate to the installed zomboid folder path.
IE. for Renegade's Zomboid Docker Server, you will need to insert the lines below before you call the `PZHook_server` script  
`PZ_HOME=$(readlink -f "$(pwd)")`  
`cd "${PZ_HOME}/GamefolderLocation" || exit 1`

You will also need to change the script that the entrypoint calls from `start_server.sh` to `PZHook_server`
for Renegade's Docker server, you will need to disable the server validation aswell.


### Adding new Java Mods
**Note: Java mods will contain a `java` folder next to the `media` folder**
1. Extract Mod Folder to the defualt mod location (IE home/user/Zomboid/mods/)
2. Add Modname (Name=) in the `mod.info` file to the `PZHook_EnabledMods.cfg`

The format for the `PZHook_EnabledMods.cfg` should look like the following:

 > Modname1  
 Modname2  
 etc...

## Developers
1. Clone the Repository
2. Change Project Zomboid Path `PZGameFolder` in PZHook_Server/gradle.build to your Zomboid Game Path.
3. Build PZHook_Server
4. Change Project Zomboid Path `PZGameFolder` in `SalaciousServer/gradle.build`
5. Change Project Zomboid Cache Path `PZGameCacheFolder` in `SalaciousServer/gradle.build`
6. Change gradle.properties to edit mod.info and update Java mod version
7. Build SalaciousServer
8. Make a file named `PZHook_EnabledMods.cfg` in the Project Zomboid Root Directory **(Where PZEXE is Located)**
9. Add the `id` property from the mod.info (IE SalaciousServer)
10. Edit File `PZHook_server` in the Project Zomboid Root Directory to change VM ARGS
11. Start Server with `sh PZHook_server` (Commands like -servername, -ip, etc can be passed as normal)

Refer to Original Author Olipro's Page [PZHook](https://github.com/Olipro/PZHook) for more information.
