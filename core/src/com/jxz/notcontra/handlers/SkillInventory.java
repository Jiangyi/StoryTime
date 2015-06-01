package com.jxz.notcontra.handlers;

import com.jxz.notcontra.entity.DynamicHitbox;
import com.jxz.notcontra.skill.Skill;

/**
 * Created by Samuel on 03/05/2015.
 * A container for entity skills.
 */
public class SkillInventory {
    private Skill[] inventory; // List of skills owned by the caster
    private boolean[] isActive; // Is skill active? Used for toggle/reactivatable skills
    private float[] cooldown;   // Self explanatory. Spells may only be cast upon zero cooldown
    private float[] weighting; // Tendency for AI to fire each skill
    private DynamicHitbox[] projectiles;   // List of active projectiles stored by the caster, if necessary

    public SkillInventory(int capacity) {
        inventory = new Skill[capacity];
        isActive = new boolean[capacity];
        cooldown = new float[capacity];
        weighting = new float[capacity];
        projectiles = new DynamicHitbox[capacity];
    }

    public void setInventory(int index, Skill skill) {
        inventory[index] = skill;
    }

    public void setInventory(int index, String name) {
        inventory[index] = SkillManager.getSkill(name);
    }

    public Skill getSkill(int index) {
        return inventory[index];
    }

    public void setActive(int index, boolean isActive) {
        this.isActive[index] = isActive;
    }

    public boolean getActive(int index) {
        return isActive[index];
    }

    public float getCooldown(int index) {
        return cooldown[index];
    }

    public void decreaseCooldown(int index, float time) {
        cooldown[index] -= time;
        if (cooldown[index] < 0) {
            cooldown[index] = 0;
        }
    }

    public void setCooldown(int index, float cd) {
        cooldown[index] = cd;
    }

    public void setCooldown(Skill skill, float cd) {
        cooldown[getIndex(skill)] = cd;
    }

    public int getIndex(Skill skill) {
        for (int i = 0; i < inventory.length; i++) {
            if (skill.equals(inventory[i])) {
                return i;
            }
        }
        return -1;
    }

    public void setWeighting(int index, float weighting) {
        this.weighting[index] = weighting;
    }

    public float getWeighting(int index) {
        return weighting[index];
    }
}
