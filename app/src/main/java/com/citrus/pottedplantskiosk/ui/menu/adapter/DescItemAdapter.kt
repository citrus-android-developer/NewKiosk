package com.citrus.pottedplantskiosk.ui.menu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.databinding.DescItemViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DescItemAdapter @Inject constructor(val context: Context) :
    RecyclerView.Adapter<DescItemAdapter.DescItemViewHolder>() {
    class DescItemViewHolder(val binding: DescItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    var groupTitles = listOf<String>()
    private var kindIndex = 0

    suspend fun updateDataset(newDataset: List<String>) = withContext(Dispatchers.Default) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return groupTitles.size
            }

            override fun getNewListSize(): Int {
                return newDataset.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return true
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return groupTitles[oldItemPosition] == newDataset[newItemPosition]
            }

        })
        withContext(Dispatchers.Main) {
            groupTitles = newDataset
            diff.dispatchUpdatesTo(this@DescItemAdapter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescItemViewHolder {
        return DescItemViewHolder(
            DescItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: DescItemViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val title = groupTitles[position]
        holder.binding.apply {
            tvTitle.text = title
            if (kindIndex == position) {
                tvTitle.background =
                    ContextCompat.getDrawable(context, R.drawable.button_mid_green_15)
                tvTitle.setTypeface(tvTitle.typeface, Typeface.BOLD)
                tvTitle.setTextColor(context.resources.getColor(R.color.colorPrimaryText))
            } else {
                tvTitle.background =
                    ContextCompat.getDrawable(context, R.drawable.button_light_45)
                tvTitle.typeface = null
                tvTitle.setTextColor(context.resources.getColor(R.color.colorSecondText))
            }

            root.setOnClickListener {
                if (position != kindIndex) {
                    kindIndex = position
                    notifyDataSetChanged()
                    onKindClickListener?.let { click ->
                        click(title)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return groupTitles.size
    }


    private var onKindClickListener: ((String) -> Unit)? = null

    fun setOnKindClickListener(listener: (String) -> Unit) {
        onKindClickListener = listener
    }
}