package com.odtheking.odinaddon.mixin;


import com.odtheking.odinaddon.features.impl.render.RenderModifier;
import net.minecraft.client.Camera;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {

    // Can probably do more with this by setting the hitResult with @At("INVOKE")
    // and not force to have BlockClip on to make the camera clip correctly, but I'm lazy atm
    @Inject(method = "getMaxZoom", at = @At("HEAD"), cancellable = true)
    private void preGetMaxZoom(float f, CallbackInfoReturnable<Float> cir) {
        if (RenderModifier.INSTANCE.getCameraBlockClip() && RenderModifier.INSTANCE.getEnabled()) cir.setReturnValue(f * RenderModifier.INSTANCE.getCameraMaxZoom());
    }

    @Inject(method = "getFluidInCamera", at = @At("HEAD"), cancellable = true)
    private void preGetFog(CallbackInfoReturnable<FogType> cir) {
        if (RenderModifier.INSTANCE.getWaterOverlay() && RenderModifier.INSTANCE.getEnabled()) cir.setReturnValue(FogType.NONE);
    }
}
