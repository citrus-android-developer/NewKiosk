package com.citrus.pottedplantskiosk.ui.menu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
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
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.citrus.pottedplantskiosk.util.Constants.clickAnimation


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


//            val rnds = (1..100).random()
//            item.tax = (100 / rnds).toDouble()

            if (item.isEdit) {
                item.qty = item._qty
                numberPicker.setValue(item._qty)
                tvPrice.text = "$ " + Constants.getValByMathWay(item._sPrice)
                addCart.text = "DONE"
            } else {
                item.qty = 1
            }

            rvSize.isVisible = false
            numberPicker.isVisible = false
            if (prefs.languagePos == 1) {
                tvName.text = item.gName2
            } else {
                tvName.text = item.gName
            }

            tvDescribe.text = item.note?.let {
                it.ifEmpty {
                    if (prefs.languagePos == 1) item.gName2 else item.gName
                }
            }.run {
                if (prefs.languagePos == 1) item.gName2 else item.gName
            }

            tvPrice.text = "$" + Constants.getValByMathWay(item._sPrice)

            tvPrice.setOnClickListener {
                item.price = 100.0
            }

            Glide.with(root)
                .load(Constants.IMG_URL + item.picname)
                .fallback(R.drawable.ic_image_gallery__2_)
                .error(R.drawable.ic_image_gallery__2_)
                .into(photo)

            addCart.setOnClickListener {
                it.clickAnimation {
                    onItemClickListener?.let { click ->
                        click(item)
                    }
                }
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
                                tvPrice.text = "$ " + Constants.getValByMathWay(item.sPrice)
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

            numberPicker.setOnButtonClickListener { _, currentNum ->
                if (currentNum == 0) {
                    numberPicker.setValue(1)
                    return@setOnButtonClickListener
                }
                item.qty = currentNum
                tvPrice.text = "$ " + Constants.getValByMathWay(item.sPrice)
            }
        }
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
                    goods.price = checkSize.price
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