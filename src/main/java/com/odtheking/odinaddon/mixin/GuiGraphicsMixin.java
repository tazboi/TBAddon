package com.odtheking.odinaddon.mixin;

import com.odtheking.odinaddon.features.impl.render.VisualWords;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    @ModifyVariable
            (
                    method = "drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;IIIZ)V",
                    at = @At("HEAD"),
                    argsOnly = true,
                    index = 2
            )
    private FormattedCharSequence modifySequence(FormattedCharSequence original) {
        if (!VisualWords.INSTANCE.getEnabled()) return original;
        return VisualWords.INSTANCE.modify(original);
    }

}
