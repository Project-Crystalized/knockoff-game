# Crystalized: Knockoff
Recreation of TubNet Knockout

## Gameplay
(For those who don't know) <br>
You are dropped onto a map with generated sections, your goal is to punch other players off and be the last person standing.

## Contributors
TotallyNoCallum - did almost everything lol <br>
Cooltexture - Programming help

## How to use
Knockoff is currently built for 1.21.6 Paper, This plugin may or may not work on newer or older versions, use incompatible versions at your own risk.

Dependencies (You need to install these) <br>
[FAWE (FastAsyncWorldEdit)](https://www.spigotmc.org/resources/fastasyncworldedit.13932/) <br>
[Crystalized Essentials](https://github.com/Project-Crystalized/crystalized-essentials) <br>
[Crystalized Lobby plugin](https://github.com/Project-Crystalized/lobby_plugin) <br>
[ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) <br>

This Plugin also supports Minecraft: Bedrock Edition through Geyser and the Floodgate API <br>
You may need to build the plugin as we don't support GitHub releases, see Building Instructions <br>

Commands: <br>
`/knockoff start` - manually start the game <br>
`/knockoff end` - manually end the game <br>

## Building Instructions
Make sure you've installed JDK 21 from [here](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html). <br>

Configuring Lobby Plugin for Knockoff:
1. Knockoff Depends on the Lobby Plugin (linked above), Get the Lobby Plugin's source and open a terminal/cmd in the folder. <br>
2. type `./gradlew publishToMavenLocal` (Windows) or `gradlew publishToMavenLocal` (Linux) and wait for that to finish.
3. You are ready to start building Knockoff

Building Knockoff:
1. Open a Terminal or Command Prompt in the root directory of the Knockoff repo
2. Type `./gradlew build` (Windows) or `gradlew build` (Linux)
3. Jar file will be in `build/libs/`

Notes:
1. You will need to enable passive mode in the lobby plugin's config
2. You may need a Knockoff compatible map for the plugin to start and work properly.
