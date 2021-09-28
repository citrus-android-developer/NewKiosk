package com.citrus.pottedplantskiosk.ui.menu


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.databinding.FragmentZoomPageBinding
import com.citrus.pottedplantskiosk.ui.menu.adapter.ZoomAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import www.sanju.zoomrecyclerlayout.ZoomRecyclerLayout
import javax.inject.Inject
import androidx.recyclerview.widget.RecyclerView
import com.citrus.pottedplantskiosk.api.remote.dto.Good


@AndroidEntryPoint
class ZoomPageFragment : BottomSheetDialogFragment() {
    private val menuViewModel: MenuViewModel by activityViewModels()
    private val args: ZoomPageFragmentArgs by navArgs()

    private var _binding: FragmentZoomPageBinding? = null
    private val binding get() = _binding!!

    private var currentIndex: Int = 0
    private var goodsList:List<Good> = listOf()


    @Inject
    lateinit var zoomItemAdapter: ZoomAdapter
    private var updateZoomItemJob: Job? = null


    override fun onStart() {
        super.onStart()
        val view: FrameLayout = dialog?.findViewById(R.id.design_bottom_sheet)!!

        view.layoutParams.height = resources.getDimension(R.dimen.dp_350).toInt()
        val behavior = BottomSheetBehavior.from(view)
        behavior.peekHeight = resources.getDimension(R.dimen.dp_350).toInt()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentZoomPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreen()
        initView()
        initAction()
    }

    private fun initAction() {
        goodsList = menuViewModel.currentDetailGoodsList
        currentIndex = goodsList.indexOf(args.goods)
        if(currentIndex == 0){
            binding.prev.visibility = View.INVISIBLE
        }
        updateZoomItemJob?.cancel()
        updateZoomItemJob = lifecycleScope.launch {
            zoomItemAdapter.updateDataset(goodsList)
            binding.zoomRv.scrollToPosition(currentIndex)
        }

    }


    private fun initView() {
        binding.apply {
            val linearLayoutManager = ZoomRecyclerLayout(requireContext())
            linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            zoomRv.layoutManager =
                linearLayoutManager

            zoomRv.adapter = zoomItemAdapter


            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(zoomRv)
            zoomRv.isNestedScrollingEnabled = false

            zoomRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        var layoutManager: LinearLayoutManager =
                            recyclerView.layoutManager as LinearLayoutManager
                        var pos = layoutManager.findFirstVisibleItemPosition()

                        when(pos){
                            0 -> {
                                binding.prev.visibility = View.INVISIBLE
                            }
                            goodsList.size -1 -> {
                                binding.next.visibility = View.INVISIBLE
                            }
                            else -> {
                                binding.prev.visibility = View.VISIBLE
                                binding.next.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            })

            closeBtn.setOnClickListener {
                
            }


//            backBtn.setOnClickListener { v ->
//                ElasticAnimation(v)
//                    .setScaleX(0.85f)
//                    .setScaleY(0.85f)
//                    .setDuration(50)
//                    .setOnFinishListener {
//                        dismiss()
//                    }
//                    .doAction()
//            }


        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setFullScreen() {
        val decorView = setSystemUiVisibilityMode()
        decorView?.setOnSystemUiVisibilityChangeListener {
            setSystemUiVisibilityMode() // Needed to avoid exiting immersive_sticky when keyboard is displayed
        }
    }

    private fun setSystemUiVisibilityMode(): View? {

        val decorView = dialog?.window?.decorView
        val options = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        decorView?.systemUiVisibility = options
        return decorView
    }
}