package dev.mrsterner.phasmophobia;

import dev.mrsterner.phasmophobia.common.registry.PhasmoObjects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
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
    default BlockHitResult rayTrace(double blockReachDistance, float partialTicks, PlayerEntity player) {
        float playerPitch = player.getPitch();
        float playerYaw = player.getYaw();
        float playerYawCos = MathHelper.cos(-playerYaw * 0.017453292F - 3.1415927F);
        float playerYawSin = MathHelper.sin(-playerYaw * 0.017453292F - 3.1415927F);
        float playerPitchCos = -MathHelper.cos(-playerPitch * 0.017453292F);
        float playerPitchSin = MathHelper.sin(-playerPitch * 0.017453292F);
        float playerNormal1 = playerYawSin * playerPitchCos;
        float playerNormal2 = playerYawCos * playerPitchCos;
        Vec3d vec3d = player.getCameraPosVec(partialTicks).add((double) playerNormal1 * blockReachDistance, (double) playerPitchSin * blockReachDistance, (double) playerNormal2 * blockReachDistance);
        return player.world.raycast(new RaycastContext(player.getCameraPosVec(partialTicks), vec3d, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
    }

    default void createLight(ItemStack itemStack, World world, PlayerEntity player) {
        if (!world.isClient) {
            if (itemStack.getNbt().getInt("on") == 1) return;

            HitResult lookPos = rayTrace(40, 1.0f, player);
            BlockPos pos = null;
            if(lookPos.getType() == HitResult.Type.MISS) return;
            else if(lookPos.getType() == HitResult.Type.ENTITY){
                EntityHitResult entityHit = (EntityHitResult) lookPos;
                Entity entity = entityHit.getEntity();
                pos = entity.getBlockPos();
                System.out.println("EntityPos: "+pos);
            }else if(lookPos.getType() == HitResult.Type.BLOCK){
                BlockHitResult blockHit = (BlockHitResult) lookPos;
                pos = blockHit.getBlockPos().add(((BlockHitResult) lookPos).getSide().getVector());
                System.out.println("BlockPos: "+pos);
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
