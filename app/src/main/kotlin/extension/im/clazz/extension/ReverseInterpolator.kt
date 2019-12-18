package im.clazz.extension

import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator

/**
 * Created by Wang on 2017/11/4.
 */
internal class ReverseInterpolator @JvmOverloads constructor(private val delegate: Interpolator = LinearInterpolator()) : Interpolator {

    override fun getInterpolation(input: Float): Float {
        return 1 - delegate.getInterpolation(input)
    }
}