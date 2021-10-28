package com.citrus.pottedplantskiosk.ui.menu

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.Good
import com.citrus.pottedplantskiosk.api.remote.dto.PayWay
import com.citrus.pottedplantskiosk.ui.menu.adapter.CartItemAdapter
import com.citrus.pottedplantskiosk.ui.menu.adapter.CheckoutAdapter
import com.citrus.pottedplantskiosk.ui.menu.adapter.PayWayAdapter
import com.citrus.pottedplantskiosk.util.Constants
import com.citrus.pottedplantskiosk.util.Constants.getCurrentTime
import com.citrus.pottedplantskiosk.util.MultiListenerMotionLayout
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.skydoves.elasticviews.ElasticAnimation
import kotlinx.coroutines.launch


class CartMotionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : MultiListenerMotionLayout(context, attrs, defStyleAttr), LifecycleObserver {


    private val closeIcon: ImageView
    private val filterIcon: ImageView
    private val shoppingBag: ImageView
    private val title:TextView
    private val shoppingHint: TextView
    private val cartItemSize: TextView
    private val tvTotalPrice: TextView
    private val filterIconText:TextView
    private val closeIconText:TextView
    private val orderTime:TextView
    private val sumPrice:TextView
    private val payType:TextView
    private val shoppingBagHint: LinearLayout
    private val filterIconArea: LinearLayout
    private val closeIconArea: LinearLayout
    private val cartRv: RecyclerView
    private val payWayRv: RecyclerView
    private val checkoutRv: RecyclerView
    lateinit var scope: LifecycleCoroutineScope
    var cartItemAdapter: CartItemAdapter?
    var payWayAdapter: PayWayAdapter?
    var checkoutAdapter: CheckoutAdapter?
    var currentPayWay: PayWay? = null



    init {
        inflate(context, R.layout.layout_cart_sheet, this)
        closeIcon = findViewById(R.id.close_icon)
        filterIcon = findViewById(R.id.filter_icon)
        shoppingBag = findViewById(R.id.shoppingBag)
        filterIconText = findViewById(R.id.filter_icon_text)
        closeIconText= findViewById(R.id.close_icon_text)
        orderTime = findViewById(R.id.orderTime)
        sumPrice = findViewById(R.id.sumPrice)
        payType = findViewById(R.id.payType)
        title = findViewById(R.id.title)
        cartRv = findViewById(R.id.cartRv)
        payWayRv = findViewById(R.id.payWayRv)
        checkoutRv = findViewById(R.id.checkoutRv)
        shoppingHint = findViewById(R.id.shoppingHint)
        shoppingBagHint = findViewById(R.id.shoppingBagHint)
        filterIconArea = findViewById(R.id.filter_icon_area)
        closeIconArea = findViewById(R.id.close_icon_area)
        cartItemSize = findViewById(R.id.cartItemSize)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)

        cartItemSize.text = "0"



        payWayAdapter = PayWayAdapter(context)
        cartItemAdapter = CartItemAdapter(context)
        checkoutAdapter = CheckoutAdapter(context)

        cartRv.apply {
            layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = cartItemAdapter
        }

        checkoutRv.apply {
            layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = checkoutAdapter
        }

        payWayRv.apply {
            layoutManager =
                GridLayoutManager(context, 5)
            adapter = payWayAdapter
        }

        cartItemAdapter?.setOnChangedListener {
            infoChange()
        }


        cartItemAdapter?.setOnGoodsClickListener { goods ->
            onGoodsClickListener?.let { click ->
                click(goods)
            }
        }


        payWayAdapter?.setOnPayWayClickListener { payWay ->
                checkout()
                payType.text = "Payment Type: " + payWay.desc
                currentPayWay = payWay
        }

        enableClicks()
    }


    private fun openSheet(): Unit = performAnimation {
        setTransition(R.id.set1_base, R.id.set2_path)
        transitionToState(R.id.set2_path)
        awaitTransitionComplete(R.id.set2_path)

        filterIconText.visibility = View.INVISIBLE
        if (cartItemAdapter?.getList().isNullOrEmpty()) {
            shoppingBagHint.visibility = View.VISIBLE
        }

        transitionToState(R.id.set3_reveal)
        awaitTransitionComplete(R.id.set3_reveal)
        updateRvData()

        filterIconText.visibility = View.VISIBLE
        closeIconText.visibility =  View.VISIBLE

        transitionToState(R.id.set4_settle)
        awaitTransitionComplete(R.id.set4_settle)
    }


    private fun closeSheet(isDone:Boolean): Unit = performAnimation {
        if (currentState == R.id.set5_payWay) {
            transitionToStart()
            awaitTransitionComplete(R.id.set4_settle)
            setTransition(R.id.set3_reveal, R.id.set4_settle)
            progress = 1f
        }else if(currentState == R.id.set6_checkout){
            title.text = "My Cart"
            title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_shopping_bag_24, 0, 0, 0)
            tvTotalPrice.visibility = View.VISIBLE
            transitionToStart()
            awaitTransitionComplete(R.id.set5_payWay)
            setTransition(R.id.set4_settle, R.id.set5_payWay)
            progress = 1f
            checkoutAdapter?.setList(mutableListOf())
            transitionToStart()
            awaitTransitionComplete(R.id.set4_settle)
            setTransition(R.id.set3_reveal, R.id.set4_settle)
            progress = 1f
        }



        cartRv.visibility = View.INVISIBLE

        transitionToStart()
        awaitTransitionComplete(R.id.set3_reveal)


        setTransition(R.id.set2_path, R.id.set3_reveal)
        progress = 1f
        transitionToStart()
        awaitTransitionComplete(R.id.set2_path)
        filterIconText.visibility = View.INVISIBLE
        closeIconText.visibility = View.INVISIBLE
        shoppingBagHint.visibility = View.INVISIBLE

        setTransition(R.id.set1_base, R.id.set2_path)
        progress = 1f
        transitionToStart()
        awaitTransitionComplete(R.id.set1_base)
        filterIconText.visibility = View.GONE

        if(isDone){
            onOrderDoneListener?.let { pass ->
                cartItemAdapter?.getList()?.let { pass(it,currentPayWay!!) }
            }
        }

    }

    private fun payWay(): Unit = performAnimation {
        transitionToState(R.id.set5_payWay)
        awaitTransitionComplete(R.id.set5_payWay)
        payWayRv.visibility = View.VISIBLE

    }

    private fun payWayReveal(): Unit = performAnimation {
        payWayRv.visibility = View.INVISIBLE
        transitionToStart()
        awaitTransitionComplete(R.id.set4_settle)
        setTransition(R.id.set3_reveal, R.id.set4_settle)
        progress = 1f
    }

    private fun checkout(): Unit = performAnimation {
        cartItemAdapter?.getList()?.let { checkoutAdapter?.setList(it) }
        checkoutRv.startLayoutAnimation()
        title.text = "Order Detail"
        title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_checklist_24, 0, 0, 0)
        tvTotalPrice.visibility = View.INVISIBLE
        orderTime.text = getCurrentTime()
        transitionToState(R.id.set6_checkout)
        awaitTransitionComplete(R.id.set6_checkout)
    }


    private fun checkoutReveal(): Unit = performAnimation {
        title.text = "My Cart"
        title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_shopping_bag_24, 0, 0, 0)
        tvTotalPrice.visibility = View.VISIBLE
        transitionToStart()
        awaitTransitionComplete(R.id.set5_payWay)
        setTransition(R.id.set4_settle, R.id.set5_payWay)
        progress = 1f
        checkoutAdapter?.setList(mutableListOf())
    }


    private fun enableClicks() = when (currentState) {
        R.id.set1_base -> {
            filterIcon.setImageResource(R.drawable.ic_round_shopping_cart_24)
            filterIconArea.setOnClickListener { openSheet() }
            closeIconArea.setOnClickListener(null)
        }

        R.id.set4_settle -> {
            filterIcon.setImageResource(R.drawable.ic_baseline_payment_24)
            filterIconText.text = "Payment Type"
            filterIconArea.setOnClickListener { v ->
                clickAnimation({
                    if(cartItemAdapter?.getList()?.isNotEmpty() == true) {
                        payWay()
                    }else{
                        YoYo.with(Techniques.Shake).duration(1000).playOn(shoppingBagHint)
                    }
                }, v)
            }
            closeIcon.setImageResource(R.drawable.ic_baseline_close_24)
            closeIconText.text = "Close"
            closeIconArea.setOnClickListener { v ->
                clickAnimation({ closeSheet(false) }, v)
            }
        }

        R.id.set5_payWay -> {
            filterIcon.setImageResource(R.drawable.ic_baseline_payment_24)
            filterIconText.text = "Payment Type"
            filterIconArea.setOnClickListener { v ->
                clickAnimation({
                    payWayReveal()
                }, v)
            }
            closeIcon.setImageResource(R.drawable.ic_baseline_close_24)
            closeIconText.text = "Close"
            closeIconArea.setOnClickListener { v ->
                clickAnimation({ closeSheet(false) }, v)
            }
        }

        R.id.set6_checkout -> {
            filterIcon.setImageResource(R.drawable.ic_baseline_price_check_24)
            filterIconText.text = "Done"
            filterIconArea.setOnClickListener { v ->
                clickAnimation({ closeSheet(true) }, v)
            }
            closeIcon.setImageResource(R.drawable.ic_baseline_keyboard_backspace_24)
            closeIconText.text = "Back"
            closeIconArea.setOnClickListener { v ->
                clickAnimation({ checkoutReveal() }, v)
            }
        }
        else -> throw IllegalStateException("Can be called only for the permitted 3 currentStates")
    }


    private fun disableClicks() {
        filterIconArea.setOnClickListener(null)
        closeIconArea.setOnClickListener(null)
    }


    fun registerLifecycleOwner(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
        scope = lifecycle.coroutineScope
    }


    private fun updateRvData() {
        cartRv.startLayoutAnimation()
        cartRv.isVisible = true
    }


    fun addCartGoods(cartGoods: Good) {
        cartItemAdapter?.updateGoods(cartGoods)
    }

    fun clearCartGoods() {
        cartItemAdapter?.clearCart()
    }


    private fun infoChange() {
        var totalPrice = 0.0
        var list = cartItemAdapter?.getList()
        list?.forEach { goods ->
            totalPrice += goods.price * goods.qty
        }

        tvTotalPrice.text = "Total Price: $ " + Constants.df.format(totalPrice)
        sumPrice.text = "Total Price: $ " + Constants.df.format(totalPrice)
        cartItemSize.text = cartItemAdapter?.getList()?.size.toString()

        if (list?.isEmpty() == true && currentPayWay == null) {
            shoppingBagHint.visibility = View.VISIBLE
        }
    }



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


    private var onGoodsClickListener: ((Good) -> Unit)? = null
    fun setOnGoodsClickListener(listener: (Good) -> Unit) {
        onGoodsClickListener = listener
    }

    private var onOrderDoneListener: ((List<Good>,PayWay) -> Unit)? = null
    fun setonOrderDoneListener(listener: (List<Good>,PayWay) -> Unit) {
        onOrderDoneListener = listener
    }

    fun releaseAdapter() {
        cartItemAdapter = null
        payWayAdapter = null
        checkoutAdapter = null
    }

}