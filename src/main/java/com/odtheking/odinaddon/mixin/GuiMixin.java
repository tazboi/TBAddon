package com.odtheking.odinaddon.mixin;

import com.odtheking.odinaddon.features.impl.render.PlayerDisplayPlus;
import com.odtheking.odinaddon.features.impl.skyblock.event.HotbarSlotRenderEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Gui.class)
public class GuiMixin {

    @Inject(method = "renderSlot", at = @At("HEAD"), cancellable = true)
    private void preRender(GuiGraphics guiGraphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack itemStack, int seed, CallbackInfo ci) {
       if (new HotbarSlotRenderEvent(guiGraphics, x, y, itemStack).postAndCatch()) ci.cancel();
    }

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    private void stopRenderEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!PlayerDisplayPlus.INSTANCE.getEffects() && PlayerDisplayPlus.INSTANCE.getEnabled()) ci.cancel();
    }

    @Inject(method = "renderTitle", at = @At("HEAD"), cancellable = true)
    private void stopRenderTitle(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!PlayerDisplayPlus.INSTANCE.getTitles() && PlayerDisplayPlus.INSTANCE.getEnabled()) ci.cancel();
    }

    @Inject(method = "renderVignette", at = @At("HEAD"), cancellable = true)
    private void stopRenderVignette(GuiGraphics guiGraphics, Entity entity, CallbackInfo ci) {
        if (!PlayerDisplayPlus.INSTANCE.getVignette() && PlayerDisplayPlus.INSTANCE.getEnabled()) ci.cancel();
    }

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void stopRenderPortalOverlay(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        if (!PlayerDisplayPlus.INSTANCE.getPortal() && PlayerDisplayPlus.INSTANCE.getEnabled()) ci.cancel();
    }

    @Inject(method = "renderConfusionOverlay", at = @At("HEAD"), cancellable = true)
    private void stopRenderNausea(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        if (!PlayerDisplayPlus.INSTANCE.getNausea() && PlayerDisplayPlus.INSTANCE.getEnabled()) ci.cancel();
    }
}
