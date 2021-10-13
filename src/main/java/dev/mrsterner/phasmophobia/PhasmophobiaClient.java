package dev.mrsterner.phasmophobia;

import dev.mrsterner.phasmophobia.common.registry.PhasmoObjects;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PhasmophobiaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        /*
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            MinecraftClient client = MinecraftClient.getInstance();
            HitResult hit = client.crosshairTarget;

            world.getPlayers().stream().filter(serverPlayerEntity -> serverPlayerEntity.getMainHandStack().getItem() == PhasmoObjects.FLASHLIGHT).forEach(serverPlayerEntity -> {
                switch(hit.getType()) {
                    case MISS:
                        break;
                    case BLOCK:
                        BlockHitResult blockHit = (BlockHitResult) hit;
                        BlockPos blockPos = blockHit.getBlockPos();
                        BlockState blockState = client.world.getBlockState(blockPos);
                        Block block = blockState.getBlock();
                        Direction direction = ((BlockHitResult) hit).getSide();
                        int newPosX = blockPos.getX();
                        int newPosY = blockPos.getY();
                        int newPosZ = blockPos.getZ();
                        switch (direction){
                            case UP -> newPosY = blockPos.getY() + 1;
                            case DOWN -> newPosY = blockPos.getY() - 1;
                            case NORTH -> newPosZ = blockPos.getZ() - 1;
                            case SOUTH -> newPosZ = blockPos.getZ() + 1;
                            case EAST -> newPosX = blockPos.getX() + 1;
                            case WEST -> newPosX = blockPos.getX() - 1;
                        }
                        BlockPos newBlockPos = new BlockPos(newPosX, newPosY, newPosZ);
                        BlockState newPlaceable = PhasmoObjects.BLOCK_LIGHT.getDefaultState();
                        world.setBlockState(newBlockPos, newPlaceable);
                        System.out.println("Placed: "+newBlockPos);
                        break;
                    case ENTITY:
                        EntityHitResult entityHit = (EntityHitResult) hit;
                        Entity entity = entityHit.getEntity();
                        break;
                }
            });
        });

         */
    }
}
