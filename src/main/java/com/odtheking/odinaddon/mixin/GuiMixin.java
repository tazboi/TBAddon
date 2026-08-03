package com.odtheking.odinaddon.mixin;

import com.odtheking.odinaddon.features.impl.render.PlayerDisplayPlus;
import com.odtheking.odinaddon.features.impl.skyblock.event.HotbarSlotRenderEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Inject(method = "extractSlot", at = @At("HEAD"), cancellable = true)
    private void preRender(GuiGraphicsExtractor graphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack itemStack, int seed, CallbackInfo ci) {
       if (new HotbarSlotRenderEvent(graphics, x, y, itemStack).postAndCatch()) ci.cancel();
    }

    @Inject(method = "extractEffects", at = @At("HEAD"), cancellable = true)
    private void stopRenderEffects(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!PlayerDisplayPlus.INSTANCE.getEffects() && PlayerDisplayPlus.INSTANCE.getEnabled()) ci.cancel();
    }

    @Inject(method = "extractTitle", at = @At("HEAD"), cancellable = true)
    private void stopRenderTitle(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!PlayerDisplayPlus.INSTANCE.getTitles() && PlayerDisplayPlus.INSTANCE.getEnabled()) ci.cancel();
    }

    @Inject(method = "extractVignette", at = @At("HEAD"), cancellable = true)
    private void stopRenderVignette(GuiGraphicsExtractor graphics, Entity camera, CallbackInfo ci) {
        if (!PlayerDisplayPlus.INSTANCE.getVignette() && PlayerDisplayPlus.INSTANCE.getEnabled()) ci.cancel();
    }

    @Inject(method = "extractPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void stopRenderPortalOverlay(GuiGraphicsExtractor graphics, float alpha, CallbackInfo ci) {
        if (!PlayerDisplayPlus.INSTANCE.getPortal() && PlayerDisplayPlus.INSTANCE.getEnabled()) ci.cancel();
    }

    @Inject(method = "extractConfusionOverlay", at = @At("HEAD"), cancellable = true)
    private void stopRenderNausea(GuiGraphicsExtractor graphics, float strength, CallbackInfo ci) {
        if (!PlayerDisplayPlus.INSTANCE.getNausea() && PlayerDisplayPlus.INSTANCE.getEnabled()) ci.cancel();
    }
}
