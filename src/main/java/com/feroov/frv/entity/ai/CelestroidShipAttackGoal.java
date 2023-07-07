package com.feroov.frv.entity.ai;

import com.feroov.frv.entity.monster.CelestroidShip;
import com.feroov.frv.sound.SoundEventsSTLCON;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class CelestroidShipAttackGoal extends Goal
{
    private final CelestroidShip celestroidShip;
    public int attackTimer;
    public int prevAttackTimer;

    public CelestroidShipAttackGoal(CelestroidShip celestroidShip) { this.celestroidShip = celestroidShip; }

    @Override
    public boolean canUse()
    {
        return this.celestroidShip.getTarget() != null && celestroidShip.shouldAttack(celestroidShip.getTarget());
    }

    @Override
    public void start() { this.attackTimer = this.prevAttackTimer = 0; }

    @Override
    public void stop() { this.celestroidShip.setCharging(false); }

    @Override
    public void tick()
    {
        LivingEntity target = this.celestroidShip.getTarget();

        if (target.distanceToSqr(this.celestroidShip) < 4096.0D && this.celestroidShip.getSensing().hasLineOfSight(target)) {
            this.prevAttackTimer = attackTimer;
            ++this.attackTimer;

            this.celestroidShip.getLookControl().setLookAt(target, 10F, this.celestroidShip.getMaxHeadXRot());

//            if (this.attackTimer == 10) {
//                celestroid.playSound(celestroid.getWarnSound(), 10.0F, celestroid.getVoicePitch());
//            }

            if (this.attackTimer == 20)
            {
                if (this.celestroidShip.shouldAttack(target))
                {
                    this.celestroidShip.playSound(SoundEventsSTLCON.RAYGUN_SHOOT.get(), 7.0F, 1.0F);
                    this.celestroidShip.shootRayBeam();
                    this.prevAttackTimer = attackTimer;
                }
                this.attackTimer = -20;
            }
        }
        else if (this.attackTimer > 0)
        {
            this.prevAttackTimer = attackTimer;
            --this.attackTimer;
        }
        this.celestroidShip.setCharging(this.attackTimer > 5);
    }
}