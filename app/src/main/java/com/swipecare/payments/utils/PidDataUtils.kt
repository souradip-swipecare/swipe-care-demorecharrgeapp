package com.swipecare.payments.utils

import com.github.underscore.U
import org.json.JSONObject

fun isPidDataCaptureSuccessful(jsonObject: JSONObject) =
    jsonObject.getString("-errCode") == "0"

fun getErrInfo(jsonObject: JSONObject) = jsonObject.getString("-errInfo")

fun getResponseJsonObject(pidDataString: String): JSONObject {
    val json = U.xmlToJson(pidDataString)
    val obj = JSONObject(json)
    val jsonObject1 = obj.getJSONObject("PidData")

    return jsonObject1.getJSONObject("Resp")
}


