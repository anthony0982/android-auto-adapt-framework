package com.halo.base

import android.content.Context
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewStub
import android.widget.EditText
import com.alexvasilkov.android.commons.utils.Views
import com.github.florent37.expectanim.ExpectAnim
import com.github.florent37.expectanim.core.Expectations
import com.halo.util.InputMethodUtil
import im.clazz.R
import im.clazz.extension.Callback
import im.clazz.extension.expectAnim
import im.clazz.extension.heightDesigned
import im.clazz.extension.heightWith
import net.tsz.afinal.FinalActivity

abstract open class BaseSlideInLayout(context: Context, attrs: AttributeSet) : BaseLayout(context, attrs) {
    private var mContentLayoutStub: ViewStub? = null
    private var mContentLayout: View? = null
    private var mBackground: View? = null

    private var shieldOnTouch: Boolean = false
    var autoHideOnTouchBackground: Boolean = false

    final override fun onInitLayoutResId(): Int {
        return R.layout.base_slide_in_layout
    }

    abstract open fun onInitContentLayoutResId(): Int
    abstract open fun onContentInit()

    open fun onInitContentHeightDesigned(): Int {
        return 800
    }

    final override fun onInit() {
        mContentLayoutStub = Views.find(this, R.id.slide_in_content_layout_stub)
        mBackground = Views.find(this, R.id.slide_in_bg)

        mContentLayoutStub?.layoutResource = onInitContentLayoutResId()
        mContentLayout = mContentLayoutStub?.inflate()

        mContentLayout?.heightWith(onInitContentHeightDesigned())

        FinalActivity.initInjectedView(this, mContentLayout)

        mContentLayout?.alpha = 0f

        mBackground?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN && autoHideOnTouchBackground)
                hide()
            shieldOnTouch
        }
        onContentInit()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        hide(true)
    }

    open fun hide(setNow: Boolean = false, endListener: Callback? = null): View {
        shieldOnTouch = false

        var anim: ExpectAnim? = null
        mContentLayout?.expectAnim()?.let {
            anim = it.toBe(Expectations.outOfScreen(Gravity.BOTTOM))

                    .expect(mBackground)
                    .toBe(Expectations.invisible())

                    .toAnimation()
                    .setInterpolator(FastOutSlowInInterpolator())
                    .setDuration(300)
                    .addEndListener {
                        endListener?.invoke()
                    }
        }

        if (setNow)
            anim?.setNow()
        else
            anim?.start()
        isShowing = false

        return this
    }

    open fun show(editText: EditText? = null, endListener: Callback? = null): View {
        InputMethodUtil.hideInputMethod(editText)
        mContentLayout?.alpha = 1f
        shieldOnTouch = true
        mContentLayout?.expectAnim()
                ?.toBe(Expectations.bottomOfParent())

                ?.expect(mBackground)
                ?.toBe(Expectations.visible())

                ?.toAnimation()
                ?.setInterpolator(FastOutSlowInInterpolator())
                ?.setDuration(300)
                ?.addEndListener {
                    endListener?.invoke()
                }
                ?.start()

        isShowing = true
        return this
    }

    var isShowing: Boolean = false

    private var shouldShow: Boolean = false

    fun showOrMark(isKeyboardShown: Boolean) {
        if (!isKeyboardShown) {
            show()
            return
        }
        shouldShow = true
    }

    fun checkShow() {
        if (!shouldShow)
            return
        show()
        shouldShow = false
    }
}