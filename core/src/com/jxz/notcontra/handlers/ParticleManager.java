package com.jxz.notcontra.handlers;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.particles.Particle;
import com.jxz.notcontra.particles.ParticleFactory;

/**
 * Created by Kevin Xiao on 2015-05-31.
 */
public class ParticleManager implements Disposable {

    private Array<Particle> masterList = new Array<Particle>();
    public static int id;
    private static ParticleManager particleManager;

    private ParticleManager() {
    }

    public static ParticleManager getInstance() {
        if (particleManager == null) {
            particleManager = new ParticleManager();
        }
        return particleManager;
    }

    public void register(Particle p) {
        masterList.add(p);
        id++;
        if (Game.getDebugMode()) System.out.println("particle" + id + "has registered");
    }

    public Array<Particle> getParticlesListIteration() {
        // Return a copy of the list for iteration purposes
        return new Array(masterList);
    }

    public Array<Particle> getParticlesList() {
        return masterList;
    }

    public void unregister(Particle p) {
        masterList.removeValue(p, true);
    }

    @Override
    public void dispose() {
        for (Particle p : masterList) {
            if (p instanceof Pool.Poolable) {
                ParticleFactory.free(p);
                Pools.get(p.getClass()).clear();
            }
        }
        masterList.clear();
        id = 0;
    }

}
