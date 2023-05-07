package com.feroov.frv.entity.ai;

import com.feroov.frv.entity.monster.Celestroid;
import com.feroov.frv.sound.SoundEventsSTLCON;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class CelestroidAttackGoal extends Goal {
    private final Celestroid celestroid;
    public int attackTimer;
    public int prevAttackTimer; // TF - add for renderer

    public CelestroidAttackGoal(Celestroid celestroid) {
        this.celestroid = celestroid;
    }

    @Override
    public boolean canUse() {
        return this.celestroid.getTarget() != null && celestroid.shouldAttack(celestroid.getTarget());
    }

    @Override
    public void start() {
        this.attackTimer = this.prevAttackTimer = 0;
    }

    @Override
    public void stop() {
        this.celestroid.setCharging(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.celestroid.getTarget();

        if (target.distanceToSqr(this.celestroid) < 4096.0D && this.celestroid.getSensing().hasLineOfSight(target)) {
            this.prevAttackTimer = attackTimer;
            ++this.attackTimer;

            // TF face our target at all times
            this.celestroid.getLookControl().setLookAt(target, 10F, this.celestroid.getMaxHeadXRot());

//            if (this.attackTimer == 10) {
//                celestroid.playSound(celestroid.getWarnSound(), 10.0F, celestroid.getVoicePitch());
//            }

            if (this.attackTimer == 20) {
                if (this.celestroid.shouldAttack(target)) {
                    this.celestroid.playSound(SoundEventsSTLCON.RAYGUN_SHOOT.get(), 7.0F, 1.0F);
                    this.celestroid.shootRayBeam();
                    this.prevAttackTimer = attackTimer;
                }
                this.attackTimer = -20;
            }
        } else if (this.attackTimer > 0) {
            this.prevAttackTimer = attackTimer;
            --this.attackTimer;
        }

        this.celestroid.setCharging(this.attackTimer > 5);
    }
}