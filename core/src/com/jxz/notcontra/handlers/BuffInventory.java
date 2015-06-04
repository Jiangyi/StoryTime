package com.jxz.notcontra.handlers;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.jxz.notcontra.buff.Buff;

/**
 * Created by Samuel on 03/06/2015.
 * Contains list of active buffs on a living entity.
 */
public class BuffInventory implements Pool.Poolable {
    // Fields
    protected Array<Buff> buffArray;

    public BuffInventory() {
        buffArray = new Array<Buff>();
    }

    public void update() {
        for (Buff b : buffArray) {
            // Step all buffs - inactive buffs will remove themselves
            b.update();
        }
    }

    public void addBuff(Buff buff) {
        buffArray.add(buff);
    }

    public void removeBuff(Buff buff) {
        buffArray.removeValue(buff, true);
    }

    public boolean hasBuff(String name) {
        for (Buff b : buffArray) {
            if (b.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void reset() {
        buffArray.clear();
    }
}
