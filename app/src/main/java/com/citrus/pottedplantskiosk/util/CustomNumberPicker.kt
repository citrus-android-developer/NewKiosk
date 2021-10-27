package com.citrus.pottedplantskiosk.util

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.util.base.onSafeClick
import com.skydoves.elasticviews.ElasticAnimation


class CustomNumberPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val decrement: ImageView
    private val increment: ImageView
    private val displayValue: TextView
    private var currentValue: Int = 1

    private var constraintLayout: ConstraintLayout =
        LayoutInflater.from(context)
            .inflate(R.layout.custom_number_picker, this, false) as ConstraintLayout

    init {
        addView(constraintLayout)

        decrement = constraintLayout.findViewById(R.id.decrement)
        increment = constraintLayout.findViewById(R.id.increment)
        displayValue = constraintLayout.findViewById(R.id.display)

        displayValue.text = "1"

        decrement.onSafeClick { v ->
            ElasticAnimation(v)
                .setScaleX(0.85f)
                .setScaleY(0.85f)
                .setDuration(50)
                .setOnFinishListener {
                    if (currentValue > 0) {
                        var prevNum = currentValue
                        currentValue--
                        updateDisplayValue()
                        onButtonClick?.let{ click ->
                            click(prevNum,currentValue)
                        }
                    }
                }.doAction()
        }


        increment.onSafeClick { v ->
            ElasticAnimation(v)
                .setScaleX(0.85f)
                .setScaleY(0.85f)
                .setDuration(50)
                .setOnFinishListener {
                    var prevNum = currentValue
                    currentValue++
                    updateDisplayValue()
                    onButtonClick?.let{ click ->
                        click(prevNum,currentValue)
                    }
                }.doAction()
        }
    }

     fun setTextSize(size: Float){
         displayValue.textSize = size
    }

     fun setValue(value:Int) {
        currentValue = value
        displayValue.text = currentValue.toString()
    }

    private fun updateDisplayValue() {
        displayValue.text = currentValue.toString()
    }


    private var onButtonClick: ((Int,Int) -> Unit)? = null
    fun setOnButtonClickListener(listener: (Int,Int) -> Unit) {
        onButtonClick = listener
    }



}