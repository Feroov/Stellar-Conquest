package com.feroov.frv.entity;

import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.RawAnimation;

public class AnimationConstants
{
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    public static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    public static final RawAnimation ATTACK_LOOP = RawAnimation.begin().thenLoop("attack");
    public static final RawAnimation ATTACK = RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE);
    public static final RawAnimation SWORD = RawAnimation.begin().then("sword", Animation.LoopType.PLAY_ONCE);
    public static final RawAnimation AGGRESIVE = RawAnimation.begin().thenLoop("aggresive");
    public static final RawAnimation DEFENSIVE = RawAnimation.begin().thenLoop("defensive");
}
