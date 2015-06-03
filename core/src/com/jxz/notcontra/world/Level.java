package com.jxz.notcontra.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.jxz.notcontra.entity.Alien;
import com.jxz.notcontra.entity.EntityFactory;
import com.jxz.notcontra.entity.Monster;
import com.jxz.notcontra.entity.Slime;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.handlers.GameStateManager;

/**
 * Created by Samuel on 04/04/2015.
 * Level
 */
public class Level {

    // Background
    public Texture[] layers = new Texture[3];
    private AssetHandler assetHandler = AssetHandler.getInstance();

    // Class constants
    public static final String STATIC_LAYER = "Static Map";     // Name of static collision map layer
    public static final String DYNAMIC_LAYER = "Dynamic Map";   // Name of dynamic collision layers (platforms and slopes)
    public static final String TRIGGER_LAYER = "Trigger Tile";  // Name of trigger layer (ladders/ropes)
    public static final String SPAWN_LAYER = "Spawn";            // Name of spawn point poly-line layer

    public static final float MAX_SECONDS_TO_SPAWN = 5.0f;
    public static final float MIN_SECONDS_TO_SPAWN = 2.0f;

    // Class Variables
    private static Array<Level> loadedMaps = new Array<Level>();    // Static list of Levels to avoid duplicates
    private SpawnPointList spawnPointList;
    private TiledMap map;
    private int height, width;
    private float gravity = 0.25f / Game.UNIT_SCALE;
    private boolean firstLoad;
    private Game game;

    // Survival Variables
    private int monsterCount;
    private int currentWave;
    private int subWavesRemaining;
    private int monstersPerWave;
    private float spawnTimer;
    private float[] spawnPercentage;

    protected Level(TiledMap map) {
        this.map = map;
        height = map.getProperties().get("height", int.class);
        width = map.getProperties().get("width", int.class);
        game = GameStateManager.getInstance().getGame();

        subWavesRemaining = 3;
        spawnTimer = 5;
        monstersPerWave = 5;
        spawnPercentage = new float[3];

        // Load parallax backgrounds from map file
        layers[0] = (Texture) assetHandler.getByName(map.getProperties().get("parallaxBackground", String.class));
        layers[1] = (Texture) assetHandler.getByName(map.getProperties().get("parallaxMidground", String.class));
        layers[2] = (Texture) assetHandler.getByName(map.getProperties().get("parallaxForeground", String.class));
        layers[2].setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        // Load array of polyline spawn points
        spawnPointList = new SpawnPointList();
        for (MapObject object : map.getLayers().get(SPAWN_LAYER).getObjects()) {
            if (object instanceof PolylineMapObject) {
                spawnPointList.addLine((PolylineMapObject) object);
            }
        }

        loadedMaps.add(this);
        firstLoad = true;
    }

    /**
     * Returns a level instance, given a TiledMap. Prevents duplicate levels from existing.
     *
     * @param map TiledMap for the level.
     * @return A reference to the Level object corresponding to the map.
     */
    public static Level getLevel(TiledMap map) {
        for (Level l : loadedMaps) {
            if (l.getMap().equals(map)) {
                l.firstLoad = false;
                return l;
            }
        }
        return new Level(map);
    }

    /**
     * Update method called to handle respawns
     */
    public void update() {
        // Updates spawn timers and behaviour based on game mode
        switch (game.getPlayMode()) {
            case STANDARD:
                // Standard game mode: Respawns don't occur, unless specifically triggered.
                break;
            case SURVIVAL:
                // Survival game mode: Waves respawn occur after each wave is dead
                // Sub waves spawn every 5 seconds, decreasing by 0.5 seconds every 2 waves, down to a minimum of 2.0
                if (subWavesRemaining > 1) {
                    if (monsterCount == 0 || spawnTimer <= 0) {
                        spawnPercentage[0] = MathUtils.clamp(1.0f - 0.05f * (currentWave - 1), 0.1f, 1.0f);  // Spawn grunts, 5% less grunts per wave
                        spawnPercentage[1] = MathUtils.clamp(0.05f * (currentWave - 1), 0, 0.9f); // Spawn ranged, 5% more per wave
                        spawnPercentage[2] =  (currentWave % 5 == 0) ?  0.05f : 0; // Spawn 5% elite mobs every 5 waves

                        // Actually spawn mobs
                        for (int i = 0; i < spawnPercentage.length; i++) {
                            if (spawnPercentage[i] > 0) {
                                spawn(MathUtils.ceil(monstersPerWave * spawnPercentage[i]), i);
                            }
                        }

                        if (currentWave % 25 == 0) {
                            // Spawn a boss every 25 waves
                            spawn(1, 3);
                        }

                        spawnTimer = MathUtils.clamp(MAX_SECONDS_TO_SPAWN - 0.5f * (currentWave / 2), MIN_SECONDS_TO_SPAWN, MAX_SECONDS_TO_SPAWN);
                        subWavesRemaining--;
                    }
                } else if (monsterCount == 0) {
                    // Subwaves complete. Next wave.
                    currentWave++;
                    // Monsters per subwave increases by 1 every 3 waves, up to 25
                    monstersPerWave = MathUtils.clamp(5 + (currentWave / 3), 5, 25);
                    // Number of subwaves start at 3, increasing by 1 every 4 waves
                    subWavesRemaining = 3 + (currentWave / 4);
                    // Increase difficulty value by 5% every wave
                    Game.setDifficultyMultiplier(1 + 0.05f * (currentWave - 1));
                    spawnTimer = Game.REST_DURATION;
                    game.setPlayMode(Game.PlayMode.REST);
                    // Show some sort of image here, saying wave x complete
                }
                break;
            case REST:
                // If rest is complete, return to survival
                if (spawnTimer < 0) {
                    game.setPlayMode(Game.PlayMode.SURVIVAL);
                    // Show some sort of image here, saying Wave x has started
                }
                break;
        }
        // Update internal level timer
        spawnTimer -= Gdx.graphics.getDeltaTime();
    }

    /**
     * Spawns stuff based on random line
     */
    public void spawn() {
        spawn(currentWave);
    }

    public void spawn(Class type) {
        Monster monster = (Monster) EntityFactory.spawn(type);
        monster.init();
        monster.setPosition(spawnPointList.randomSpawn());
        monster.setCurrentLevel(this);

        // Automatically aggro to players in survival
        if (GameStateManager.getInstance().getGame().getPlayMode() == Game.PlayMode.SURVIVAL) {
            monster.setTarget(GameStateManager.getInstance().getPlayState().getPlayer());
        }
    }

    public void spawn(int monsters) {
        for (int i = 0; i < monsters; i++) {
            spawn(Alien.class);
        }
    }

    /**
     * Method to spawn monsters based on monster hierarchy.
     *
     * @param monsters Number of monsters to spawn.
     * @param monsterLevel Rank of monster. 0 - grunt, 1 - basic, 2 - elite, 3 - boss.
     */
    public void spawn(int monsters, int monsterLevel) {
        // List of valid monster classes
        Array<Class> validMonsterType = new Array<Class>();

        // Add appropriate monster classes to the array
        switch (monsterLevel) {
            case 0:
                validMonsterType.add(Slime.class);
                break;
            case 1:
                validMonsterType.add(Alien.class);
                break;
            case 2:
                break;
            case 3:
                break;
        }

        // Spawns random monsters from selected class
        if (validMonsterType.size > 0) {
            for (int i = 0; i < monsters; i++) {
                spawn(validMonsterType.random());
            }
        }
    }

    /**
     * Checks for a tile at target pixel coordinates. Defaults to static tile without layer parameter.
     *
     * @param x     X-coordinate of the target tile in pixels.
     * @param y     Y-coordinate of the target tile in pixels.
     * @param layer Layer to check. See constants for more information.
     * @return Returns null if there is no tile. Returns tile(0,0) if out of bounds. In any other case, returns the target tile.
     */
    public TiledMapTile getTileAt(float x, float y, String layer) {
        int tileX = pixelToTile(x);
        int tileY = pixelToTile(y);
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get(layer);
        TiledMapTileLayer.Cell cell = tileLayer.getCell(tileX, tileY);

        // Treats out of bounds tiles as tile(0,0), to represent a static tile.
        // NOTE: There better be a tile at 0,0.
        if (cell == null) {
            if (tileX < 0 || tileX >= tileLayer.getWidth() || tileY < 0 || tileY > tileLayer.getHeight()) {
                return tileLayer.getCell(0, 0).getTile();
            }
            return null;
        }

        return cell.getTile();
    }

    /**
     * Returns the maximum distance the player can travel before bumping into an static obstacle, given direction.
     *
     * @param x        Initial x coordinate.
     * @param y        Initial y coordinate.
     * @param dir      Direction to scan. Negative indicates left/down.
     * @param vertical True when scanning for vertical.
     * @param layer    The layer to scan. Defaults to Level.STATIC_LAYER without parameter.
     * @param tileType The tile property key to search for. Defaults to none.
     * @return Distance to closest obstacle on specified layer.
     */
    public float distToObstacle(float x, float y, float dir, boolean vertical, String layer, String tileType) {
        float dist = 0;
        for (int i = 0; i <= Math.abs(dir); i++) {
            TiledMapTile target = getTileAt(x + (vertical ? 0 : (dir > 0 ? 1 : -1)) * i, y + (vertical ? (dir > 0 ? 1 : -1) : 0) * i, layer);
            if (target == null) {
                dist++;
            } else {
                if (tileType.equals("") || target.getProperties().containsKey(tileType)) {
                    break;
                } else {
                    dist++;
                }
            }
        }

        if (dist > 0) {
            dist--;
        }

        return dist;
    }

    public float distToObstacle(float x, float y, float dir, boolean vertical) {
        return distToObstacle(x, y, dir, vertical, STATIC_LAYER, "");
    }

    /**
     * Returns the distance downwards (in pixels) to the nearest one-way platform.
     *
     * @param x        X-coordinate of the check, in pixels.
     * @param y        Y-coordinate of the initial check, in pixels.
     * @param distance Distance downwards to scan, in pixels.
     * @return Returns the whole number of pixels away from the nearest platform.
     */
    public float distToPlatform(float x, float y, float distance) {
        float dist = 0;
        float tileY = 0;
        for (int i = 0; i <= distance; i++) {
            TiledMapTile target = getTileAt(x, y - i, DYNAMIC_LAYER);
            if (target == null) {
                dist++;
            } else {
                // Makes sure tile detected is a platform
                if (target.getProperties().containsKey("platform")) {
                    tileY = pixelToTile(y - i) / Game.UNIT_SCALE;
                    break;
                } else {
                    dist++;
                }
            }
        }

        if (dist > 0) {
            // Collision only applies if player is completely above platform
            if (y >= tileY) {
                return --dist;
            }
        }

        // Return if no obstacle found
        return 9999;
    }

    /**
     * Returns the slope of the target tile. For use when computing slopes.
     *
     * @param x X-coordinate (in pixels) of the tile to check.
     * @param y Y-coordinate (in pixels) of the tile to check.
     * @return Returns the slope as a function of x. 1.00 would denote an upwards slope of 1 Y-unit per 1 X-unit.
     */
    public float getSlopeOfTile(float x, float y) {
        TiledMapTile tile = getTileAt(x, y, DYNAMIC_LAYER);
        if (tile != null) {
            if (tile.getProperties().containsKey("slope")) {
                return Float.valueOf((String) tile.getProperties().get("slope"));
            }
        }

        // Tile is not sloped
        return 0;
    }

    /**
     * Convenience method to find the target y-position of a sloped tile, given an x value.
     *
     * @param x X-coordinate (in pixels) of the tile to check.
     * @param y Y-coordinate (in pixels) of the tile to check.
     * @return Returns the calculated y-position of the slope at the desired x-y coordinate pair.
     */
    public float getSlopePosition(float x, float y) {
        float slope = getSlopeOfTile(x, y);
        if (slope == 0) {
            // Return absurd value if there is no slope
            return -99999;
        }
        float deltaX = x % (1 / Game.UNIT_SCALE);
        float deltaY = deltaX * slope; // y = mx
        // Negative slopes start at the top of the tile, and so, 1 tile needs to be added to the result
        if (slope < 0) {
            deltaY += (1 / Game.UNIT_SCALE) * Math.abs(slope); // + b if necessary
        }
        return (float) Math.floor(y * Game.UNIT_SCALE) / Game.UNIT_SCALE + deltaY;
    }


    /**
     * Convenience method to convert pixel coordinates to tile coordinates.
     *
     * @param px The pixel quantity to convert.
     * @return The corresponding integer tile coordinate.
     */
    public int pixelToTile(float px) {
        return (int) Math.floor(px * Game.UNIT_SCALE);
    }

    public float getGravity() {
        return gravity;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public TiledMap getMap() {
        return map;
    }

    public Texture[] getBackground() {
        return layers;
    }

    public boolean isFirstLoad() {
        return firstLoad;
    }

    public int getMonsterCount() {
        return monsterCount;
    }

    public void incMonsterCount() {
        monsterCount++;
    }

    public void decMonsterCount() {
        monsterCount--;
    }

    public void dispose() {
        for (Texture i : layers) {
            i.dispose();
        }
        for (Level i : loadedMaps) {
            i.map.dispose();
        }
        loadedMaps.clear();

    }

    public float getSpawnTimer() {
        return spawnTimer;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public void setCurrentWave(int currentWave) {
        this.currentWave = currentWave;
        Game.setDifficultyMultiplier(1 + 0.05f * (currentWave - 1));
    }
}