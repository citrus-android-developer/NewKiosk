package com.citrus.pottedplantskiosk.ui.menu


import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.Good
import com.citrus.pottedplantskiosk.databinding.FragmentMenuBinding
import com.citrus.pottedplantskiosk.ui.menu.adapter.*
import com.citrus.pottedplantskiosk.util.Constants
import com.citrus.pottedplantskiosk.util.Constants.setTransitionExecute
import com.citrus.pottedplantskiosk.util.Constants.setTransitionReverse
import com.citrus.pottedplantskiosk.util.base.BindingFragment
import com.skydoves.elasticviews.ElasticAnimation
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

    private var detailPageGoods: List<Good>? = null
    private var currentCartGoods: List<Good>? = null

    @Inject
    lateinit var groupItemAdapter: GroupItemAdapter
    private var updateGroupItemJob: Job? = null

    @Inject
    lateinit var cartItemAdapter: CartItemAdapter
    private var updateCartItemJob: Job? = null

    private var updateKindItemJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onTransformationStartContainer()
    }

    override fun initView() {
        binding.apply {
            cartMotionLayout.registerLifecycleOwner(lifecycle)
            groupRv.apply {
                layoutManager =
                    LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
                adapter = groupItemAdapter
            }

            itemRv.apply {
                layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            }

            typeItemRv.apply {
                layoutManager = GridLayoutManager(requireActivity(), 4)
            }

//            cartRv.apply {
//                layoutManager =
//                    LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
//                adapter = cartItemAdapter
//            }

            setBack.setOnClickListener { v ->
                ElasticAnimation(v)
                    .setScaleX(0.85f)
                    .setScaleY(0.85f)
                    .setDuration(50)
                    .setOnFinishListener {
                        root.apply {
                            setTransitionReverse(R.id.switchPreview, 500)
                        }
                    }.doAction()
            }
        }
    }

    override fun initObserve() {
        lifecycleScope.launchWhenStarted {
            menuViewModel.currentCartGoods.collect { cartGoods ->
                updateCartItemJob?.cancel()
                updateCartItemJob = lifecycleScope.launch {
                    cartItemAdapter.updateDataset(cartGoods)
                }
            }
        }


        lifecycleScope.launchWhenStarted {
            menuViewModel.menuGroupName.collect { groupList ->
                updateGroupItemJob?.cancel()
                updateGroupItemJob = lifecycleScope.launch {
                    groupItemAdapter.updateDataset(groupList)
                    binding.groupRvArea.isVisible = true
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
                                menuViewModel.onGoodsClick(goods.deepCopy(), list)
                            }
                        )

                        detailPageGoods = goods
                        binding.typeName.text = title
                        binding.typeName.paint.flags = Paint.UNDERLINE_TEXT_FLAG

                        binding.root.apply {
                            setTransitionExecute(R.id.switchPreview, 500)
                        }
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
                    bundleOf("goods" to goods.deepCopy())
                )
            }
        }


        lifecycleScope.launchWhenStarted {
            menuViewModel.tikTok.collect { timer ->
                if (timer == Constants.TWO_MINUTES) {
                    menuViewModel.stopTimer()
                    findNavController().popBackStack(R.id.mainFragment, false)
                }
            }
        }
    }

    override fun initAction() {
        groupItemAdapter.setOnKindClickListener { groupName ->
            menuViewModel.onGroupChange(groupName)
            binding.motionLayout.apply {
                setTransitionReverse(R.id.switchPreview, 500)
            }
        }

        binding.root.setTransitionListener(object : TransitionAdapter() {
            override fun onTransitionCompleted(
                motionLayout: MotionLayout?, currentId: Int
            ) {
                when (currentId) {
                    R.id.start -> {

                    }
                    R.id.end -> {
                        detailPageGoods?.let {
                            lifecycleScope.launch {
                                (binding.typeItemRv.adapter as GoodsItemAdapter).updateDataset(
                                    it
                                )
                                binding.typeItemRv.scheduleLayoutAnimation()
                            }
                        }

                        binding.setBack.isVisible = true
                    }
                }
            }
        })
    }

}