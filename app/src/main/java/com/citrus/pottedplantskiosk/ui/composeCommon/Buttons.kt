package com.citrus.jennyoneplus.member.view.components.loadingButton

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.citrus.pottedplantskiosk.R


/**
 * Sets a shape to use for all My Buttons.
 */
private val buttonShape = RoundedCornerShape(50)


/**
 * Alpha to use for disabled buttons and disabled content
 */
private const val DISABLED_BUTTON_ALPHA = 0.5f


/**
 * Sets the content padding to use on all buttons
 */
private val buttonContentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp)


/**
 * Renders a solid, filled in Button.
 * The button will have a solid background color with text on top.
 *
 * @param text The text to show on the button.
 * @param onClick A callback that is invoked when the button is clicked.
 * @param enabled Whether the button can be clicked or not.
 *   When NOT enabled, the onClick() handler will NOT be called when the button is clicked.
 *   When NOT enabled, the button will use the "disabled" colors in the passed in ButtonColors.
 *   This value will be ignored (and set to false) if the loading argument is true.
 *   Defaults to true.
 * @param loading A boolean indicating if the button is in the loading state. If this is
 *   set to true, then enabled will automatically be set to false.
 *   Defaults to false.
 * @param loadingIndicatorType A [LoadingIndicatorTypes] that sets the type of indicator
 *   to use for this button.
 *   Defaults to [LoadingIndicatorTypes.Pulsing]
 */
@Composable
fun MyButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    loading: Boolean = false,
    bgColorId: Int = R.color.colorWhite,
    loadingIndicatorType: LoadingIndicatorTypes = LoadingIndicatorTypes.Pulsing,
) {

    // The default material button colors use a shade of gray
    // for the disabled state. These buttons instead use
    // an alpha on the primary color.
    // If you want to use a different overall button color, just
    // change the `buttonColor` variable below. Or, for even more flexibility,
    // allow the caller to pass in a `buttonColor`.

    val buttonColor = colorResource(id = bgColorId)
//    val buttonColor = MaterialTheme.colors.secondary
//    val buttonColor = MaterialTheme.colors.primary
    val textColor = contentColorFor(backgroundColor = buttonColor)

    val colors = ButtonDefaults.buttonColors(
        containerColor = buttonColor,
        contentColor = textColor,
        disabledContainerColor = buttonColor.copy(alpha = DISABLED_BUTTON_ALPHA),
        disabledContentColor = textColor.copy(alpha = DISABLED_BUTTON_ALPHA)
    )

    Button(
        modifier = modifier,
        colors = colors,
        shape = buttonShape,
        contentPadding = buttonContentPadding,
        enabled = enabled && !loading,
        onClick = onClick
    ) {

        MyButtonContent(
            text = text,
            loading = loading,
            loadingIndicatorType = loadingIndicatorType
        )
    }
}


/**
 * Renders a outlined Button.
 * The button will have a transparent background with
 * a colored border and text in the center.
 *
 * @param text The text to show on the button.
 * @param onClick A callback that is invoked when the button is clicked.
 * @param enabled Whether the button can be clicked or not.
 *   When NOT enabled, the onClick() handler will NOT be called when the button is clicked.
 *   When NOT enabled, the button will use the "disabled" colors in the passed in ButtonColors.
 *   This value will be ignored (and set to false) if the loading argument is true.
 *   Defaults to true.
 * @param loading A boolean indicating if the button is in the loading state. If this is
 *   set to true, then enabled will automatically be set to false.
 *   Defaults to false.
 * @param loadingIndicatorType A [LoadingIndicatorTypes] that sets the type of indicator
 *   to use for this button.
 *   Defaults to [LoadingIndicatorTypes.Pulsing]
 */
@Composable
fun MyOutlinedButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    loading: Boolean = false,
    loadingIndicatorType: LoadingIndicatorTypes = LoadingIndicatorTypes.Pulsing,
) {
    var colors = ButtonDefaults.outlinedButtonColors()

    OutlinedButton(
        enabled = enabled,
        contentPadding = buttonContentPadding,
        shape = buttonShape,
        modifier = modifier,
        colors =colors,
        onClick = onClick
    ) {

        MyButtonContent(
            text = text,
            loading = loading,
            loadingIndicatorType = loadingIndicatorType
        )
    }
}

/**
 * Renders the content in a Solid or Outlined button.
 *
 * @param text The text to show on the button.
 * @param loading Whether to show the loading indicator.
 *   If true, the text will still be rendered, but will be transparent/invisible.
 * @param loadingIndicatorType A [LoadingIndicatorTypes] that sets the type of indicator
 *   to use for this button.
 */
@Composable
private fun MyButtonContent(
    text: String,
    loading: Boolean,
    loadingIndicatorType: LoadingIndicatorTypes
) {

    // Use a Custom Layout so that we can measure the width of both the
    // button text and the indicator and make sure that the resulting
    // layout is sized to fit either/both.
    // Then we can place either the text or the indicator based on the loading state.
    // This helps ensure that the button does not change size when switching the loading state.
    Layout(
        content = {
            // Content is the Text and the LoadingIndicator
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.layoutId("buttonText")
            )
            LoadingIndicator(
                type = loadingIndicatorType,
                modifier = Modifier.layoutId("loadingIndicator"),
                color = colorResource(id = R.color.colorLightGray)
            )
        }) { measureables, constraints ->

        // First, measure both the text and the indicator, with no additional contraints on their size.
        val textPlaceable = measureables.first { it.layoutId == "buttonText" }.measure(constraints)
        val indicatorPlaceable =
            measureables.first { it.layoutId == "loadingIndicator" }.measure(constraints)

        // Now calculate the layout width,
        // making sure it's big enough to fit the larger of the 2 placeables.
        val layoutWidth = textPlaceable.width.coerceAtLeast(indicatorPlaceable.width)
        val layoutHeight = textPlaceable.height.coerceAtLeast(indicatorPlaceable.height)

        // Now, create the layout at the appropriate size
        layout(layoutWidth, layoutHeight) {
            // Place EITHER the indicator or the text (but not both), based on the loading state
            if (loading) {
                // Calculate the X and Y position of the indicator so that it's centered in the layout.
                val indicatorX = (layoutWidth - indicatorPlaceable.width) / 2
                val indicatorY = (layoutHeight - indicatorPlaceable.height) / 2
                // Place the indicator
                indicatorPlaceable.placeRelative(x = indicatorX, y = indicatorY)
            } else {
                // Calculate the X and Y position of the text so that it's centered in the layout.
                val textX = (layoutWidth - textPlaceable.width) / 2
                val textY = (layoutHeight - textPlaceable.height) / 2
                //Place the text
                textPlaceable.placeRelative(x = textX, y = textY)
            }
        }
    }
}