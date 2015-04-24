package com.jxz.notcontra.skill;

/**
 * Created by Samuel on 23/04/2015.
 * Base class for skills.
 */
public abstract class Skill {

    // Enum to classify damage type
    public enum DamageType {
        MELEE, PHYSICAL, MAGICAL
    }

    // Statistical Info
    protected String name;
    protected float cost;
    protected DamageType damageType;

    // Constructor
    public Skill(String name) {
        this.name = name;
    }

    public abstract void use();

}
