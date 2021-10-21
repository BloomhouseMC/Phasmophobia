package dev.mrsterner.phasmophobia.common.registry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RevenantSpecificSensor extends Sensor<LivingEntity> {
    public RevenantSpecificSensor() {
    }

    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
    }

    protected void sense(ServerWorld world, LivingEntity entity) {
        Brain<?> brain = entity.getBrain();
        Optional<MobEntity> optional = Optional.empty();
        List<LivingEntity> list2 = (List)brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse(ImmutableList.of());
        Iterator var7 = list2.iterator();

        while(var7.hasNext()) {
            LivingEntity livingEntity = (LivingEntity)var7.next();
            if (livingEntity instanceof WitherSkeletonEntity || livingEntity instanceof WitherEntity) {
                optional = Optional.of((MobEntity)livingEntity);
                break;
            }
        }

        List<LivingEntity> list3 = (List)brain.getOptionalMemory(MemoryModuleType.MOBS).orElse(ImmutableList.of());
        Iterator var11 = list3.iterator();

        brain.remember(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, optional);
    }
}

