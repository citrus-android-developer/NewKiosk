package com.citrus.pottedplantskiosk.api.remote.dto

import com.citrus.pottedplantskiosk.R


class DataBean {
    var imageRes: Int? = null
    var imageUrl: String? = null
    var title: String?
    var viewType: Int

    constructor(imageRes: Int?, title: String?, viewType: Int) {
        this.imageRes = imageRes
        this.title = title
        this.viewType = viewType
    }

    constructor(imageUrl: String?, title: String?, viewType: Int) {
        this.imageUrl = imageUrl
        this.title = title
        this.viewType = viewType
    }

    companion object {
        val testData: List<DataBean>
            get() {
                val list: MutableList<DataBean> = ArrayList()
                list.add(
                    DataBean(
                         imageRes = R.drawable.plants1,
                        null,
                        1
                    )
                )
                list.add(
                    DataBean(
                        imageRes =  R.drawable.plants2,
                        null,
                        1
                    )
                )
                list.add(
                    DataBean(
                        imageRes = R.drawable.plants3,
                        null,
                        1
                    )
                )
                list.add(
                    DataBean(
                        imageRes = R.drawable.plants4,
                        null,
                        1
                    )
                )
                list.add(
                    DataBean(
                        imageRes = R.drawable.plants5,
                        null,
                        1
                    )
                )
                return list
            }
    }
}