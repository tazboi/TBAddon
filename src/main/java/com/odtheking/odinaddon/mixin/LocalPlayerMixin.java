package com.odtheking.odinaddon.mixin;

import com.odtheking.odinaddon.features.impl.skyblock.event.DropEvent;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void preDrop(boolean bl, CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = (LocalPlayer) (Object) this;

        if (new DropEvent(player.getInventory().getSelectedItem(), bl).postAndCatch()) cir.setReturnValue(false);
    }
}
