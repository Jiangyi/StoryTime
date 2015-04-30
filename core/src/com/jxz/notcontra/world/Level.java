package com.jxz.notcontra.world;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.jxz.notcontra.game.Game;

/**
 * Created by Samuel on 04/04/2015.
 * Level
 */
public class Level {

    // Class constants
    public static final String STATIC_LAYER = "Static Map";     // Name of static collision map layer
    public static final String DYNAMIC_LAYER = "Dynamic Map";   // Name of dynamic collision layers (platforms and slopes)
    public static final String TRIGGER_LAYER = "Trigger Tile";  // Name of trigger layer (ladders/ropes)

    // Class Variables
    private static Array<Level> loadedMaps = new Array<Level>();    // Static list of Levels to avoid duplicates
    private TiledMap map;
    private int height, width;
    private float gravity = 0.15f / Game.UNIT_SCALE;

    protected Level(TiledMap map) {
        this.map = map;
        height = map.getProperties().get("height", int.class);
        width = map.getProperties().get("width", int.class);
        loadedMaps.add(this);
    }

    /**
     * Returns a level instance, given a TiledMap. Prevents duplicate levels from existing.
     * @param map TiledMap for the level.
     * @return A reference to the Level object corresponding to the map.
     */
    public static Level getLevel(TiledMap map) {
        for (Level l : loadedMaps) {
            if (l.getMap().equals(map)) {
                return l;
            }
        }
        return new Level(map);
    }

    /**
     * Checks for a tile at target pixel coordinates. Defaults to static tile without layer parameter.
     *
     * @param x X-coordinate of the target tile in pixels.
     * @param y Y-coordinate of the target tile in pixels.
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
     * @param x X-coordinate of the check, in pixels.
     * @param y Y-coordinate of the initial check, in pixels.
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
     * @param x     X-coordinate (in pixels) of the tile to check.
     * @param y     Y-coordinate (in pixels) of the tile to check.
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
}
