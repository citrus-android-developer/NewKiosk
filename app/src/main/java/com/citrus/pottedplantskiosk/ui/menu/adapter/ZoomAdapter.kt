package com.citrus.pottedplantskiosk.ui.menu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.Good
import com.citrus.pottedplantskiosk.databinding.ZoomItemViewBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.Constants
import com.citrus.pottedplantskiosk.util.Constants.df
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.skydoves.elasticviews.ElasticAnimation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.graphics.drawable.Drawable

import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target


class ZoomAdapter(val context: Context, private val lifecycle: LifecycleCoroutineScope) :
    RecyclerView.Adapter<ZoomAdapter.ZoomViewHolder>() {
    class ZoomViewHolder(val binding: ZoomItemViewBinding) :
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
            diff.dispatchUpdatesTo(this@ZoomAdapter)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZoomViewHolder {
        return ZoomViewHolder(
            ZoomItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun onBindViewHolder(
        holder: ZoomViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.setIsRecyclable(false)
        val item = cartGoods[position]
        holder.binding.apply {
            item.qty = 1
            rvSize.isVisible = false
            numberPicker.isVisible = false
            if (prefs.languagePos == 1) {
                tvName.text = item.gName2
            } else {
                tvName.text = item.gName
            }

            tvPrice.text = "$" + item.price

            Glide.with(root)
                .applyDefaultRequestOptions(
                    RequestOptions()
                        .placeholder(R.drawable.ic_image_gallery__2_)
                        .error(R.drawable.ic_image_gallery__2_)
                )
                .load(Constants.IMG_URL + item.picName)
                .into(photo)

            addCart.setOnClickListener { v ->
                ElasticAnimation(v)
                    .setScaleX(0.85f)
                    .setScaleY(0.85f)
                    .setDuration(50)
                    .setOnFinishListener {
                        onItemClickListener?.let { click ->
                            click(item)
                        }
                    }
                    .doAction()
            }

            root.setTransitionListener(object : TransitionAdapter() {
                override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                    if (currentId == R.id.end) {
                        if (ifNeedSize(item)) {
                            rvSize.isVisible = true
                            val sizeAdapter = SizeAdapter(context)
                            val layoutManager = FlexboxLayoutManager(context)
                            layoutManager.flexDirection = FlexDirection.ROW
                            layoutManager.justifyContent = JustifyContent.FLEX_START
                            rvSize.apply {
                                this.layoutManager = layoutManager
                                adapter = sizeAdapter
                            }
                            lifecycle.launch {
                                sizeAdapter.updateDataset(item.size!!)
                                numberPicker.isVisible = true
                            }

                            sizeAdapter.setOnSizeClickListener { choseSize ->
                                item.gID = choseSize.gID
                                item.price = choseSize.price
                                sumPriceCount(tvPrice, item.qty, item.price)
                                item.size?.forEach { size ->
                                    size.isCheck = size.desc == choseSize.desc
                                }
                            }
                        } else {
                            rvSize.isVisible = false
                            numberPicker.isVisible = true
                        }
                    }

                }
            })

            numberPicker.setOnBtnClickListener {
                item.qty = it
                sumPriceCount(tvPrice, item.qty, item.price)
            }
        }
    }

    fun sumPriceCount(tvPrice: TextView, qty: Int, price: Double): Double {
        val sumPrice = price * qty
        setPriceText(sumPrice, tvPrice)
        return sumPrice
    }

    private fun setPriceText(sumPrice: Double, tvPrice: TextView) {
        tvPrice.text = "$ " + df.format(sumPrice)
    }

    override fun getItemCount(): Int {
        return cartGoods.size
    }

    private fun ifNeedSize(goods: Good): Boolean {
        if (goods.size?.isNotEmpty() == true) {
            return if (goods.size?.size == 1 && goods.size?.get(0)?.desc == ".") {
                false
            } else {
                if (!goods.isEdit) {
                    goods.size?.get(0)?.isCheck = true
                } else {
                    val checkSize = goods.size?.first { it.isCheck }
                    goods.gID = checkSize!!.gID
                    goods.price = checkSize!!.price
                }
                true
            }
        } else {
            return false
        }
    }

    private var onItemClickListener: ((Good) -> Unit)? = null

    fun setOnItemClickListener(listener: (Good) -> Unit) {
        onItemClickListener = listener
    }

}