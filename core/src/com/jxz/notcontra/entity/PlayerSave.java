package com.jxz.notcontra.entity;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Andrew on 2015-05-07.
 * Prototype for saving crucial player object information
 */
public class PlayerSave {

    public String name;
    public int score;
    public float x;
    public float y;
    public int health;
    public int mana;
    public String mode;
    public String level;
    public long timestamp = System.currentTimeMillis();
    public int wave;

    public PlayerSave() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setPosition(Vector2 position) {
        this.x = position.x;
        this.y = position.y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setWave(int wave) {
        this.wave = wave;
    }

    public int getWave() {
        return wave;
    }
}
