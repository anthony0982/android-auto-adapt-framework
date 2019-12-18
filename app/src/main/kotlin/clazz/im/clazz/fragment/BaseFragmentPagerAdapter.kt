package im.clazz.fragment

import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

abstract class BaseFragmentPagerAdapter(private val mFragmentManager: FragmentManager) : PagerAdapter() {
    private var mCurTransaction: FragmentTransaction? = null
    private var mCurrentPrimaryItem: Fragment? = null

    /**
     * Return the Fragment associated with a specified position.
     */
    abstract fun getItem(position: Int): Fragment?

    override fun startUpdate(container: ViewGroup) {
        if (container.id == View.NO_ID) {
            throw IllegalStateException("ViewPager with adapter " + this
                    + " requires a view id")
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any? {
        if (mCurTransaction == null)
            mCurTransaction = mFragmentManager.beginTransaction()

        // Do we already have this fragment?
        val tag = getFragmentTag(position)
        var fragment: Fragment? = mFragmentManager.findFragmentByTag(tag)
        if (fragment != null)
            mCurTransaction?.attach(fragment)
        else {
            fragment = getItem(position)
            mCurTransaction?.add(container.id, fragment, tag)
        }
        if (fragment !== mCurrentPrimaryItem) {
            fragment?.setMenuVisibility(false)
            fragment?.userVisibleHint = false
        }

        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction()
        }
        val fragment = `object` as Fragment
        mCurTransaction!!.detach(fragment)
        //        mCurTransaction.remove(fragment);
    }


    override fun getItemPosition(`object`: Any?): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, obj: Any?) {
        val fragment = obj as? Fragment
        if (fragment === mCurrentPrimaryItem)
            return
        mCurrentPrimaryItem?.setMenuVisibility(false)
        mCurrentPrimaryItem?.userVisibleHint = false
        fragment?.setMenuVisibility(true)
        fragment?.userVisibleHint = true
        mCurrentPrimaryItem = fragment
    }

    override fun finishUpdate(container: ViewGroup) {
        if (mCurTransaction != null) {
            mCurTransaction!!.commitNowAllowingStateLoss()
            mCurTransaction = null
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (`object` as Fragment).view === view
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    /**
     * Return a unique identifier for the item at the given position.
     *
     *
     *
     * The default implementation returns the given position.
     * Subclasses should override this method if the positions of items can change.

     * @param position Position within this adapter
     * *
     * @return Unique identifier for the item at position
     */
    fun getItemId(position: Int): Long {
        return position.toLong()
    }

    protected open fun getFragmentTag(position: Int): String? {
        return "android:pager:" + position
    }

    fun getCurrentFragment(): Fragment? {
        return mCurrentPrimaryItem
    }
}