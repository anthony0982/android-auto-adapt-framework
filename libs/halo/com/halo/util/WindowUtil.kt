package com.halo.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import com.halo.helper.ResourcesHelper
import im.clazz.R
import im.clazz.extension.log
import im.clazz.helper.FontHelper
import java.lang.reflect.Field

/**
 * Window及UI缩放工具

 * @author wangzengyang
 */
object WindowUtil {
    /**
     * UI设计的竖向高度,单位：px
     */
    const val UI_DESIGN_PORTRAIT_SIZE = 1920
    /**
     * UI设计的横向高度,单位：px
     */
    private val UI_DESIGN_LANDSCAPE_SIZE = 1080

    /**
     * 自动缩放严格模式标志
     */
    private val AUTO_RESIZE_STRICT_TAG = "strict_mode"
    private val AUTO_RESIZE_HORIZONTAL_TAG = "horizontal_mode"
    private val AUTO_RESIZE_VERTICAL_TAG = "vertical_mode"
    /**
     * 状态栏高度
     */
    var STATUS_BAR_HEIGHT: Int = 0
    /**
     * 缩放比例:水平
     */
    var SCALE_RATIO_HORIZONTAL: Float = 0.toFloat()
    /**
     * 缩放比例:垂直
     */
    var SCALE_RATIO_VERTICAL: Float = 0.toFloat()
    /**
     * 缩放比例
     */
    var scaleRatio: Float = 0.toFloat()
    /**
     * 屏幕旋转度
     */
    var windowRotation: Int = 0

    var displayDensity = 1.0f
        private set


    var DISPLAY_HEIGHT_WIDTH_RATIO: Float = 0.toFloat()

    init {
        computeScaleRatio()
        computeScreenDensity()
        computeWindowRotation()
    }

    /**
     * 递归重新计算View及其子View的宽高

     * @param view
     * *
     * @return
     */
    fun resizeRecursively(view: View?): Boolean {
        if (isResized(view))
            return false
        return resizeRecursively(view, scaleRatio, scaleRatio)
    }


    fun isResized(view: View?): Boolean {
        var resized = view?.getTag(R.id.resized) as? Boolean
        return resized != null && resized
    }

    fun resize(vararg views: View?) {
        for (view in views) {
            resizeView(view)
        }
    }

    fun resizeView(view: View?): Boolean {
        if (isResized(view))
            return false
        return resize(view, SCALE_RATIO_HORIZONTAL, scaleRatio)
    }

    /**
     * 重新计算view的宽高、边距、文本大小<br></br>
     * （其中宽高、文本大小按照相同缩放系数；内外边距水平方向按照水平比例系数，垂直方向按照垂直比例系数）

     * @param view
     * *
     * @param horizontalRatio
     * *
     * @param verticalRatio
     * *
     * @return
     */

    fun resize(view: View?, horizontalRatio: Float = SCALE_RATIO_HORIZONTAL, verticalRatio: Float = scaleRatio): Boolean {
        if (view == null)
            return false
        if (isResized(view))
            return false
        view?.setTag(R.id.resized, true)
        /* 重新计算宽高 */
        resizeWidthAndHeight(view, horizontalRatio, verticalRatio)
        /* 重新计算内边距 */
        repadding(view)
        /* 重新计算外边距 */
        remargin(view)
        /* 重新计算文本大小 */
        if (view is TextView)
            resizeText(view as TextView?)
        resizeMinSize(view)
        return true
    }

    private fun resizeMinSize(v: View) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            return
        var minWidth = v.minimumWidth
        if (minWidth > 0) {
            minWidth = (minWidth * SCALE_RATIO_HORIZONTAL).toInt()
            v.minimumWidth = minWidth
        }
        var minHeight = v.minimumHeight
        if (minHeight > 0) {
            minHeight = (minHeight * SCALE_RATIO_VERTICAL).toInt()
            v.minimumHeight = minHeight
        }

        if (v is TextView)
            resizeMinSize(v)
    }

    private fun resizeMinSize(v: TextView) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
            return
        var minWidth = v.minWidth
        if (minWidth > 0) {
            minWidth = (minWidth * SCALE_RATIO_HORIZONTAL).toInt()
            v.minWidth = minWidth
        }
        var minHeight = v.minHeight
        if (minHeight > 0) {
            minHeight = (minHeight * SCALE_RATIO_VERTICAL).toInt()
            v.minHeight = minHeight
        }
    }

    /**
     * 重新计算view的宽高、边距、文本大小<br></br>
     * (严格模式)

     * @param view
     * *
     * @param horizontalRatio
     * *
     * @param verticalRatio
     * *
     * @return
     */

    fun resizeStrictly(view: View?, horizontalRatio: Float, verticalRatio: Float): Boolean {
        if (view == null)
            return false
        /* 重新计算宽高 */
        resizeWidthAndHeight(view, horizontalRatio, verticalRatio)
        /* 重新计算内边距 */
        repadding(view, horizontalRatio, verticalRatio)
        /* 重新计算外边距 */
        remargin(view, horizontalRatio, verticalRatio)
        /* 重新计算文本大小 */
        if (view is TextView)
            resizeText(view as TextView?)
        return true
    }

    private fun shouldIgnore(view: View): Boolean {
        val tag = view.tag
        if (tag is String) {
            return "ignore" == tag
        }
        return false
    }

    /**
     * 重新计算view的宽高

     * @param view
     * *
     * @param horizontalRatio
     * *
     * @param verticalRatio
     * *
     * @return
     */

    fun resizeWidthAndHeight(view: View?, horizontalRatio: Float, verticalRatio: Float): Boolean {
        var horizontalRatio = horizontalRatio
        var verticalRatio = verticalRatio
        if (view == null)
            return false
        val tag = view.tag
        if (tag is String) {
            if ("ignoreSize" == tag) {
                return true
            }
        }
        if (isHorizontalMode(view)) {
            horizontalRatio = SCALE_RATIO_HORIZONTAL
            verticalRatio = SCALE_RATIO_HORIZONTAL
        }

        if (isVerticalMode(view)) {
            horizontalRatio = SCALE_RATIO_VERTICAL
            verticalRatio = SCALE_RATIO_VERTICAL
        }

        val params = view.layoutParams
        if (params != null) {
            var width = params.width
            var height = params.height
            if (params.width != LayoutParams.MATCH_PARENT && params.width != LayoutParams.WRAP_CONTENT) {
                width = (width * horizontalRatio).toInt()
                if (width > 1)
                    params.width = width
            }
            if (params.height != LayoutParams.MATCH_PARENT && params.height != LayoutParams.WRAP_CONTENT) {
                height = (height * verticalRatio).toInt()
                if (height > 1)
                    params.height = height
            }
            view.layoutParams = params
        }

        return true
    }

    /**
     * 重新计算view的Padding(严格模式)

     * @param view
     * *
     * @return
     */
    @JvmOverloads
    fun repadding(view: View?, horizontalRatio: Float = SCALE_RATIO_HORIZONTAL, verticalRatio: Float = SCALE_RATIO_VERTICAL): Boolean {
        if (view == null)
            return false
        var paddingStart = view.paddingStart
        var paddingEnd = view.paddingEnd
        if (view.paddingLeft > 0)
            setPaddingLeft(view, (view.paddingLeft * horizontalRatio).toInt())
        if (view.paddingRight > 0)
            setPaddingRight(view, (view.paddingRight * horizontalRatio).toInt())
        setPaddingTop(view, (view.paddingTop * verticalRatio).toInt())
        setPaddingBottom(view, (view.paddingBottom * verticalRatio).toInt())
        if (paddingStart > 0)
            setPaddingStart(view, (paddingStart * horizontalRatio).toInt())
        if (paddingEnd > 0)
            setPaddingEnd(view, (paddingEnd * horizontalRatio).toInt())
        return true
    }

    /**
     * 重新计算view的Margin

     * @param view
     * *
     * @return
     */
    @JvmOverloads
    fun remargin(view: View, horizontalRatio: Float = SCALE_RATIO_HORIZONTAL, verticalRatio: Float = SCALE_RATIO_VERTICAL) {
        var marginParams: MarginLayoutParams? = null
        try {
            marginParams = view.layoutParams as MarginLayoutParams
        } catch (e: ClassCastException) {
            return
        }

        if (marginParams == null)
            return
        val left = (marginParams.leftMargin * horizontalRatio).toInt()
        val top = (marginParams.topMargin * verticalRatio).toInt()
        val right = (marginParams.rightMargin * horizontalRatio).toInt()
        val bottom = (marginParams.bottomMargin * verticalRatio).toInt()
        marginParams.setMargins(left, top, right, bottom)
        view.layoutParams = marginParams
    }

    /**
     * 重新计算TextView中文本的大小

     * @param view
     * *
     * @return
     */
    fun resizeText(view: TextView?): Boolean {
        if (view == null)
            return false
        val tag = view.tag
        val tagString = (tag as? String)?.toString() ?: ""
        if (!TextUtil.contains(tagString, "defaultFont"))
            FontHelper.applyFont(view)
        if (TextUtil.contains(tagString, "ignoreSize"))
            return true
        val text = view.text.toString()
        var id = 0
        if (text.startsWith("@"))
            try {
                id = text.substring(1).toInt()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }

        // LogUtil.d("resizeText id : " + id);
        if (id > 0) {
            val string = ResourcesHelper.getString(id)
            // LogUtil.d("resizeText id : " + view.getId() + " string : " +
            // string);
            view.text = string
        }
        val textSize = view.textSize
        val ratio = scaleRatio
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize * ratio)
        resizeMaxWidth(view)
        resizeMaxHeight(view)
        return true
    }

    private fun resizeMaxWidth(view: TextView) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
            return
        var maxWidth = view.maxWidth
        if (maxWidth <= 0)
            return
        maxWidth = (maxWidth * scaleRatio).toInt()
        view.maxWidth = maxWidth
    }

    private fun resizeMaxHeight(view: TextView) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
            return
        var maxHeight = view.maxHeight
        if (maxHeight <= 0)
            return
        maxHeight = (maxHeight * scaleRatio).toInt()
        view.maxHeight = maxHeight
    }

    /**
     * 重新计算view的宽高(高度及宽度均按照水平缩放比例)

     * @param view
     * *
     * @return
     */
    fun resizeWithHorizontalRatio(view: View): Boolean {
        return resize(view, SCALE_RATIO_HORIZONTAL, SCALE_RATIO_HORIZONTAL)
    }

    fun resizeWithVerticalRatio(view: View): Boolean {
        return resize(view, SCALE_RATIO_VERTICAL, SCALE_RATIO_VERTICAL)
    }

    /**
     * 重新计算view的宽高(高度按照垂直缩放比例,宽度按照水平缩放比例)

     * @param view
     * *
     * @return
     */
    fun resizeWithRespectiveRatio(view: View): Boolean {
        return resize(view, SCALE_RATIO_HORIZONTAL, SCALE_RATIO_VERTICAL)
    }

    fun resizeChildrenRecursively(view: View?): Boolean {
        if (view == null)
            return false

        if (view !is ViewGroup)
            return true
        val group = view
        val childCount = group.childCount
        var child: View? = null
        for (i in 0..childCount - 1) {
            child = group.getChildAt(i)
            resizeRecursively(child)
        }
        return true
    }

    /**
     * 递归重新计算view的宽高(高度按照垂直缩放比例,宽度按照水平缩放比例)

     * @param view
     * *
     * @return
     */
    fun resizeRecursivelyWithRespectiveRatio(view: View): Boolean {
        return resizeRecursively(view, SCALE_RATIO_HORIZONTAL, SCALE_RATIO_VERTICAL)
    }

    /**
     * 递归重新计算view的宽高(高度和宽度均按照垂直缩放比例)

     * @param view
     * *
     * @return
     */
    fun resizeRecursivelyWithVerticalRatio(view: View): Boolean {
        return resizeRecursively(view, SCALE_RATIO_VERTICAL, SCALE_RATIO_VERTICAL)
    }

    /**
     * 递归重新计算view的宽高

     * @param view
     * *
     * @param horizontalRatio 水平缩放比例
     * *
     * @param verticalRatio   垂直缩放比例
     * *
     * @return
     */
    private fun resizeRecursively(view: View?, horizontalRatio: Float, verticalRatio: Float): Boolean {
        if (view == null)
            return false
        if (isResized(view))
            return false
        /* 是否为严格模式 */
        val strictMode = isStrictMode(view)
        /* 如果当前View需要以严格模式缩放，自动将所有子孙View按照严格模式缩放 */
        if (strictMode)
            return resizeStrictRecursively(view, scaleRatio, scaleRatio)
        if (shouldIgnore(view))
            return true
        resize(view, horizontalRatio, verticalRatio)
        if (view !is ViewGroup)
            return true
        val group = view
        val childCount = group.childCount
        var child: View? = null
        for (i in 0..childCount - 1) {
            child = group.getChildAt(i)
            resizeRecursively(child, horizontalRatio, verticalRatio)
        }
        return true
    }

    /**
     * 递归重新计算view的宽高(严格模式)

     * @param view
     * *
     * @param horizontalRatio 水平缩放比例
     * *
     * @param verticalRatio   垂直缩放比例
     * *
     * @return
     */
    private fun resizeStrictRecursively(view: View?, horizontalRatio: Float, verticalRatio: Float): Boolean {
        if (view == null)
            return false
        resizeStrictly(view, horizontalRatio, verticalRatio)
        if (view !is ViewGroup)
            return true
        val group = view
        val childCount = group.childCount
        var child: View? = null
        for (i in 0..childCount - 1) {
            child = group.getChildAt(i)
            resizeStrictRecursively(child, horizontalRatio, verticalRatio)
        }
        return true
    }

    /**
     * 是否为严格缩放模式

     * @param view
     * *
     * @return
     */
    private fun isStrictMode(view: View): Boolean {
        var strictMode = false
        val tag = view.tag ?: return false
        val tagString = tag.toString()
        if (AUTO_RESIZE_STRICT_TAG == tagString)
            strictMode = true
        return strictMode
    }

    /**
     * 是否按水平比例缩放

     * @param view
     * *
     * @return
     */
    private fun isHorizontalMode(view: View): Boolean {
        val params = view.layoutParams ?: return false
        val width = params.width
        return UI_DESIGN_LANDSCAPE_SIZE == width
    }

    /**
     * 是否按垂直比例缩放

     * @param view
     * *
     * @return
     */
    private fun isVerticalMode(view: View): Boolean {
        var strictMode = false
        val tag = view.tag ?: return false
        val tagString = tag.toString()
        if (AUTO_RESIZE_VERTICAL_TAG == tagString)
            strictMode = true
        return strictMode
    }

    /**
     * 根据屏幕宽度设置传入View的宽度

     * @param view
     * *
     * @param designedWidthResId UI设计的高度资源ID
     * *
     * @return 是否成功设置
     */
    fun setDesignedWidthResId(view: View, designedWidthResId: Int): Boolean {
        val designedWidth = ResourcesHelper.getDimension(designedWidthResId)
        var params: LayoutParams? = null
        // if (view instanceof ViewGroup)
        params = view.layoutParams
        if (params == null)
            params = (view.parent as View).layoutParams
        if (params == null)
            return false
        params.width = (designedWidth * SCALE_RATIO_HORIZONTAL).toInt()
        view.layoutParams = params
        return true
    }

    /**
     * 根据屏幕宽度设置传入View的高度

     * @param view
     * *
     * @param designedHeightResId UI设计的高度资源ID(以像素为单位)
     * *
     * @return 是否成功设置
     */
    fun setHeight(view: View?, height: Int): Boolean {
        if (view == null)
            return false
        var params: LayoutParams? = null
        // if (view instanceof ViewGroup)
        params = view.layoutParams
        if (params == null)
            params = (view.parent as View).layoutParams
        if (params == null)
            return false
        params.height = height
        view.layoutParams = params
        return true
    }

    /**
     * 根据屏幕宽度设置传入View的高度

     * @param view
     * *
     * @param designedHeight UI设计的高度(以像素为单位)
     * *
     * @return 是否成功设置
     */
    fun setDesignedHeight(view: View?, designedHeight: Int): Boolean {
        if (view == null)
            return false
        var params: LayoutParams? = null
        // if (view instanceof ViewGroup)
        params = view.layoutParams
        if (params == null && view.parent != null)
            params = (view.parent as View).layoutParams
        if (params == null)
            params = LayoutParams(view.measuredWidth, view.measuredHeight)
        params.height = (designedHeight * SCALE_RATIO_VERTICAL).toInt()
        view.layoutParams = params
        return true
    }

    fun setDesignedWidth(view: View?, designedWidth: Int): Boolean {
        if (view == null)
            return false
        var params: LayoutParams? = null
        // if (view instanceof ViewGroup)
        params = view.layoutParams
        if (params == null && view.parent != null)
            params = (view.parent as View).layoutParams
        if (params == null)
            params = LayoutParams(view.measuredWidth, view.measuredHeight)
        params.width = (designedWidth * SCALE_RATIO_HORIZONTAL).toInt()
        view.layoutParams = params
        return true
    }

    fun setDesignedMinHeight(view: View?, designedMinHeight: Int): Boolean {
        if (view == null)
            return false
        var minHeight = designedMinHeight * SCALE_RATIO_VERTICAL
        view.minimumHeight = minHeight.toInt()
        return true
    }

    /**
     * 根据屏幕宽度设置传入View的高度

     * @param view
     * *
     * @param designedHeight UI设计的高度(以像素为单位)
     * *
     * @return 是否成功设置
     */
    fun setDesignedSize(view: View?, designedWidth: Int, designedHeight: Int): Boolean {
        if (view == null)
            return false
        var params: LayoutParams? = null
        // if (view instanceof ViewGroup)
        params = view.layoutParams
        if (params == null && view.parent != null)
            params = (view.parent as View).layoutParams
        if (params == null)
            params = LayoutParams(view.measuredWidth, view.measuredHeight)
        params.width = (designedWidth * scaleRatio).toInt()
        params.height = (designedHeight * scaleRatio).toInt()

        view.layoutParams = params
        return true
    }

    /**
     * 根据屏幕宽高设置传入View的宽高(按照宽度比例等比缩放)

     * @param view
     * *
     * @param designedWidthResId  UI设计的宽度资源ID(以像素为单位)
     * *
     * @param designedHeightResId UI设计的高度资源ID(以像素为单位)
     * *
     * @return 是否成功设置
     */
    fun setSize(view: View, designedWidthResId: Int, designedHeightResId: Int): Boolean {
        val designedWidth = ResourcesHelper.getDimension(designedWidthResId)
        val designedHeight = ResourcesHelper.getDimension(designedHeightResId)
        var params: LayoutParams? = null
        // if (view instanceof ViewGroup)
        params = view.layoutParams
        if (params == null)
            params = (view.parent as View).layoutParams
        if (params == null)
            return false
        params.width = (designedWidth * SCALE_RATIO_HORIZONTAL).toInt()
        params.height = (designedHeight * SCALE_RATIO_HORIZONTAL).toInt()
        view.layoutParams = params
        return true
    }

    /**
     * 根据屏幕宽度设置传入View的高度

     * @param view
     * *
     * @param height 像素值
     * *
     * @return 是否成功设置
     */
    fun setViewHeight(view: View?, height: Int?): Boolean {
        if (view == null || height == null)
            return false
        var params: LayoutParams? = view?.layoutParams
        if (params == null)
            params = LayoutParams(view.width, view.height)
        params.height = height
        view.layoutParams = params
        return true
    }

    /**
     * 根据屏幕宽度设置传入View的宽度

     * @param view
     * *
     * @param width 像素值
     * *
     * @return 是否成功设置
     */
    fun setViewWidth(view: View, width: Int): Boolean {
        var params: LayoutParams? = view.layoutParams
        if (params == null)
            params = LayoutParams(view.width, view.height)
        params.width = width
        view.layoutParams = params
        return true
    }

    /**
     * 设置视图宽高

     * @param view
     * *
     * @param width
     * *
     * @param height
     * *
     * @return
     */
    fun setViewSize(view: View, width: Int, height: Int): Boolean {
        var params: LayoutParams? = view.layoutParams
        if (params == null)
            params = LayoutParams(view.width, view.height)
        params.width = width
        params.height = height
        view.layoutParams = params
        return true
    }

    /**
     * 根据屏幕宽度设置传入TextView的文本大小

     * @param view
     * *
     * @param designedTextSizeResId UI设计的文本大小资源ID(以像素为单位)
     * *
     * @return 是否成功设置
     */
    fun setTextSize(view: TextView?, designedTextSizeResId: Int): Boolean {
        if (view == null)
            return false
        val designedSize = ResourcesHelper.getDimension(designedTextSizeResId)
        val size = designedSize * scaleRatio
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        return true
    }

    /**
     * 根据屏幕宽度设置传入TextView的文本大小

     * @param view
     * *
     * @param designedSize UI设计的文本大小(以像素为单位)
     * *
     * @return 是否成功设置
     */
    fun setTextDesignedSize(view: TextView?, designedSize: Int): Boolean {
        if (view == null)
            return false
        val size = designedSize * SCALE_RATIO_HORIZONTAL
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        return true
    }

    /**
     * 设置View的外边距(像素值)

     * @param view
     * *
     * @param left
     * *
     * @param top
     * *
     * @param right
     * *
     * @param bottom
     * *
     * @return
     */
    fun setMargin(view: View, left: Int, top: Int, right: Int, bottom: Int): Boolean {
        var marginParams: MarginLayoutParams? = null
        try {
            marginParams = view.layoutParams as MarginLayoutParams
        } catch (e: ClassCastException) {
            return false
        }

        if (marginParams == null)
            return false
        marginParams.leftMargin = left
        marginParams.topMargin = top
        marginParams.rightMargin = right
        marginParams.bottomMargin = bottom
        view.layoutParams = marginParams
        return true
    }

    /**
     * 设置View的顶部外边距(像素值)

     * @param view
     * *
     * @return
     */
    fun setMarginTop(view: View?, marginTop: Int): Boolean {
        if (view == null)
            return false
        var marginParams: MarginLayoutParams? = null
        try {
            marginParams = view.layoutParams as MarginLayoutParams
        } catch (e: ClassCastException) {
            return false
        }

        if (marginParams == null)
            return false
        marginParams.topMargin = marginTop
        view.layoutParams = marginParams
        return true
    }

    /**
     * 设置View的左侧外边距(像素的资源ID)

     * @param view
     * *
     * @param resId 资源ID
     * *
     * @return
     */
    fun setMarginLeftRes(view: View, resId: Int): Boolean {
        return setMarginLeft(view, computeScaledDimenByHorizontalRatio(resId).toInt())
    }

    /**
     * 设置View的左侧外边距(像素值)

     * @param view
     * *
     * @return
     */
    fun setMarginLeft(view: View?, marginLeft: Int): Boolean {
        if (view == null)
            return false
        var marginParams: MarginLayoutParams? = null
        try {
            marginParams = view.layoutParams as MarginLayoutParams
        } catch (e: ClassCastException) {
            return false
        }

        if (marginParams == null)
            return false
        marginParams.leftMargin = marginLeft
        view.layoutParams = marginParams
        return true
    }

    /**
     * 设置View的顶部外边距(像素值)

     * @param view
     * *
     * @return
     */
    fun setMarginTopDesigned(view: View?, designedMarginTop: Int): Boolean {
        var marginParams: MarginLayoutParams? = null
        try {
            marginParams = view?.layoutParams as? MarginLayoutParams
        } catch (e: ClassCastException) {
            return false
        }

        if (marginParams == null)
            return false
        marginParams.topMargin = computeVerticallScaledSize(designedMarginTop)
        view?.layoutParams = marginParams
        return true
    }

    /**
     * 设置View的底部外边距(像素值)

     * @param view
     * *
     * @return
     */
    fun setMarginBottomDesigned(view: View, designedMarginBottom: Int): Boolean {
        var marginParams: MarginLayoutParams? = null
        try {
            marginParams = view.layoutParams as MarginLayoutParams
        } catch (e: ClassCastException) {
            return false
        }

        if (marginParams == null)
            return false
        marginParams.bottomMargin = computeVerticallScaledSize(designedMarginBottom)
        view.layoutParams = marginParams
        return true
    }

    /**
     * 设置View的右侧外边距(像素值)

     * @param view
     * *
     * @return
     */
    fun setMarginRightDesigned(view: View?, designedMarginRight: Int): Boolean {
        var marginParams: MarginLayoutParams? = null
        try {
            marginParams = view?.layoutParams as MarginLayoutParams
        } catch (e: ClassCastException) {
            return false
        }

        if (marginParams == null)
            return false
        marginParams.rightMargin = computeHorizontalScaledSize(designedMarginRight)
        view.layoutParams = marginParams
        return true
    }

    /**
     * 设置View的左侧外边距(像素值)

     * @param view
     * *
     * @return
     */
    fun setMarginLeftDesigned(view: View, designedMarginLeft: Int): Boolean {
        var marginParams: MarginLayoutParams? = null
        try {
            marginParams = view.layoutParams as MarginLayoutParams
        } catch (e: ClassCastException) {
            return false
        }

        if (marginParams == null)
            return false
        marginParams.leftMargin = computeHorizontalScaledSize(designedMarginLeft)
        view.layoutParams = marginParams
        return true
    }

    /**
     * 设置View的右侧外边距(像素值)

     * @param view
     * *
     * @return
     */
    fun setMarginRight(view: View?, marginRight: Int): Boolean {
        if (view == null)
            return false
        var marginParams: MarginLayoutParams? = null
        try {
            marginParams = view.layoutParams as MarginLayoutParams
        } catch (e: ClassCastException) {
            return false
        }

        if (marginParams == null)
            return false
        marginParams.rightMargin = marginRight
        view.layoutParams = marginParams
        return true
    }

    /**
     * 设置View的底部外边距(像素值)

     * @param view
     * *
     * @return
     */
    fun setMarginBottom(view: View, marginBottom: Int): Boolean {
        var marginParams = view.layoutParams as? MarginLayoutParams

        if (marginParams == null)
            marginParams = MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        marginParams.bottomMargin = marginBottom
        view.layoutParams = marginParams
        return true
    }

    /**
     * 设置View的内边距(像素值)

     * @param view
     * *
     * @return
     */
    fun setPadding(view: View?, left: Int, top: Int, right: Int, bottom: Int): Boolean {
        if (view == null)
            return false
        view.setPadding(left, top, right, bottom)
        return true
    }

    /**
     * 设置View的顶部内边距(像素值)

     * @param view
     * *
     * @return
     */
    fun setPaddingTop(view: View?, top: Int): Boolean {
        if (view == null)
            return false
        view.setPadding(view.paddingLeft, top, view.paddingRight, view.paddingBottom)
        return true
    }

    fun setPaddingTopBottomDesigned(view: View?, paddingTopBottomDesigned: Int): Boolean {
        if (view == null)
            return false
        var padding = computeVerticallScaledSize(paddingTopBottomDesigned)
        view.setPadding(view.paddingLeft, padding, view.paddingRight, padding)
        return true
    }

    fun setPaddingStartEnd(view: View?, paddingStartEnd: Int): Boolean {
        if (view == null)
            return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            view.setPaddingRelative(paddingStartEnd, view.paddingTop, paddingStartEnd, view.paddingBottom)
        }
        return true
    }

    fun setPaddingStart(view: View?, paddingStart: Int): Boolean {
        if (view == null)
            return false
        view.setPaddingRelative(paddingStart, view.paddingTop, view.paddingEnd, view.paddingBottom)
        return true
    }

    fun setPaddingEnd(view: View?, paddingEnd: Int): Boolean {
        if (view == null)
            return false
        view.setPaddingRelative(view.paddingStart, view.paddingTop, paddingEnd, view.paddingBottom)
        return true
    }

    /**
     * 设置View的左侧内边距(像素值)

     * @param view
     * *
     * @return
     */
    fun setPaddingLeft(view: View?, paddingLeft: Int): Boolean {
        if (view == null)
            return false
        view.setPadding(paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)
        return true
    }

    /**
     * 设置View的右侧内边距(像素值)

     * @param view
     * *
     * @return
     */
    fun setPaddingRight(view: View?, paddingRight: Int): Boolean {
        if (view == null)
            return false
        view.setPadding(view.paddingLeft, view.paddingTop, paddingRight, view.paddingBottom)
        return true
    }

    fun setPaddingRightDesigned(view: View?, paddingRightDesigned: Int): Boolean {
        if (view == null)
            return false
        view.setPadding(view.paddingLeft, view.paddingTop, (paddingRightDesigned * SCALE_RATIO_HORIZONTAL).toInt(), view.paddingBottom)
        return true
    }

    fun setPaddingLeftDesigned(view: View?, paddingLeftDesigned: Int): Boolean {
        if (view == null)
            return false
        view.setPadding((paddingLeftDesigned * SCALE_RATIO_HORIZONTAL).toInt(), view.paddingTop, view.paddingRight, view.paddingBottom)
        return true
    }

    fun setPaddingTopDesigned(view: View?, paddingTopDesigned: Int): Boolean {
        if (view == null)
            return false
        view.setPadding(view.paddingLeft, (paddingTopDesigned * SCALE_RATIO_VERTICAL).toInt(), view.paddingRight, view.paddingBottom)
        return true
    }

    fun setPaddingBottomDesigned(view: View?, paddingBottomDesigned: Int): Boolean {
        if (view == null)
            return false
        view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, (paddingBottomDesigned * SCALE_RATIO_VERTICAL).toInt())
        return true
    }

    /**
     * 设置View的底部内边距(像素值)

     * @param view
     * *
     * @return
     */
    fun setPaddingBottom(view: View?, paddingBottom: Int): Boolean {
        if (view == null)
            return false
        view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, paddingBottom)
        return true
    }

    /**
     * 获取View的宽度(像素值)

     * @param view
     * *
     * @return
     */
    fun getWidth(view: View): Int {
        val params = view.layoutParams ?: return 0
        return params.width
    }

    /**
     * 获取View的高度(像素值)

     * @param view
     * *
     * @return
     */
    fun getHeight(view: View): Int {
        val params = view.layoutParams ?: return 0
        return params.height
    }

    /**
     * 获取高度值(像素)(按照宽度比例缩放)

     * @return
     */
    fun getHorizontalScaledDimen(heightResId: Int): Float {
        return ResourcesHelper.getDimension(heightResId) * SCALE_RATIO_HORIZONTAL
    }

    /**
     * 获取高度值(像素)(按照高度比例缩放)

     * @return
     */
    fun getVerticalScaledDimen(heightResId: Int): Float {
        return ResourcesHelper.getDimension(heightResId) * SCALE_RATIO_VERTICAL
    }

    val displayWidth: Int
        get() = windowWidth

    /**
     * 获取屏幕宽度(像素)

     * @return
     */
    val windowWidth: Int
        get() {
            var dm: DisplayMetrics? = DisplayMetrics()
            dm = ResourcesHelper.displayMetrics
            if (dm == null)
                return 0
            return dm.widthPixels
        }

    /**
     * 获取屏幕高度(像素)

     * @return
     */
    val windowHeight: Int
        get() {
            var dm: DisplayMetrics? = DisplayMetrics()
            dm = ResourcesHelper.displayMetrics
            if (dm == null)
                return 0
            return dm.heightPixels
        }

    val displayHeight: Int
        get() {
            val windowHeight = windowHeight
            val navigationBarHeight = ActivityUtil.navigationBarHeight
            if (!ActivityUtil.checkDeviceHasNavigationBar())
                return windowHeight
            return windowHeight + navigationBarHeight
        }

    /**
     * 计算资源文件中定义的尺寸像素值

     * @param resId dimen.xml中定义的资源ID
     * *
     * @return
     */
    fun computeDimen(resId: Int): Float {
        return ResourcesHelper.getDimension(resId)
    }

    /**
     * 计算资源文件中定义的尺寸像素值，并乘以缩放系数ratio

     * @return
     */
    fun computeScaledDimen(resId: Int, ratio: Float): Float {
        return computeDimen(resId) * ratio
    }

    /**
     * 重新计算尺寸像素值，并乘以缩放系数ratio

     * @return
     */
    fun computeScaledSize(size: Int): Int {
        return (size * scaleRatio).toInt()
    }

    /**
     * 重新计算尺寸像素值，并乘以缩放系数ratio

     * @return
     */
    fun computeScaledSizeByDensity(size: Int): Int {
        var size = size
        val density = ApplicationUtil.context!!.resources.displayMetrics.density
        size = (size * density / 3.0f).toInt()
        return size
    }

    /**
     * 重新计算尺寸像素值，并乘以缩放系数ratio

     * @return
     */
    fun computeHorizontalScaledSize(size: Int): Int {
        return (size * SCALE_RATIO_HORIZONTAL).toInt()
    }

    /**
     * 重新计算尺寸像素值，并乘以缩放系数ratio

     * @return
     */
    fun computeHorizontalScaledSize(size: Float): Float {
        return size * SCALE_RATIO_HORIZONTAL
    }

    /**
     * 重新计算尺寸像素值，并乘以缩放系数ratio

     * @return
     */
    fun computeVerticallScaledSize(size: Int): Int {
        return (size * SCALE_RATIO_VERTICAL).toInt()
    }

    /**
     * 重新计算尺寸像素值，并乘以缩放系数ratio

     * @return
     */
    fun computeVerticallScaledSize(size: Float): Float {
        return size * SCALE_RATIO_VERTICAL
    }

    /**
     * 重新计算尺寸像素值，并乘以缩放系数ratio

     * @return
     */
    fun computeScaledSize(size: Float): Float {
        return size * scaleRatio
    }

    /**
     * 计算资源文件中定义的尺寸像素值，并乘以垂直缩放系数

     * @param resId UI设计的大小资源ID(以像素为单位)
     * *
     * @return
     */
    fun computeScaledDimenByVerticalRatio(resId: Int): Float {
        return computeScaledDimen(resId, SCALE_RATIO_VERTICAL)
    }

    /**
     * 计算资源文件中定义的尺寸像素值，并乘以水平缩放系数

     * @param resId UI设计的大小资源ID(以像素为单位)
     * *
     * @return
     */
    fun computeScaledDimenByHorizontalRatio(resId: Int): Float {
        return computeScaledDimen(resId, SCALE_RATIO_HORIZONTAL)
    }

    /**
     * 计算资源文件中定义的尺寸像素值，并乘以均衡缩放系数

     * @param resId UI设计的大小资源ID(以像素为单位)
     * *
     * @return
     */
    fun computeScaledDimenByBalancedRatio(resId: Int): Float {
        return computeScaledDimen(resId, scaleRatio)
    }

    /**
     * 计算屏幕密度
     */
    fun computeScreenDensity() {
        var dm: DisplayMetrics? = DisplayMetrics()
        dm = ResourcesHelper.displayMetrics
        if (dm == null)
            return
        displayDensity = dm.density
    }

    /**
     * 计算UI/字体缩放比例
     */
    fun computeScaleRatio() {
        val displayWidth = displayWidth
        val displayHeight = displayHeight
        if (displayWidth == 0 || displayHeight == 0)
            return
        val designedWidth = if (displayWidth > displayHeight) UI_DESIGN_PORTRAIT_SIZE else UI_DESIGN_LANDSCAPE_SIZE
        val designedHeight = if (displayWidth > displayHeight) UI_DESIGN_LANDSCAPE_SIZE else UI_DESIGN_PORTRAIT_SIZE
        if (displayWidth == 2392)
            LogUtil.trace()
        "computeScaleRatio displayWidth : $displayWidth displayHeight : $displayHeight designedWidth : $designedWidth designedHeight: $designedHeight".log
        SCALE_RATIO_HORIZONTAL = displayWidth.toFloat() / designedWidth.toFloat()
        SCALE_RATIO_VERTICAL = displayHeight.toFloat() / designedHeight.toFloat()

        val ratioDesigned = UI_DESIGN_PORTRAIT_SIZE.toFloat() / UI_DESIGN_LANDSCAPE_SIZE.toFloat()
        val ratioDevice = displayHeight.toFloat() / displayWidth.toFloat()
        /*
		 * 当设备宽高比例与UI设计的比例相同，或者设备宽高比例比UI设计的比例瘦长时按照宽度等比缩放(主流)，相反，
		 * 如果比UI设计的比例胖扁时按照高度缩放
		 */
        scaleRatio = if (ratioDevice >= ratioDesigned) SCALE_RATIO_HORIZONTAL else SCALE_RATIO_VERTICAL
    }

    /**
     * 检查当前屏幕方向是否为横向

     * @return
     */
    val isLandscape: Boolean
        get() = ResourcesHelper.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    /**
     * dip转换px

     * @param dip
     * *
     * @return
     */
    fun dip2px(dip: Float): Int {
        val f = ResourcesHelper.displayMetrics.density
        return (dip * f + 0.5f).toInt()
    }

    /**
     * dip转换px

     * @param dip
     * *
     * @return
     */
    fun px2dip(px: Float): Int {
        val f = ResourcesHelper.displayMetrics.density
        return (px / f).toInt()
    }

    /**
     * 检查UI事件是否发生在视图view的区域内

     * @param v
     * *
     * @param ev
     * *
     * @return
     */
    fun intersects(v: View?, ev: MotionEvent): Boolean {
        return intersects(v, ev.x.toInt(), ev.y.toInt())
    }

    fun intersects(v: View?, ev: MotionEvent, offsetX: Int, offsetY: Int): Boolean {
        return intersects(v, ev.x.toInt() + offsetX, ev.y.toInt() + offsetY)
    }

    fun intersects(v: View?, x: Int, y: Int): Boolean {
        if (v == null)
            return false
        val rect = Rect()
        v.getHitRect(rect)
        val r = Rect()
        r.right = x
        r.left = r.right
        r.top = y
        r.bottom = r.top
        return rect.intersects(r.left, r.top, r.right, r.bottom)
    }

    fun getBottom(v: View?): Int {
        if (v == null)
            return 0
        val rect = Rect()
        v.getHitRect(rect)
        return rect.bottom
    }

    val navigationBarHeight: Int
        get() {
            val isPortrait = ApplicationUtil.context!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

            val isTablet = ApplicationUtil.context!!.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE

            val key = (if (isPortrait)
                "navigation_bar_height"
            else
                if (isTablet) "navigation_bar_height_landscape" else null) ?: return 0

            val res = ApplicationUtil.context!!.resources
            var navigationHeight = res.getIdentifier(key, "dimen", "android")
            navigationHeight = res.getDimensionPixelSize(navigationHeight)
            return navigationHeight
        }

    /**
     * 通过反射计算状态栏高度

     * @return
     */
    val statusBarHeight: Int
        get() {
            if (STATUS_BAR_HEIGHT != 0)
                return STATUS_BAR_HEIGHT
            var c: Class<*>? = null
            var obj: Any? = null
            var field: Field? = null
            var x = 0
            var statusBarHeight = 0
            try {
                c = Class.forName("com.android.internal.R\$dimen")
                obj = c!!.newInstance()
                field = c.getField("status_bar_height")
                x = Integer.parseInt(field!!.get(obj).toString())
                statusBarHeight = ResourcesHelper.getDimensionPixelSize(x)
                STATUS_BAR_HEIGHT = statusBarHeight
            } catch (e1: Exception) {
                e1.printStackTrace()
            }

            return STATUS_BAR_HEIGHT
        }

    /**
     * 根据Activity获取状态栏高度

     * @param activityo
     * *
     * @return
     */
    fun getStatusBarHeight(activity: Activity): Int {
        val frame = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(frame)
        return frame.top
    }

    fun computeWindowRotation() {
        val windowManager = ApplicationUtil.context!!
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowRotation = windowManager.defaultDisplay.rotation
        // LogUtil.d("computeWindowRotation rotation : " + WINDOW_ROTATION);
    }

    val displayHeightWidthRatio: Float
        get() {
            if (DISPLAY_HEIGHT_WIDTH_RATIO == 0f)
                DISPLAY_HEIGHT_WIDTH_RATIO = displayHeight.toFloat() / windowWidth.toFloat()
            return DISPLAY_HEIGHT_WIDTH_RATIO
        }

    val displayDensityString: String
        get() = displayDensity.toString()

    val displayDensityDpiString: String
        get() {
            var dm: DisplayMetrics? = DisplayMetrics()
            dm = ApplicationUtil.context!!.resources.displayMetrics
            if (dm == null)
                return ""
            return dm.densityDpi.toString()
        }

    val displayXdpiString: String
        get() {
            var dm: DisplayMetrics? = DisplayMetrics()
            dm = ApplicationUtil.context!!.resources.displayMetrics
            if (dm == null)
                return ""
            return dm.xdpi.toString()
        }

    val displayYdpiString: String
        get() {
            var dm: DisplayMetrics? = DisplayMetrics()
            dm = ApplicationUtil.context!!.resources.displayMetrics
            if (dm == null)
                return ""
            return dm.ydpi.toString()
        }

    val displayResolution: String
        get() {
            val dm = DisplayMetrics()
            val windowManager = ApplicationUtil.context!!
                    .getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            display.getMetrics(dm)

            val screenWidth = dm.widthPixels
            val screenHeight = screenHeight
            return screenWidth.toString() + "×" + screenHeight.toString()
        }

    // since SDK_INT = 1;
    // includes window decorations (statusbar bar/navigation bar)
    // includes window decorations (statusbar bar/navigation bar)
    val screenHeight: Int
        get() {
            var heightPixels: Int
            val windowManager = ApplicationUtil.context!!
                    .getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val d = windowManager.defaultDisplay
            val metrics = DisplayMetrics()
            d.getMetrics(metrics)
            heightPixels = metrics.heightPixels
            if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
                try {
                    heightPixels = Display::class.java.getMethod("getRawHeight").invoke(d) as Int
                } catch (ignored: Exception) {
                }
            else if (Build.VERSION.SDK_INT >= 17)
                try {
                    val realSize = android.graphics.Point()
                    Display::class.java.getMethod("getRealSize", android.graphics.Point::class.java).invoke(d, realSize)
                    heightPixels = realSize.y
                } catch (ignored: Exception) {
                }

            return heightPixels
        }

    fun getMarginTop(view: View): Int {
        var marginParams: MarginLayoutParams? = null
        try {
            marginParams = view.layoutParams as MarginLayoutParams
        } catch (e: ClassCastException) {
            return 0
        }

        if (marginParams == null)
            return 0
        return marginParams.topMargin
    }

    fun getMarginBottom(view: View): Int {
        var marginParams: MarginLayoutParams? = null
        try {
            marginParams = view.layoutParams as MarginLayoutParams
        } catch (e: ClassCastException) {
            return 0
        }

        if (marginParams == null)
            return 0
        return marginParams.bottomMargin
    }

    fun getMarginLeft(view: View): Int {
        var marginParams: MarginLayoutParams? = null
        try {
            marginParams = view.layoutParams as MarginLayoutParams
        } catch (e: ClassCastException) {
            return 0
        }

        if (marginParams == null)
            return 0
        return marginParams.leftMargin
    }

    fun setFontSize(tv: TextView, rid: Int) {
        val size = ResourcesHelper.getDimensionPixelSize(rid)
        val fontSize = computeScaledSize(size)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize.toFloat())
    }

    fun setFontSizeByDesignedPx(tv: TextView, designedPx: Int) {
        val fontSize = computeScaledSize(designedPx)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize.toFloat())
    }

    fun pxToDip(pxValue: Float): Int {
        return (pxValue / Resources.getSystem()
                .displayMetrics.density + 0.5f).toInt()
    }
}