# mc_multimeter


## Install and use

This mod runs on [Fabric](https://fabricmc.net/), so make sure you have that installed.

Download the [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api/files) if you don't have it already. Put it in your mods folder.

Go to [Releases](https://github.com/antonilol/mc_multimeter/releases) and download the latest release. Also put it in your mods folder.

The `mods` folder can be found in [.minecraft](https://minecraft.fandom.com/wiki/.minecraft#Locating_.minecraft).
If not, create it.

Start Minecraft and open a world or join a server.

TODO more text here

## Compiling

#### Linux and Mac OS

Clone the repo

```bash
git clone https://github.com/antonilol/mc_multimeter.git
```
or download the [zip](https://github.com/antonilol/mc_multimeter/archive/refs/heads/master.zip) and unzip it.

Enter the folder (`cd` or double click).

If you downloaded the zip make `gradlew` executable.

```bash
chmod +x gradlew
```

And finally, compile.

```bash
./gradlew build
```

#### Windows

Clone or download like mentioned above and build with

```bash
gradlew.bat build
```

## Developing

Clone the repo.

To get completions in your IDE (if applicable) run `./gradlew genSources` (unix) or `gradlew.bat genSources` (windows).

More on that [here](https://fabricmc.net/wiki/tutorial:setup#generating_sources).

## License

MIT
