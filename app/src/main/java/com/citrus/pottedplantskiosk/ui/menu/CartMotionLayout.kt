package com.citrus.pottedplantskiosk.ui.menu

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.*
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.util.MultiListenerMotionLayout
import kotlinx.coroutines.launch

/**
 * A MotionLayout version of [FiltersLayout]
 * All Transitions and ConstraintSets are defined in R.xml.scene_filter
 *
 * Code in this class contains mostly only choreographing the transitions.
 */
class CartMotionLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : MultiListenerMotionLayout(context, attrs, defStyleAttr), LifecycleObserver {


    private val closeIcon: ImageView
    private val filterIcon: ImageView
    lateinit var scope: LifecycleCoroutineScope


    init {
        inflate(context, R.layout.layout_cart_sheet, this)
        closeIcon = findViewById(R.id.close_icon)
        filterIcon = findViewById(R.id.filter_icon)
        enableClicks()
    }

    /**
     * Opens the filter sheet. Set adapters before starting.
     *
     * Order of animations:
     * set1_base -> set2_path -> set3_reveal -> set4_settle
     */
    private fun openSheet(): Unit = performAnimation {
        setTransition(R.id.set1_base, R.id.set2_path)

        // 1) set1_base -> set2_path
        // (Start scale down animation simultaneously)
        transitionToState(R.id.set2_path)

        awaitTransitionComplete(R.id.set2_path)

        // 2) set2_path -> set3_reveal
        transitionToState(R.id.set3_reveal)
        awaitTransitionComplete(R.id.set3_reveal)

        // 3) set3_reveal -> set4_settle
        transitionToState(R.id.set4_settle)
        awaitTransitionComplete(R.id.set4_settle)
    }

    /**
     * Closes the filter sheet. Remove adapters after it's complete, useless to
     * keep them around unless opened again.
     * Instead of creating more transitions, we reverse the transitions by setting
     * the required transition at progress=1f (end state) and using [transitionToStart].
     *
     * Order of animations:
     * set4_settle -> set3_reveal -> set2_path -> set1_base
     */
    private fun closeSheet(): Unit = performAnimation {
        // 1) set4_settle -> set3_reveal
        transitionToStart()
        awaitTransitionComplete(R.id.set3_reveal)

        // 2) set3_reveal -> set2_path
        setTransition(R.id.set2_path, R.id.set3_reveal)
        progress = 1f
        transitionToStart()
        awaitTransitionComplete(R.id.set2_path)

        // 3) set2_path -> set1_base
        // (Start scale 'up' animator simultaneously)
        setTransition(R.id.set1_base, R.id.set2_path)
        progress = 1f
        transitionToStart()
        awaitTransitionComplete(R.id.set1_base)

        // Remove adapters after closing filter sheet

    }





    /**
     * Based on the currentState (ConstraintSet), we set the appropriate click listeners.
     * Do not call this method during an animation.
     */
    private fun enableClicks() = when (currentState) {
        R.id.set1_base -> {
            Log.e("currentState","set1_base")
            filterIcon.setImageResource(R.drawable.ic_round_shopping_cart_24)
            filterIcon.setOnClickListener { openSheet() }
            closeIcon.setOnClickListener(null)
        }

        R.id.set4_settle -> {
            Log.e("currentState","set4_settle")
            filterIcon.setImageResource(R.drawable.ic_baseline_payment_24)
            filterIcon.setOnClickListener { null }
            closeIcon.setOnClickListener { closeSheet() }
        }
        else -> throw IllegalStateException("Can be called only for the permitted 3 currentStates")
    }

    /**
     * Called when an animation is started so that double clicking or
     * clicking during animation will not trigger anything
     */
    private fun disableClicks() {
        filterIcon.setOnClickListener(null)
        closeIcon.setOnClickListener(null)
    }


    fun registerLifecycleOwner(lifecycle: Lifecycle){
        lifecycle.addObserver(this)
         scope = lifecycle.coroutineScope
    }


    /**
     * Convenience method to launch a coroutine in MainActivity's lifecycleScope
     * (to start animating transitions in MotionLayout) and to handle clicks appropriately.
     *
     * Note: [block] must contain only animation related code. Clicks are
     * disabled at start and enabled at the end.
     *
     * Warning: [awaitTransitionComplete] must be called for the final state at the end of
     * [block], otherwise [enableClicks] will be called at the wrong time for the wrong state.
     */
    private inline fun performAnimation(crossinline block: suspend () -> Unit) {
        scope.launch {
            disableClicks()
            block()
            enableClicks()
        }
    }

}