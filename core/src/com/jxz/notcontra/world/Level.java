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
    public final String STATIC_LAYER = "Static Map"; // Name of static collision map layer

    // Class Variables
    private Game game;
    private TiledMap map;
    private float gravity = 9.8f;

    public Level(Game g, TiledMap map) {
        this.game = g;
        this.map = map;
    }

    /**
     * Checks for a static collision tile at target pixel coordinates. Used for static collision checks.
     *
     * @param x X-coordinate of the target tile in pixels.
     * @param y Y-coordinate of the target tile in pixels.
     * @return Returns null if there is no tile. Returns tile(0,0) if out of bounds. In any other case, returns the target tile.
     */
    public TiledMapTile getStaticTileAt(float x, float y) {
        int tileX = PixelToTile(x);
        int tileY = PixelToTile(y);
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get(STATIC_LAYER);
        TiledMapTileLayer.Cell cell = tileLayer.getCell(tileX, tileY);

        // Treats out of bounds tiles as tile(0,0), to represent a static tile.
        // NOTE: There better be a tile at 0,0.
        if (cell == null) {
            if (tileX < 0 || tileX > tileLayer.getWidth() || tileY < 0 || tileY > tileLayer.getHeight()) {
                return tileLayer.getCell(0, 0).getTile();
            }
            return null;
        }

        return cell.getTile();
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
}
