package com.citrus.pottedplantskiosk.ui.menu

import androidx.constraintlayout.motion.widget.MotionLayout

interface MotionTransitionAdapter : MotionLayout.TransitionListener {
    override fun onTransitionChange(
        motionLayout: MotionLayout?,
        startId: Int,
        endId: Int,
        progress: Float
    ) = Unit

    override fun onTransitionTrigger(
        motionLayout: MotionLayout?,
        triggerId: Int,
        positive: Boolean,
        progress: Float
    ) = Unit

    override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) = Unit

    override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) = Unit
}