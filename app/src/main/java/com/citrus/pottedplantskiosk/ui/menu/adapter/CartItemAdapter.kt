package com.citrus.pottedplantskiosk.ui.menu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.citrus.pottedplantskiosk.api.remote.dto.MockGoods
import com.citrus.pottedplantskiosk.databinding.CartItemViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartItemAdapter @Inject constructor(val context: Context):
    RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>(){
    class CartItemViewHolder(val binding: CartItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    var cartGoods = listOf<MockGoods>()

    suspend fun updateDataset(newDataset: List<MockGoods>) = withContext(Dispatchers.Default) {
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
            tvItemName.text = item.ItemName
        }
    }

    override fun getItemCount(): Int {
        return cartGoods.size
    }


}