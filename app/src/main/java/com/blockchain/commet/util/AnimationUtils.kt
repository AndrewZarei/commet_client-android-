package com.blockchain.commet.util

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.RecyclerView

object AnimationUtils {

    private var counter = 0

    fun scaleXY(view: View) {
        view.scaleX = 0f
        view.scaleY = 0f
        val propX = PropertyValuesHolder.ofFloat("scaleX", 1f)
        val propY = PropertyValuesHolder.ofFloat("scaleY", 1f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(view, propX, propY)
        animator.duration = 800
        animator.start()
    }

    fun scaleX(holder: RecyclerView.ViewHolder) {
        holder.itemView.scaleX = 0f
        val propx = PropertyValuesHolder.ofFloat("scaleX", 1f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(holder.itemView, propx)
        animator.duration = 800
        animator.start()
    }

    fun scaleY(holder: RecyclerView.ViewHolder) {
        holder.itemView.scaleY = 0f
        val propy = PropertyValuesHolder.ofFloat("scaleY", 1f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(holder.itemView, propy)
        animator.duration = 800
        animator.start()
    }

    fun animateToolbarDroppingDown(containerToolbar: View) {
        containerToolbar.rotationX = -90f
        containerToolbar.alpha = 0.2f
        containerToolbar.pivotX = 0.0f
        containerToolbar.pivotY = 0.0f
        val alpha: Animator =
            ObjectAnimator.ofFloat(containerToolbar, "alpha", 0.2f, 0.4f, 0.6f, 0.8f, 1.0f)
                .setDuration(4000)
        val rotationX: Animator = ObjectAnimator.ofFloat(
            containerToolbar,
            "rotationX",
            -90f,
            60f,
            -45f,
            45f,
            -10f,
            30f,
            0f,
            20f,
            0f,
            5f,
            0f
        ).setDuration(8000)
        val animatorSet = AnimatorSet()
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.playTogether(alpha, rotationX)
        animatorSet.start()
    }

    /**
     * Courtesy: Vladimir Topalovic
     *
     * @param holder
     * @param goesDown
     */
    fun animate1(holder: RecyclerView.ViewHolder, goesDown: Boolean) {
        val holderHeight = holder.itemView.height
        holder.itemView.pivotY = (if (goesDown) 0 else holderHeight).toFloat()
        val animatorSet = AnimatorSet()
        val animatorTranslateY = ObjectAnimator.ofFloat(
            holder.itemView,
            "translationY",
            (if (goesDown) 300 else -300).toFloat(),
            0f
        )
        val scaleY = ObjectAnimator.ofFloat(holder.itemView, "scaleY", 1f, 0.4f, 1f)
        val scaleX = ObjectAnimator.ofFloat(holder.itemView, "scaleX", 1f, 1.3f, 1f)
        animatorTranslateY.interpolator = AccelerateInterpolator()
        scaleY.interpolator = OvershootInterpolator()
        scaleX.interpolator = OvershootInterpolator()
        animatorSet.play(animatorTranslateY).before(scaleY).before(scaleX)
        animatorSet.duration = 700
        animatorSet.start()
    }

    /**
     * Courtesy: Vladimir Topalovic
     *
     * @param holder
     * @param goesDown
     */
    fun animateScatter(holder: RecyclerView.ViewHolder, goesDown: Boolean) {
        counter = ++counter % 4
        val holderHeight = holder.itemView.height
        val holderWidth = holder.itemView.width
        val holderItemView = holder.itemView
        holderItemView.pivotY = (if (goesDown) 0 else holderHeight).toFloat()
        holderItemView.pivotX = (holderWidth / 2).toFloat()
        val animatorSet = AnimatorSet()
        val animatorTranslateY = ObjectAnimator.ofFloat(
            holderItemView,
            "translationY",
            (if (goesDown) 300 else -300).toFloat(),
            0f
        )
        val animatorTranslateX = ObjectAnimator.ofFloat(
            holderItemView,
            "translationX",
            (if (counter == 1 || counter == 3) holderWidth else -holderWidth).toFloat(),
            0f
        )
        val animatorScaleX = ObjectAnimator.ofFloat(
            holderItemView,
            "scaleX",
            (if (counter == 1 || counter == 2) 0 else 2).toFloat(),
            1f
        )
        val animatorScaleY = ObjectAnimator.ofFloat(
            holderItemView,
            "scaleY",
            (if (counter == 1 || counter == 2) 0 else 2).toFloat(),
            1f
        )
        val animatorAlpha = ObjectAnimator.ofFloat(holderItemView, "alpha", 0f, 1f)
        animatorAlpha.interpolator = AccelerateInterpolator(1.5f)
        animatorSet.playTogether(
            animatorAlpha,
            animatorScaleX,
            animatorScaleY,
            animatorTranslateX,
            animatorTranslateY
        )
        animatorSet.setDuration(800).interpolator = DecelerateInterpolator(1.1f)
        animatorSet.start()
    }

}
