package im.clazz.extension

import com.halo.util.WindowUtil


fun Float.scale() = WindowUtil.computeScaledSize(this)
fun Float.scaleVertical() = WindowUtil.computeVerticallScaledSize(this)
fun Float.scaleHorizontal() = WindowUtil.computeHorizontalScaledSize(this)


fun Float.b() = WindowUtil.computeScaledSize(this)
fun Float.v() = WindowUtil.computeVerticallScaledSize(this)
fun Float.h() = WindowUtil.computeHorizontalScaledSize(this)


val Float.b: Float
    get() = WindowUtil.computeScaledSize(this)

val Float.v: Float
    get() = WindowUtil.computeVerticallScaledSize(this)

val Float.h: Float
    get() = WindowUtil.computeHorizontalScaledSize(this)
