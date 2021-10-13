package dev.mrsterner.phasmophobia.common.item;

import dev.mrsterner.phasmophobia.common.Light;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;


public class FlashLightItem extends Item implements Light {
    public FlashLightItem(Settings settings) {
    super(settings);
    }
    @Override
    public void inventoryTick(ItemStack itemStack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!(entity instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) entity;
        if (!(player.getStackInHand(Hand.MAIN_HAND) == itemStack) && !(player.getStackInHand(Hand.OFF_HAND) == itemStack))
            return;
        createLight(itemStack, world, player);
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        worldIn.playSound(null, playerIn.getBlockPos(), SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 0.2F,2);
        return toggleLight(playerIn, handIn);
    }
}
