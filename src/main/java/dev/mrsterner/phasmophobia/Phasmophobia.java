package dev.mrsterner.phasmophobia;

import dev.mrsterner.phasmophobia.common.block.PlaceableBlock;
import dev.mrsterner.phasmophobia.common.block.entity.PlaceableBlockEntity;
import dev.mrsterner.phasmophobia.common.registry.PhasmoObjects;
import dev.mrsterner.phasmophobia.common.registry.PhasmoTags;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import net.fabricmc.api.ModInitializer;

import static dev.mrsterner.phasmophobia.common.block.entity.PlaceableBlockEntity.handleGUILessInventory;

public class Phasmophobia implements ModInitializer
{
    public static final String MODID = "phasmophobia";
    public static final ItemGroup PHASMOPHOBIA_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, MODID), () -> new ItemStack(PhasmoObjects.CRUCIFIX));

    @Override
    public void onInitialize() {
        PhasmoObjects.init();

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (player.isSneaking() && PhasmoTags.PLACEABLES.contains(player.getStackInHand(hand).getItem()) && world.getBlockState(hitResult.getBlockPos()) != PhasmoObjects.PLACEABLE.getDefaultState()) {
                BlockPos pos = hitResult.getBlockPos().add(hitResult.getSide().getVector());
                BlockPos blockPos = new BlockPos(pos);
                BlockState newPlaceable = PhasmoObjects.PLACEABLE.getDefaultState();
                newPlaceable = PhasmoObjects.PLACEABLE.getDefaultState().with(Properties.HORIZONTAL_FACING, newPlaceable.get(PlaceableBlock.FACING));
                BlockPos checkAir = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
                if (world.getBlockState(pos).getBlock() == Blocks.AIR && world.getBlockState(checkAir).getBlock() != Blocks.AIR && world.getBlockState(checkAir).getBlock() != PhasmoObjects.PLACEABLE) {
                    world.setBlockState(blockPos, newPlaceable, 3);
                    BlockEntity placeableEntity = world.getBlockEntity(blockPos);
                    PlaceableBlockEntity placeableEntity1 = (PlaceableBlockEntity) placeableEntity;
                    double normalX = (hitResult.getPos().x - pos.getX());
                    double normalZ = (hitResult.getPos().z - pos.getZ());
                    if (normalX < 0.5 && normalZ > 0.5)
                        handleGUILessInventory(player.getStackInHand(hand), player, hand, placeableEntity1.inventory, 2);
                    if (normalX > 0.5 && normalZ > 0.5)
                        handleGUILessInventory(player.getStackInHand(hand), player, hand, placeableEntity1.inventory, 3);
                    if (normalX < 0.5 && normalZ < 0.5)
                        handleGUILessInventory(player.getStackInHand(hand), player, hand, placeableEntity1.inventory, 0);
                    if (normalX > 0.5 && normalZ < 0.5)
                        handleGUILessInventory(player.getStackInHand(hand), player, hand, placeableEntity1.inventory, 1);

                    return ActionResult.SUCCESS;
                }
                return ActionResult.PASS;
            }
            return ActionResult.PASS;
        });

    }
}

