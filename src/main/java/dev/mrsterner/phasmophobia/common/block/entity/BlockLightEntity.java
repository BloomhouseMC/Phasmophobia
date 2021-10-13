package dev.mrsterner.phasmophobia.common.block.entity;

import dev.mrsterner.phasmophobia.common.registry.PhasmoObjects;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockLightEntity extends BlockEntity {
    public BlockLightEntity(BlockPos pos, BlockState state) {
        super(PhasmoObjects.BLOCK_LIGHT_ENTITY, pos, state);
    }
    public static void tick(World world, BlockPos pos, BlockState state, BlockLightEntity blockEntity) {
        if (world.isClient) return;
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
    }
}
