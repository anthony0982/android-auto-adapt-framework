package im.clazz.extension

import android.annotation.SuppressLint
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.widget.ImageView
import android.widget.TextView
import com.github.florent37.expectanim.ExpectAnim
import com.github.florent37.expectanim.ViewExpectation
import com.halo.base.BaseFragment
import com.halo.helper.UiHelper
import com.halo.util.TextUtil
import com.halo.util.ViewUtil
import com.halo.util.WindowUtil
import im.clazz.R
import kotlinx.android.synthetic.main.common_material_dialog_layout.*
import org.jetbrains.anko.find


@SuppressLint("WrongConstant")
fun View.hide(): View {
    this?.visibility = GONE
    return this
}

@SuppressLint("WrongConstant")
fun View.show(): View {
    this?.visibility = VISIBLE
    return this
}

@SuppressLint("WrongConstant")
fun View.invisible(): View {
    this?.visibility = INVISIBLE
    return this
}

@SuppressLint("WrongConstant")
fun View.showIf(condition: Boolean?): View {
    if (condition == null) {
        this?.visibility = GONE
        return this
    }
    this?.visibility = if (condition) VISIBLE else GONE

    return this
}

fun View.toggle(): View {
    if (visibility == VISIBLE) hide() else show()
    return this
}

fun TextView.bold(): View {
    paint.isFakeBoldText = true
    return this
}

fun TextView.blockTouch(): View {
    UiHelper.shieldTouchEvent(this)
    return this
}

fun View.isResized(): Boolean {
    return WindowUtil.isResized(this)
}

fun View.resize(): Boolean {
    return WindowUtil.resize(this)
}

fun View.resizeRecursively(): Boolean {
    return WindowUtil.resizeRecursively(this)
}

fun View.heightDesigned(heightDesigned: Int) {
    WindowUtil.setDesignedHeight(this, heightDesigned)
}

fun View.heightWith(height: Int) {
    WindowUtil.setHeight(this, height)
}

fun View.widthDesigned(widthDesigned: Int) {
    WindowUtil.setDesignedWidth(this, widthDesigned)
}

fun View.shieldTouch() {
    UiHelper.shieldTouchEvent(this)
}

fun View.unShieldTouch() {
    UiHelper.unShieldTouchEvent(this)
}

fun ImageView.tint(drawableResId: Int, colorResId: Int) {
    return ViewUtil.setImageViewTint(this, colorResId, drawableResId)
}

fun TextView.color(colorResId: Int) {
    TextUtil.setTextColor(this, colorResId)
}

fun View.expectAnim(): ViewExpectation? {
    var anim = this.getTag(R.id.tag_expect_anim) as? ExpectAnim
    if (anim != null && anim.isPlaying)
        return null
    anim = ExpectAnim()
    this.setTag(R.id.tag_expect_anim, anim)
    anim?.addEndListener { this.setTag(R.id.tag_expect_anim, null) }
    return anim.expect(this)
}

fun View.onClick(callback: Callback?) {
    this.setOnClickListener {
        callback?.invoke()
    }
}

fun View.onTouch(l: ((v: View, event: MotionEvent) -> Boolean)) {
    this.setOnTouchListener(l)
}

fun RecyclerView.onScroll(l: ((dx: Int, dy: Int) -> Unit)) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            l.invoke(dx, dy)
        }
    })
}


fun View.onLongClick(callback: Callback?) {
    this.setOnLongClickListener {
        callback?.invoke()
        callback != null
    }
}

fun View.yInScreen(): Int {
    var rect = Rect()
    this.getGlobalVisibleRect(rect)
    return rect.top
}

fun View.xInScreen(): Int {
    var rect = Rect()
    this.getGlobalVisibleRect(rect)
    return rect.left
}

fun View.mrd(marginRightDesign: Int): View {
    WindowUtil.setMarginRightDesigned(this, marginRightDesign)
    return this
}

fun View.mld(marginLeftDesign: Int): View {
    WindowUtil.setMarginLeftDesigned(this, marginLeftDesign)
    return this
}

fun View.mtd(marginTopDesign: Int): View {
    WindowUtil.setMarginTopDesigned(this, marginTopDesign)
    return this
}

fun View.mbd(marginBottomDesign: Int): View {
    WindowUtil.setMarginBottomDesigned(this, marginBottomDesign)
    return this
}

fun View.prd(paddingRightDesign: Int): View {
    WindowUtil.setPaddingRightDesigned(this, paddingRightDesign)
    return this
}

fun View.pld(paddingLeftDesign: Int): View {
    WindowUtil.setPaddingLeftDesigned(this, paddingLeftDesign)
    return this
}

fun View.ptd(paddingTopDesign: Int): View {
    WindowUtil.setPaddingTopDesigned(this, paddingTopDesign)
    return this
}

fun View.pbd(paddingBottomDesign: Int): View {
    WindowUtil.setPaddingBottomDesigned(this, paddingBottomDesign)
    return this
}

fun Int.textView(parent: View): x.TextView {
    return parent.find(this)
}

fun Int.textView(fragment: BaseFragment): x.TextView {
    return fragment.contentView.find(this)
}

fun Int.imageView(fragment: BaseFragment): x.ImageView {
    return fragment.contentView.find(this)
}