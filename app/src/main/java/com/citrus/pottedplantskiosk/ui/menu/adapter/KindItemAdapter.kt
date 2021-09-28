package com.citrus.pottedplantskiosk.ui.menu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.citrus.pottedplantskiosk.api.remote.dto.Good
import com.citrus.pottedplantskiosk.api.remote.dto.Kind
import com.citrus.pottedplantskiosk.databinding.InsideRecyclerItemBinding
import com.skydoves.elasticviews.ElasticAnimation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class KindItemAdapter(
    val context: Context,
    private val lifecycle: LifecycleCoroutineScope,
    private val onItemClick: (Good, List<Good>) -> Unit,
    private val onViewMoreClick: (String, List<Good>) -> Unit
) :
    RecyclerView.Adapter<KindItemAdapter.KindItemViewHolder>() {
    class KindItemViewHolder(val binding: InsideRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    var kindList = listOf<Kind>()

    suspend fun updateDataset(newDataset: List<Kind>) = withContext(Dispatchers.Default) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return kindList.size
            }

            override fun getNewListSize(): Int {
                return newDataset.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return true
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return kindList[oldItemPosition] == newDataset[newItemPosition]
            }

        })
        withContext(Dispatchers.Main) {
            kindList = newDataset
            diff.dispatchUpdatesTo(this@KindItemAdapter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KindItemViewHolder {
        return KindItemViewHolder(
            InsideRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: KindItemViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val kind = kindList[position]
        holder.binding.apply {

            val goodsAdapter = GoodsItemAdapter(
                context,
                onItemClick = { goods, list ->
                    onItemClick(goods, list)
                }
            )

            goodsRv.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter = goodsAdapter
            }

            lifecycle.launch {
                goodsAdapter.updateDataset(kind.goods)
                goodsRv.scheduleLayoutAnimation()
                tvTypeName.text = kind.desc
                tvTypeName.paint.flags = Paint.UNDERLINE_TEXT_FLAG
                viewMore.isVisible = true

            }

            viewMore.setOnClickListener { v ->
                ElasticAnimation(v)
                    .setScaleX(0.85f)
                    .setScaleY(0.85f)
                    .setDuration(50)
                    .setOnFinishListener {
                        onViewMoreClick(kind.desc, kind.goods)
                    }
                    .doAction()
            }


        }
    }

    override fun getItemCount(): Int {
        return kindList.size
    }
}