package com.citrus.pottedplantskiosk.ui.setting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.citrus.pottedplantskiosk.databinding.SpinnerLayoutBinding
import com.skydoves.powerspinner.*


data class UsbNameWithID(val name: String, val id: Int)

class MySpinnerAdapter(
    powerSpinnerView: PowerSpinnerView
) : RecyclerView.Adapter<MySpinnerAdapter.MySpinnerViewHolder>(),
    PowerSpinnerInterface<UsbNameWithID> {

    override var index: Int = powerSpinnerView.selectedIndex
    override val spinnerView: PowerSpinnerView = powerSpinnerView
    override var onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener<UsbNameWithID>? = null

    private val spinnerItems: MutableList<UsbNameWithID> = arrayListOf()

    class MySpinnerViewHolder(val binding: SpinnerLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MySpinnerViewHolder {
        return MySpinnerViewHolder(
            SpinnerLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MySpinnerViewHolder, position: Int) {
        holder.binding.title.text = spinnerItems[position].name
        holder.itemView.setOnClickListener {
            notifyItemSelected(position)
        }
    }

    override fun notifyItemSelected(index: Int) {
        if (index == -1) return

        val item = spinnerItems[index]
        val oldIndex = this.index
        this.index = index
        this.spinnerView.notifyItemSelected(index, this.spinnerItems[index].name)
        this.onSpinnerItemSelectedListener?.onItemSelected(
            oldIndex = oldIndex,
            oldItem = oldIndex.takeIf { it != -1 }?.let { spinnerItems[oldIndex] },
            newIndex = index,
            newItem = item
        )
    }

    override fun getItemCount(): Int = this.spinnerItems.size

    override fun setItems(itemList: List<UsbNameWithID>) {
        this.spinnerItems.clear()
        this.spinnerItems.addAll(itemList)
        notifyDataSetChanged()
    }

}