package dev.mrsterner.phasmophobia;

import dev.mrsterner.phasmophobia.common.block.PlaceableBlock;
import dev.mrsterner.phasmophobia.common.block.entity.PlaceableBlockEntity;
import dev.mrsterner.phasmophobia.common.entity.UngodlyRevenantEntity;
import dev.mrsterner.phasmophobia.common.item.CrucifixItem;
import dev.mrsterner.phasmophobia.common.registry.PhasmoBrains;
import dev.mrsterner.phasmophobia.common.registry.PhasmoObjects;
import dev.mrsterner.phasmophobia.common.registry.PhasmoTags;
import dev.mrsterner.phasmophobia.common.world.PhasmoWorldState;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.mixin.object.builder.SpawnRestrictionAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.Heightmap;

import java.util.function.Predicate;

import static dev.mrsterner.phasmophobia.common.block.entity.PlaceableBlockEntity.handleGUILessInventory;

public class Phasmophobia implements ModInitializer
{
    public static final String MODID = "phasmophobia";
    public static final ItemGroup PHASMOPHOBIA_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, MODID), () -> new ItemStack(PhasmoObjects.CRUCIFIX));
    private static void registerEntitySpawn(EntityType<?> type, Predicate<BiomeSelectionContext> predicate, int weight, int minGroupSize, int maxGroupSize) {
        BiomeModifications.addSpawn(predicate, type.getSpawnGroup(), type, weight, minGroupSize, maxGroupSize);
    }

    @Override
    public void onInitialize() {
        PhasmoObjects.init();
        PhasmoBrains.init();
        registerEntitySpawn(PhasmoObjects.REVENANT, BiomeSelectors.foundInOverworld().and(context -> !context.getBiome().getSpawnSettings().getSpawnEntries(PhasmoObjects.REVENANT.getSpawnGroup()).isEmpty()), 10, 1, 1);
        SpawnRestrictionAccessor.callRegister(PhasmoObjects.REVENANT, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, UngodlyRevenantEntity::canSpawn);


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
                    if(player.getStackInHand(hand).getItem() instanceof CrucifixItem && !world.isClient){
                        PhasmoWorldState worldState = PhasmoWorldState.get(world);
                        worldState.crucifix.add(pos.asLong());
                        worldState.markDirty();
                    }
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

