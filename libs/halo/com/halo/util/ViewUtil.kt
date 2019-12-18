package com.halo.util

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.TouchDelegate
import android.view.View
import android.view.View.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.halo.handler.UiThreadHandler
import com.halo.helper.ResourcesHelper
import com.halo.image.TintedDrawable
import com.vansuita.library.Icon
import im.clazz.R

/**
 * 视图工具类

 * @author wangzengyang@gmail.com
 * *
 * @since 2014-5-20
 */
@SuppressLint("WrongConstant")
object ViewUtil {

    fun hide(v: View?) {
        v?.visibility = GONE
    }

    fun show(v: View?) {
        v?.visibility = VISIBLE
    }

    fun invisible(v: View?) {
        v?.visibility = INVISIBLE
    }

    fun toggle(v: View?) {
        v?.visibility = if (v!!.isShown) GONE else VISIBLE
    }

    fun showIf(v: View?, condition: Boolean) {
        v?.visibility = if (condition) VISIBLE else GONE
    }

    fun offset(view: View, xOffset: Int, yOffset: Int) {
        val left = view.left + xOffset
        val top = view.top + yOffset
        val width = view.width
        val height = view.height
        val right = left + width
        val bottom = top + height
        // view.layout(left, top, right, bottom);
        // boolean marginLefted = WindowUtil.setMarginLeft(view, left);
        // boolean marginToped = WindowUtil.setMarginTop(view, top);
        view.layout(left, top, right, bottom)
        LogUtil.d("move  margin left : $left top : $top view : $view")
        // + " marginLefted : " + marginLefted + " marginToped : " +
        // marginToped);
        view.setTag(R.id.center, null)
    }

    /**
     * 临时使视图数组中的视图可点击或不可点击

     * @param clickable
     * *            是否可点击
     * *
     * @param duration
     * *            作用时间
     * *
     * @param viewArray
     * *            视图数组
     */
    fun setViewClickableTemply(clickable: Boolean, duration: Int, vararg viewArray: View) {
        if (CollectionUtil.isEmpty(viewArray))
            return
        for (view in viewArray) {
            view.isClickable = false
        }
        UiThreadHandler.postDelayed({
            for (view in viewArray) {
                view.isClickable = true
            }
        }, duration.toLong())
    }

    /**
     * 扩大View的触摸和点击响应范围,最大不超过其父View范围

     * @param view
     * *
     * @param top
     * *
     * @param bottom
     * *
     * @param left
     * *
     * @param right
     */
    fun expandViewTouchDelegateScaled(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        var left = left
        var top = top
        var right = right
        var bottom = bottom
        left = WindowUtil.computeHorizontalScaledSize(left)
        right = WindowUtil.computeHorizontalScaledSize(right)
        top = WindowUtil.computeVerticallScaledSize(top)
        bottom = WindowUtil.computeVerticallScaledSize(bottom)
        expandViewTouchDelegate(view, left, top, right, bottom)
    }

    /**
     * 扩大View的触摸和点击响应范围,最大不超过其父View范围

     * @param view
     * *
     * @param top
     * *
     * @param bottom
     * *
     * @param left
     * *
     * @param right
     */
    fun expandViewTouchDelegate(view: View, left: Int, top: Int, right: Int,
                                bottom: Int) {

        (view.parent as View).post {
            val bounds = Rect()
            view.isEnabled = true
            view.getHitRect(bounds)

            bounds.top -= top
            bounds.bottom += bottom
            bounds.left -= left
            bounds.right += right

            val touchDelegate = TouchDelegate(bounds, view)

            if (View::class.java.isInstance(view.parent)) {
                (view.parent as View).touchDelegate = touchDelegate
            }
        }
    }

    /**
     * 还原View的触摸和点击响应范围,最小不小于View自身范围

     * @param view
     */
    fun restoreViewTouchDelegate(view: View) {

        (view.parent as View).post {
            val bounds = Rect()
            bounds.setEmpty()
            val touchDelegate = TouchDelegate(bounds, view)

            if (View::class.java.isInstance(view.parent)) {
                (view.parent as View).touchDelegate = touchDelegate
            }
        }
    }

    fun isVisible(view: View?): Boolean {
        return view != null && view.visibility == VISIBLE
    }

    fun setCursorDrawableColorRes(editText: EditText, colorResId: Int) {
        setCursorDrawableColor(editText, ResourcesHelper.getColor(colorResId))
    }

    fun setCursorDrawableColor(editText: EditText, color: Int) {
        try {
            val fCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            fCursorDrawableRes.isAccessible = true
            val mCursorDrawableRes = fCursorDrawableRes.getInt(editText)
            val fEditor = TextView::class.java.getDeclaredField("mEditor")
            fEditor.isAccessible = true
            val editor = fEditor.get(editText)
            val clazz = editor.javaClass
            val fCursorDrawable = clazz.getDeclaredField("mCursorDrawable")
            fCursorDrawable.isAccessible = true
            val drawables = arrayOfNulls<Drawable>(2)
            drawables[0] = editText.context.resources.getDrawable(mCursorDrawableRes)
            drawables[1] = editText.context.resources.getDrawable(mCursorDrawableRes)
            drawables[0]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            drawables[1]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            fCursorDrawable.set(editor, drawables)
        } catch (ignored: Throwable) {
        }

    }


    fun setCursorDrawable(editText: EditText, d: Drawable) {
        val d1 = d
        val d2 = d
        try {
            val fCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            fCursorDrawableRes.isAccessible = true
            val fEditor = TextView::class.java.getDeclaredField("mEditor")
            fEditor.isAccessible = true
            val editor = fEditor.get(editText)
            val clazz = editor.javaClass
            val fCursorDrawable = clazz.getDeclaredField("mCursorDrawable")
            fCursorDrawable.isAccessible = true
            val drawables = arrayOfNulls<Drawable>(2)
            drawables[0] = d1
            drawables[1] = d2
            fCursorDrawable.set(editor, drawables)
        } catch (ignored: Throwable) {
        }

    }

    fun setViewBackgroundTint(v: View?, color: Int, colorPressed: Int) {
        if (v == null)
            return
        val tintDrawable = object : TintedDrawable(v.background, true) {

            override fun getColor(isPressed: Boolean, isFocused: Boolean, isActive: Boolean): Int {
                return if (isPressed) colorPressed else color
            }
        }
        v.setBackgroundDrawable(tintDrawable)
    }

    fun setImageViewTint(v: ImageView?, colorResId: Int, drawableResId: Int) {
        Icon.on(v).pressedEffect(false).color(colorResId).icon(drawableResId).put();
    }
}