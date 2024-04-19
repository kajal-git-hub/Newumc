package com.ms.app.JSONModel.skiplogic

import com.ms.app.JSONModel.SkipLogicData

data class SkipLogicElement(
    val relation: String?,
    val flag: Boolean?,
    val data: List<SkipLogicData>?
)
