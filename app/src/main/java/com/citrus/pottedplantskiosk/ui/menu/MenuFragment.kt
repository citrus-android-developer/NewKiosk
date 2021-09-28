package com.citrus.pottedplantskiosk.ui.menu


import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.constraintlayout.motion.widget.MotionLayout
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
import com.citrus.pottedplantskiosk.ui.menu.adapter.*
import com.citrus.pottedplantskiosk.util.base.BindingFragment
import com.skydoves.elasticviews.ElasticAnimation
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

    @Inject
    lateinit var groupItemAdapter: GroupItemAdapter
    private var updateGroupItemJob: Job? = null

    private var updateKindItemJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onTransformationStartContainer()
    }

    override fun initView() {
        binding.apply {
            groupRv.apply {
                layoutManager =
                    LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
                adapter = groupItemAdapter
            }

            itemRv.apply {
                layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            }

            typeItemRv.apply {
                layoutManager = GridLayoutManager(requireActivity(), 3)
            }

            setBack.setOnClickListener { v ->
                ElasticAnimation(v)
                    .setScaleX(0.85f)
                    .setScaleY(0.85f)
                    .setDuration(50)
                    .setOnFinishListener {
                        root.apply {
                            setTransitionDuration(500)
                            transitionToStart()
                        }
                    }.doAction()
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
                binding.itemRv.adapter = KindItemAdapter(
                    requireActivity(),
                    lifecycleScope,
                    onItemClick = { goods, list ->
                        menuViewModel.onGoodsClick(goods, list)
                    },
                    onViewMoreClick = { title, goods ->
                        binding.typeItemRv.adapter = GoodsItemAdapter(
                            requireActivity(),
                            onItemClick = { goods, list ->
                                menuViewModel.onGoodsClick(goods, list)
                            }
                        )

                        binding.typeName.text = title
                        binding.typeName.paint.flags = Paint.UNDERLINE_TEXT_FLAG

                        binding.root.apply {
                            setTransitionDuration(500)
                            transitionToEnd()
                        }

                        binding.root.setTransitionListener(object : MotionTransitionAdapter {
                            override fun onTransitionCompleted(
                                motionLayout: MotionLayout?, currentId: Int
                            ) {
                                lifecycleScope.launch {
                                    (binding.typeItemRv.adapter as GoodsItemAdapter).updateDataset(goods)
                                    binding.typeItemRv.scheduleLayoutAnimation()
                                }
                            }
                        })
                    }
                )
                updateKindItemJob?.cancel()
                updateKindItemJob = lifecycleScope.launch {
                    (binding.itemRv.adapter as KindItemAdapter).updateDataset(kindList)
                }
            }
        }


        lifecycleScope.launchWhenStarted {
            menuViewModel.showDetailEvent.collect { goods ->
                findNavController().navigate(
                    R.id.action_menuFragment_to_zoomPageFragment,
                    bundleOf("goods" to goods)
                )
            }
        }
    }

    override fun initAction() {
        groupItemAdapter.setOnKindClickListener { groupName ->
            menuViewModel.onGroupChange(groupName)
        }
    }


}