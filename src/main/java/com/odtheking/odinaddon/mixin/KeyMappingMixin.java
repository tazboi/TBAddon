package com.odtheking.odinaddon.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.odtheking.odinaddon.features.impl.skyblock.event.InputReleaseEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {

    @Shadow
    private boolean isDown;
    @Shadow
    protected InputConstants.Key key;

    @Shadow
    private int clickCount;

    @Inject(
            method = "setDown",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preRelease(boolean bl, CallbackInfo ci) {
        if (!this.isDown || bl) return;
        if (new InputReleaseEvent(this.key).postAndCatch()) ci.cancel();
    }

    @Inject(
            method = "consumeClick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/KeyMapping;clickCount:I",
                    opcode = org.objectweb.asm.Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            )
    )
    private void onClick(CallbackInfoReturnable<Boolean> cir) {

        if (this.clickCount == 0 && !this.isDown) new InputReleaseEvent(this.key).postAndCatch();
    }
}
