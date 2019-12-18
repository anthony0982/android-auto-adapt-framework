package im.clazz.extension

import android.animation.Animator
import android.animation.AnimatorSet
import android.os.Build
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.Interpolator


fun ViewPropertyAnimator.endShow(view: View? = null): ViewPropertyAnimator {
    withEndAction {
        var v = view
        if (v == null)
            v = ViewPropertyAnimator::class.java.getDeclaredField("mView").get(this) as? View
        v?.show()
    }
    return this
}

fun ViewPropertyAnimator.endHide(view: View? = null): ViewPropertyAnimator {
    withEndAction {
        var v = view
        if (v == null)
            v = ViewPropertyAnimator::class.java.getDeclaredField("mView").get(this) as? View
        v?.hide()
    }
    return this
}


fun ViewPropertyAnimator.startShow(view: View? = null): ViewPropertyAnimator {
    withStartAction {
        var v = view
        if (v == null)
            v = ViewPropertyAnimator::class.java.getDeclaredField("mView").get(this) as? View
        v?.show()
    }
    return this
}

fun ViewPropertyAnimator.startHide(view: View? = null): ViewPropertyAnimator {
    withStartAction {
        var v = view
        if (v == null)
            v = ViewPropertyAnimator::class.java.getDeclaredField("mView").get(this) as? View
        v?.hide()
    }
    return this
}

fun AnimatorSet.reverseSet(): AnimatorSet {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        this.interpolator = ReverseInterpolator(this.interpolator as Interpolator)
    } else
        this.interpolator = ReverseInterpolator()
    this.start()
    return this
}

fun Animator.reverseAnim(): Animator {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        this.interpolator = ReverseInterpolator(this.interpolator as Interpolator)
    } else
        this.interpolator = ReverseInterpolator()
    this.start()
    return this
}