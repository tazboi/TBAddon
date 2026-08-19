package com.odtheking.odinaddon.mixin;

import com.odtheking.odinaddon.features.impl.skyblock.event.SlotInteractEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin{

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void preClick(Slot slot, int slotId, int buttonNum, ContainerInput containerInput, CallbackInfo ci) {
        if (slot == null) return;
        if (new SlotInteractEvent((Screen) (Object) this, slotId, buttonNum, containerInput).postAndCatch()) ci.cancel();
    }
}
