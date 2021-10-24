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
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public class RevenantBrain {
    private static final UniformIntProvider AVOID_MEMORY_DURATION = TimeHelper.betweenSeconds(5, 20);
    private static final UniformIntProvider GO_TO_NEMESIS_MEMORY_DURATION = TimeHelper.betweenSeconds(5, 7);
    private Function getPreferredTarget;

    public RevenantBrain() {
    }

    protected static Brain<?> create(Brain<RevenantEntity> brain) {
        addCoreTasks(brain);
        addIdleTasks(brain);
        addFightTasks(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreTasks(Brain<RevenantEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(
            new LookAroundTask(45, 90),
            new WanderAroundTask()));

    }

    private static void addIdleTasks(Brain<RevenantEntity> brain) {
        brain.setTaskList(Activity.IDLE, 10, ImmutableList.of(
            new PacifyTask(MemoryModuleType.NEAREST_REPELLENT, 200),
            GoToRememberedPositionTask.toBlock(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, true),
            new UpdateAttackTargetTask<RevenantEntity>(RevenantBrain::getNearestVisibleTargetablePlayer),
            makeRandomWalkTask()));
    }

    private static void addFightTasks(Brain<RevenantEntity> brain) {
        brain.setTaskList(Activity.FIGHT, 0, ImmutableList.of(
            new MeleeAttackTask(40),
            new PacifyTask(MemoryModuleType.NEAREST_REPELLENT, 200),
            new ForgetAttackTargetTask<>()), MemoryModuleType.ATTACK_TARGET);
    }

    protected static void refreshActivities(RevenantEntity revenant) {
        Brain<RevenantEntity> brain = revenant.getBrain();
//        Activity activity = (Activity)brain.getFirstPossibleNonCoreActivity().orElse((Object)null);
        brain.resetPossibleActivities(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
//        Activity activity2 = (Activity)brain.getFirstPossibleNonCoreActivity().orElse((Object)null);
        revenant.setAttacking(brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
    }

    private static void targetEnemy(RevenantEntity revenant, LivingEntity target) {
        if (target.getType() != PhasmoObjects.REVENANT) {
            if (Sensor.testAttackableTargetPredicate(revenant, target)) {
                if (target.getType() != PhasmoObjects.REVENANT) {
                    if (!LookTargetUtil.isNewTargetTooFar(revenant, target, 4.0D)) {
                        setAttackTarget(revenant, target);
                    }
                }
            }
        }
    }

    private static Optional<? extends LivingEntity> getNearestVisibleTargetablePlayer(RevenantEntity revenant) {
        return !isNearPlayer(revenant) ? revenant.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER) : Optional.empty();
    }

    private static void setAttackTarget(HoglinEntity hoglin, LivingEntity target) {
        Brain<HoglinEntity> brain = hoglin.getBrain();
        brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        brain.remember(MemoryModuleType.ATTACK_TARGET, target, 200L);
    }

    private static void setAttackTargetIfCloser(RevenantEntity revenant, LivingEntity targetCandidate) {
        if (!isNearPlayer(revenant)) {
            Optional<LivingEntity> optional = revenant.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
            LivingEntity livingEntity = LookTargetUtil.getCloserEntity(revenant, optional, targetCandidate);
            setAttackTarget(revenant, livingEntity);
        }
    }

    private static boolean isHuntingTarget(LivingEntity revenant, LivingEntity target) {
        if (target.getType() != EntityType.HOGLIN) {
            return false;
        } else {
            return (new Random(revenant.world.getTime())).nextFloat() < 0.1F;
        }
    }

    private static Optional<? extends LivingEntity> getPreferredTarget(RevenantEntity revenant) {
        Brain<RevenantEntity> brain = revenant.getBrain();
        return LookTargetUtil.getEntity(revenant, MemoryModuleType.ANGRY_AT);
    }

    private static RandomTask<RevenantEntity> makeRandomWalkTask() {
        return new RandomTask(ImmutableList.of(
            Pair.of(new StrollTask(0.4F), 2),
            Pair.of(new GoTowardsLookTarget(0.4F, 3), 2),
            Pair.of(new WaitTask(30, 60), 1)));
    }

    private static void setAttackTarget(RevenantEntity revenant, LivingEntity target) {
        Brain<RevenantEntity> brain = revenant.getBrain();
        brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        brain.remember(MemoryModuleType.ATTACK_TARGET, target, 200L);
    }

    static boolean isCrucifixAround(RevenantEntity revenant, BlockPos pos) {
        Optional<BlockPos> optional = revenant.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_REPELLENT);
        return optional.isPresent() && ((BlockPos) optional.get()).isWithinDistance(pos, 8.0D);
    }

    protected static void onAttacked(RevenantEntity revenant, LivingEntity attacker) {
        Brain<RevenantEntity> brain = revenant.getBrain();
        brain.forget(MemoryModuleType.PACIFIED);
        brain.forget(MemoryModuleType.BREED_TARGET);
        targetEnemy(revenant, attacker);
    }

    private static boolean hasNearestRepellent(RevenantEntity revenant) {
        return revenant.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_REPELLENT);
    }

    protected static void method_35198(RevenantEntity revenant, LivingEntity livingEntity) {
        revenant.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        revenant.getBrain().remember(MemoryModuleType.ANGRY_AT, livingEntity.getUuid(), 600L);
    }

    protected static boolean isNearPlayer(RevenantEntity revenant) {
        return revenant.getBrain().hasMemoryModule(MemoryModuleType.PACIFIED);
    }
}
