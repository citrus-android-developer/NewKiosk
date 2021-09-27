package com.citrus.pottedplantskiosk.ui.menu


import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.databinding.FragmentMenuBinding
import com.citrus.pottedplantskiosk.ui.menu.adapter.DescItemAdapter
import com.citrus.pottedplantskiosk.ui.menu.adapter.GoodsItemAdapter
import com.citrus.pottedplantskiosk.ui.menu.adapter.GroupItemAdapter
import com.citrus.pottedplantskiosk.util.base.BindingFragment
import com.skydoves.transformationlayout.onTransformationStartContainer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MenuFragment : BindingFragment<FragmentMenuBinding>() {
    private val menuViewModel: MenuViewModel by activityViewModels()
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentMenuBinding::inflate

    @Inject
    lateinit var groupItemAdapter: GroupItemAdapter
    private var updateGroupItemJob: Job? = null

    @Inject
    lateinit var goodsItemAdapter: GoodsItemAdapter
    private var updateGoodsItemJob: Job? = null


    @Inject
    lateinit var descItemAdapter: DescItemAdapter
    private var updateDescItemJob: Job? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onTransformationStartContainer()
    }

    override fun initView() {
        binding.apply {
            groupRv.apply {
                layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
                adapter = groupItemAdapter
            }
            itemRv.apply {
                layoutManager = GridLayoutManager(activity, 3, RecyclerView.VERTICAL, false)
                adapter = goodsItemAdapter
            }
            descRv.apply {
                layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                adapter = descItemAdapter
            }

        }
    }

    override fun initObserve() {
       lifecycleScope.launchWhenStarted {
           menuViewModel.menuGroupName.collect { groupNameList ->
               updateGroupItemJob?.cancel()
               updateGroupItemJob = lifecycleScope.launch {
                   groupItemAdapter.updateDataset(groupNameList)
               }
           }
       }

        lifecycleScope.launchWhenStarted {
            menuViewModel.descName.collect { descList ->
                updateDescItemJob?.cancel()
                updateDescItemJob = lifecycleScope.launch {
                    descItemAdapter.updateDataset(descList)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            menuViewModel.goods.collect { goods ->
                updateGoodsItemJob?.cancel()
                updateGoodsItemJob = lifecycleScope.launch {
                    goodsItemAdapter.updateDataset(goods)
                    binding.itemRv.smoothScrollToPosition(0)
                }
            }
        }
    }

    override fun initAction() {
        goodsItemAdapter.setOnItemClickListener { _,goods,_ ->
            findNavController().navigate(R.id.action_menuFragment_to_zoomPageFragment, bundleOf("goods" to goods))
        }

        groupItemAdapter.setOnKindClickListener {  groupName ->
            menuViewModel.onGroupChange(groupName)
        }


    }


}