package dev.mrsterner.phasmophobia.common.registry;


import dev.mrsterner.phasmophobia.common.entity.ai.RevenantSpecificSensor;
import dev.mrsterner.phasmophobia.mixin.SensorTypeMixin;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PhasmoBrains {
    public static final Map<SensorType, Identifier> SENSOR = new LinkedHashMap<>();

    public static final SensorType<RevenantSpecificSensor> REVENANT_SPECIFIC_SENSOR = register("revenant_specific_sensor", RevenantSpecificSensor::new);
    private static <U extends Sensor<?>> SensorType register(String id, Supplier<U> factory) {
        return (SensorType) Registry.register(Registry.SENSOR_TYPE, new Identifier(id), SensorTypeMixin.newSensorType(factory));
    }
    public static void init(){
        SENSOR.keySet().forEach(sensorType -> Registry.register(Registry.SENSOR_TYPE, SENSOR.get(sensorType), sensorType));
    }
}
