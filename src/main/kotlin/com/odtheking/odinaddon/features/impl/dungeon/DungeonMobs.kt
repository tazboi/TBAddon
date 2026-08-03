package com.odtheking.odinaddon.features.impl.dungeon

import com.odtheking.odin.clickgui.settings.impl.ColorSetting
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.render.textDim
import com.odtheking.odin.utils.skyblock.dungeon.DungeonUtils
import com.odtheking.odinaddon.utils.EntityCollection
import com.odtheking.odinaddon.utils.getRoomData
import net.minecraft.world.entity.decoration.ArmorStand
import kotlin.collections.find

object DungeonMobs : Module(
    name = "Dungeon Mobs",
    description = "Modules for dungeon-mob related features"
) {
    private val bloodMobs by HUD("Giant HP Hud", desc = "Displays the health of the blood giant.") { example ->
        if (example) textDim("Giant Name Here", 0, 0, bloodMobsColor)
        else if (DungeonUtils.inDungeons && !bloodMobCollection.isEmpty()) {
            textDim(bloodMobCollection.find { it.removalReason == null }?.customName?.string ?: "", 0, 0, bloodMobsColor)
        }
        else return@HUD 0 to 0

    }

    private val bloodMobsColor by ColorSetting("Giant HP Hud Color", Colors.WHITE, desc = "Color to render the the Giant HP Hud")

    private val bloodMobCollection = EntityCollection( {
        //Only giant for now
        //TODO other blood mob names/types (golden, stealthy, etc.)
        DungeonUtils.inDungeons &&
                it is ArmorStand &&
                getRoomData(it.x.toInt(), it.z.toInt())?.name == "Blood" &&
                (it.customName?.string?.contains("Giant") ?: false)
   } )


}