package com.ms.app.api.respose

import com.ms.app.JSONModel.JSONFormDataModel
import com.google.gson.annotations.SerializedName

data class SchemaResponse(
    @SerializedName("data")
    var data: JSONFormDataModel? = null
)