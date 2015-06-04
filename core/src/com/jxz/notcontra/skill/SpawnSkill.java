package com.jxz.notcontra.skill;

import com.jxz.notcontra.entity.EntityFactory;
import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.entity.Monster;

/**
 * Created by Samuel on 03/06/2015.
 * Skills that spawn minions. Generally reserved for bosses.
 */
public class SpawnSkill extends Skill {

    protected Class monsterType;
    protected int number;

    public SpawnSkill(String name, Class monsterType, int number) {
        super(name);
        requiresCastPriority = true;
        cooldown = 0;
        this.monsterType = monsterType;
        this.number = number;
    }

    @Override
    public void use(LivingEntity caster) {
        for (int i = 0; i < number; i++) {
            // Spawn monsters
            Monster monster = (Monster) EntityFactory.spawn(monsterType);
            monster.init();
            monster.setPosition(caster.getCurrentLevel().getSpawnPointList().randomSpawn());
            monster.setCurrentLevel(caster.getCurrentLevel());

            // Immediately aggroes the monsters to the caster's target if applicable
            if (caster instanceof Monster) {
                Monster m = (Monster) caster;
                if (m.getTarget() != null) {
                    monster.setTarget(m.getTarget());
                }
            }
        }
    }

    @Override
    public void preCast(LivingEntity caster) {
        // There's no real precast for spawning more mobs
    }


}
