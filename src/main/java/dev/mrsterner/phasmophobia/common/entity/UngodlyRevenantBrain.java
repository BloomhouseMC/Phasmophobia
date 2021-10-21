package dev.mrsterner.phasmophobia.common.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import java.util.Optional;

public class UngodlyRevenantBrain {

    private static final UniformIntProvider AVOID_MEMORY_DURATION = TimeHelper.betweenSeconds(5, 20);


    public UngodlyRevenantBrain() {
    }

    protected static Brain<?> create(UngodlyRevenantEntity ungodlyRevenantEntity, Brain<UngodlyRevenantEntity> brain) {
        addCoreTasks(brain);
        addIdleTasks(brain);
        addFightTasks(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreTasks(Brain<UngodlyRevenantEntity> brain) {
        brain.setTaskList(Activity.CORE, 0,
            ImmutableList.of(
                new StayAboveWaterTask(0.8F),
                new WalkTask(2.0F),
                new LookAroundTask(45, 90),
                new AttackTask<>(10, 1),
                new WanderAroundTask()));
    }

    private static void addIdleTasks(Brain<UngodlyRevenantEntity> brain) {
        brain.setTaskList(Activity.IDLE, 10, ImmutableList.of(
            new PacifyTask(MemoryModuleType.NEAREST_REPELLENT, 200), GoToRememberedPositionTask.toBlock(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, true), makeRandomWalkTask()));
    }

    private static void addFightTasks(Brain<UngodlyRevenantEntity> brain) {
        brain.setTaskList(Activity.FIGHT, 0, ImmutableList.of(
            new PacifyTask(MemoryModuleType.NEAREST_REPELLENT, 200)), MemoryModuleType.ATTACK_TARGET);
    }

    private static RandomTask<UngodlyRevenantEntity> makeRandomWalkTask() {
        return new RandomTask(ImmutableList.of(Pair.of(new StrollTask(0.4F), 2), Pair.of(new GoTowardsLookTarget(0.4F, 3), 2), Pair.of(new WaitTask(30, 60), 1)));
    }

    protected static void refreshActivities(UngodlyRevenantEntity revenant) {
        Brain<UngodlyRevenantEntity> brain = revenant.getBrain();
//        Activity activity = (Activity)brain.getFirstPossibleNonCoreActivity().orElse((Object)null);
        brain.resetPossibleActivities(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
//        Activity activity2 = (Activity)brain.getFirstPossibleNonCoreActivity().orElse((Object)null);
    }

    private static void avoid(UngodlyRevenantEntity revenant, LivingEntity target) {
        revenant.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
        revenant.getBrain().forget(MemoryModuleType.WALK_TARGET);
        revenant.getBrain().remember(MemoryModuleType.AVOID_TARGET, target, (long)AVOID_MEMORY_DURATION.get(revenant.world.random));
    }

    static boolean isCrucifixAround(UngodlyRevenantEntity revenant, BlockPos pos) {
        Optional<BlockPos> optional = revenant.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_REPELLENT);
        return optional.isPresent() && ((BlockPos)optional.get()).isWithinDistance(pos, 8.0D);
    }


    private static boolean hasNearestRepellent(UngodlyRevenantEntity revenant) {
        return revenant.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_REPELLENT);
    }
}
