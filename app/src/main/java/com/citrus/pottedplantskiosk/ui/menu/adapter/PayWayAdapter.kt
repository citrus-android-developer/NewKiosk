package com.citrus.pottedplantskiosk.ui.menu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.citrus.pottedplantskiosk.api.remote.dto.PayWay
import com.citrus.pottedplantskiosk.api.remote.dto.payWayList
import com.citrus.pottedplantskiosk.databinding.PayWayItemViewBinding
import com.citrus.pottedplantskiosk.util.Constants.clickAnimation
import com.skydoves.elasticviews.ElasticAnimation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PayWayAdapter(val context: Context) : RecyclerView.Adapter<PayWayAdapter.PayWayViewHolder>() {

    class PayWayViewHolder(val binding: PayWayItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    var payList = payWayList()

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
            payWayImg.setImageDrawable(context.resources.getDrawable(item.imgRes,null))
            payWayHint.text = item.desc


            root.setOnClickListener {
                it.clickAnimation {
                    onPayWayClickListener?.let {  click ->
                        click(item)
                    }
                }
            }
        }
    }

    private var onPayWayClickListener: ((PayWay) -> Unit)? = null
    fun setOnPayWayClickListener(listener: (PayWay) -> Unit) {
        onPayWayClickListener = listener
    }

}