package com.citrus.pottedplantskiosk.ui.menu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.Good
import com.citrus.pottedplantskiosk.databinding.ItemContainerGoodsBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GoodsPageAdapter @Inject constructor(val context: Context) :
    RecyclerView.Adapter<GoodsPageAdapter.GoodsPageViewHolder>() {
    class GoodsPageViewHolder(val binding: ItemContainerGoodsBinding) :
        RecyclerView.ViewHolder(binding.root)

    var goods = listOf<Good>()

    suspend fun updateDataset(newDataset: List<Good>) = withContext(Dispatchers.Default) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return goods.size
            }

            override fun getNewListSize(): Int {
                return newDataset.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return true
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return goods[oldItemPosition] == newDataset[newItemPosition]
            }

        })
        withContext(Dispatchers.Main) {
            goods = newDataset.map { it.copy() }
            diff.dispatchUpdatesTo(this@GoodsPageAdapter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsPageViewHolder {
        return GoodsPageViewHolder(
            ItemContainerGoodsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: GoodsPageViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = goods[position]
        holder.binding.apply {

            Glide.with(holder.itemView)
                .load(Constants.IMG_URL + item.picName)
                .into(photo)

            if(prefs.languagePos == 1){
                textTitle.text = item.gName2.substring(3,item.gName2.length)
                textTitle.textSize = context.resources.getDimension(R.dimen.sp_6)
            }else{
                textTitle.text = item.gName.substring(3,item.gName.length)
                textTitle.textSize = context.resources.getDimension(R.dimen.sp_5)
            }


            goodsPrice.text = item.price.toString()

        }
    }

    override fun getItemCount(): Int {
        return goods.size
    }


    private var onItemClickListener: ((View,Good,List<Good>) -> Unit)? = null

    fun setOnItemClickListener(listener: (View,Good,List<Good>) -> Unit) {
        onItemClickListener = listener
    }
}