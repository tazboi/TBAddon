package com.odtheking.odinaddon.commands

import com.github.stivais.commodore.Commodore
import com.odtheking.odin.OdinMod
import com.odtheking.odin.features.ModuleManager
import com.odtheking.odin.utils.itemId
import com.odtheking.odin.utils.itemUUID
import com.odtheking.odin.utils.modMessage
import com.odtheking.odinaddon.features.impl.skyblock.ProtectItem
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack


val protectItemCommand = Commodore("protectitem", "pi") {
    runs {
        val item = OdinMod.mc.player?.mainHandItem.takeUnless { it == ItemStack.EMPTY } ?: return@runs modMessage("Please hold a valid item.")

        val protectedItem = ProtectItem.ProtectedItem(item.customName?.string, item.itemId, item.itemUUID)
        val itemList = ProtectItem.itemList
        val foundItem = if (protectedItem.uuid.isNullOrEmpty()) itemList.find { protectedItem.sbID == it.sbID }
        else itemList.find { protectedItem.uuid == it.uuid }
        (foundItem)?.let {
            itemList.remove(it)
            modMessage(
                Component.literal("Removed ")
                    .append(item.customName ?: Component.literal(it.sbID))
                    .append(Component.literal(" from protection whitelist."))
            )
            ModuleManager.saveConfigurations()
            return@runs
        }

        itemList.add(protectedItem)
        modMessage(
            Component.literal("Added ")
                .append(item.customName ?: Component.literal(protectedItem.sbID))
                .append(Component.literal(" to protection whitelist."))
        )
        ModuleManager.saveConfigurations()

    }

    literal("protectall", "pa") {
        runs {
            val itemList = ProtectItem.itemList
            OdinMod.mc.player?.inventory?.forEach { item ->
                if (item == ItemStack.EMPTY) return@forEach

                val protectedItem = ProtectItem.ProtectedItem(item.customName?.string, item.itemId, item.itemUUID)
                val foundItem = if (protectedItem.uuid.isNullOrEmpty()) itemList.find { protectedItem.sbID == it.sbID }
                else itemList.find { protectedItem.uuid == it.uuid }
                if (foundItem != null) return@forEach; //Prevent adding duplicates

                itemList.add(protectedItem);
            }

            modMessage("Added current inventory to protection whitelist.")
            ModuleManager.saveConfigurations()
        }
    }

    literal("removeall", "ra") {
        runs {
            val itemList = ProtectItem.itemList
            OdinMod.mc.player?.inventory?.forEach { item ->
                if (item == ItemStack.EMPTY) return@forEach

                val protectedItem = ProtectItem.ProtectedItem(item.customName?.string, item.itemId, item.itemUUID)
                val itemList = ProtectItem.itemList

                val foundItem = if (protectedItem.uuid.isNullOrEmpty()) itemList.find { protectedItem.sbID == it.sbID }
                else itemList.find { protectedItem.uuid == it.uuid }

                (foundItem)?.let {
                    itemList.remove(it)
                    ModuleManager.saveConfigurations()
                }
            }

            modMessage("Removed current inventory from protection whitelist.")
            ModuleManager.saveConfigurations()
        }
    }
}