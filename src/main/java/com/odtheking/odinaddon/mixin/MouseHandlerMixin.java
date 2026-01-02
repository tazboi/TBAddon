package com.odtheking.odinaddon.mixin;


import com.mojang.blaze3d.platform.InputConstants;
import com.odtheking.odin.OdinMod;
import com.odtheking.odinaddon.features.impl.skyblock.event.MouseEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.odtheking.odin.utils.ChatUtilsKt.modMessage;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    private void prePress(long window, int button, int action, int modifier, CallbackInfo ci) {
        if (Minecraft.getInstance().screen != null) return;
        fireEvent(button, action, modifier, ci);
    }

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void preScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (new MouseEvent.Scroll(horizontal, vertical).postAndCatch()) ci.cancel();
    }

    @Unique
    private static void fireEvent(int button, int action, int modifier, CallbackInfo ci) {
        InputConstants.Key key = InputConstants.getKey(button, modifier);
        if (action == 1) {
            if (new MouseEvent.Click(key.getValue()).postAndCatch()) ci.cancel();
        } else {
            if (new MouseEvent.Release(key.getValue()).postAndCatch()) ci.cancel();
        }

    }
}
