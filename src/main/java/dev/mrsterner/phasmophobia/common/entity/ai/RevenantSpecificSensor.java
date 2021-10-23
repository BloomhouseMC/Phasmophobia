package dev.mrsterner.phasmophobia.common.entity.ai;

import com.google.common.collect.ImmutableSet;
import dev.mrsterner.phasmophobia.common.entity.RevenantEntity;
import dev.mrsterner.phasmophobia.common.registry.PhasmoTags;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.Set;

public class RevenantSpecificSensor extends Sensor<RevenantEntity> {
    public RevenantSpecificSensor() {
    }

    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_REPELLENT);
    }

    @Override
    protected void sense(ServerWorld serverWorld, RevenantEntity revenantEntity) {
        Brain<?> brain = revenantEntity.getBrain();
        brain.remember(MemoryModuleType.NEAREST_REPELLENT, findNearestCrucifix(serverWorld, revenantEntity));
    }

    private Optional<BlockPos> findNearestCrucifix(ServerWorld world, RevenantEntity revenant) {
        return BlockPos.findClosest(revenant.getBlockPos(), 8, 4, (blockPos) -> {
            return world.getBlockState(blockPos).isIn(PhasmoTags.REVENANT_REPELLENTS);
        });
    }
}
