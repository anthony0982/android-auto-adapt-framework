package im.clazz.extension

import android.view.View
import com.halo.helper.LayoutHelper
import com.halo.helper.ResourcesHelper
import com.halo.helper.UiHelper
import com.halo.util.WindowUtil


fun Int.scale() = WindowUtil.computeScaledSize(this)
fun Int.scaleVertical() = WindowUtil.computeVerticallScaledSize(this)

fun Int.b() = WindowUtil.computeScaledSize(this)
fun Int.v() = WindowUtil.computeVerticallScaledSize(this)
fun Int.h() = WindowUtil.computeHorizontalScaledSize(this)

fun Int.resToColor() = ResourcesHelper.getColor(this)

fun Int.tip() {
    return UiHelper.showTip(ResourcesHelper.getString(this))
}

val Int.tip: Unit
    get() = UiHelper.showTip(ResourcesHelper.getString(this))

val Int.string: String
    get() = ResourcesHelper.getString(this)

val Int.inflateResized: View
    get() = LayoutHelper.inflateResized(this)

val Int.inflate: View
    get() = LayoutHelper.inflate(this)

val Int.color: Int
    get() = ResourcesHelper.getColor(this)

fun Int.string(vararg args: Any?): String = ResourcesHelper.getString(this, *args)

val Int.array: Array<String>
    get() = ResourcesHelper.getStringArray(this)
val Int.intArray: IntArray
    get() = ResourcesHelper.getIntArray(this)

val Int.pixel: Int
    get() = ResourcesHelper.getDimensionPixelSize(this)

val Int.b: Int
    get() = WindowUtil.computeScaledSize(this)

val Int.v: Int
    get() = WindowUtil.computeVerticallScaledSize(this)

val Int.h: Int
    get() = WindowUtil.computeHorizontalScaledSize(this)


val Int.week: Int
    get() = this
val Int.startClass: Int
    get() = this
val Int.classCount: Int
    get() = this
val Long.id: Long
    get() = this
val Int.id: Int
    get() = this