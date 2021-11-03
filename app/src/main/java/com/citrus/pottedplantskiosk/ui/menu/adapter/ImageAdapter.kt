package com.citrus.pottedplantskiosk.ui.menu.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.citrus.pottedplantskiosk.api.remote.dto.BannerData
import com.citrus.pottedplantskiosk.databinding.BannerImageBinding
import com.citrus.pottedplantskiosk.util.Constants
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.util.BannerUtils


class ImageAdapter(datas: List<BannerData>?):BannerAdapter<BannerData, ImageAdapter.ImageHolder>(
    datas
) {
    class ImageHolder(val binding: BannerImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(
            BannerImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindView(holder: ImageHolder, data: BannerData?, position: Int, size: Int) {
        holder.binding.apply {
            data?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    BannerUtils.setBannerRound(image, 20f)
                }

                Glide.with(holder!!.itemView)
                    .load(Constants.IMG_URL + data.pic)
                    .apply(RequestOptions.bitmapTransform( RoundedCorners(60)))
                    .into(image)
            }
        }
    }

}