package com.citrus.pottedplantskiosk.ui.menu.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.Good
import com.citrus.pottedplantskiosk.databinding.GoodsItemViewBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.Constants
import com.skydoves.elasticviews.ElasticAnimation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoodsItemAdapter (val context: Context, private val onItemClick:(Good,List<Good>) -> Unit) :
    RecyclerView.Adapter<GoodsItemAdapter.GoodsItemViewHolder>() {
    class GoodsItemViewHolder(val binding: GoodsItemViewBinding) :
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
            diff.dispatchUpdatesTo(this@GoodsItemAdapter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsItemViewHolder {
        return GoodsItemViewHolder(
            GoodsItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: GoodsItemViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = goods[position]
        holder.binding.apply {

            Glide.with(holder.itemView)
                .load(Constants.IMG_URL + item.picName)
                .into(itemImage)

            if(prefs.languagePos == 1){
                tvItemName.text = item.gName2.substring(3,item.gName2.length)
                tvItemName.textSize = context.resources.getDimension(R.dimen.sp_6)
            }else{
                tvItemName.text = item.gName.substring(3,item.gName.length)
                tvItemName.textSize = context.resources.getDimension(R.dimen.sp_5)
            }


            tvPrice.text = "$" + item.price.toString()

            root.setOnClickListener { v ->
                ElasticAnimation(v)
                    .setScaleX(0.85f)
                    .setScaleY(0.85f)
                    .setDuration(50)
                    .setOnFinishListener {
                        onItemClick(item,goods)
                    }
                    .doAction()
            }
        }
    }

    override fun getItemCount(): Int {
        return goods.size
    }

}