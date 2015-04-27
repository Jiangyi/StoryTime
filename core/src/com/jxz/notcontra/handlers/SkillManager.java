package com.jxz.notcontra.handlers;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.jxz.notcontra.entity.EntityFactory;
import com.jxz.notcontra.entity.Projectile;
import com.jxz.notcontra.skill.MeleeAttack;
import com.jxz.notcontra.skill.Skill;

import java.util.ArrayList;

/**
 * Created by Samuel on 24/04/2015.
 * Where skills are defined.
 */

public class SkillManager {
    private static ArrayList<Skill> inventory;
    private static AssetHandler assetManager = AssetHandler.getInstance();

    // Gets skill from skill array
    public static Skill getSkill(String name) {
        for (Skill s : inventory) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    // Populates skill array - call before using any skills
    public static void init() {
        // Initialize list
        inventory = new ArrayList<Skill>();

        // Skill 1: Melee Attack
        MeleeAttack basicMeleeAttack = new MeleeAttack("testmelee");
        basicMeleeAttack.setRootWhileCasting(true);
        TextureAtlas animFrames = (TextureAtlas) assetManager.getByName("swing");
        basicMeleeAttack.setVfx(animFrames);
        basicMeleeAttack.setTime(0.1f);
        basicMeleeAttack.setAnimName("0.swingD1.1");
        basicMeleeAttack.setHitboxSize(98, 89);
        basicMeleeAttack.setHitboxOffset(-85, 0);
        basicMeleeAttack.setFlipOffset(115, 0);
        basicMeleeAttack.setSwingAnimation(new Animation(1 / 10.0f,
                (animFrames.findRegion("0.swingD1.1", 0)),
                (animFrames.findRegion("0.swingD1.1", 1))));
        basicMeleeAttack.setDamage(20);
        inventory.add(basicMeleeAttack);

        // Skill 2: Second Melee Attack
        MeleeAttack secondMeleeAttack = new MeleeAttack("melee2");
        secondMeleeAttack.setRootWhileCasting(true);
        animFrames = (TextureAtlas) assetManager.getByName("swing");
        secondMeleeAttack.setVfx(animFrames);
        secondMeleeAttack.setTime(0.2f);
        secondMeleeAttack.setAnimName("0.swingD2.1");
        secondMeleeAttack.setHitboxSize(98, 89);
        secondMeleeAttack.setHitboxOffset(-65, 0);
        secondMeleeAttack.setFlipOffset(60, 0);
        secondMeleeAttack.setSwingAnimation(new Animation(1 / 7.0f,
                (animFrames.findRegion("0.swingD2.1", 0)),
                (animFrames.findRegion("0.swingD2.1", 1))));
        secondMeleeAttack.setDamage(10);
        inventory.add(secondMeleeAttack);

    }
}
