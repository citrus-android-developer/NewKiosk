package com.citrus.pottedplantskiosk.ui.menu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.citrus.pottedplantskiosk.api.remote.dto.PayWay
import com.citrus.pottedplantskiosk.databinding.PayWayItemViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PayWayAdapter(val context: Context) : RecyclerView.Adapter<PayWayAdapter.PayWayViewHolder>() {

    class PayWayViewHolder(val binding: PayWayItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    suspend fun updateDataset(newDataset: List<PayWay>) =
        withContext(Dispatchers.Default) {
            val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return payList.size
                }

                override fun getNewListSize(): Int {
                    return newDataset.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return payList[oldItemPosition] == newDataset[newItemPosition]
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return payList[oldItemPosition] == newDataset[newItemPosition]
                }
            })
            withContext(Dispatchers.Main) {
                payList = newDataset.map { it.copy() }
                diff.dispatchUpdatesTo(this@PayWayAdapter)
            }
        }

    var payList = listOf<PayWay>()


    override fun getItemCount(): Int {
        return payList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayWayViewHolder {
        return PayWayViewHolder(
            PayWayItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }



    override fun onBindViewHolder(holder: PayWayViewHolder, position: Int) {
        val item = payList[position]
        holder.binding.apply {

        }
    }

    private var onPayWayClickListener: ((PayWay) -> Unit)? = null
    fun setOnPayWayClickListener(listener: (PayWay) -> Unit) {
        onPayWayClickListener = listener
    }

}