package com.citrus.pottedplantskiosk.ui.menu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.citrus.pottedplantskiosk.api.remote.dto.Good
import com.citrus.pottedplantskiosk.api.remote.dto.MockGoods
import com.citrus.pottedplantskiosk.databinding.ZoomItemViewBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.Constants
import com.skydoves.elasticviews.ElasticAnimation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ZoomAdapter @Inject constructor(val context: Context) :
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
        val item = cartGoods[position]
        holder.binding.apply {
            ViewCompat.setTransitionName(photo, item.gName2)
            Glide.with(root)
                .load(
                  Constants.IMG_URL + item.picName
                )
                .into(photo)

            //collapsingToolbar.title = if(prefs.languagePos == 1) item.gName2.substring(3, item.gName2.length) else item.gName.substring(3, item.gName.length)

            when {
                position == 0 -> {
                    onPosChangeListener?.let { click ->
                        click(0)
                    }
                }
                position < cartGoods.size-1 -> {
                    onPosChangeListener?.let { click ->
                        click(1)
                    }
                }
                else -> {
                    onPosChangeListener?.let { click ->
                        click(-1)
                    }
                }
            }




        }
    }

    override fun getItemCount(): Int {
        return cartGoods.size
    }


    private var onPosChangeListener: ((Int) -> Unit)? = null

    fun setOnPosChangeListener(listener: (Int) -> Unit) {
        onPosChangeListener = listener
    }

}