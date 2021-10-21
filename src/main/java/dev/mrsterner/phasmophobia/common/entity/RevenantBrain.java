package dev.mrsterner.phasmophobia.common.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import dev.mrsterner.phasmophobia.common.registry.PhasmoObjects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.util.math.intprovider.UniformIntProvider;
/*
public class RevenantBrain {
    private static final UniformIntProvider WALKING_SPEED = UniformIntProvider.create(5, 16);
    public RevenantBrain() {
    }

    protected static Brain<?> create(UngodlyRevenantEntity revenant, Brain<UngodlyRevenantEntity> brain) {
        addCoreActivities(brain);
        addIdleActivities(brain);
        addFightActivities(brain);
        addFightTasks(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }
    private static void addCoreActivities(Brain<RevenantEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(
            new StayAboveWaterTask(0.8F),
            new WalkTask(2.0F),
            new LookAroundTask(45, 90),
            new WanderAroundTask()));
    }

    private static void addFightTasks(Brain<RevenantEntity> brain) {
        brain.setTaskList(Activity.FIGHT, 0, ImmutableList.of(
            new MeleeAttackTask(10)));
    }

    private static void addFightActivities(Brain<RevenantEntity> brain) {
        brain.setTaskList(Activity.FIGHT, 0, ImmutableList.of(
            new MeleeAttackTask(10)), MemoryModuleType.ATTACK_TARGET);
    }

    private static void addIdleActivities(Brain<RevenantEntity> brain) {
        brain.setTaskList(Activity.IDLE, ImmutableList.of(
            Pair.of(0, new TimeLimitedTask(new FollowMobTask(EntityType.PLAYER, 6.0F), UniformIntProvider.create(30, 60))),
            Pair.of(2, new WalkTowardClosestAdultTask(WALKING_SPEED, 1.25F)),
            Pair.of(3, new RandomTask(ImmutableList.of(
            Pair.of(new StrollTask(1.0F), 2),
            Pair.of(new GoTowardsLookTarget(1.0F, 3), 2))))));
    }

    private static void setAttackTarget(RevenantEntity revenant, LivingEntity target) {
        Brain<RevenantEntity> brain = revenant.getBrain();
        brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        brain.remember(MemoryModuleType.ATTACK_TARGET, target, 200L);
    }

    protected static void onAttacking(RevenantEntity revenant, LivingEntity target) {


    }


    private static void targetEnemy(RevenantEntity revenant, LivingEntity target) {
        if (!revenant.getBrain().hasActivity(Activity.AVOID) || target.getType() != PhasmoObjects.REVENANT) {
            if (Sensor.testAttackableTargetPredicate(revenant, target)) {
                if (target.getType() != EntityType.HOGLIN) {
                    if (!LookTargetUtil.isNewTargetTooFar(revenant, target, 4.0D)) {
                        setAttackTarget(revenant, target);
                    }
                }
            }
        }
    }

    public static void updateActivities(RevenantEntity revenant) {
        revenant.getBrain().resetPossibleActivities(ImmutableList.of(Activity.RAM, Activity.LONG_JUMP, Activity.IDLE));

        Brain<RevenantEntity> brain = revenant.getBrain();
        revenant.setAttacking(brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
    }

}

 */


