package dev.mrsterner.phasmophobia.client.renderer;

import dev.mrsterner.phasmophobia.common.block.entity.PlaceableBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class PlaceableBlockEntityRenderer implements BlockEntityRenderer<PlaceableBlockEntity> {
    @Override
    public void render(PlaceableBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Direction direction = entity.getCachedState().get(Properties.HORIZONTAL_FACING);
        float rotation = -direction.asRotation();
        matrices.translate(0.73f, 0F, 0.16f);
        matrices.scale(0.6F,0.6F,0.6F);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotation));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
        renderRow(entity.inventory.get(0), entity.inventory.get(2), 0.2f, matrices, vertexConsumers, light, overlay);
        renderRow(entity.inventory.get(1), entity.inventory.get(3), 0.0f, matrices, vertexConsumers, light, overlay);

    }

    private static void renderRow(ItemStack one, ItemStack two, float xOffset, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        xOffset *= 4;
        matrices.translate(xOffset, -1.0, 0);
        MinecraftClient.getInstance().getItemRenderer().renderItem(two, ModelTransformation.Mode.FIXED, light, overlay, matrices, vertexConsumers, 0);
        matrices.translate(0, 0.75, 0);
        MinecraftClient.getInstance().getItemRenderer().renderItem(one, ModelTransformation.Mode.FIXED, light, overlay, matrices, vertexConsumers, 0);
        matrices.pop();
    }
}
