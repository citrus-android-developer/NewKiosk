package com.citrus.pottedplantskiosk.ui.menu.adapter



import android.content.Context
import android.graphics.Paint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.Good
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.Constants
import com.skydoves.elasticviews.ElasticAnimation
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import kotlinx.android.synthetic.main.goods_header_view.view.*
import kotlinx.android.synthetic.main.goods_item_view.view.*


class MenuSectionAdapter (
    val context: Context,
    private val header: String,
    private val goodsList: List<Good>,
    private val onItemClick:(Good,List<Good>) -> Unit
) :
    Section(
        SectionParameters.builder()
            .itemResourceId(R.layout.goods_item_view)
            .headerResourceId(R.layout.goods_header_view)
            .build()
    ) {

    override fun getContentItemsTotal(): Int {
        return goodsList.size
    }

    override fun getHeaderViewHolder(view: View): ViewHolder {
        return HeaderViewHolder(view)
    }

    override fun getItemViewHolder(view: View): ViewHolder {
        return ItemViewHolder(view)
    }

    override fun onBindHeaderViewHolder(holder: ViewHolder) {
        val headerHolder = holder as HeaderViewHolder
        headerHolder.tvTypeName.text = header
        headerHolder.tvTypeName.paint.flags = Paint.UNDERLINE_TEXT_FLAG
    }


    override fun onBindItemViewHolder(holder: ViewHolder, position: Int) {
        val itemHolder = holder as ItemViewHolder
        var goods = goodsList[position]
        Glide.with(holder.itemView)
            .load(Constants.IMG_URL + goods.picName)
            .into(itemHolder.itemImage)

        if(prefs.languagePos == 1){
            itemHolder.tvItemName.text = goods.gName2.substring(3,goods.gName2.length)
            itemHolder.tvItemName.textSize = context.resources.getDimension(R.dimen.sp_6)
        }else{
            itemHolder.tvItemName.text = goods.gName.substring(3,goods.gName.length)
            itemHolder.tvItemName.textSize = context.resources.getDimension(R.dimen.sp_5)
        }

        itemHolder.tvPrice.text = "$" + goods.price.toString()

        itemHolder.itemView.setOnClickListener { v ->
            ElasticAnimation(v)
                .setScaleX(0.85f)
                .setScaleY(0.85f)
                .setDuration(50)
                .setOnFinishListener {
                    onItemClick(goods,goodsList)
                }
                .doAction()
        }
    }

    internal inner class HeaderViewHolder(itemView: View) : ViewHolder(itemView) {
        val tvTypeName:TextView = itemView.tvTypeName
    }

    internal inner class ItemViewHolder(itemView: View) : ViewHolder(itemView) {
        val itemImage: ImageView = itemView.itemImage
        val tvItemName:TextView = itemView.tvItemName
        val tvPrice:TextView = itemView.tvPrice
    }
}