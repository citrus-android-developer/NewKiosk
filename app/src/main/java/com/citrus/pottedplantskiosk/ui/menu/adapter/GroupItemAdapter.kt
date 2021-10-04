package com.citrus.pottedplantskiosk.ui.menu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.databinding.GroupItemViewBinding
import com.citrus.pottedplantskiosk.ui.menu.MenuViewModel
import com.citrus.pottedplantskiosk.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GroupItemAdapter @Inject constructor(val context: Context) :
    RecyclerView.Adapter<GroupItemAdapter.GroupItemViewHolder>() {
    class GroupItemViewHolder(val binding: GroupItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    var groupTitles = listOf<MenuViewModel.GroupItem>()
    private var kindIndex = 0

    suspend fun updateDataset(newDataset: List<MenuViewModel.GroupItem>) = withContext(Dispatchers.Default) {
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
            diff.dispatchUpdatesTo(this@GroupItemAdapter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupItemViewHolder {
        return GroupItemViewHolder(
            GroupItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: GroupItemViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val title = groupTitles[position]
        holder.binding.apply {

            title.imgUrl?.let {
                Glide.with(holder.itemView)
                    .load(Constants.IMG_URL + it)
                    .into(itemImage)
            } ?: run {
                itemImage.setImageResource(R.drawable.ic_image_gallery__2_)
            }

            tvGroupName.text = title.name
            if (kindIndex == position) {
                imgCard.background =
                    ContextCompat.getDrawable(context, R.drawable.button_mid_green_15)
                tvGroupName.setTypeface(tvGroupName.typeface, Typeface.BOLD)
                tvGroupName.setTextColor(context.resources.getColor(R.color.colorPrimaryText))
            } else {
                imgCard.background =
                    ContextCompat.getDrawable(context, R.drawable.button_light_45)
                tvGroupName.typeface = null
                tvGroupName.setTextColor(context.resources.getColor(R.color.colorSecondText))
            }

            root.setOnClickListener {
                if (position != kindIndex) {
                    kindIndex = position
                    notifyDataSetChanged()
                    onKindClickListener?.let { click ->
                        click(title.name)
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