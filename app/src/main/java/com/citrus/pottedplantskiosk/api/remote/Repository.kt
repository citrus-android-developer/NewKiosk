package com.citrus.pottedplantskiosk.api.remote

import com.citrus.pottedplantskiosk.api.remote.dto.Data
import kotlinx.coroutines.flow.Flow

interface Repository{
    fun getMenu(url:String,rsNo:String): Flow<Resource<Data>>
}