package com.citrus.pottedplantskiosk.ui.menu

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import com.citrus.pottedplantskiosk.R

@OptIn(ExperimentalMaterial3Api::class) //ModalBottomSheet
@Composable
fun MGoodsDialog(
    onDismiss: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
//        properties = DialogProperties(
//            dismissOnBackPress = false,
//            dismissOnClickOutside = false,
//            securePolicy = SecureFlagPolicy.SecureOff,
//            usePlatformDefaultWidth = false //讓畫面可以隨著內容動態增長
//        ),
    ) {

        Button(onClick = {
            onDismiss()
        }) {
            Text("Hide bottom sheet")
        }
//        Surface(
//            shape = RoundedCornerShape(16.dp),
//            color = colorResource(R.color.lightBg),
//            modifier = Modifier
//                .width(500.dp)
//                .fillMaxWidth()
//                .wrapContentHeight()
//        ) {
//
//        }
    }
}