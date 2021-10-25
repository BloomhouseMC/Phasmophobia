package dev.mrsterner.phasmophobia.common.registry;

import dev.mrsterner.phasmophobia.Phasmophobia;
import dev.mrsterner.phasmophobia.common.entity.*;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.mixin.object.builder.SpawnRestrictionAccessor;
import net.minecraft.entity.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class PhasmoEntities {
    public static final Map<EntityType<?>, Identifier> ENTITY_TYPES = new LinkedHashMap<>();

    public static final EntityType<RevenantEntity> REVENANT = create("revenant", FabricEntityTypeBuilder.create(
        SpawnGroup.MONSTER, RevenantEntity::new).dimensions(EntityDimensions.changing(1f, 1.75f)).build());
    public static final EntityType<OniEntity> ONI = create("oni", FabricEntityTypeBuilder.create(
        SpawnGroup.MONSTER, OniEntity::new).dimensions(EntityDimensions.changing(1f, 1.75f)).build());
    public static final EntityType<MareEntity> MARE = create("mare", FabricEntityTypeBuilder.create(
        SpawnGroup.MONSTER, MareEntity::new).dimensions(EntityDimensions.changing(1f, 1.75f)).build());
    public static final EntityType<BansheeEntity> BANSHEE = create("banshee", FabricEntityTypeBuilder.create(
        SpawnGroup.MONSTER, BansheeEntity::new).dimensions(EntityDimensions.changing(1f, 1.75f)).build());


    private static <T extends Entity> EntityType<T> create(String name, EntityType<T> type) {
        ENTITY_TYPES.put(type, new Identifier(Phasmophobia.MODID, name));
        return type;
    }

    private static void registerEntitySpawn(EntityType<?> type, Predicate<BiomeSelectionContext> predicate, int weight, int minGroupSize, int maxGroupSize) {
        BiomeModifications.addSpawn(predicate, type.getSpawnGroup(), type, weight, minGroupSize, maxGroupSize);
    }

    public static void init() {
        ENTITY_TYPES.keySet().forEach(entityType -> Registry.register(Registry.ENTITY_TYPE, ENTITY_TYPES.get(entityType), entityType));
        FabricDefaultAttributeRegistry.register(REVENANT, BaseGhostEntity.createMobAttributes());
        registerEntitySpawn(REVENANT, BiomeSelectors.foundInOverworld().and(context -> !context.getBiome().getSpawnSettings().getSpawnEntries(REVENANT.getSpawnGroup()).isEmpty()), 10, 1, 1);
        SpawnRestrictionAccessor.callRegister(REVENANT, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RevenantEntity::canSpawnInDark);
    }
}
