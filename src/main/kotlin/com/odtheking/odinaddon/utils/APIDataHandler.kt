package com.odtheking.odinaddon.utils

import com.odtheking.odin.OdinMod.scope
import kotlinx.coroutines.launch
import java.io.File

object APIDataHandler {

    var bazaarData = mutableMapOf<String, BazaarQuickStatus>()
    var NEURepoNames = mutableMapOf<String, String>()

    data class BazaarAPIResponse(
        val products: Map<String, BazaarQuickStatus>,
        val success: Boolean
    )

    data class BazaarQuickStatus(
        val productId: String,
        val sellPrice: Double,
        val sellVolume: Long,
        val sellMovingWeek: Long,
        val sellOrders: Long,
        val buyPrice: Double,
        val buyVolume: Long,
        val buyMovingWeek: Long,
        val buyOrders: Long
    )

    // In the future, can upgrade this to an API that links to NEU Repo
    // that GETs the data for each item's common name
    // or investigate how the repo can exist tied to the project itself as a library

}