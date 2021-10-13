package dev.mrsterner.phasmophobia;

import dev.mrsterner.phasmophobia.common.registry.PhasmoObjects;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;

public class Phasmophobia implements ModInitializer
{
    public static final String MODID = "phasmophobia";
    public static final ItemGroup PHASMOPHOBIA_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID), () -> new ItemStack(PhasmoObjects.CRUSIFIX));

    @Override
    public void onInitialize() {
        PhasmoObjects.init();
    }
}
