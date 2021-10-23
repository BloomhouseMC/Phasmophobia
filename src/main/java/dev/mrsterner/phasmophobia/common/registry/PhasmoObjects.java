package dev.mrsterner.phasmophobia.common.registry;

import dev.mrsterner.phasmophobia.Phasmophobia;
import dev.mrsterner.phasmophobia.common.block.PlaceableBlock;
import dev.mrsterner.phasmophobia.common.block.entity.PlaceableBlockEntity;
import dev.mrsterner.phasmophobia.common.entity.RevenantEntity;
import dev.mrsterner.phasmophobia.common.item.CrucifixItem;
import dev.mrsterner.phasmophobia.common.item.FlashLightItem;
import dev.mrsterner.phasmophobia.common.item.Journal;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class PhasmoObjects {
    public static final Map<Block, Identifier> BLOCKS = new LinkedHashMap<>();
    public static final Map<Item, Identifier> ITEMS = new LinkedHashMap<>();
    private static final Map<BlockEntityType<?>, Identifier> BLOCK_ENTITY_TYPES = new LinkedHashMap<>();
    private static final Map<EntityType<?>, Identifier> ENTITY_TYPES = new LinkedHashMap<>();

    //Items
    public static final Item CRUCIFIX = register("crucifix", new CrucifixItem(gen().maxCount(1),(byte) 16));
    public static final Item FLASHLIGHT = register("flashlight", new FlashLightItem(gen().maxCount(1)));
    public static final Item JOURNAL = register("journal", new Journal(gen().maxCount(1)));

    //Blocks
    public static final Block PLACEABLE = register("placeable", new PlaceableBlock(FabricBlockSettings.copyOf(Blocks.STONE)), false);

    //Block Entities
    public static final BlockEntityType<PlaceableBlockEntity> PLACEABLE_BLOCK_ENTITY = register("placeable_block_entity", FabricBlockEntityTypeBuilder.create(PlaceableBlockEntity::new, PLACEABLE).build(null));

    //Entities
    public static final EntityType<RevenantEntity> REVENANT = create("revenant", FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, RevenantEntity::new).dimensions(EntityDimensions.changing(1f, 1.75f)).build());



    private static <T extends Entity> EntityType<T> create(String name, EntityType<T> type) {
        ENTITY_TYPES.put(type, new Identifier(Phasmophobia.MODID, name));
        return type;
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType<T> type) {
        BLOCK_ENTITY_TYPES.put(type, new Identifier(Phasmophobia.MODID, id));
        return type;
    }

    private static <T extends Block> T register(String name, T block, boolean createItem) {
        BLOCKS.put(block, new Identifier(Phasmophobia.MODID, name));
        if (createItem) {
            ITEMS.put(new BlockItem(block, gen()), BLOCKS.get(block));
        }
        return block;
    }

    private static <T extends Item> T register(String name, T item) {
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
        ENTITY_TYPES.keySet().forEach(entityType -> Registry.register(Registry.ENTITY_TYPE, ENTITY_TYPES.get(entityType), entityType));
        FabricDefaultAttributeRegistry.register(REVENANT, RevenantEntity.createMobAttributes());
    }
}
