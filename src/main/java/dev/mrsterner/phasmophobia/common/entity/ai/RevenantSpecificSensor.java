package dev.mrsterner.phasmophobia.common.entity.ai;

    import com.google.common.collect.ImmutableSet;
    import dev.mrsterner.phasmophobia.common.block.PlaceableBlock;
    import dev.mrsterner.phasmophobia.common.block.entity.PlaceableBlockEntity;
    import dev.mrsterner.phasmophobia.common.entity.RevenantEntity;
    import dev.mrsterner.phasmophobia.common.item.CrucifixItem;
    import dev.mrsterner.phasmophobia.common.registry.PhasmoTags;
    import net.minecraft.block.Block;
    import net.minecraft.block.entity.BlockEntity;
    import net.minecraft.entity.LivingEntity;
    import net.minecraft.entity.ai.brain.Brain;
    import net.minecraft.entity.ai.brain.MemoryModuleType;
    import net.minecraft.entity.ai.brain.sensor.Sensor;
    import net.minecraft.item.Item;
    import net.minecraft.server.world.ServerWorld;
    import net.minecraft.util.math.BlockPos;

    import java.util.Optional;
    import java.util.Set;

public class RevenantSpecificSensor extends Sensor<LivingEntity> {
    public RevenantSpecificSensor() {
    }

    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_REPELLENT);
    }

    @Override
    protected void sense(ServerWorld world, LivingEntity entity) {
        Brain<?> brain = entity.getBrain();
        brain.remember(MemoryModuleType.NEAREST_REPELLENT, this.findNearestCrucifix(world, (RevenantEntity) entity));
    }

    private Optional<BlockPos> findNearestCrucifix(ServerWorld world, RevenantEntity revenant) {
        return BlockPos.findClosest(revenant.getBlockPos(), 8, 4, (blockPos2) ->
            {
                BlockEntity blockEntity = world.getBlockEntity(blockPos2);
                if(blockEntity instanceof PlaceableBlockEntity placeableBlockEntity){
                    for (int i = 0; i < placeableBlockEntity.size(); i++) {
                        Item item = placeableBlockEntity.getStack(i).getItem();
                        if(item instanceof CrucifixItem){
                            return true;
                        }
                    }
                }
                return false;
            });
    }
}

