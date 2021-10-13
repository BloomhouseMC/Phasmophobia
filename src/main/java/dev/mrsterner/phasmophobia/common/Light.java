package dev.mrsterner.phasmophobia.common;

import dev.mrsterner.phasmophobia.common.registry.PhasmoObjects;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface Light {
    default TypedActionResult<ItemStack> toggleLight(PlayerEntity playerIn, Hand handIn) {
        ItemStack itemStack = playerIn.getStackInHand(handIn);
        if (!itemStack.hasNbt()) {
            itemStack.getOrCreateNbt();
            NbtCompound tag = new NbtCompound();
            tag.putInt("on", 1);
            itemStack.setNbt(tag);
        }
        if (itemStack.getNbt().getInt("on") == 1) {
            NbtCompound tag = new NbtCompound();
            tag.putInt("on", 0);
            itemStack.setNbt(tag);
        } else if (itemStack.getNbt().getInt("on") == 0) {
            NbtCompound tag = new NbtCompound();
            tag.putInt("on", 1);
            itemStack.setNbt(tag);
        }
        return new TypedActionResult<ItemStack>(ActionResult.PASS, playerIn.getStackInHand(handIn));
    }

    default HitResult getRayTraceTarget(Entity entity, World world, double reach, boolean ignoreEntities, boolean ignoreLiquids) {
        HitResult crosshairTarget = null;
        if (entity != null && world != null) {
            float td = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? MinecraftClient.getInstance().getTickDelta() : 1f;
            crosshairTarget = entity.raycast(reach, td, !ignoreLiquids);
            if (!ignoreEntities) {
                Vec3d vec3d = entity.getCameraPosVec(td);
                double e = reach;
                e *= e;
                if (crosshairTarget != null)
                    e = crosshairTarget.getPos().squaredDistanceTo(vec3d);
                Vec3d vec3d2 = entity.getRotationVec(td);
                Vec3d vec3d3 = vec3d.add(vec3d2.x * reach, vec3d2.y * reach, vec3d2.z * reach);
                Box box = entity.getBoundingBox().stretch(vec3d2.multiply(reach)).expand(1.0D, 1.0D, 1.0D);
                EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, vec3d, vec3d3, box, (entityx) -> !entityx.isSpectator() && entityx.collides(), e);
                if (entityHitResult != null) crosshairTarget = entityHitResult;
            }
        }
        return crosshairTarget;
    }

    default void createLight(ItemStack itemStack, World world, PlayerEntity player) {
        if (!world.isClient) {
            if (itemStack.getNbt().getInt("on") == 1) return;
            HitResult lookPos = getRayTraceTarget(player, world, 30, false, false);
            BlockPos pos;
            switch (lookPos.getType()) {
                case MISS -> {return;}
                case BLOCK -> {
                    BlockHitResult blockHit = (BlockHitResult) lookPos;
                    pos = blockHit.getBlockPos().add(((BlockHitResult) lookPos).getSide().getVector());
                    System.out.println("BlockPos: "+pos);
                }
                case ENTITY -> {
                    EntityHitResult entityHit = (EntityHitResult) lookPos;
                    Entity entity = entityHit.getEntity();
                    pos = entity.getBlockPos();
                    System.out.println("EntityPos: "+pos);
                }
                default -> pos = ((BlockHitResult) lookPos).getBlockPos();
            }
            double vecDistance = Math.pow(lookPos.getPos().squaredDistanceTo(player.getPos()), 0.5);
            if (vecDistance <= 40) {
                setBlockToLight(pos, world, player);
            }
        }
    }

    default void setBlockToLight(BlockPos pos, World world, PlayerEntity player) {
        if (world.getBlockState(pos).getBlock().getDefaultState().isAir()) {
            player.world.setBlockState(pos, PhasmoObjects.BLOCK_LIGHT.getDefaultState(), 2);
        } else if (world.getBlockState(pos.add(0, 1, 0)).getBlock().getDefaultState().isAir()) {
            player.world.setBlockState(pos.add(0, 1, 0), PhasmoObjects.BLOCK_LIGHT.getDefaultState(), 2);
        }
    }
}
