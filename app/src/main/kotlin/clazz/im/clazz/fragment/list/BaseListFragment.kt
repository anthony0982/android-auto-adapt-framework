package im.clazz.frament.list

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.RelativeLayout
import com.halo.base.BaseFragment
import com.halo.component.SimpleDividerDecoration
import com.halo.helper.ResourcesHelper
import com.halo.net.ResponseXListener
import com.halo.util.CollectionUtil
import com.halo.util.TextUtil
import com.halo.view.FloatingActionButton
import com.halo.view.LoadMoreAdapterWrapper
import com.hlab.fabrevealmenu.view.FABRevealMenu
import im.clazz.R
import im.clazz.campus.BottomSwitcher
import im.clazz.campus.CampusFragment
import im.clazz.campus.news.BaseCampusFragment
import im.clazz.extension.*
import im.clazz.model.BaseItem
import im.clazz.model.BaseList
import im.clazz.ui.component.EmptyView
import im.clazz.ui.search.SearchActivity
import im.clazz.widget.LoadingView
import im.clazz.widget.PullRefreshLayout
import kotlinx.android.synthetic.main.base_list_fragment_layout.*
import net.tsz.afinal.annotation.view.ViewInject
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.textResource

abstract open class BaseListFragment<T : BaseItem, TList : BaseList<T>> : BaseFragment() {
    @ViewInject(id = R.id.base_list_recycler_view)
    val mRecyclerView: RecyclerView? = null
    @ViewInject(id = R.id.mEmptyView)
    val mViewEmpty: EmptyView? = null
    @ViewInject(id = R.id.base_list_error)
    val mViewError: x.TextView? = null
    @ViewInject(id = R.id.base_list_swipe_to_refresh)
    val mPullRefreshLayout: PullRefreshLayout? = null
    @ViewInject(id = R.id.mLoadingView)
    val mLoadingView: LoadingView? = null
    var mFab: FloatingActionButton? = null
    var mSearch: RelativeLayout? = null
    var mLayoutFilter: RelativeLayout? = null
    var mFabMenu: FABRevealMenu? = null
    var mBottomSwitcher: BottomSwitcher? = null

    var adapter: BaseListAdapter<T>? = null

    var list: ArrayList<T> = ArrayList()

    var searchResultList: ArrayList<T> = ArrayList()

    var wrapper: LoadMoreAdapterWrapper? = null

    var search = false
    var showFab = false
    var showSearch = false
    var showFilter = false
    var showTitleButton = false
    var showBottomSwitcher = false

    var filterCallback: Callback? = null

    var addItemDecoration = false
    var itemDecorationHeight = 20.v
    var itemDecorationColorResId = R.color.gray_fa
    var bindFabMenu = false
    var showRecyclerViewWithSearch = false

    var titleButtonStringResId: Int = R.string.base_list_empty
    var backgroudColorResId: Int = R.color.white
    var emptyStringResId: Int = R.string.base_list_empty
        set(value) {
            field = value
            mViewEmpty?.setTip(value)
        }

    var searchEmptyStringResId: Int = R.string.base_list_empty

    var fabDrawableResId: Int = R.drawable.ic_pencil
    var fabMenuResId: Int = R.menu.club_fragment_menu_fab

    var loadMethod: RequestMethod<TList>? = null

    var itemViewHolderBuilder: ItemViewHolderBuilder<T>? = null
        set(value) {
            field = value
            searchResultList.clear()
            list.clear()
            initAdapter()
        }

    var itemViewHolderBuilderList: ArrayList<ItemViewHolderBuilder<T>>? = null
        set(value) {
            field = value
            searchResultList.clear()
            list.clear()
            initAdapter()
        }

    var position: Int? = 0

    override fun onInitLayoutResId(): Int {
        return R.layout.base_list_fragment_layout
    }

    override fun onPostViewCreated(view: View?) {
        super.onPostViewCreated(view)
        mFab = mFabInListFragment
        mFabMenu = mFabMenuInListFragment
        onPostFragmentCreated()
        mPullRefreshLayout?.onRefresh = this::load
        mViewEmpty?.setTip(emptyStringResId)
        load()
        mViewError?.setOnClickListener(this::onErrorClick)
        initRecyclerView()
        mRecyclerView?.backgroundResource = backgroudColorResId
    }

    fun updateFab() {
        mFab?.showIf(showFab)
        if (showFab)
            mFab?.imageResource = fabDrawableResId
        mFab?.setOnClickListener(this::onFabClick)
        if (!bindFabMenu)
            return
        if (mFab != null && mFabMenu != null) {
            fabMenu = mFabMenu
            mFabMenu?.bindAncherView(mFab!!)
            mFabMenu?.setOnFABMenuSelectedListener(this::onMenuItemSelected)
        }
        mFabMenu?.setMenu(fabMenuResId)
    }

    open fun onPostFragmentCreated() {
    }

    open fun initSwitchView() {

    }

    override fun onResume() {
        super.onResume()
        wrapper?.notifyDataSetChanged()
        if (this is BaseCampusFragment)
            updateSeachType()
    }

    override fun onShow() {
        super.onShow()
        updateFab()

        mLayoutFilter?.showIf(showFilter)
        CampusFragment.mImgFilter?.imageResource = R.drawable.ic_filter
        mLayoutFilter?.onClick { filterCallback?.invoke() }
        mBottomSwitcher?.showIf(showBottomSwitcher)
        initSwitchView()
        if (showBottomSwitcher)
            mBottomSwitcher?.init(this.javaClass)
        updateSeachType()
    }

    fun updateSeachType() {
        if (CampusFragment.currentFragment == this)
            SearchActivity.searchType = this.javaClass.simpleName.toLowerCase().replace("fragment", "")
    }

    fun onSearchBarCreated() {
        adapter?.mTitleButton?.textResource = titleButtonStringResId
        adapter?.mTitleButton?.showIf(showTitleButton)
        adapter?.mTitleButton?.onClick {
            onTitleButtonClick()
        }
    }

    fun onSearchCancel() {
        search = false
        var list = checkList()
        if (adapter?.list != list) {
            adapter?.list = list
            wrapper?.notifyDataSetChanged()
        }
        checkVisibility()
    }

    fun checkVisibility() {
        val list = checkList()
        mViewEmpty?.showIf(list.isEmpty())
        mRecyclerView?.showIf(list.isNotEmpty() || showSearch && showRecyclerViewWithSearch)
    }

    fun search(keyword: String?) {
        search = !TextUtil.isEmpty(keyword)
        var emptyStringResId = if (search) searchEmptyStringResId else emptyStringResId
        mViewEmpty?.setTip(emptyStringResId)
        if (!search) {
            adapter?.list = list
            wrapper?.notifyDataSetChanged()
            onLoad(null, null, null, list)
            return
        }
        load(keyword = keyword)
    }

    private fun initRecyclerView() {
        mRecyclerView?.layoutManager = LinearLayoutManager(context)
        mRecyclerView?.setHasFixedSize(true)
        val itemDecoration = SimpleDividerDecoration(itemDecorationHeight, itemDecorationColorResId.color)
        if (addItemDecoration)
            mRecyclerView?.addItemDecoration(itemDecoration)
        initAdapter()
    }

    fun initAdapter() {
        adapter = BaseListAdapter(checkList(), itemViewHolderBuilder, showSearch)
        adapter?.itemViewHolderBuilderList = itemViewHolderBuilderList
        adapter?.searchMethod = this::search
        adapter?.onSearchCancelMethod = this::onSearchCancel
        adapter?.onSearchBarCreated = this::onSearchBarCreated
        adapter?.activity = activity

        initRecyclerViewFooter()
    }

    private fun initRecyclerViewFooter() {
        wrapper = LoadMoreAdapterWrapper.wrap(adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>).into(mRecyclerView)
                .setListener(this::loadMore)
    }

    fun loadMore() {
        if (list.size < 20) {
            return
        }
        var lastId = list?.last()?.id
        load(lastId) {
            wrapper?.onLoadMoreFinish()
        }
    }

    fun load() {
        load(null, null, null)
    }


    private fun checkList(): ArrayList<T> {
        return if (search) searchResultList else list
    }

    private fun load(offset: Int? = null, keyword: String? = null, callback: Callback? = null) {
        val list = checkList()
        mLoadingView?.showIf(list.isEmpty())
        mViewError?.hide()
        mViewEmpty?.hide()
        loadMethod?.invoke(offset, keyword, object : ResponseXListener<TList>() {
            override fun onBeforeProcess(t: TList?) {
                callback?.invoke()
                if (search)
                    searchResultList?.clear()
                else
                    if (offset == null)
                        list.clear()
            }

            override fun onSuccess(t: TList?) {
                super.onSuccess(t)
                if (!CollectionUtil.isEmpty(t?.list)) {
                    var oldCount = list.size
                    if (offset == null)
                        list.clear()
                    list.addAll(t?.list!! as Iterable<T>)
                    adapter?.list = list

                    var index = if (offset != null) oldCount else 0
                    if (adapter!!.showSearch)
                        index += 1
                    if (search || offset == null) {
                        wrapper?.notifyDataSetChanged()
                        mRecyclerView?.scrollToPosition(0)
                    } else
                        wrapper?.notifyItemRangeInserted(index, t?.list!!.size)
                }

                onLoad(offset, keyword, t, list)
            }

            override fun onFail(t: TList?) {
                super.onFail(t)
                mViewError?.setText(ResourcesHelper.getString(R.string.load_error, t?.status?.toString()))
                mViewError?.show()
                mViewEmpty?.hide()
            }


            override fun onFinish(t: TList?) {
                super.onFinish(t)
                mPullRefreshLayout?.finishRefresh()
                mLoadingView?.hide()
            }
        })
    }

    open fun onLoad(offset: Int?, keyword: String?, newResultList: TList? = null, list: ArrayList<T>) {
        wrapper?.shouldShowNoMore = list.size > 10
        wrapper?.noMore(CollectionUtil.size(newResultList?.list) < 20)

        if (list.isEmpty()) {
            onEmpty()
            adapter?.list = list
            wrapper?.notifyDataSetChanged()
            return
        } else {
            mRecyclerView?.show()
        }
        if (CollectionUtil.isEmpty(newResultList?.list)) {
            adapter?.list = list
            wrapper?.notifyDataSetChanged()
        }
        mViewError?.hide()
        mViewEmpty?.hide()
    }

    open fun onEmpty() {
        mViewError?.hide()
        checkVisibility()
    }

    fun onErrorClick(v: View?) {
        load()
    }

    fun showFab(show: Boolean) {
        mFab?.showIf(show)
    }

    open fun onFabClick(v: View?) {
    }

    open fun onTitleButtonClick() {

    }

    open fun onMenuItemSelected(view: View, id: Int) {
    }

    override fun onKeyboardHide() {
        super.onKeyboardHide()
        adapter?.mSearchBar?.onKeyboardHide()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK)
            load()
    }

    fun notifyDataSetChanged() {
        wrapper?.notifyDataSetChanged()
    }
}