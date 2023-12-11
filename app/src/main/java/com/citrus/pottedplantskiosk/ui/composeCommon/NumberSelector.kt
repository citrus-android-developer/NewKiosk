package com.citrus.pottedplantskiosk.ui.composeCommon

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.citrus.pottedplantskiosk.R
import kotlin.math.max
import kotlin.math.min

@Composable
fun NumberSelector(
    maxLimit: Int,
    onValueChange: (Int) -> Unit
) {
    var value by remember { mutableStateOf(0) }

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .height(30.dp)
                .width(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    1.5.dp,
                    colorResource(id = R.color.lightGreen),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable {
                    value = max(0, value - 1)
                    onValueChange(value)
                }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_remove_24),
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier.size(18.dp).align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.width(2.dp))

        BasicTextField(
            value = value.toString(),
            onValueChange = {
                if (it.isNotEmpty()) {
                    value = it.toInt()
                    value = min(value, maxLimit)
                    onValueChange(value)
                }
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.baloo2_medium))
            ),
            singleLine = true,
            modifier = Modifier
                .width(40.dp)
                .clip(RectangleShape)
        )

        Spacer(modifier = Modifier.width(2.dp))

        IconButton(
            onClick = {
                value = min(maxLimit, value + 1)
                onValueChange(value)
            },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .height(30.dp)
                .width(50.dp)
                .background(colorResource(id = R.color.lightGreen), shape = RectangleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NumberSelectorPreview() {
    NumberSelector(maxLimit = 10) { /* Handle value change */ }
}