package com.odtheking.odinaddon.mixin;

import com.odtheking.odinaddon.features.impl.skyblock.event.EntityWorldEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    @Inject(
            method ="addEntity",
            at = @At("TAIL"),
            cancellable = true
    )
    void postAddEntity(Entity entity, CallbackInfo ci) {
        if (new EntityWorldEvent.Join(entity, (ClientLevel) (Object) this).postAndCatch()) ci.cancel();
    }
}
