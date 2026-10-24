package com.backpackinv.mixin;

import com.backpackinv.client.BackpackInvConfig;
import com.backpackinv.client.InventoryAnimation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract float calculateMapTilt(float pitch);

    @Shadow protected abstract void renderMapHand(PoseStack poseStack, MultiBufferSource buffer, int packedLight, HumanoidArm side);

    @Inject(method = "renderHandsWithItems", at = @At("HEAD"), cancellable = true)
    private void backpackinv$renderBackpack(float partialTicks, PoseStack poseStack, MultiBufferSource.BufferSource buffer,
                                            LocalPlayer player, int light, CallbackInfo ci) {
        if (!InventoryAnimation.isActive()) {
            return;
        }

        BackpackInvConfig cfg = InventoryAnimation.config();
        float progress = InventoryAnimation.progress(partialTicks);
        float pitch = Mth.lerp(partialTicks, player.xRotO, player.getXRot());
        float tilt = cfg.useVanillaTilt ? this.calculateMapTilt(pitch) : cfg.fixedTilt;

        poseStack.pushPose();

        // та же поза, в которой ваниль держит карту двумя руками
        poseStack.translate(0.0F, 0.04F + (1.0F - progress) * -1.2F + tilt * -0.5F, -0.72F);
        poseStack.mulPose(Axis.XP.rotationDegrees(tilt * -85.0F));

        if (!player.isInvisible()) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            this.renderMapHand(poseStack, buffer, light, HumanoidArm.RIGHT);
            this.renderMapHand(poseStack, buffer, light, HumanoidArm.LEFT);
            poseStack.popPose();
        }

        // рюкзак между руками
        poseStack.pushPose();
        poseStack.translate(cfg.offsetX, cfg.offsetY, cfg.offsetZ);
        poseStack.mulPose(Axis.XP.rotationDegrees(cfg.rotX));
        poseStack.mulPose(Axis.YP.rotationDegrees(cfg.rotY));
        poseStack.mulPose(Axis.ZP.rotationDegrees(cfg.rotZ));
        poseStack.scale(cfg.scale, cfg.scale, cfg.scale);
        poseStack.translate(0.0F, 0.21875F, 0.0F); // центрируем модель по высоте
        this.minecraft.getItemRenderer().renderStatic(
                InventoryAnimation.propStack(),
                ItemDisplayContext.FIXED,
                light,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                this.minecraft.level,
                0
        );
        poseStack.popPose();

        poseStack.popPose();
        buffer.endBatch();
        ci.cancel();
    }
}
