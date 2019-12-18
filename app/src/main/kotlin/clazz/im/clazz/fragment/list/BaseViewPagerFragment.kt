package im.clazz.frament.list

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import com.halo.base.BaseFragment
import com.halo.helper.PreferenceHelper
import com.halo.helper.ResourcesHelper
import com.halo.viewpager.OnPageChangeListener
import im.clazz.R
import im.clazz.campus.CampusModuleFragment
import im.clazz.fragment.BaseFragmentPagerAdapter
import kotlinx.android.synthetic.main.base_view_pager_fragment_layout.*

abstract class BaseViewPagerFragment : CampusModuleFragment() {

    var leftTitleTextResId: Int? = null
    var rightTitleTextResId: Int? = null
    var mLeftFragment: BaseFragment? = null
    var mRightFragment: BaseFragment? = null

    var mCurrentFragment: BaseFragment? = null

    private var adapter: PagerAdapter? = null


    override fun onInitLayoutResId(): Int {
        return R.layout.base_view_pager_fragment_layout
    }

    override fun onPostViewCreated(view: View?) {
        super.onPostViewCreated(view)
        onPostFragmentCreated()
        initViews()
    }

    open fun onPostFragmentCreated() {
    }

    private fun initViews() {

        adapter = PagerAdapter(childFragmentManager)
        mViewPager?.adapter = adapter
        mPagerIndicator.setViewPager(mViewPager)
        mViewPager?.currentItem = PreferenceHelper.getInt(this.javaClass.name, 0)
        mViewPager?.addOnPageChangeListener(object : OnPageChangeListener() {

            override fun onPageSelected(position: Int) {
                mCurrentFragment = if (position == 0) mLeftFragment else mRightFragment
                mCurrentFragment?.onShow()
            }
        })
    }

    private fun onTitleBarClick(isLeftSelected: Boolean) {
        val currentItem = if (isLeftSelected) 0 else 1
        mViewPager?.setCurrentItem(currentItem)
    }

    override fun onShow() {
        super.onShow()
        mCurrentFragment = if (mViewPager?.currentItem == 0) mLeftFragment else mRightFragment
        mCurrentFragment?.onShow()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveCurrentItem()
    }

    override fun onPause() {
        super.onPause()
        saveCurrentItem()
    }

    fun saveCurrentItem() {
        super.onDestroyView()
        PreferenceHelper.setInt(this.javaClass.name, mViewPager!!.currentItem)
    }

    inner class PagerAdapter(fragmentManager: FragmentManager) : BaseFragmentPagerAdapter(fragmentManager) {

        override fun getPageTitle(position: Int): CharSequence {
            var titleResId = if (position == 0) leftTitleTextResId else rightTitleTextResId
            return ResourcesHelper.getString(titleResId)
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getItem(position: Int): Fragment? {
            if (position == 0)
                return mLeftFragment
            return mRightFragment
        }
    }
}