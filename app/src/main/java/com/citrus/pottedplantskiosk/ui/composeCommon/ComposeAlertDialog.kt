package com.citrus.pottedplantskiosk.ui.composeCommon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import com.citrus.jennyoneplus.member.view.components.loadingButton.MyButton
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.util.UiText

@Composable
fun AlertDialog(
    title: UiText?,
    message: UiText?,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            securePolicy = SecureFlagPolicy.SecureOff,
        ),
    ) {

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colorResource(R.color.lightBg),
            modifier = Modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_warning),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (title != null) {
                    Text(
                        text = title.asString(context),
                        color = colorResource(R.color.colorPrimaryText),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                if (message != null) {
                    Text(
                        text = message.asString(context),
                        color = colorResource(R.color.colorPrimaryText),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                MyButton(
                    text = stringResource(id = R.string.cancel),
                    modifier = Modifier.fillMaxWidth(),
                    bgColorId = R.color.colorWhite,
                    onClick = onDismiss
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlertDialogPreView() {
    AlertDialog(
        title = UiText.DynamicString("輸入錯誤"),
        message = UiText.DynamicString("請再次確認您的輸入\n請再次確認您的輸入\n請再次確認您的輸入\n請再次確認您的輸入\n請再次確認您的輸入\n請再次確認您的輸入\n請再次確認您的輸入"),
        onDismiss = {},
    )
}