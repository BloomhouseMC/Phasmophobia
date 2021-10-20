package dev.mrsterner.phasmophobia.common.item;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;


public class FlashLightItem extends Item {
    public FlashLightItem(Settings settings) {
    super(settings);
    }
    public static BooleanProperty on;


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 0.2F,2);
        if (world.isClient()) return super.use(world, user, hand);

        ServerPlayNetworking.send((ServerPlayerEntity) user, new Identifier("phasmophobia:flashlight"), PacketByteBufs.empty());
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}
