package com.odtheking.odinaddon.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.odtheking.odinaddon.features.impl.render.RenderModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {


    @Inject(method = "renderWater", at = @At("HEAD"), cancellable = true)
    private static void preRenderWater(Minecraft minecraft, PoseStack poseStack, MultiBufferSource multiBufferSource, CallbackInfo ci) {
        if (RenderModifier.INSTANCE.getWaterOverlay()) ci.cancel();
    }

    @Inject(method = "getViewBlockingState", at = @At("HEAD"), cancellable = true)
    private static void preGetBlockingState(Player player, CallbackInfoReturnable<BlockState> cir) {
        if (RenderModifier.INSTANCE.getCameraBlockClip()) cir.setReturnValue(null);
    }
}
