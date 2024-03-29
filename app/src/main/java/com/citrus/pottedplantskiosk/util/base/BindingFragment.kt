package com.citrus.pottedplantskiosk.util.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.skydoves.elasticviews.ElasticAnimation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


abstract class BindingFragment<out T : ViewBinding> : Fragment() {
    protected abstract val bindingInflater: (LayoutInflater) -> ViewBinding
    private var _binding: ViewBinding? = null
    @Suppress("UNCHECKED_CAST")
    protected val binding: T
        get() = _binding as T

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = bindingInflater(inflater)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initAction()
        initObserve()
    }

    abstract fun initView()
    abstract fun initObserve()
    abstract fun initAction()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun <T> Fragment.lifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    val launch = viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }
}

suspend fun <T> MutableSharedFlow<T>.fineEmit(obj: T, expectObserveCount: Int = 1) = run {
    do {
        val count = this.subscriptionCount.value
        if (count < expectObserveCount) {
            delay(50)
        }
    } while (count < expectObserveCount)

    this.emit(obj)
}

fun <T> Fragment.lifecycleLatestFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }
}