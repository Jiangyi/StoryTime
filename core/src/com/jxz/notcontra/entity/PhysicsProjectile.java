package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.skill.LinearProjectileSkill;
import com.jxz.notcontra.skill.Skill;
import com.jxz.notcontra.world.Level;

import java.util.ArrayList;

/**
 * Created by Samuel on 2015-06-04.
 */
public class PhysicsProjectile extends Projectile {
    protected float currentGravity;
    protected float gravityScaling = 1.0f;
    protected boolean ignorePlatforms;
    protected boolean explodeOnGround = false;
    protected boolean isOnSlope, isGrounded, isOnPlatform;

    @Override
    public void update() {
        // Projectile is out of life
        if (time < 0 || (targets == 0 && targetLimited)) {
            EntityFactory.free(this);
        } else {
            // Move projectile forwards
            // Update Collision
            /** Declare local variables */
            float slopeLeft, slopeRight;
            float centerX, centerY;
            float deltaX, deltaY;
            deltaX = 0;
            deltaY = 0;

            // Previous State storage
            boolean prevGrounded = isGrounded;

            // Update pre-positional fields
            centerX = position.x + aabb.getWidth() / 2;
            centerY = position.y + aabb.getHeight() / 2;

            int height = (int) Math.ceil(aabb.getHeight() * Game.UNIT_SCALE); // Tile height, rounded up
            int width = (int) Math.ceil(aabb.getWidth() * Game.UNIT_SCALE); // Gets tile width, rounded up, of the entity
            float widthOffset, heightOffset;

            /** Step X coordinate */
            // Step X for static tile data
            if (direction.x != 0) {
                // Add effective speed to delta X
                float effectiveSpeed = speed * (isOnSlope ? 0.75f : 1);
                deltaX += effectiveSpeed * direction.x;
            }

            // Check collision bounds
            float boundingEdgeDelta = (deltaX > 0 ? 1 : -1) * aabb.getWidth() / 2;
            float dist;

            // Low FPS check - ensure collisions are checked properly if FPS < 60
            if (Game.getFpsTimer() > 1) {
                deltaX *= Game.getFpsTimer();
            }

            // X collision check
            float maxDist = Math.abs(deltaX);
            for (int i = 0; i <= height; i++) {
                if (i == width) {
                    heightOffset = aabb.getHeight();
                } else {
                    heightOffset = i / Game.UNIT_SCALE;
                }
                dist = currentLevel.distToObstacle(centerX + boundingEdgeDelta, position.y + heightOffset, deltaX, false);
                if (dist < maxDist) {
                    maxDist = dist;
                }
            }

            deltaX = (deltaX > 0 ? 1 : -1) * maxDist;

            // Update x
            position.x += (Game.getFpsTimer() > 1) ? deltaX : deltaX * Game.getFpsTimer();

            // Step upwards movement
            deltaY += speed * direction.y;
            // Update Y if on slope
            if (isOnSlope) {
                slopeLeft = currentLevel.getSlopeOfTile(position.x, position.y - 1);
                slopeRight = currentLevel.getSlopeOfTile(position.x + aabb.getWidth(), position.y - 1);
                float currentSlope = (Math.abs(slopeLeft) > Math.abs(slopeRight) ? slopeLeft : slopeRight);
                if (currentLevel.getTileAt(position.x, position.y, Level.DYNAMIC_LAYER) == currentLevel.getTileAt(position.x + aabb.getWidth(), position.y, Level.DYNAMIC_LAYER)) {
                    //deltaY += currentSlope;
                }
                position.y += ((Game.getFpsTimer() > 1) ? deltaX : deltaX * Game.getFpsTimer()) * currentSlope;
            }

            float currentDist, currentSlope; // local variables
            currentGravity += currentLevel.getGravity() * Gdx.graphics.getDeltaTime() * gravityScaling; // Step gravity
            //System.out.println(currentGravity);
            maxDist = 9999; // Reset max distance
            // Iterates through all intersecting Y tiles
            for (int i = 0; i <= width; i++) {
                if (i == width) {
                    widthOffset = aabb.getWidth();
                } else {
                    widthOffset = i / Game.UNIT_SCALE;
                }

                // Updates position due to gravity, if applicable (not climbing)
                if (!isGrounded) {
                    deltaY -= currentGravity;
                } else {
                    // Resets jump counter if player is already grounded
                    currentGravity = 0;
                }

                // Stick player to slope
                if (deltaX != 0 && isOnSlope) {
                    deltaY -= Math.abs(deltaX) * currentLevel.getGravity() * gravityScaling;
                }

                // Low fps check - ensures collisions are handled properly when FPS < 60
                if (Game.getFpsTimer() > 1) {
                    deltaY *= Game.getFpsTimer();
                }

                // Y-check
                // Static Collision Check:
                boundingEdgeDelta = (deltaY > 0 ? 1 : -1) * aabb.getHeight() / 2; // Defines either the top edge of the AABB or bottom edge, depending on direction
                dist = currentLevel.distToObstacle(position.x + widthOffset, centerY + boundingEdgeDelta, deltaY, true);

                // Dynamic collision check
                // Slope / One way platform check only applies if player is moving down
                if (deltaY <= 0) {
                    // One way platform check
                    if (!ignorePlatforms) {
                        currentDist = currentLevel.distToPlatform(position.x + widthOffset, position.y, Math.abs(deltaY));
                        if (currentDist < dist) {
                            dist = currentDist;
                        }
                    }

                }

                // Update max possible distance travelled
                if (dist < maxDist) {
                    maxDist = dist;
                }
            }

            // Slope Check
            if (deltaY <= 0) {
                float leftDist, rightDist;
                // Slope Check - scan downwards until a slope tile is found
                leftDist = currentLevel.distToObstacle(position.x, position.y, deltaY, true, Level.DYNAMIC_LAYER, "slope");
                rightDist = currentLevel.distToObstacle(position.x + aabb.getWidth(), position.y, deltaY, true, Level.DYNAMIC_LAYER, "slope");

                // Get y-coordinate of nearest slope to the left and right sides
                slopeLeft = currentLevel.getSlopePosition(position.x, position.y - leftDist);
                slopeRight = currentLevel.getSlopePosition(position.x + aabb.getWidth(), position.y - rightDist);

                // Calculate differences
                leftDist = position.y - slopeLeft;
                rightDist = position.y - slopeRight;

                // Current slope is the closest edge
                if (leftDist > rightDist) {
                    currentSlope = rightDist;
                    currentDist = slopeRight;
                } else {
                    currentSlope = leftDist;
                    currentDist = slopeLeft;
                }

                // If player is within correcting distance under the slope, correct them onto the slope
                if (currentSlope < 0 && currentSlope > -3) {
                    maxDist = 0;
                    position.y = currentDist;
                } else if (currentSlope < maxDist) {
                    // If not, get the maximum distance that can be travelled
                    maxDist = currentSlope;
                }
            }

            // Finalize delta Y based on lowest distance
            deltaY = (deltaY > 0 ? 1 : -1) * maxDist;
            position.y += (Game.getFpsTimer() > 1) ? deltaY : deltaY * Game.getFpsTimer();


            /** Update boolean states **/
            // Check if player is on static ground
            isGrounded = currentLevel.distToObstacle(position.x, position.y, -1, true) == 0 || currentLevel.distToObstacle(position.x + aabb.getWidth(), position.y, -1, true) == 0;

            // Check if player is on dynamic ground (platform)
            isOnPlatform = currentLevel.distToPlatform(position.x, position.y, 1) == 0 || currentLevel.distToPlatform(position.x + aabb.getWidth(), position.y, 1) == 0;

            // If player is in a slope tile, they are also grounded if they are in the proper y-position
            if (currentLevel.getSlopeOfTile(position.x, position.y) != 0 || currentLevel.getSlopeOfTile(position.x + aabb.getWidth(), position.y) != 0) {
                slopeLeft = currentLevel.getSlopePosition(position.x, position.y);
                slopeRight = currentLevel.getSlopePosition(position.x + aabb.getWidth(), position.y);
                isOnSlope = (position.y == slopeLeft) || (position.y == slopeRight);
            } else {
                // Check edge case where player is technically not on slope tile, but is still on the slope
                if (isOnSlope) {
                    slopeLeft = currentLevel.getSlopePosition(position.x, position.y - 1);
                    slopeRight = currentLevel.getSlopePosition(position.x + aabb.getWidth(), position.y - 1);
                    isOnSlope = (position.y == slopeLeft || position.y == slopeRight);
                } else {
                    isOnSlope = false;
                }
            }

            if (!isGrounded) {
                isGrounded = isOnSlope;
            }

            /** Update final positions */
            aabb.setPosition(position);
            animate();

            // Update lifespan
            time -= Gdx.graphics.getDeltaTime();

            // Check collisions
            if (isCollidable) {
                ArrayList<Entity> target = collisionCheck();
                if (!target.isEmpty()) {
                    for (Entity e : target) {
                        if (targets > 0 && targetLimited) {
                            targets--;
                        }
                        parent.hit(e);
                    }
                }
            }

            if (isGrounded && explodeOnGround) {
                EntityFactory.free(this);
            }
        }
    }

    public void setGravityScaling(float gravityScaling) {
        this.gravityScaling = gravityScaling;
    }

    public void setIgnorePlatforms(boolean ignorePlatforms) {
        this.ignorePlatforms = ignorePlatforms;
    }

    @Override
    public void reset() {
        currentGravity = 0;
        super.reset();
    }

    public void setExplodeOnGround(boolean explodeOnGround) {
        this.explodeOnGround = explodeOnGround;
    }

}
