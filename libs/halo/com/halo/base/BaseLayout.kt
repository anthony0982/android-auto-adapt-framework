package com.halo.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.halo.helper.LayoutHelper
import com.halo.util.WindowUtil
import net.tsz.afinal.FinalActivity
import org.jetbrains.anko.forEachChild

/**
 * 自定义控件的基类

 * @author wangzengyang@gmail.com
 * *
 * @since 2013-8-28
 */
abstract class BaseLayout : RelativeLayout {
    var contentView: View? = null
        private set

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initialize()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context?) : super(context) {
        initialize()
    }

    private fun initialize() {
        setContentView(onInitLayoutResId())
        FinalActivity.initInjectedView(this, this)
        onInit()
    }

    private fun setContentView(layoutResId: Int) {
        LayoutHelper.inflate(layoutResId, this)
        contentView = getChildAt(0)
    }

    protected abstract fun onInitLayoutResId(): Int

    protected open fun onInit() {}

    override fun setBackgroundResource(resid: Int) {
        contentView!!.setBackgroundResource(resid)
    }

    /**
     * 重新计算View及子View的宽高、边距

     * @param view
     */
    fun resizeView(view: View) {
        WindowUtil.resizeRecursively(view)
    }

    val logTag: String
        get() = this.javaClass.simpleName

    open fun onDestroy() {
        this.removeAllViews()
        contentView = null
    }

    open fun onResume() {}

    open fun onPause() {}

    open fun clear() {}

    fun reset() {}

    open fun onKeyboardShow() {
        forEachChild {
            (it as? BaseLayout)?.onKeyboardShow()
        }
    }

    open fun onKeyboardHide() {
        forEachChild {
            (it as? BaseLayout)?.onKeyboardHide()
        }
    }

    open fun onShow() {

    }
}
