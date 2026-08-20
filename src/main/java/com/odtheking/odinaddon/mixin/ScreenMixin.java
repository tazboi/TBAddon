package com.odtheking.odinaddon.mixin;

import com.odtheking.odinaddon.features.impl.render.RenderModifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Screen.class)
public class ScreenMixin {
    @Shadow
    public int width;

    @Shadow
    public int height;

    //Scale graphics pose to custom dimensions
    @Inject(
            method = "extractRenderStateWithTooltipAndSubtitles",
            at = @At("HEAD")
    )
    private void customScale$push(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick,
            CallbackInfo ci
    ) {
        RenderModifier.INSTANCE.scaleGraphics(graphics, (Screen) (Object) this);
    }
    //Unscale at beginning of rendering background
    @Inject(
            method = "extractBackground",
            at = @At("HEAD")
    )
    private void preBackground$pop(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        RenderModifier.INSTANCE.unscaleGraphics(graphics, (Screen) (Object) this);
    }
    //Rescale graphics to custom size
    @Inject(
            method = "extractBackground",
            at = @At("TAIL")
    )
    private void postBackground$push(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        RenderModifier.INSTANCE.scaleGraphics(graphics, (Screen) (Object) this);
    }

    //pop graphics pose to prevent unnecessary scale/transformations
    @Inject(
            method = "extractRenderStateWithTooltipAndSubtitles",
            at = @At("TAIL")
    )
    private void customScale$pop(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick,
            CallbackInfo ci
    ) {
        RenderModifier.INSTANCE.unscaleGraphics(graphics, (Screen) (Object) this);
    }

    //Render tooltips relative to the scaled mouse location
    @ModifyArgs(
            method = "extractRenderStateWithTooltipAndSubtitles",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"
            )
    )
    private void tbaddon$scaleRenderMouse(Args args) {
        if (!RenderModifier.INSTANCE.getEnabled()) return;
        if (!((Object) this instanceof AbstractContainerScreen<?>)) {
            return;
        }

        int mouseX = args.get(1);
        int mouseY = args.get(2);

        args.set(1, (int) Math.round(
                RenderModifier.INSTANCE.transformInventoryMouse(mouseX, this.width)
        ));

        args.set(2, (int) Math.round(
                RenderModifier.INSTANCE.transformInventoryMouse(mouseY, this.height)
        ));
    }


}

