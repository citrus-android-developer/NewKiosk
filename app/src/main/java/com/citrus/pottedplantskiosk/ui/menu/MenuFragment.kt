package com.citrus.pottedplantskiosk.ui.menu


import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.databinding.FragmentMenuBinding
import com.citrus.pottedplantskiosk.ui.menu.adapter.DescItemAdapter
import com.citrus.pottedplantskiosk.ui.menu.adapter.GoodsItemAdapter
import com.citrus.pottedplantskiosk.ui.menu.adapter.GroupItemAdapter
import com.citrus.pottedplantskiosk.ui.menu.adapter.MenuSectionAdapter
import com.citrus.pottedplantskiosk.util.base.BindingFragment
import com.skydoves.transformationlayout.onTransformationStartContainer
import dagger.hilt.android.AndroidEntryPoint
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MenuFragment : BindingFragment<FragmentMenuBinding>() {
    private val menuViewModel: MenuViewModel by activityViewModels()
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentMenuBinding::inflate


    private val menuAdapter by lazy { SectionedRecyclerViewAdapter() }
    private val itemPerLine: Int = 3

    @Inject
    lateinit var groupItemAdapter: GroupItemAdapter
    private var updateGroupItemJob: Job? = null

    @Inject
    lateinit var goodsItemAdapter: GoodsItemAdapter
    private var updateGoodsItemJob: Job? = null



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
                val glm = GridLayoutManager(activity, itemPerLine)
                glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (menuAdapter.getSectionItemViewType(position) == SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER) {
                            itemPerLine
                        } else {
                            1
                        }
                    }
                }
                this.layoutManager = glm
            }


        }
    }

    override fun initObserve() {
       lifecycleScope.launchWhenStarted {
           menuViewModel.menuGroupName.collect { groupList ->
               updateGroupItemJob?.cancel()
               updateGroupItemJob = lifecycleScope.launch {
                   groupItemAdapter.updateDataset(groupList)
               }
           }
       }


        lifecycleScope.launchWhenStarted {
            menuViewModel.kindList.collect { kindList ->
                menuAdapter.removeAllSections()
                kindList.forEach { kind ->
                    menuAdapter.addSection(
                        MenuSectionAdapter(requireContext(),kind.desc,kind.goods){ good,list ->
                            menuViewModel.onGoodsClick(good,list)
                        }
                    )
                }
                binding.itemRv.adapter = menuAdapter
            }
        }


        lifecycleScope.launchWhenStarted {
            menuViewModel.showDetailEvent.collect{ goods ->
                findNavController().navigate(R.id.action_menuFragment_to_zoomPageFragment, bundleOf("goods" to goods))
            }
        }
    }

    override fun initAction() {
        groupItemAdapter.setOnKindClickListener {  groupName ->
            menuViewModel.onGroupChange(groupName)
        }
    }


}