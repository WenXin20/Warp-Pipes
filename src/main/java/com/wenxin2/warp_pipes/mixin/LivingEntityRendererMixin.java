package com.wenxin2.warp_pipes.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.warp_pipes.client.PipeTravelRotations;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @WrapOperation(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;setupRotations(Lnet/minecraft/world/entity/LivingEntity;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V"),
            require = 0)
    private void wp$setupPipeRotations(LivingEntityRenderer<T, M> renderer, T entity, PoseStack poseStack, float bob, float bodyRot,
                                       float partialTick, float scale, Operation<Void> original) {
        if (!PipeTravelRotations.applyRotations(entity, poseStack, partialTick, scale))
            original.call(renderer, entity, poseStack, bob, bodyRot, partialTick, scale);
    }
}
