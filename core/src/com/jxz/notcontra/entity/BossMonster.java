package com.jxz.notcontra.entity;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jxz.notcontra.handlers.GameStateManager;

/**
 * Created by Samuel on 03/06/2015.
 * Boss monster class. Most implementation will be done in subclass, as bosses are unique.
 */
public abstract class BossMonster extends RangedMonster {

    // Each boss has access to the hudcam for drawing their healthbar
    protected static OrthographicCamera hudCam;

    public BossMonster(String name, int skillCapacity) {
        super(name, skillCapacity);

        // Set HUD cam for drawing health bar
        if (hudCam == null) {
            hudCam = GameStateManager.getInstance().getGame().getHudCam();
        }
    }
}
