package com.citrus.pottedplantskiosk.ui.composeCommon

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import kotlinx.coroutines.delay


@Composable
fun IdleDialog(
    onDismiss: () -> Unit,
    onCountDownFinish: () -> Unit
) {

    var timeLeft by remember { mutableIntStateOf(20) }

    LaunchedEffect(key1 = timeLeft) {

        if (timeLeft == 0) {
            onCountDownFinish()
        }

        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }


    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = true,
            securePolicy = SecureFlagPolicy.SecureOff,
        ),
    ) {

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colorResource(R.color.greenDark),
            modifier = Modifier.clickable {
                onDismiss()
            }
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.idle),
                    contentDescription = null,
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(colorResource(R.color.lightBg)),
                    modifier = Modifier
                        .padding(start = 8.dp,end = 12.dp)
                        .size(36.dp)
                )

                Box (
                    Modifier.width(1.5.dp).background(colorResource(R.color.lightBg)).height(50.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.idle),
                        color = colorResource(R.color.lightBg),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding( end = 8.dp, bottom = 4.dp, top = 8.dp)
                    )
                    Text(
                        text = stringResource(R.string.idleHint, timeLeft),
                        color = colorResource(R.color.lightBg),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding( end = 8.dp, bottom = 8.dp, top = 4.dp)
                    )
                }
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun IdleDialogPreView() {
    IdleDialog(
        onDismiss = {}, onCountDownFinish = {}
    )
}