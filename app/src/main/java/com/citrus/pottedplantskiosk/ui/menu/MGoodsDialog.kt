package com.citrus.pottedplantskiosk.ui.menu

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import com.citrus.pottedplantskiosk.R

@OptIn(ExperimentalMaterial3Api::class) //ModalBottomSheet
@Composable
fun MGoodsDialog(
    onDismiss: () -> Unit,
//    onTouchEvent: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier
//            .clickable {
//                Log.d("TEST", "MGoodsDialog: click")
//            }
//            .pointerInput(null) {
//                detectTapGestures(
//                    onDoubleTap = {},
//                    onLongPress = {},
//                    onPress = {},
//                    onTap = {
//                        Log.d("TEST", "MGoodsDialog: onTap")
//                    }
//                )
//            }
//            .pointerInput(Unit) {
//            detectTransformGestures { centroid, pan, zoom, rotation ->
//                Log.d("TEST", "MGoodsDialog: ontouch")
//            }
//        }
    ) {
        Content()
//        Column {
//            Text("Hide bottom sheet")
//            Button(onClick = {
//                onDismiss()
//            }) {
//                Text("Hide bottom sheet")
//            }
//        }
    }
}

@Composable
fun Content() {
    Column() {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "經濟套餐",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))

    }
}

@Preview(showBackground = true)
@Composable
fun MGoodsDialogPreview() {
    Content()
}
