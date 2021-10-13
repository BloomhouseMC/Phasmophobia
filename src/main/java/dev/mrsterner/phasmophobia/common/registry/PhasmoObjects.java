package dev.mrsterner.phasmophobia.common.registry;

import dev.mrsterner.phasmophobia.Phasmophobia;
import dev.mrsterner.phasmophobia.common.block.BlockLight;
import dev.mrsterner.phasmophobia.common.block.BlockLightEntity;
import dev.mrsterner.phasmophobia.common.item.CrusifixItem;
import dev.mrsterner.phasmophobia.common.item.FlashLightItem;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class PhasmoObjects {
    public static final Map<Block, Identifier> BLOCKS = new LinkedHashMap<>();
    public static final Map<Item, Identifier> ITEMS = new LinkedHashMap<>();
    private static final Map<BlockEntityType<?>, Identifier> BLOCK_ENTITY_TYPES = new LinkedHashMap<>();

    //Items
    public static final Item CRUSIFIX = create("crusifix", new CrusifixItem(gen()));
    public static final Item FLASHLIGHT = create("flashlight", new FlashLightItem(gen()));

    //Blocks
    public static final Block BLOCK_LIGHT = create("block_light", new BlockLight(FabricBlockSettings.copyOf(Blocks.AIR)), true);
    //Block Entities
    public static final BlockEntityType<BlockLightEntity> BLOCK_LIGHT_ENTITY = register("block_light_entity", FabricBlockEntityTypeBuilder.create(BlockLightEntity::new,BLOCK_LIGHT).build(null));



    public static <T extends Block> T register(String id, T block) {
        BLOCKS.put(block, new Identifier(Phasmophobia.MODID, id));
        return block;
    }

    public static <T extends Block> T register(String id, T block, Boolean hasBlockItem, ItemGroup itemGroup) {
        BLOCKS.put(block, new Identifier(Phasmophobia.MODID, id));
        if (hasBlockItem) {
            ITEMS.put(new BlockItem(block, gen()), BLOCKS.get(block));
        }
        return block;
    }
    private static <T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType<T> type) {
        BLOCK_ENTITY_TYPES.put(type, new Identifier(Phasmophobia.MODID, id));
        return type;
    }

    private static <T extends Block> T create(String name, T block, boolean createItem) {
        BLOCKS.put(block, new Identifier(Phasmophobia.MODID, name));
        if (createItem) {
            ITEMS.put(new BlockItem(block, gen()), BLOCKS.get(block));
        }
        return block;
    }

    private static <T extends Item> T create(String name, T item) {
        ITEMS.put(item, new Identifier(Phasmophobia.MODID, name));
        return item;
    }

    private static Item.Settings gen() {
        return new Item.Settings().group(Phasmophobia.PHASMOPHOBIA_GROUP);
    }

    public static void init() {
        BLOCKS.keySet().forEach(block -> Registry.register(Registry.BLOCK, BLOCKS.get(block), block));
        BLOCK_ENTITY_TYPES.keySet().forEach(blockEntityType -> Registry.register(Registry.BLOCK_ENTITY_TYPE, BLOCK_ENTITY_TYPES.get(blockEntityType), blockEntityType));
        ITEMS.keySet().forEach(item -> Registry.register(Registry.ITEM, ITEMS.get(item), item));
    }
}
