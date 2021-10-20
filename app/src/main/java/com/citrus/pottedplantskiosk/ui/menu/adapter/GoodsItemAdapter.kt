package com.citrus.pottedplantskiosk.ui.menu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.Good
import com.citrus.pottedplantskiosk.databinding.GoodsItemViewBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.Constants
import com.skydoves.elasticviews.ElasticAnimation
import javax.inject.Inject

class GoodsItemAdapter @Inject constructor(val context: Context) :
    RecyclerView.Adapter<GoodsItemAdapter.GoodsItemViewHolder>() {
    class GoodsItemViewHolder(val binding: GoodsItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    var goods = listOf<Good>()

    fun setGoodsList(newDataset: List<Good>){
        goods = newDataset.map { it.deepCopy() }
        notifyDataSetChanged()
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

            Glide.with(root)
                .applyDefaultRequestOptions(
                    RequestOptions()
                        .placeholder(R.drawable.ic_image_gallery__2_)
                        .error(R.drawable.ic_image_gallery__2_)
                )
                .load(Constants.IMG_URL + item.picName)
                .into(itemImage)

            if (prefs.languagePos == 1) {
                tvItemName.text = item.gName2
            } else {
                tvItemName.text = item.gName
            }


            tvPrice.text = "$" + item.price.toString()

            root.setOnClickListener { v ->
                ElasticAnimation(v)
                    .setScaleX(0.85f)
                    .setScaleY(0.85f)
                    .setDuration(50)
                    .setOnFinishListener {
                        onClickListener?.let {  click ->
                            click(item,goods)
                        }
                    }
                    .doAction()
            }
        }
    }

    override fun getItemCount(): Int {
        return goods.size
    }

    private var onClickListener: ((Good,List<Good>) -> Unit)? = null

    fun setOnGoodsClickListener(listener: (Good,List<Good>) -> Unit) {
        onClickListener = listener
    }

}