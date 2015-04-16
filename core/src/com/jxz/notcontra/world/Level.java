package com.jxz.notcontra.world;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.jxz.notcontra.game.Game;

/**
 * Created by Samuel on 04/04/2015.
 */
public class Level {

    // Class constants
    public static final String STATIC_LAYER = "Static Map"; // Name of static collision map layer
    public static final String DYNAMIC_LAYER = "Dynamic Map"; // Name of dynamic collision layers (platforms and slopes)
    public static final String CLIMB_LAYER = "Trigger Tile"; // Name of trigger layer (ladders/ropes)

    // Class Variables
    private Game game;
    private TiledMap map;
    private int height, width;
    private float gravity = 0.15f / Game.UNIT_SCALE;

    public Level(Game g, TiledMap map) {
        this.game = g;
        this.map = map;
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
        int tileX = PixelToTile(x);
        int tileY = PixelToTile(y);
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

    public TiledMapTile getTileAt(float x, float y) {
        return getTileAt(x, y, STATIC_LAYER);
    }

    /**
     * Returns the maximum distance the player can travel before bumping into an static obstacle, given direction.
     *
     * @param x        Initial x coordinate.
     * @param y        Initial y coordinate.
     * @param dir      Direction to scan. Negative indicates left/down.
     * @param vertical True when scanning for vertical.
     * @return
     */
    public float distToObstacle(float x, float y, float dir, boolean vertical) {
        float dist = 0;
        for (int i = 0; i <= Math.abs(dir); i++) {
            if (getTileAt(x + (vertical ? 0 : (dir > 0 ? 1 : -1)) * i, y + (vertical ? (dir > 0 ? 1 : -1) : 0) * i) == null) {
                dist++;
            } else {
                break;
            }
        }

        if (dist > 0) {
            dist--;
        }

        return dist;
    }

    /**
     * Convenience method to convert pixel coordinates to tile coordinates.
     *
     * @param px The pixel quantity to convert.
     * @return The corresponding integer tile coordinate.
     */
    public int PixelToTile(float px) {
        return (int) Math.floor(px * Game.UNIT_SCALE);
    }

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
