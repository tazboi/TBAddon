package com.odtheking.odinaddon.features.impl.dungeon

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.ColorSetting
import com.odtheking.odin.clickgui.settings.impl.DropdownSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.clickgui.settings.impl.SelectorSetting
import com.odtheking.odin.events.RenderEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.features.impl.render.Etherwarp
import com.odtheking.odin.utils.Color
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.capitalizeFirst
import com.odtheking.odin.utils.skyblock.dungeon.DungeonUtils
import com.odtheking.odin.utils.skyblock.dungeon.Floor
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

/*
*   Port of @odtheking DungeonWaypoints to work in Boss
*   Not currently implemented.
 */
object BossWaypoints : Module(
    name = "Boss Waypoints",
    description = "Custom Waypoints for Dungeon Boss."
) {
    private var allowEdits by BooleanSetting("Allow Edits", false, desc = "Allows boss waypoint editing.")
    private var allowMidair by BooleanSetting(
        "Allow Midair",
        false,
        desc = "Allows waypoints to be placed midair if they reach the end of distance without hitting a block."
    ).withDependency { BossWaypoints.allowEdits }
    private var reachColor by ColorSetting(
        "Reach Color",
        Color(0, 255, 213, 0.43f),
        true,
        desc = "Color of the reach box highlight."
    ).withDependency { BossWaypoints.allowEdits }
    private val allowTextEdit by BooleanSetting(
        "Allow Text Edit",
        true,
        desc = "Allows you to set the text of a waypoint while sneaking."
    ).withDependency { BossWaypoints.allowEdits }

    private val renderTitle by BooleanSetting("Render Title", true, desc = "Renders the titles of waypoints")
    private val titleScale by NumberSetting(
        "Title Scale",
        1f,
        0.1f,
        4f,
        increment = 0.1f,
        desc = "The scale of the titles of waypoints."
    ).withDependency { renderTitle }
    private val disableDepth by BooleanSetting(
        "Global Depth",
        false,
        desc = "Disables depth testing for all waypoints."
    )

    private val settingsDropDown by DropdownSetting("Next Waypoint Settings")
    var waypointType by SelectorSetting(
        "Waypoint Type",
        WaypointType.NONE.displayName,
        WaypointType.getArrayList(),
        desc = "The type of waypoint you want to place."
    ).withDependency { settingsDropDown }
    private val colorPallet by SelectorSetting(
        "Color pallet",
        "None",
        arrayListOf("None", "Aqua", "Magenta", "Yellow", "Lime", "Red"),
        desc = "The color pallet of the next waypoint you place."
    ).withDependency { settingsDropDown }
    var color by ColorSetting(
        "Color",
        Colors.MINECRAFT_GREEN,
        true,
        desc = "The color of the next waypoint you place."
    ).withDependency { colorPallet == 0 && settingsDropDown }
    var filled by BooleanSetting(
        "Filled",
        false,
        desc = "If the next waypoint you place should be 'filled'."
    ).withDependency { settingsDropDown }
    var depthCheck by BooleanSetting(
        "Depth check",
        false,
        desc = "Whether the next waypoint you place should have a depth check."
    ).withDependency { settingsDropDown }
    var useBlockSize by BooleanSetting(
        "Use block size",
        true,
        desc = "Use the size of the block you click for waypoint size."
    ).withDependency { settingsDropDown }
    var size by NumberSetting(
        "Size",
        1.0,
        .1,
        1.0,
        0.01,
        desc = "The size of the next waypoint you place."
    ).withDependency { !useBlockSize && settingsDropDown }

    private inline val selectedColor
        get() = when (BossWaypoints.colorPallet) {
            0 -> BossWaypoints.color
            1 -> Colors.MINECRAFT_DARK_AQUA
            2 -> Colors.MINECRAFT_DARK_PURPLE
            3 -> Colors.MINECRAFT_YELLOW
            4 -> Colors.MINECRAFT_GREEN
            5 -> Colors.MINECRAFT_RED
            else -> BossWaypoints.color
        }

    var lastEtherPos: BlockPos? = null
    var lastEtherTime = 0L


    init {
        on<RenderEvent.Last> {
            if (!DungeonUtils.inDungeons || !DungeonUtils.inBoss) return@on;

            //draw boxes for current floor's boss
        }
    }

    private inline val reachPosition: BlockPos?
        get() {
            val hitResult = mc.hitResult
            return when {
                hitResult?.type == HitResult.Type.MISS && !allowMidair -> Etherwarp.getEtherPos(
                    mc.player?.position(),
                    5.0
                ).pos

                hitResult is BlockHitResult -> hitResult.blockPos;
                else -> null;
            }
        }

    fun Floor.setWaypoints() {

    }


    enum class WaypointType {
        NONE, NORMAL, SECRET, ETHERWARP, BREAKER, NOBREAKER;

        inline val displayName get() = name.lowercase().capitalizeFirst();

        companion object {
            fun getArrayList() = ArrayList(entries.map { it.displayName })
            fun getByInt(i: Int) = entries.getOrNull(i).takeIf { it != NONE }
            fun getByName(name: String): WaypointType? = entries.find { it.name == name.uppercase() }
        }
    }

    data class BossWaypoint(
        val blockPos: BlockPos, val color: Color,
        val filled: Boolean, val depth: Boolean,
        val aabb: AABB, val title: String? = null,
        var type: WaypointType? = null,
        @Transient var isClicked: Boolean = false,
        @Transient var whitelist: Boolean = false
    )
    //add ability to add floor's boss waypoints via saving for specified floor

}