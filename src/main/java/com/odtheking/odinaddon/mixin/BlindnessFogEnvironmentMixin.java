package com.odtheking.odinaddon.mixin;

import com.odtheking.odinaddon.features.impl.render.RenderModifier;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.BlindnessFogEnvironment;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlindnessFogEnvironment.class)
public class BlindnessFogEnvironmentMixin {

   @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
   private void stopBlindness(FogData fogData, Entity entity, BlockPos blockPos, ClientLevel level, float f, DeltaTracker tickCounter, CallbackInfo ci) {
       if (!RenderModifier.INSTANCE.getEnabled() || !RenderModifier.INSTANCE.getNoBlindness()) return;
       fogData.environmentalStart = f;
       fogData.environmentalEnd = f;
       fogData.renderDistanceStart = f;
       fogData.renderDistanceEnd = f;
       fogData.skyEnd = f;
       fogData.cloudEnd = f;
       ci.cancel();
   }

    @Inject(method = "getModifiedDarkness", at = @At("HEAD"), cancellable = true)
    private void setModifiedDarkness(LivingEntity livingEntity, float darkness, float tickDelta, CallbackInfoReturnable<Float> cir) {
       if (RenderModifier.INSTANCE.getEnabled() && RenderModifier.INSTANCE.getNoBlindness()) cir.setReturnValue(0.0f);
    }


}
