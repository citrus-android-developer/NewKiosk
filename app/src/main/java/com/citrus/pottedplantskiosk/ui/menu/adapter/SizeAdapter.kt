package com.citrus.pottedplantskiosk.ui.menu.adapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.Size
import com.citrus.pottedplantskiosk.databinding.ItemSizeBinding
import com.citrus.pottedplantskiosk.di.prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SizeAdapter(val context: Context) : RecyclerView.Adapter<SizeAdapter.SizeViewHolder>() {

    class SizeViewHolder(val binding: ItemSizeBinding) :
        RecyclerView.ViewHolder(binding.root)


    suspend fun updateDataset(newDataset: List<Size>) =
        withContext(Dispatchers.Default) {
            val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return sizeList.size
                }

                override fun getNewListSize(): Int {
                    return newDataset.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return sizeList[oldItemPosition] == newDataset[newItemPosition]
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return sizeList[oldItemPosition] == newDataset[newItemPosition]
                }
            })
            withContext(Dispatchers.Main) {
                sizeList = newDataset.map { it.copy() }
                diff.dispatchUpdatesTo(this@SizeAdapter)
            }
        }

    var sizeList = listOf<Size>()


    override fun getItemCount(): Int {
        return sizeList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        return SizeViewHolder(
            ItemSizeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }



    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        val item = sizeList[position]

        holder.setIsRecyclable(false)
        holder.binding.apply {
            tvSize.setTextColor(ContextCompat.getColor(context, R.color.noClick))
            tvSize.text = item.desc

            if (prefs.languagePos != 1 && item.sName2.isNotEmpty()) {
                tvSize.text = item.sName2
            }

            root.setOnClickListener {
                sizeList.forEach { it.isCheck = false }
                item.isCheck = true
                notifyDataSetChanged()
                onSizeClickListener?.let { click ->
                    click(item)
                }
            }

            if (item.isCheck) {
                tvSize.backgroundTintList = ContextCompat.getColorStateList(context, R.color.lightGreen)
                tvSize.setTextColor(ContextCompat.getColor(context, R.color.colorDeepOrange))
                tvSize.elevation = 3f
            } else {
                if (item.stock == "0") {
                    tvSize.elevation = 0f
                    tvSize.setTextColor(ContextCompat.getColor(context, R.color.colorLightGray2))
                    tvSize.backgroundTintList = ContextCompat.getColorStateList(context, R.color.grayOverlay2)
                    tvSize.isEnabled = false
                } else {
                    tvSize.elevation = 3f
                    tvSize.setTextColor(ContextCompat.getColor(context, R.color.selector_text))
                    tvSize.backgroundTintList = ContextCompat.getColorStateList(context, R.color.colorWhite)
                    tvSize.isEnabled = true
                }
            }
        }
    }


    private var onSizeClickListener: ((Size) -> Unit)? = null

    fun setOnSizeClickListener(listener: (Size) -> Unit) {
        onSizeClickListener = listener
    }

}