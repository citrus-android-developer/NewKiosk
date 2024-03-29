package com.citrus.pottedplantskiosk.ui.menu.adapter



import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.citrus.pottedplantskiosk.api.remote.dto.Good
import com.citrus.pottedplantskiosk.util.Constants
import com.citrus.pottedplantskiosk.databinding.CartItemViewDoneBinding
import com.citrus.pottedplantskiosk.di.prefs


class CheckoutAdapter (val context: Context) :
    RecyclerView.Adapter<CheckoutAdapter.CartItemViewHolder>() {
    class CartItemViewHolder(val binding: CartItemViewDoneBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var _cartGoods = mutableListOf<Good>()
    private val cartGoods get() = _cartGoods

    fun getList(): MutableList<Good> {
        return cartGoods
    }


    fun setList(newData:MutableList<Good>) {
        _cartGoods = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        return CartItemViewHolder(
            CartItemViewDoneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

            tvQtyPrice.text = "$" + Constants.getValByMathWay(item.price ?: 0.0) + " x " + item.qty

            Glide.with(holder.itemView)
                .load(Constants.IMG_URL + item.picname)
                .into(goodsImg)
        }
    }

    private fun checkPrice(price: Double, qty: Int): Double {
        return price * qty
    }

    override fun getItemCount(): Int {
        return _cartGoods.size
    }


}