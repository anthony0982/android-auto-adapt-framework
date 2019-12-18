package com.halo.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.halo.util.WindowUtil
import com.hlab.fabrevealmenu.view.FABRevealMenu
import net.tsz.afinal.FinalActivity

abstract class BaseFragment : Fragment() {

    protected var mContentView: View? = null

    protected var isNewCreated = true

    protected var isInFront: Boolean = false

    protected var fabMenu: FABRevealMenu? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mContentView != null) {
            isNewCreated = false
            val parent = mContentView!!.parent as? ViewGroup
            parent?.removeView(mContentView)
            return mContentView
        }
        val layoutResId = onInitLayoutResId()
        if (layoutResId == -1)
            return super.onCreateView(inflater, container, savedInstanceState)
        return inflater!!.inflate(layoutResId, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContentView = view
        if (!isNewCreated)
            return
        resizeView(view)
        FinalActivity.initInjectedView(this, view)
        onPostViewCreated(view)
    }

    override fun getView(): View? {
        return mContentView
    }

    fun getRootView(): View? {
        return mContentView
    }

    open fun onPostViewCreated(view: View?) {}

    /**
     * 重新计算View宽高、边距

     * @param view
     */
    fun resizeView(view: View?) {
        WindowUtil.resizeRecursively(view)
    }

    protected open fun onInitLayoutResId(): Int {
        return -1
    }

    protected val contentView: View?
        get() = view


    open fun shouldBeAddedToBackStack(): Boolean {
        return true
    }

    open fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return false
    }

    fun startActivity(clazz: Class<out Activity>) {
        val intent = Intent(activity, clazz)
        activity.startActivity(intent)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        LogUtil.d(this.javaClass.simpleName, "onAttach")
    }

    override fun onStart() {
        super.onStart()
        LogUtil.d(this.javaClass.simpleName, "onStart")
    }

    override fun onResume() {
        super.onResume()
        isInFront = true
        LogUtil.d(this.javaClass.simpleName, "onResume")
    }

    override fun onPause() {
        super.onPause()
        isInFront = false
        LogUtil.d(this.javaClass.simpleName, "onPause")
    }

    override fun onStop() {
        super.onStop()
        LogUtil.d(this.javaClass.simpleName, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.d(this.javaClass.simpleName, "onDestroy")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LogUtil.d(this.javaClass.simpleName, "onDestroyView")
    }

    override fun onDetach() {
        super.onDetach()
        LogUtil.d(this.javaClass.simpleName, "onDetach")
    }

    fun finish() {
        activity.finish()
    }

    open fun hide() {}

    open fun onShow() {}

    open fun onBackPressed(): Boolean {
        if (fabMenu != null && fabMenu!!.isShowing) {
            fabMenu!!.closeMenu()
            return true
        }
        return false
    }

    open fun onKeyboardShow() {
        childFragmentManager?.fragments?.forEach {
            (it as? BaseFragment)?.onKeyboardShow()
        }
    }

    open fun onKeyboardHide() {
        childFragmentManager?.fragments?.forEach {
            (it as? BaseFragment)?.onKeyboardHide()
        }
    }
}
