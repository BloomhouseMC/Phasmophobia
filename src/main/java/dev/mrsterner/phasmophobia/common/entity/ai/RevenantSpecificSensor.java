package dev.mrsterner.phasmophobia.common.entity.ai;

    import com.google.common.collect.ImmutableSet;
    import dev.mrsterner.phasmophobia.common.entity.RevenantEntity;
    import dev.mrsterner.phasmophobia.common.registry.PhasmoTags;
    import net.minecraft.entity.LivingEntity;
    import net.minecraft.entity.ai.brain.Brain;
    import net.minecraft.entity.ai.brain.MemoryModuleType;
    import net.minecraft.entity.ai.brain.sensor.NearestVisibleLivingEntitySensor;
    import net.minecraft.entity.ai.brain.sensor.PiglinSpecificSensor;
    import net.minecraft.entity.ai.brain.sensor.Sensor;
    import net.minecraft.server.world.ServerWorld;
    import net.minecraft.util.math.BlockPos;

    import java.util.Optional;
    import java.util.Set;

public class RevenantSpecificSensor extends Sensor<LivingEntity> {
    public RevenantSpecificSensor() {
    }


    private boolean canHunt(LivingEntity revenant, LivingEntity target) {
        return !revenant.getBrain().hasMemoryModule(MemoryModuleType.HAS_HUNTING_COOLDOWN);
    }

    private boolean isAlwaysHostileTo(LivingEntity entity) {
        return true;
    }

    private boolean isInRange(LivingEntity axolotl, LivingEntity target) {
        return target.squaredDistanceTo(axolotl) <= 64.0D;
    }

    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(/*MemoryModuleType.VISIBLE_MOBS,*/ MemoryModuleType.NEAREST_REPELLENT/*, new MemoryModuleType[0]*/);
    }

    @Override
    protected void sense(ServerWorld world, LivingEntity entity) {
        Brain<?> brain = entity.getBrain();
        brain.remember(MemoryModuleType.NEAREST_REPELLENT, this.findNearestCrucifix(world, (RevenantEntity) entity));
    }

    private Optional<BlockPos> findNearestCrucifix(ServerWorld world, RevenantEntity revenant) {
        return BlockPos.findClosest(revenant.getBlockPos(), 8, 4, (blockPos) ->
            world.getBlockState(blockPos).isIn(PhasmoTags.REVENANT_REPELLENTS));
    }


    protected boolean matches(LivingEntity entity, LivingEntity target) {
        if (!Sensor.testAttackableTargetPredicate(entity, target) || !this.isAlwaysHostileTo(target) && !this.canHunt(entity, target)) {
            return false;
        } else {
            return this.isInRange(entity, target) && target.isInsideWaterOrBubbleColumn();
        }
    }


}

