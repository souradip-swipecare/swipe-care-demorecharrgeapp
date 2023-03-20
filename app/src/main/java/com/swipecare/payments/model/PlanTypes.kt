package com.swipecare.payments.model

import com.squareup.moshi.Json

data class PlanTypes(
    @Json(name = "FULLTT")
    val fullTT: MutableList<Plan>? = mutableListOf<Plan>(),
    @Json(name = "TOPUP")
    val topUp: MutableList<Plan>? = mutableListOf<Plan>(),
    @Json(name = "DATA")
    val data: MutableList<Plan>? = mutableListOf(),
    @Json(name = "Romaing")
    val roaming: MutableList<Plan>? = mutableListOf(),
    @Json(name = "FRC")
    val frc: MutableList<Plan>? = mutableListOf(),
    @Json(name = "STV")
    val stv: MutableList<Plan>? = mutableListOf(),
    @Json(name = "JioPhone")
    val jioPhone: MutableList<Plan>? = mutableListOf(),
    @Json(name = "BSNLValidExtension")
    val BSNLValidExtension: MutableList<Plan>? = mutableListOf(),
) {
    fun getAllPlans(): List<Plan> {
        val result: MutableList<Plan> = mutableListOf()
        val lists = mutableListOf<MutableList<Plan>?>(
            fullTT,
            topUp,
            data,
            roaming,
            frc,
            stv,
            BSNLValidExtension
        )
        lists.forEach {
            result.addAll(it ?: emptyList())
        }
        return result
    }


    fun getTabTexts(): List<String> {
        val tabTexts = mutableListOf<String>()
        if (getAllPlans().isNotEmpty()) tabTexts.add("All Plans")
        if (fullTT?.isNotEmpty() == true) tabTexts.add("Full Talk Time")
        if (topUp?.isNotEmpty() == true) tabTexts.add("Topup")
        if (data?.isNotEmpty() == true) tabTexts.add("Data")
        if (roaming?.isNotEmpty() == true) tabTexts.add("Roaming")
        if (frc?.isNotEmpty() == true) tabTexts.add("FRC")
        if (stv?.isNotEmpty() == true) tabTexts.add("STV")
        if (jioPhone?.isNotEmpty() == true) tabTexts.add("JioPhone")
        if (BSNLValidExtension?.isNotEmpty() == true) tabTexts.add("BSNLValidExtension")

        return tabTexts
    }

    fun getPlanTypesList(): MutableList<List<Plan>> {
        val planTypesList = mutableListOf<List<Plan>>()
        if (getAllPlans()?.isNotEmpty() == true) planTypesList.add(getAllPlans()!!)
        if (fullTT?.isNotEmpty() == true) planTypesList.add(fullTT)
        if (topUp?.isNotEmpty() == true) planTypesList.add(topUp)
        if (data?.isNotEmpty() == true) planTypesList.add(data)
        if (roaming?.isNotEmpty() == true) planTypesList.add(roaming)
        if (frc?.isNotEmpty() == true) planTypesList.add(frc)
        if (stv?.isNotEmpty() == true) planTypesList.add(stv)
        if (jioPhone?.isNotEmpty() == true) planTypesList.add(jioPhone)
        if (BSNLValidExtension?.isNotEmpty() == true) planTypesList.add(BSNLValidExtension)
        return planTypesList
    }
}
