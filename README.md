# Mushroom Forest

A cozy, storybook biome for **Minecraft 26.3** on **Fabric** (Loader 0.19.5, Fabric API 0.161.0+26.3).

- **Mushroom Forest biome**: a forest where the trees are giant mushrooms, in rosy pink, lavender, azure, buttercup yellow and classic red and brown. Three shapes: round toadstools (plus rare towering *ancient* ones), wide parasols on slender stems, and pointy witch-hat bells with curled tips. Gill-lined undersides, glowing shroomlight "lanterns" tucked under the caps, fairy rings in the grass, moss, pink petals, wildflowers, firefly bushes, floating spores, pink-tinted clouds and the Cherry Grove music.
- **As common as Plains**: every Plains region in the Overworld is shared 50/50 with the Mushroom Forest (the more humid half), so you'll find one about as often as you find Plains, usually where Plains meet forests. Works in Default, Large Biomes and Amplified worlds.
- **Single-biome worlds**: Create World → World Type: *Single Biome* → Customize → *Mushroom Forest*.
- **Mushroom Fairy**: a tiny, peaceful flying sprite with a toadstool hat and shimmering wings. Fairies live in the Mushroom Forest, follow you when you hold a mushroom, and leave a trail of fairy dust. **Right-click a fairy with a mushroom while you're hurt and it heals you one heart** (the mushroom is used up). At full health the fairy just giggles and you keep your mushroom.
- English and French names.

## Building the mod (one time setup)

You need a **Java 25 JDK** to build (the Minecraft launcher's built-in Java doesn't count).

1. Install a Java 25 JDK, for example *Eclipse Temurin 25* from https://adoptium.net (tick "Set JAVA_HOME" in the installer on Windows).
2. Unzip this project somewhere, and open a terminal **in the project folder** (on Windows: open the folder, click the address bar, type `cmd`, press Enter).
3. Run:
   - Windows: `gradlew.bat build`
   - macOS / Linux: `./gradlew build`

   The first build downloads Minecraft and Fabric, so it takes a few minutes.
4. The finished mod is `build/libs/mushroomforest-1.0.0.jar`.

To try it straight away without installing anything, run `gradlew.bat runClient` (or `./gradlew runClient`). It opens a test copy of Minecraft with the mod loaded.

## Installing

1. Install Fabric Loader 0.19.5 for Minecraft 26.3 with the Fabric installer (https://fabricmc.net/use/installer/).
2. Put `mushroomforest-1.0.0.jar` **and** Fabric API (0.161.0+26.3 or newer for 26.3) into your `.minecraft/mods` folder.
3. Start the game with the Fabric profile and create a **new world** (existing chunks don't change; only newly explored land can grow mushroom forests).

## Finding things

- `/locate biome mushroomforest:mushroom_forest` points you to the nearest Mushroom Forest.
- The fairy spawn egg and the four new mushroom blocks are in the Creative inventory (Spawn Eggs and Natural Blocks tabs).

## Tweaking it

Almost everything is plain JSON in `src/main/resources/data/mushroomforest/`, so you can change it without touching Java (rebuild afterwards):

| What | Where |
|---|---|
| Biome colours, music, particles, which mobs spawn and how often | `worldgen/biome/mushroom_forest.json` |
| How many giant mushrooms per chunk | `worldgen/placed_feature/giant_mushrooms.json` (the `count`) |
| Mix of shapes and colours | `worldgen/feature/giant_mushrooms.json` and `giant_*s.json` |
| Size, height, lanterns, leaning of each mushroom type | `worldgen/feature/giant_<shape>_<colour>.json` |
| Which items fairies accept | `tags/item/fairy_treats.json` (all vanilla mushrooms by default) |

The 50/50 Plains split is in `src/main/java/com/mushroomforest/mixin/OverworldBiomeBuilderMixin.java`.

## Project layout

- `src/main/java/com/mushroomforest/` - mod setup, the fairy (`entity/`), the giant mushroom and fairy ring generators (`worldgen/`), and the biome placement (`mixin/`).
- `src/client/java/com/mushroomforest/client/` - the fairy's 3D model and renderer.
- `src/main/resources/assets/mushroomforest/` - textures, models, sounds, translations.
