package com.feroov.frv.entity.ai;

import com.feroov.frv.entity.monster.Mothership;
import com.feroov.frv.sound.SoundEventsSTLCON;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class MothershipAttackGoal extends Goal
{
    private final Mothership mothership;
    public int attackTimer;
    public int prevAttackTimer;

    public MothershipAttackGoal(Mothership celestroid) { this.mothership = celestroid; }

    @Override
    public boolean canUse()
    {
        return this.mothership.getTarget() != null && mothership.shouldAttack(mothership.getTarget());
    }

    @Override
    public void start() { this.attackTimer = this.prevAttackTimer = 0; }

    @Override
    public void stop() { this.mothership.setCharging(false); }

    @Override
    public void tick()
    {
        LivingEntity target = this.mothership.getTarget();

        if (target.distanceToSqr(this.mothership) < 6096.0D && this.mothership.getSensing().hasLineOfSight(target))
        {
            this.prevAttackTimer = attackTimer;
            ++this.attackTimer;

            this.mothership.getLookControl().setLookAt(target, 10F, this.mothership.getMaxHeadXRot());

            if (this.attackTimer == 10)
            {
                mothership.playSound(mothership.getWarnSound(), 10.0F, mothership.getVoicePitch());
            }

            if (this.attackTimer == 20)
            {
                if (this.mothership.shouldAttack(target))
                {
                    this.mothership.playSound(SoundEventsSTLCON.RAYGUN_SHOOT.get(), 7.0F, 0.5F);
                    this.mothership.shootRayBeam();
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
        this.mothership.setCharging(this.attackTimer > 5);
    }
}