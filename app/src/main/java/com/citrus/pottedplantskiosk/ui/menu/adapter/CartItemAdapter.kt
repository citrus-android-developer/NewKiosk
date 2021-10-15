package com.citrus.pottedplantskiosk.ui.menu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.Good
import com.citrus.pottedplantskiosk.databinding.CartItemViewBinding
import com.citrus.pottedplantskiosk.util.Constants
import com.citrus.pottedplantskiosk.util.Constants.df
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartItemAdapter @Inject constructor(val context: Context) :
    RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {
    class CartItemViewHolder(val binding: CartItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    var cartGoods = listOf<Good>()

    suspend fun updateDataset(newDataset: List<Good>) = withContext(Dispatchers.Default) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return cartGoods.size
            }

            override fun getNewListSize(): Int {
                return newDataset.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return true
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return cartGoods[oldItemPosition] == newDataset[newItemPosition]
            }

        })
        withContext(Dispatchers.Main) {
            cartGoods = newDataset.map { it.copy() }
            diff.dispatchUpdatesTo(this@CartItemAdapter)
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
        val item = cartGoods[position]
        holder.binding.apply {
            tvItemName.text = item.gName

            itemNo.text = (position + 1).toString()

            Glide.with(holder.itemView)
                .load(Constants.IMG_URL + item.picName)
                .into(itemImage)

            tvPrice.text = "$" + df.format(checkPrice(item.price, item.qty))

            numberPicker.setValue(item.qty)
            numberPicker.setTextSize(R.dimen.sp_7)

            numberPicker.setOnBtnClickListener { value ->
                item.qty = value
                item.sPrice = checkPrice(item.price, item.qty)
                tvPrice.text = "$" + df.format(item.sPrice)
            }

            numberPicker.setOnRemoveItemListener {
                onItemDeleteListener?.let { remove ->
                    remove(item)
                }
            }


        }
    }

    private fun checkPrice(price: Double, qty: Int): Double {
        return price * qty
    }

    override fun getItemCount(): Int {
        return cartGoods.size
    }


    private var onItemDeleteListener: ((Good) -> Unit)? = null
    fun setOnItemDeleteListener(listener: (Good) -> Unit) {
        onItemDeleteListener = listener
    }

}