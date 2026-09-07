package com.odtheking.odinaddon.mixin;

import com.odtheking.odin.features.impl.dungeon.map.MapRendererKt;
import com.odtheking.odin.features.impl.dungeon.map.tile.DungeonDoor;
import com.odtheking.odin.features.impl.dungeon.map.tile.DungeonRoom;
import com.odtheking.odin.features.impl.dungeon.map.tile.DungeonTile;
import com.odtheking.odinaddon.features.impl.dungeon.SillyMap;
import com.odtheking.odinaddon.utils.render.OdinCustomMapDispatcherKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(MapRendererKt.class)
public abstract class MapRendererMixin {


    @Inject(
            method = "renderMap",
            at = @At(
                value = "INVOKE",
                target = "Lcom/odtheking/odin/features/impl/dungeon/map/MapRendererKt;renderDoors(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Ljava/util/Collection;)V",
                shift = Shift.AFTER
            ),
            cancellable = false
    )
    private static void tbaddon$renderNonVisibleRooms(GuiGraphicsExtractor $this$renderMap, Collection<DungeonRoom> rooms, Collection<DungeonDoor> doors, Collection<DungeonTile> pathHints, CallbackInfo ci) {
        if(!SillyMap.INSTANCE.getEnabled()) return;

        for (DungeonRoom room : rooms) {
            OdinCustomMapDispatcherKt.customRenderRooms($this$renderMap, room);
        }
    }

    @Inject(
            method = "renderMap",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/odtheking/odin/features/impl/dungeon/map/MapRendererKt;renderPathHints(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Ljava/util/Collection;)V"
            ),
            cancellable = true
    )
    private static void tbaddon$onRenderPathHints(GuiGraphicsExtractor $this$renderMap, Collection<DungeonRoom> rooms, Collection<DungeonDoor> doors, Collection<DungeonTile> pathHints, CallbackInfo ci) {
        if(!SillyMap.INSTANCE.getEnabled()) return;

        ci.cancel();
    }

    @Inject(
            method = "renderRoomText",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void tbaddon$customRenderRoomText(GuiGraphicsExtractor $this$renderRoomText, DungeonRoom room, CallbackInfo ci) {
        if(!SillyMap.INSTANCE.getEnabled()) return;

        OdinCustomMapDispatcherKt.customRenderRoomText($this$renderRoomText, room);
        ci.cancel();
    }
}
