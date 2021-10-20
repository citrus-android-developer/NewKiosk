package com.citrus.pottedplantskiosk.ui.menu

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.Good
import com.citrus.pottedplantskiosk.ui.menu.adapter.CartItemAdapter
import com.citrus.pottedplantskiosk.util.Constants
import com.citrus.pottedplantskiosk.util.MultiListenerMotionLayout
import com.skydoves.elasticviews.ElasticAnimation
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A MotionLayout version of [FiltersLayout]
 * All Transitions and ConstraintSets are defined in R.xml.scene_filter
 *
 * Code in this class contains mostly only choreographing the transitions.
 */
class CartMotionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MultiListenerMotionLayout(context, attrs, defStyleAttr), LifecycleObserver {


    private val closeIcon: ImageView
    private val filterIcon: ImageView
    private val shoppingBag: ImageView
    private val shoppingHint: TextView
    private val cartItemSize: TextView
    private val tvTotalPrice: TextView
    private val shoppingBagHint: LinearLayout
    private val cartRv: RecyclerView
    private var updateCartItemJob: Job? = null
    lateinit var scope: LifecycleCoroutineScope
    private var list: List<Good> = listOf()
    private var emptyListForAnimate: List<Good> = listOf()



    init {
        inflate(context, R.layout.layout_cart_sheet, this)
        closeIcon = findViewById(R.id.close_icon)
        filterIcon = findViewById(R.id.filter_icon)
        shoppingBag = findViewById(R.id.shoppingBag)
        cartRv = findViewById(R.id.cartRv)
        shoppingHint = findViewById(R.id.shoppingHint)
        shoppingBagHint = findViewById(R.id.shoppingBagHint)
        cartItemSize = findViewById(R.id.cartItemSize)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)

        cartRv.apply {
            layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        enableClicks()

    }

    /**
     * Opens the filter sheet. Set adapters before starting.
     *
     * Order of animations:
     * set1_base -> set2_path -> set3_reveal -> set4_settle
     */
    private fun openSheet(): Unit = performAnimation {
        onOpenSheetListener?.let { open ->
            open()
        }

        setTransition(R.id.set1_base, R.id.set2_path)
        // 1) set1_base -> set2_path
        // (Start scale down animation simultaneously)
        transitionToState(R.id.set2_path)

        awaitTransitionComplete(R.id.set2_path)

        // 2) set2_path -> set3_reveal
        transitionToState(R.id.set3_reveal)
        awaitTransitionComplete(R.id.set3_reveal)

        cartRv.isVisible = true
        updateRvData(list)

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
        cartRv.isVisible = false
        updateRvData(emptyListForAnimate)

        shoppingBagHint.visibility = View.INVISIBLE
        // 1) set4_settle -> set3_reveal
        transitionToStart()
        awaitTransitionComplete(R.id.set3_reveal)


        // 2) set3_reveal -> set2_path
        setTransition(R.id.set2_path, R.id.set3_reveal)
        progress = 1f
        transitionToStart()
        awaitTransitionComplete(R.id.set2_path)

        onCloseSheetListener?.let { close ->
            close()
        }

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
            filterIcon.setImageResource(R.drawable.ic_round_shopping_cart_24)
            filterIcon.setOnClickListener { openSheet() }
            closeIcon.setOnClickListener(null)
        }

        R.id.set4_settle -> {
            filterIcon.setImageResource(R.drawable.ic_baseline_payment_24)
            filterIcon.setOnClickListener { v ->
                clickAnimation({
                    onPayButtonClickListener?.let { call ->
                        call(list)
                    }
                }, v)
            }
            closeIcon.setOnClickListener { v ->
                clickAnimation({ closeSheet() }, v)
            }
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


    fun registerLifecycleOwner(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
        scope = lifecycle.coroutineScope
    }

    fun setCloseSheet() {
        closeSheet()
    }

    fun setAdapter(cartItemAdapter: CartItemAdapter) {
        cartRv.adapter = cartItemAdapter
    }

    private fun updateRvData(mList:List<Good>) {
        updateCartItemJob?.cancel()
        updateCartItemJob = scope.launch {
            (cartRv.adapter as (CartItemAdapter)).updateDataset(mList)
            cartItemSize.text = list.size.toString()
            cartRv.scheduleLayoutAnimation()
        }
    }

    fun addCartGoods(cartGoods: Good?) {
        cartGoods?.let {
            list = list + it
        } ?: run {
            list = listOf()
        }
        updateRvData(list)
        showTotalPrice()
    }

    fun removeGoods(good: Good) {
        list = list - good
        updateRvData(list)
        showTotalPrice()
    }

    private fun showTotalPrice(){
        var total = 0.0
        list.forEach {
            Log.e("price",(it.price * it.qty).toString())
            total += (it.price * it.qty)
        }

        tvTotalPrice.text = "Total Price: $ " + Constants.df.format(total)
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




    private inline fun clickAnimation(crossinline block: suspend () -> Unit, view: View) {
        ElasticAnimation(view)
            .setScaleX(0.85f)
            .setScaleY(0.85f)
            .setDuration(50)
            .setOnFinishListener {
                scope.launch {
                    block()
                }
            }.doAction()
    }


    private var onOpenSheetListener: (() -> Unit)? = null
    fun setOnOpenSheetListener(listener: () -> Unit) {
        onOpenSheetListener = listener
    }

    private var onCloseSheetListener: (() -> Unit)? = null
    fun setOnCloseSheetListener(listener: () -> Unit) {
        onCloseSheetListener = listener
    }

    private var onCloseSheetWhenSwitchListener: (() -> Unit)? = null
    fun setOnCloseSheetWhenSwitchListener(listener: () -> Unit) {
        onCloseSheetWhenSwitchListener = listener
    }

    private var onPayButtonClickListener: ((List<Good>) -> Unit)? = null
    fun setOnPayButtonClickListener(listener: (List<Good>) -> Unit) {
        onPayButtonClickListener = listener
    }

}