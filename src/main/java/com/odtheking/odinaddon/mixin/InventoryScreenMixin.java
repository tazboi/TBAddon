package com.odtheking.odinaddon.mixin;

import com.odtheking.odinaddon.features.impl.render.RenderModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin  {

    @ModifyArgs(
            method = "extractBackground",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;extractEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V"
            )
    )
    private static void tbaddon$scaleInventoryPlayer(Args args) {
        if (!RenderModifier.INSTANCE.getEnabled()) return;

        Screen screen = Minecraft.getInstance().screen;
        if (!(screen instanceof InventoryScreen)) {
            return;
        }

        int width = screen.width;
        int height = screen.height;

        double scale = RenderModifier.INSTANCE.getCustomInventoryScale();

        int x0 = args.get(1);
        int y0 = args.get(2);
        int x1 = args.get(3);
        int y1 = args.get(4);
        int size = args.get(5);

        float mouseX = args.get(7);
        float mouseY = args.get(8);

        args.set(1, (int) Math.round(
                RenderModifier.INSTANCE.transformInventoryRender(x0, width)
        ));
        args.set(2, (int) Math.round(
                RenderModifier.INSTANCE.transformInventoryRender(y0, height)
        ));
        args.set(3, (int) Math.round(
                RenderModifier.INSTANCE.transformInventoryRender(x1, width)
        ));
        args.set(4, (int) Math.round(
                RenderModifier.INSTANCE.transformInventoryRender(y1, height)
        ));

        args.set(5, (int) Math.round(size * scale));

        // Mouse needs to stay in the same logical space as the transformed bounds
        args.set(7, (float) RenderModifier.INSTANCE.transformInventoryRender(
                mouseX,
                width
        ));

        args.set(8, (float) RenderModifier.INSTANCE.transformInventoryRender(
                mouseY,
                height
        ));
    }
}
