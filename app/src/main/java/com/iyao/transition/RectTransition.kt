package com.iyao.transition

import ArgbEvaluator
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.support.transition.Transition
import android.support.transition.TransitionValues
import android.view.ViewGroup

class RectTransition : Transition() {

    private val propertyColor = "rectColor"
    private val propertyRadius = "rectRadius"

    init {
        addTarget(RectView::class.java)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }


    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }


    private fun captureValues(transitionValues: TransitionValues) {
        val rectView = transitionValues.view as RectView
        transitionValues.values.put(propertyRadius, rectView.radius)
        transitionValues.values.put(propertyColor, rectView.color)
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        if(endValues == null || startValues == null) {
            return null
        }
        val startColor: Int = startValues.values[propertyColor] as Int
        val endColor: Int = endValues.values[propertyColor] as Int
        val startRadius = startValues.values[propertyRadius] as Float
        val endRadius = endValues.values[propertyRadius] as Float
        val rectView = endValues.view as RectView
        var radiusAnimator : Animator? = null
        var argbAnimator : Animator? = null
        if (startRadius != endRadius) {
            rectView.radius = startRadius
            radiusAnimator = createRadiusAnimator(rectView, startRadius, endRadius)
        }
        if (startColor != endColor) {
            rectView.color = startColor
            argbAnimator = createArgbAnimator(rectView, startColor, endColor)
        }
        return when {
            radiusAnimator != null && argbAnimator != null -> {
                AnimatorSet().apply {
                    playTogether(radiusAnimator, argbAnimator)
                }
            }
            else -> radiusAnimator ?: argbAnimator
        }
    }

    private fun createRadiusAnimator(target: Any, vararg radius : Float): Animator
            = ObjectAnimator.ofFloat(target, "radius", *radius)


    private fun createArgbAnimator(target: Any, vararg colors: Int): Animator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ObjectAnimator.ofArgb(target, "color", *colors)
        } else {
            ObjectAnimator.ofInt(target, "color", *colors).apply {
                setEvaluator(ArgbEvaluator.instance)
            }
        }
    }
}

