package com.citrus.pottedplantskiosk.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.ui.composeCommon.NumberSelector
import com.citrus.pottedplantskiosk.ui.composeCommon.forwardingPainter

@OptIn(ExperimentalMaterial3Api::class) //ModalBottomSheet
@Composable
fun MGoodsDialog(
    onDismiss: () -> Unit,
    onTouchEvent: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Box(modifier = Modifier.pointerInput(Unit) {
            detectTapGestures {
                onTouchEvent()
            }
        }) {
            Content()
        }
    }
}

@Composable
fun Content() {
    Column {
        Text(
            "經濟套餐",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            "商品描述，這是商品描述",
            modifier = Modifier.fillMaxWidth(),
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "$200",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.baloo2_medium)),
            style = MaterialTheme.typography.titleLarge,
            color = colorResource(R.color.colorGreen),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://people.com/thmb/rkzuOIWafoVchyLmmkMFzR1WDiM=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc():focal(749x0:751x2)/mcdonalds-burger-041423-2-b645d0fbbf7f4dae82f5a70480d75367.jpg")
                .crossfade(true)
                .build(),
            placeholder = forwardingPainter(
                painter = painterResource(R.drawable.ic_default_image),
                colorFilter = ColorFilter.tint(Color.Gray),
            ),
            contentDescription = "經濟套餐",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size = 150.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .clip(
                    shape = RoundedCornerShape(16.dp)
                )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .padding(20.dp)
                .clip(shape = RoundedCornerShape(25.dp))
                .background(colorResource(R.color.lightBg))
        ) {
            Column(modifier = Modifier.padding(vertical = 25.dp)) {
                Text(
                    "主食",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(5.dp))
                Column(modifier = Modifier.padding(vertical = 15.dp, horizontal = 15.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            "漢堡包",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        NumberSelector(maxLimit = 99, onValueChange = {

                        })
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun MGoodsDialogPreview() {
    Content()
}
