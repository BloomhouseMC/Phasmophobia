package dev.mrsterner.phasmophobia.common.entity.ai;

import com.google.common.collect.ImmutableSet;
import dev.mrsterner.phasmophobia.common.entity.UngodlyRevenantEntity;
import dev.mrsterner.phasmophobia.common.registry.PhasmoTags;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.Set;

public class RevenantSpecificSensor extends Sensor<UngodlyRevenantEntity> {
    public RevenantSpecificSensor() {
    }

    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(/*MemoryModuleType.VISIBLE_MOBS,*/ MemoryModuleType.NEAREST_REPELLENT/*, new MemoryModuleType[0]*/);
    }

    protected void sense(ServerWorld serverWorld, UngodlyRevenantEntity revenantEntity) {
        Brain<?> brain = revenantEntity.getBrain();
        brain.remember(MemoryModuleType.NEAREST_REPELLENT, this.findNearestCrucifix(serverWorld, revenantEntity));
//        Optional<PiglinEntity> optional = Optional.empty();
//        int i = 0;
//        List<RevenantEntity> list = Lists.newArrayList();
//        List<LivingEntity> list2 = (List) brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS)
//                                               .orElse(Lists.newArrayList());
//        Iterator var8 = list2.iterator();
//
//        while (var8.hasNext()) {
//            LivingEntity livingEntity = (LivingEntity) var8.next();
//            if (livingEntity instanceof PiglinEntity && !livingEntity.isBaby()) {
//                ++i;
//                if (!optional.isPresent()) {
//                    optional = Optional.of((PiglinEntity) livingEntity);
//                }
//            }
//
//            if (livingEntity instanceof RevenantEntity && !livingEntity.isBaby()) {
//                list.add((RevenantEntity) livingEntity);
//            }
//        }
    }

    private Optional<BlockPos> findNearestCrucifix(ServerWorld world, UngodlyRevenantEntity revenant) {
        return BlockPos.findClosest(revenant.getBlockPos(), 8, 4, (blockPos) -> {
            return world.getBlockState(blockPos).isIn(PhasmoTags.REVENANT_REPELLENTS);
        });
    }
}
