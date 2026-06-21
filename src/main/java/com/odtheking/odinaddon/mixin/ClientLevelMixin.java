package com.odtheking.odinaddon.mixin;

import com.odtheking.odinaddon.features.impl.skyblock.event.EntityWorldEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Shadow
    public abstract @Nullable Entity getEntity(int i);

    @Inject(
            method ="addEntity",
            at = @At("TAIL")
    )
    void postAddEntity(Entity entity, CallbackInfo ci) {
        new EntityWorldEvent.Join(entity, (ClientLevel) (Object) this).postAndCatch();
    }

    @Inject(
            method ="removeEntity",
            at = @At("TAIL")
    )
    void postRemoveEntity(int entityId, Entity.RemovalReason reason, CallbackInfo ci) {
        Entity entity = this.getEntity(entityId);
        if (entity == null) return;

        new EntityWorldEvent.Leave(entity, (ClientLevel) (Object) this).postAndCatch();
    }
}
