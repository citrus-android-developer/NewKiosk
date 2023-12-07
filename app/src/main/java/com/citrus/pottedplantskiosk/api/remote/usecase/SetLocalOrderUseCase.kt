package com.citrus.pottedplantskiosk.api.remote.usecase

import android.provider.SyncStateContract.Constants
import com.citrus.pottedplantskiosk.api.remote.RemoteRepository
import com.citrus.pottedplantskiosk.api.remote.dto.OrdersRequest
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.Constants.POS_SET_ORDERS
import com.google.gson.Gson
import kotlinx.coroutines.flow.transform


class SetLocalOrderUseCase(private val remoteRepository: RemoteRepository) {
    suspend operator fun invoke(orderRequest: OrdersRequest) =
        remoteRepository.postOrders(
          url = "http://" + prefs.serverIp + POS_SET_ORDERS,
            jsonData =  Gson().toJson(orderRequest)
        ).transform {
            emit(it)
        }
}