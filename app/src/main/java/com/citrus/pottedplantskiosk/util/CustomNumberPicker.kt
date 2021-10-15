package com.citrus.pottedplantskiosk.util

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.citrus.pottedplantskiosk.R
import com.skydoves.elasticviews.ElasticAnimation
import kotlin.math.roundToInt

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

        decrement.setOnClickListener { v ->
            ElasticAnimation(v)
                .setScaleX(0.85f)
                .setScaleY(0.85f)
                .setDuration(50)
                .setOnFinishListener {
                    if (currentValue > 1) {
                        currentValue--
                        updateDisplayValue()
                    }else{
                        onRemoveItemListener?.let{ call ->
                            call(0)
                        }
                    }
                }.doAction()
        }


        increment.setOnClickListener { v ->
            ElasticAnimation(v)
                .setScaleX(0.85f)
                .setScaleY(0.85f)
                .setDuration(50)
                .setOnFinishListener {
                    currentValue++
                    updateDisplayValue()
                }.doAction()
        }
    }

     fun setTextSize(id:Int){
        displayValue.textSize = context.resources.getDimension(id)
    }

     fun setValue(value:Int) {
        currentValue = value
        displayValue.text = currentValue.toString()
    }

    private fun updateDisplayValue() {
        displayValue.text = currentValue.toString()
        onBtnClickListener?.let { click ->
            click(currentValue)
        }
    }

    private var onBtnClickListener: ((Int) -> Unit)? = null

    fun setOnBtnClickListener(listener: (Int) -> Unit) {
        onBtnClickListener = listener
    }

    private var onRemoveItemListener: ((Int) -> Unit)? = null

    fun setOnRemoveItemListener(listener: (Int) -> Unit) {
        onRemoveItemListener = listener
    }
}