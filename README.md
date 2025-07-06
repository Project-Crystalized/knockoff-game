# Crystalized: Knockoff
Recreation of TubNet Knockout

## Gameplay
(For those who don't know) <br>
You are dropped onto a map with generated sections, your goal is to punch other players off and be the last person standing.

## Contributors
TotallyNoCallum - did almost everything lol <br>
Cooltexture - Programming help

## How to use
Knockoff is currently built for 1.21.6 Paper, This plugin won't work on newer or older versions.

Dependencies (You need to install these) <br>
[FAWE (FastAsyncWorldEdit)](https://www.spigotmc.org/resources/fastasyncworldedit.13932/) <br>
[Crystalized Essentials](https://github.com/Project-Crystalized/crystalized-essentials) <br>
[ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) <br>

This Plugin also supports Minecraft: Bedrock Edition through Geyser and the Floodgate API <br>
You may need to build the plugin as we don't support GitHub releases, see Building Instructions <br>

Commands: <br>
`/knockoff start` - manually start the game <br>
`/knockoff end` - manually end the game <br>

## Building Instructions
1. Make sure you've installed JDK 21 from [here](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html).
2. `git clone` this repo and open a Terminal or Command Prompt in the root directory
3. Type `./gradlew build` (Windows) or `gradlew build` (Linux)
4. Jar file will be in `build/libs/`
