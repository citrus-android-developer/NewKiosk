package com.citrus.pottedplantskiosk.ui.menu

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.DeliveryInfo
import com.citrus.pottedplantskiosk.api.remote.dto.Good
import com.citrus.pottedplantskiosk.api.remote.dto.PayWay
import com.citrus.pottedplantskiosk.databinding.LayoutCartSheetBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.ui.menu.adapter.CartItemAdapter
import com.citrus.pottedplantskiosk.ui.menu.adapter.CheckoutAdapter
import com.citrus.pottedplantskiosk.ui.menu.adapter.PayWayAdapter
import com.citrus.pottedplantskiosk.util.*
import com.citrus.pottedplantskiosk.util.Constants.getGstStr
import com.citrus.pottedplantskiosk.util.Constants.getValByMathWay
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.skydoves.elasticviews.ElasticAnimation
import kotlinx.coroutines.launch
import java.math.BigDecimal


@SuppressLint("SetTextI18n")
class CartMotionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : MultiListenerMotionLayout(context, attrs, defStyleAttr), LifecycleObserver {

    private lateinit var scope: LifecycleCoroutineScope
    private var cartItemAdapter: CartItemAdapter? = null
    private var payWayAdapter: PayWayAdapter? = null
    private var checkoutAdapter: CheckoutAdapter? = null
    private var currentPayWay: PayWay? = null
    private var binding: LayoutCartSheetBinding =
        LayoutCartSheetBinding.inflate(LayoutInflater.from(context), this)

    private var deliveryInfo: DeliveryInfo? = null
    private var gstValue = 0.0
    private var grandTotalValue = 0.0


    init {
        binding.apply {
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
                payType.text = context.getString(R.string.paymentType)
                vPayType.text = payWay.desc
                currentPayWay = payWay
            }
            enableClicks()
        }
    }


    private fun openSheet(): Unit = performAnimation {
        setTransition(R.id.set1_base, R.id.set2_path)
        transitionToState(R.id.set2_path)
        awaitTransitionComplete(R.id.set2_path)

        binding.filterIconText.visibility = View.INVISIBLE
        if (cartItemAdapter?.getList().isNullOrEmpty()) {
            binding.shoppingBagHint.visibility = View.VISIBLE
        }

        transitionToState(R.id.set3_reveal)
        awaitTransitionComplete(R.id.set3_reveal)
        updateRvData()

        binding.filterIconText.visibility = View.VISIBLE
        binding.closeIconText.visibility = View.VISIBLE

        transitionToState(R.id.set4_settle)
        awaitTransitionComplete(R.id.set4_settle)
    }


    private fun closeSheet(isDone: Boolean): Unit = performAnimation {
        if (currentState == R.id.set5_payWay) {
            transitionToStart()
            awaitTransitionComplete(R.id.set4_settle)
            setTransition(R.id.set3_reveal, R.id.set4_settle)
            progress = 1f
        } else if (currentState == R.id.set6_checkout) {
            binding.title.text = context.getString(R.string.myCart)
            binding.title.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_baseline_shopping_bag_24,
                0,
                0,
                0
            )
            binding.tvTotalPrice.visibility = View.VISIBLE
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

        if (isDone) {
            deliveryInfo = DeliveryInfo(
                goodsList = cartItemAdapter?.getList()!!,
                payWay = currentPayWay!!,
                gst = gstValue,
                grandTotal = grandTotalValue,
                orderNo = ""
            )
            onOrderDoneListener?.let { pass ->
                cartItemAdapter?.getList()?.let { pass(deliveryInfo!!) }
            }
        }


        binding.cartRv.visibility = View.INVISIBLE

        transitionToStart()
        awaitTransitionComplete(R.id.set3_reveal)

        setTransition(R.id.set2_path, R.id.set3_reveal)
        progress = 1f
        transitionToStart()
        awaitTransitionComplete(R.id.set2_path)
        binding.filterIconText.visibility = View.INVISIBLE
        binding.closeIconText.visibility = View.INVISIBLE
        binding.shoppingBagHint.visibility = View.INVISIBLE

        setTransition(R.id.set1_base, R.id.set2_path)
        progress = 1f
        transitionToStart()
        awaitTransitionComplete(R.id.set1_base)
        binding.filterIconText.visibility = View.GONE

    }

    private fun payWay(): Unit = performAnimation {
        transitionToState(R.id.set5_payWay)
        awaitTransitionComplete(R.id.set5_payWay)
        binding.payWayRv.visibility = View.VISIBLE

    }

    private fun payWayReveal(): Unit = performAnimation {
        binding.payWayRv.visibility = View.INVISIBLE
        transitionToStart()
        awaitTransitionComplete(R.id.set4_settle)
        setTransition(R.id.set3_reveal, R.id.set4_settle)
        progress = 1f
    }

    private fun checkout(): Unit = performAnimation {
        cartItemAdapter?.getList()?.let { checkoutAdapter?.setList(it) }
        binding.checkoutRv.startLayoutAnimation()
        binding.title.text = context.getString(R.string.orderDetail)
        binding.title.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_baseline_checklist_24,
            0,
            0,
            0
        )
        transitionToState(R.id.set6_checkout)
        awaitTransitionComplete(R.id.set6_checkout)
    }


    private fun checkoutReveal(): Unit = performAnimation {
        binding.title.text = context.getString(R.string.myCart)
        binding.title.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_baseline_shopping_bag_24,
            0,
            0,
            0
        )
        transitionToStart()
        awaitTransitionComplete(R.id.set5_payWay)
        setTransition(R.id.set4_settle, R.id.set5_payWay)
        progress = 1f
        checkoutAdapter?.setList(mutableListOf())
    }


    private fun enableClicks() = when (currentState) {
        R.id.set1_base -> {
            binding.apply {
                filterIcon.setImageResource(R.drawable.ic_round_shopping_cart_24)
                filterIconArea.setOnClickListener { openSheet() }
                closeIconArea.setOnClickListener(null)
            }
        }

        R.id.set4_settle -> {
            binding.apply {
                filterIcon.setImageResource(R.drawable.ic_baseline_payment_24)
                filterIconText.text = context.getString(R.string.paymentType_)
                filterIconArea.setOnClickListener { v ->
                    clickAnimation({
                        if (cartItemAdapter?.getList()?.isNotEmpty() == true) {
                            payWay()
                        } else {
                            YoYo.with(Techniques.Shake).duration(1000).playOn(shoppingBagHint)
                        }
                    }, v)
                }
                closeIcon.setImageResource(R.drawable.ic_baseline_close_fullscreen_24)
                closeIconText.text = context.getString(R.string.close)
                closeIconArea.setOnClickListener { v ->
                    clickAnimation({ closeSheet(false) }, v)
                }
            }
        }

        R.id.set5_payWay -> {
            binding.apply {
                filterIcon.setImageResource(R.drawable.ic_baseline_payment_24)
                filterIconText.text = context.getString(R.string.paymentType_)
                filterIconArea.setOnClickListener { v ->
                    clickAnimation({
                        payWayReveal()
                    }, v)
                }
                closeIcon.setImageResource(R.drawable.ic_baseline_close_24)
                closeIconText.text = context.getString(R.string.close)
                closeIconArea.setOnClickListener { v ->
                    clickAnimation({ closeSheet(false) }, v)
                }
            }
        }

        R.id.set6_checkout -> {
            binding.apply {
                filterIcon.setImageResource(R.drawable.ic_baseline_price_check_24)
                filterIconText.text = context.getString(R.string.done)
                filterIconArea.setOnClickListener { v ->
                    clickAnimation({ closeSheet(true) }, v)
                }
                closeIcon.setImageResource(R.drawable.ic_baseline_keyboard_backspace_24)
                closeIconText.text = context.getString(R.string.back)
                closeIconArea.setOnClickListener { v ->
                    clickAnimation({ checkoutReveal() }, v)
                }
            }
        }
        else -> throw IllegalStateException("Can be called only for the permitted 3 currentStates")
    }


    private fun disableClicks() {
        binding.apply {
            filterIconArea.setOnClickListener(null)
            closeIconArea.setOnClickListener(null)
        }
    }


    fun registerLifecycleOwner(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
        scope = lifecycle.coroutineScope
    }


    private fun updateRvData() {
        binding.apply {
            cartRv.startLayoutAnimation()
            cartRv.isVisible = true
        }
    }


    fun addCartGoods(cartGoods: Good) {
        cartItemAdapter?.updateGoods(cartGoods)
    }

    fun clearCartGoods() {
        cartItemAdapter?.clearCart()
    }


    @SuppressLint("SetTextI18n")
    private fun infoChange() {
        binding.apply {
            var grandTotalStr = ""
            gstValue = 0.0
            var pureSPrice = 0.0

            val list = cartItemAdapter!!.getList()
            list.forEach { goods ->
                pureSPrice = add(pureSPrice, goods.sPrice)
            }

            for (item in list) {
                gstValue = add(gstValue, item.gst)
            }

            if (prefs.taxFunction == 1 || prefs.taxFunction == 0) {
                grandTotalStr = pureSPrice.toString()
            } else if (prefs.taxFunction == 2) {
                val gstRounding =
                    BigDecimal(gstValue).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                grandTotalStr = (pureSPrice + gstRounding).toString()
            }


            tvTotalPrice.text = context.getString(R.string.SubTotal) + getValByMathWay(pureSPrice)

            sumPrice.text = context.getString(R.string.SubTotal)
            vSumPrice.text = getValByMathWay(pureSPrice)
            gts.text = context.getGstStr()
            vGts.text = getValByMathWay(gstValue)
            grandTotal.text = context.getString(R.string.grandTotal)
            vGrandTotal.text = getValByMathWay(grandTotalStr.toDouble())

            cartItemSize.text = cartItemAdapter?.getList()?.size.toString()
            if (list.isEmpty() && currentPayWay == null) {
                shoppingBagHint.visibility = View.VISIBLE
            }
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

    private var onOrderDoneListener: ((DeliveryInfo) -> Unit)? = null
    fun setonOrderDoneListener(listener: (DeliveryInfo) -> Unit) {
        onOrderDoneListener = listener
    }

    fun setCartItemSizePulse() {
        infoChange()
        YoYo.with(Techniques.Pulse).pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT).duration(600)
            .playOn(binding.cartItemSize)
    }

    fun releaseAdapter() {
        cartItemAdapter = null
        payWayAdapter = null
        checkoutAdapter = null
    }

}