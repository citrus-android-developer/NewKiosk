package com.citrus.pottedplantskiosk.ui.menu


import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
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
import com.citrus.pottedplantskiosk.util.base.BindingFragment
import com.google.android.material.snackbar.Snackbar
import com.skydoves.elasticviews.ElasticAnimation
import com.skydoves.transformationlayout.onTransformationStartContainer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MenuFragment : BindingFragment<FragmentMenuBinding>() {
    private val menuViewModel: MenuViewModel by activityViewModels()

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentMenuBinding::inflate

    private var currentCartGoods: List<Good>? = null

    private var snackbar: Snackbar? = null
    private var updateTimerJob: Job? = null

    @Inject
    lateinit var groupItemAdapter: GroupItemAdapter
    private var updateGroupItemJob: Job? = null

    @Inject
    lateinit var cartItemAdapter: CartItemAdapter

    private var updateKindItemJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onTransformationStartContainer()
    }

    override fun initView() {
        binding.apply {
            cartMotionLayout.registerLifecycleOwner(lifecycle)
            cartMotionLayout.setAdapter(cartItemAdapter)

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
                            lifecycleScope.launchWhenStarted {
                                if (currentState == R.id.end_with_cart_open) {
                                    binding.cartMotionLayout.setCloseSheet()
                                }
                                transitionToState(R.id.start)
                                awaitTransitionComplete(R.id.start)
                            }
                        }
                    }.doAction()
            }
        }
    }

    override fun initObserve() {
        lifecycleScope.launchWhenStarted {
            menuViewModel.currentCartGoods.collect { cartGoods ->
                binding.cartMotionLayout.addCartGoods(cartGoods)
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

                        binding.typeName.text = title
                        binding.typeName.paint.flags = Paint.UNDERLINE_TEXT_FLAG

                        binding.root.apply {
                            lifecycleScope.launchWhenStarted {
                                if (currentState == R.id.start_with_cart_open) {
                                    binding.cartMotionLayout.setCloseSheet()
                                }
                                transitionToState(R.id.end)
                                awaitTransitionComplete(R.id.end)
                                (binding.typeItemRv.adapter as GoodsItemAdapter).updateDataset(goods)
                                binding.typeItemRv.scheduleLayoutAnimation()
                                binding.setBack.isVisible = true
                            }
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
                if (timer == 0) {
                    /**If click anywhere except continue button*/
                    releaseSnack()
                }

                if (timer == 100) {
                    var temp = 100
                    snackbar = Snackbar.make(requireView(), "", 20000)
                    val customSnackView: View =
                        layoutInflater.inflate(R.layout.custom_snackbar_view, null)
                    snackbar!!.view.setBackgroundColor(Color.TRANSPARENT)
                    val snackbarLayout = snackbar!!.view as Snackbar.SnackbarLayout
                    snackbarLayout.setPadding(0, 0, 0, 0)
                    val bGotoWebsite: Button = customSnackView.findViewById(R.id.gotoWebsiteButton)
                    val timerHint: TextView = customSnackView.findViewById(R.id.textView2)
                    updateTimerJob = lifecycleScope.launch {
                        while (temp < 120) {
                            delay(1000)
                            temp++
                            timerHint.text = (120 - temp).toString() + "秒後將返回主畫面"
                        }
                    }

                    bGotoWebsite.setOnClickListener {
                        releaseSnack()
                    }
                    snackbarLayout.addView(customSnackView, 0)
                    snackbar!!.show()
                }

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
                when (binding.root.currentState) {
                    R.id.end_with_cart_open -> {
                        binding.cartMotionLayout.setCloseSheet()
                        binding.root.transitionToState(R.id.start)
                    }
                    R.id.start_with_cart_open -> {
                        binding.cartMotionLayout.setCloseSheet()
                    }
                    R.id.start -> Unit
                    R.id.end -> {
                        binding.root.transitionToState(R.id.start)
                    }
                }
            }
        }

        binding.cartMotionLayout.setOnOpenSheetListener {
            when (binding.root.currentState) {
                R.id.start -> {
                    lifecycleScope.launchWhenStarted {
                        binding.root.transitionToState(R.id.start_with_cart_open)
                        binding.root.awaitTransitionComplete(R.id.start_with_cart_open)
                    }
                }
                R.id.end -> {
                    lifecycleScope.launchWhenStarted {
                        binding.root.transitionToState(R.id.end_with_cart_open)
                        binding.root.awaitTransitionComplete(R.id.end_with_cart_open)
                    }
                }
            }
        }

        binding.cartMotionLayout.setOnCloseSheetListener {
            lifecycleScope.launchWhenStarted {
                when (binding.root.currentState) {
                    R.id.start_with_cart_open -> {
                        binding.root.transitionToState(R.id.start)
                        binding.root.awaitTransitionComplete(R.id.start)
                    }
                    R.id.end_with_cart_open -> {
                        binding.root.transitionToState(R.id.end)
                        binding.root.awaitTransitionComplete(R.id.end)
                    }
                }
            }
        }

        binding.cartMotionLayout.setOnCloseSheetWhenSwitchListener {
            when (binding.root.currentState) {
                R.id.start -> {
                    binding.root.transitionToState(R.id.end)
                }
                R.id.end -> {
                    binding.root.transitionToState(R.id.start)
                }
            }
        }

        binding.cartMotionLayout.setOnPayButtonClickListener { list ->
            Log.e("list",list.toString())
        }

        cartItemAdapter.setOnItemDeleteListener { good ->
            binding.cartMotionLayout.removeGoods(good)
        }
    }

    private fun releaseSnack(){
        updateTimerJob?.cancel()
        updateTimerJob = null
        snackbar?.dismiss()
        snackbar = null
    }

}