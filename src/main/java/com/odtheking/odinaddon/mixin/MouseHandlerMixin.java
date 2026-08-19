package com.odtheking.odinaddon.mixin;


import com.mojang.blaze3d.platform.InputConstants;
import com.odtheking.odin.OdinMod;
import com.odtheking.odin.events.GuiEvent;
import com.odtheking.odinaddon.features.impl.render.RenderModifier;
import com.odtheking.odinaddon.features.impl.skyblock.event.MouseEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static com.odtheking.odin.utils.ChatUtilsKt.modMessage;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "onButton", at = @At("HEAD"), cancellable = true)
    private void prePress(long window, MouseButtonInfo mouseButtonInfo, int action, CallbackInfo ci) {
        if (Minecraft.getInstance().screen != null) return;
        fireEvent(mouseButtonInfo.button(), action, mouseButtonInfo.modifiers(), ci);
    }

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void preScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (new MouseEvent.Scroll(horizontal, vertical).postAndCatch()) ci.cancel();
    }

    @Unique
    private static void fireEvent(int button, int action, int modifier, CallbackInfo ci) {
        InputConstants.Key key = InputConstants.Type.MOUSE.getOrCreate(button);
        if (action == 1) {
            if (new MouseEvent.Click(key.getValue()).postAndCatch()) ci.cancel();
        } else {
            if (new MouseEvent.Release(key.getValue()).postAndCatch()) ci.cancel();
        }

    }

    //Transform clicks to where they are on modified (custom) inventory sizes
    @ModifyArgs(
            method = "onButton",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/input/MouseButtonEvent;<init>(DDLnet/minecraft/client/input/MouseButtonInfo;)V"
            )
    )
    private void tbaddon$transformContainerMouse(Args args) {
        if (!RenderModifier.INSTANCE.getEnabled()) return;

        Screen screen = this.minecraft.screen;
        if (!(screen instanceof AbstractContainerScreen<?>)) {
            return;
        }

        double mouseX = args.get(0);
        double mouseY = args.get(1);

        //mouseX, mouseY
        args.set(0,
                RenderModifier.INSTANCE.transformInventoryMouse(mouseX, screen.width)
        );
        args.set(1,
                RenderModifier.INSTANCE.transformInventoryMouse(mouseY, screen.height)
        );
    }

    //For niche use case of clicking + dragging to interpolate the mouse location relative to custom UI size
    @ModifyArgs(
            method = "handleAccumulatedMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;mouseDragged(Lnet/minecraft/client/input/MouseButtonEvent;DD)Z"
            )
    )
    private void tbaddon$transformContainerDrag(Args args) {
        if (!RenderModifier.INSTANCE.getEnabled()) return;

        Screen screen = this.minecraft.screen;
        if (!(screen instanceof AbstractContainerScreen<?>)) return;

        MouseButtonEvent event = args.get(0);

        MouseButtonEvent transformed = new MouseButtonEvent(
                RenderModifier.INSTANCE.transformInventoryMouse(event.x(), screen.width),
                RenderModifier.INSTANCE.transformInventoryMouse(event.y(), screen.height),
                event.buttonInfo()
        );

        args.set(0, transformed);

        double dragX = args.get(1);
        double dragY = args.get(2);

        args.set(1,
                RenderModifier.INSTANCE.transformInventoryDelta(dragX)
        );

        args.set(2,
                RenderModifier.INSTANCE.transformInventoryDelta(dragY)
        );
    }

}

