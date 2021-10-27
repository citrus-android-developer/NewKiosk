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
import com.citrus.pottedplantskiosk.databinding.GroupNameItemViewBinding
import com.skydoves.elasticviews.ElasticAnimation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GroupItemAdapter @Inject constructor(val context: Context) :
    RecyclerView.Adapter<GroupItemAdapter.GroupItemViewHolder>() {
    class GroupItemViewHolder(val binding: GroupNameItemViewBinding) :
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
            diff.dispatchUpdatesTo(this@GroupItemAdapter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupItemViewHolder {
        return GroupItemViewHolder(
            GroupNameItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: GroupItemViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val name = groupTitles[position]
        holder.binding.apply {
            title.text = name

            if (kindIndex == position) {
                cardView.background =
                    ContextCompat.getDrawable(context, R.drawable.button_mid_green_15)
                title.setTypeface(title.typeface, Typeface.BOLD)
                title.setTextColor(context.resources.getColor(R.color.colorPrimaryText))
            } else {
                cardView.background =
                    ContextCompat.getDrawable(context, R.drawable.button_light_45)
                title.typeface = null
                title.setTextColor(context.resources.getColor(R.color.colorSecondText))
            }

            root.setOnClickListener { v ->
                ElasticAnimation(v)
                    .setScaleX(0.85f)
                    .setScaleY(0.85f)
                    .setDuration(50)
                    .setOnFinishListener {
                        if (position != kindIndex) {
                            kindIndex = position
                            notifyDataSetChanged()
                            onDescClickListener?.let { click ->
                                click(name)
                            }
                        }
                    }.doAction()
            }
        }
    }

    override fun getItemCount(): Int {
        return groupTitles.size
    }

    fun resetKindIndex(){
        kindIndex = 0
    }


    private var onDescClickListener: ((String) -> Unit)? = null

    fun setOnDescClickListener(listener: (String) -> Unit) {
        onDescClickListener = listener
    }
}