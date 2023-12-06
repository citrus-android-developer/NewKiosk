package com.citrus.pottedplantskiosk.ui.menu.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.Good
import com.citrus.pottedplantskiosk.databinding.CartItemViewBinding
import com.citrus.pottedplantskiosk.util.Constants
import javax.inject.Inject
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.Constants.clickAnimation
import com.citrus.pottedplantskiosk.util.DensityUtil.px2sp
import com.citrus.pottedplantskiosk.util.base.onSafeClick


class CartItemAdapter @Inject constructor(val context: Context) :
    RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {
    class CartItemViewHolder(val binding: CartItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var _cartGoods = mutableListOf<Good>()
    private val cartGoods get() = _cartGoods

    fun getList(): MutableList<Good> {
        return cartGoods
    }

    fun clearCart() {
        cartGoods.clear()
        onChangedListener?.let { notify ->
            notify()
        }
    }

    private fun insertGoods(goods: Good?) {
        goods?.let {
            _cartGoods.add(goods)
            notifyItemInserted(_cartGoods.size)
            onChangedListener?.let { notify ->
                notify()
            }
        }
    }

    fun updateGoods(goods: Good) {
        var position = -1
        for (item in _cartGoods) {
            if (item.gID == goods.gID && item.gKID == goods.gKID && item.size == goods.size) {
                position = _cartGoods.indexOf(item)
                if (goods.isEdit) {
                    /**修改的商品覆蓋原品項參數*/
                    item.qty = goods.qty
                    item.size = goods.size
                    item.sPrice = goods.sPrice
                    item.add = goods.add
                    item.flavor = goods.flavor
                } else {
                    /**重複購買的商品增加原數量*/
                    item.qty += goods.qty
                }
                notifyItemChanged(position)
                break
            }
        }

        if (position == -1) {
            /**新增品項、註記為可修改品項*/
            goods.isEdit = true
            insertGoods(goods)
        }

        onChangedListener?.let { notify ->
            notify()
        }
    }

    private fun removeGoods(goods: Good) {
        val position = _cartGoods.indexOf(goods)
        _cartGoods.remove(goods)
        notifyItemRemoved(position)
        onChangedListener?.let { notify ->
            notify()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        return CartItemViewHolder(
            CartItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun onBindViewHolder(
        holder: CartItemViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = _cartGoods[position]
        holder.binding.apply {

            var size: String = item.size?.find { it.isCheck }?.desc ?: ""
            if (size == "") {
                tvSize.isVisible = false
            } else {
                tvSize.text = size
            }

            if (prefs.languagePos == 1) {
                tvGoodsName.text = item.gName2
            } else {
                tvGoodsName.text = item.gName
            }

            Glide.with(holder.itemView)
                .load(Constants.IMG_URL + item.picname)
                .placeholder(R.drawable.ic_image_gallery__2_)
                .fallback(R.drawable.ic_image_gallery__2_)
                .into(goodsImg)

            tvPrice.text = "$" + Constants.getValByMathWay(item.sPrice)


            val textSize: Float = context.px2sp(tvPrice.textSize)
            numberPicker.setValue(item.qty)
            numberPicker.setTextSize(textSize)



            numberPicker.setOnButtonClickListener { _, newValue ->
                if (newValue > 0) {
                    item.qty = newValue
                    item.sPrice = checkPrice(item.price ?: 0.0, item.qty)
                    tvPrice.text = "$" + Constants.getValByMathWay(item.sPrice)
                    notifyItemChanged(position)
                    onChangedListener?.let { notify ->
                        notify()
                    }
                } else {
                    removeGoods(item)
                }
            }


            deleteBtn.onSafeClick {
                it.clickAnimation {
                    removeGoods(item)
                }
            }

            root.onSafeClick {
                it.clickAnimation {
                    onGoodsClickListener?.let { click ->
                        click(item)
                    }
                }
            }
        }
    }

    private fun checkPrice(price: Double, qty: Int): Double {
        return price * qty
    }

    override fun getItemCount(): Int {
        return _cartGoods.size
    }


    private var onChangedListener: (() -> Unit)? = null
    fun setOnChangedListener(listener: () -> Unit) {
        onChangedListener = listener
    }


    private var onGoodsClickListener: ((Good) -> Unit)? = null
    fun setOnGoodsClickListener(listener: (Good) -> Unit) {
        onGoodsClickListener = listener
    }

}