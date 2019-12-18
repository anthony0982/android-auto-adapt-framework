package im.clazz.frament.list

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.halo.helper.LayoutHelper
import com.halo.util.CollectionUtil
import im.clazz.R
import im.clazz.extension.Callback
import im.clazz.extension.ItemViewHolderBuilder
import im.clazz.extension.StringCallback
import im.clazz.model.BaseItem
import im.clazz.view.SearchBar
import kotlinx.android.synthetic.main.base_list_header_view.view.*
import x.TextView

class BaseListAdapter<T : BaseItem>() : RecyclerView.Adapter<BaseListItemViewHolder<T>>() {

    var list: ArrayList<T>? = null
    var builder: ItemViewHolderBuilder<T>? = null
    var itemViewHolderBuilderList: ArrayList<ItemViewHolderBuilder<T>>? = null
    var mSearchBar: SearchBar? = null
    var mTitleButton: TextView? = null

    var searchMethod: StringCallback? = null
    var onSearchCancelMethod: Callback? = null
    var onSearchBarCreated: Callback? = null

    val ITEM_TYPE_SEARCH = -1
    val ITEM_TYPE_DATA = -2
    var showSearch = false
    var activity: Activity? = null

    constructor(list: ArrayList<T>?, builder: ItemViewHolderBuilder<T>? = null, showSearch: Boolean) : this() {
        this.list = list
        this.builder = builder
        this.showSearch = showSearch
    }

    override fun getItemCount(): Int {
        val size = CollectionUtil.size(list  as? Collection<T>)
        return size + if (showSearch) 1 else 0
    }

    override fun onBindViewHolder(holder: BaseListItemViewHolder<T>, position: Int) {
        var position = position
        if (showSearch && position == 0)
            return
        holder.activity = activity
        holder.list = list as ArrayList<T>
        if (showSearch)
            position = position - 1
        holder.update(position)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0 && showSearch)
            return ITEM_TYPE_SEARCH

        if (itemViewHolderBuilderList != null) {
            var item = (list as ArrayList<T>).get(position)
            return item.contentType
        }
        return ITEM_TYPE_DATA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseListItemViewHolder<T>? {
        if (viewType == ITEM_TYPE_SEARCH)
            return HeaderViewHolder()
        if (itemViewHolderBuilderList != null) {
            return itemViewHolderBuilderList!!.get(viewType).invoke()
        }
        return builder?.invoke()
    }

    inner class HeaderViewHolder : BaseListItemViewHolder<T>(LayoutHelper.inflateResized(R.layout.base_list_header_view)) {
        init {
            mSearchBar = itemView.mSearchBar
            mTitleButton = itemView.mTitleButton
            mSearchBar?.onCreateNoResize()
            mSearchBar?.onSearch = searchMethod
            onSearchBarCreated?.invoke()
        }
    }
}