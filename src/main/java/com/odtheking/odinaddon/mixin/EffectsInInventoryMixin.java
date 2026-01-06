package com.odtheking.odinaddon.mixin;

import com.odtheking.odinaddon.features.impl.render.PlayerDisplayPlus;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectsInInventory.class)
public class EffectsInInventoryMixin {

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    private void stopRenderEffects(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {
        if (!PlayerDisplayPlus.INSTANCE.getEffects() && PlayerDisplayPlus.INSTANCE.getEnabled()) ci.cancel();
    }
}
