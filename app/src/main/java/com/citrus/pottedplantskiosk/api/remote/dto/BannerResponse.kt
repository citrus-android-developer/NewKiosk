package com.citrus.pottedplantskiosk.api.remote.dto



import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.*
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.lang.reflect.Type
import java.util.Base64



data class BannerRequest(
    @SerializedName("RSNO")
    val rsno:String
): Serializable


data class BannerResponse(
    val status: Int,
    val data: List<BannerData>
): Serializable


data class StatusCode<T>(
    val status:Int,
    val data:T
): Serializable

data class ResultMock(
    val desc:String
): Serializable

class  StatusCodeDeserializer<T> : JsonDeserializer<StatusCode<T>> {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): StatusCode<T> {
        val jsonObject = json.asJsonObject

        if(typeOfT.toString() == object :TypeToken<StatusCode<Int>>() {}.type.toString()){

        }

        if(typeOfT.toString() == object :TypeToken<StatusCode<ResultMock>>() {}.type.toString()){

        }

        return Gson().fromJson(jsonObject, typeOfT)
    }
}


data class BannerData(
    val autoNo: Long? = null,
    @SerializedName("OnSaleName")
    val onSaleName: String? = null,
    @SerializedName("Seq")
    val seq: Long? = null,
    @SerializedName("Pic")
    val pic: String,
    @SerializedName("Hyperlink")
    val hyperlink: String? = null,
): Serializable

class BannerDataDeserializer : JsonDeserializer<BannerData> {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): BannerData {
        val jsonObject = json.asJsonObject

        val data = jsonObject.get("Hyperlink").asString
        if (data.isEmpty()) {
            jsonObject.remove("Hyperlink")
        }
        return Gson().fromJson(jsonObject, typeOfT)
    }
}