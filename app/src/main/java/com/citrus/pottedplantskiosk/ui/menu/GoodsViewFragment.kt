package com.citrus.pottedplantskiosk.ui.menu

import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.citrus.pottedplantskiosk.api.remote.dto.Good
import com.citrus.pottedplantskiosk.databinding.FragmentGoodsViewBinding
import com.citrus.pottedplantskiosk.ui.menu.adapter.GoodsPageAdapter
import com.citrus.pottedplantskiosk.util.base.BindingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject



@AndroidEntryPoint
class GoodsViewFragment : BindingFragment<FragmentGoodsViewBinding>() {
    private val menuViewModel: MenuViewModel by activityViewModels()
    private val args: GoodsViewFragmentArgs by navArgs()
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentGoodsViewBinding::inflate

    @Inject
    lateinit var goodsPageAdapter: GoodsPageAdapter
    private var updateGoodsPageJob: Job? = null

    private var currentIndex: Int = 0
    private var goodsList:List<Good> = listOf()

    override fun initView() {
        goodsList = menuViewModel.kindList.value[0].goods
        currentIndex = goodsList.indexOf(args.goods)

        binding.apply {
            goodsViewPager.adapter = goodsPageAdapter

        }

        updateGoodsPageJob?.cancel()
        updateGoodsPageJob = lifecycleScope.launch {
            goodsPageAdapter.updateDataset(goodsList)
            binding.goodsViewPager.currentItem = currentIndex
        }

    }

    override fun initObserve() {
        Log.e("initObserve","notDefine")
    }

    override fun initAction() {
        Log.e("initAction","notDefine")
    }


}