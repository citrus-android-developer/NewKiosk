package com.citrus.pottedplantskiosk.ui.menu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.databinding.MainGroupItemViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainGroupItemAdapter @Inject constructor(val context: Context) :
    RecyclerView.Adapter<MainGroupItemAdapter.GroupItemViewHolder>() {
    class GroupItemViewHolder(val binding: MainGroupItemViewBinding) :
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
            diff.dispatchUpdatesTo(this@MainGroupItemAdapter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupItemViewHolder {
        return GroupItemViewHolder(
            MainGroupItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: GroupItemViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val title = groupTitles[position]
        holder.binding.apply {
            mainTitle.text = title

            if (kindIndex == position) {
                selectLine.visibility = View.VISIBLE
                mainTitle.setTypeface(mainTitle.typeface, Typeface.BOLD)
                mainTitle.setTextColor(context.resources.getColor(R.color.greenDark))
                mainTitle.alpha = 1f
            } else {
                selectLine.visibility = View.INVISIBLE
                mainTitle.typeface = null
                mainTitle.setTextColor(context.resources.getColor(R.color.greenDark))
                mainTitle.alpha = 0.5f
            }

            root.setOnClickListener {
                if (position != kindIndex) {
                    kindIndex = position
                    notifyDataSetChanged()
                    onClickListener?.let { click ->
                        click(title)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return groupTitles.size
    }


    private var onClickListener: ((String) -> Unit)? = null

    fun setOnMainGroupNameClickListener(listener: (String) -> Unit) {
        onClickListener = listener
    }
}