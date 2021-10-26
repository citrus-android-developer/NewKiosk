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
import com.citrus.pottedplantskiosk.util.Constants.df


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
            tvGoodsName.text = item.gName
            tvQtyPrice.text = "$" + item.price + " x " + item.qty

            Glide.with(holder.itemView)
                .load(Constants.IMG_URL + item.picName)
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