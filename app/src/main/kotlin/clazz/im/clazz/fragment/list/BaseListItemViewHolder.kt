package im.clazz.frament.list

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import im.clazz.model.BaseItem

abstract open class BaseListItemViewHolder<T : BaseItem>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open var list: ArrayList<T>? = null

    var activity: Activity? = null

    var index: Int = 0
    var item: T? = null

    open fun update(position: Int) {
        this.index = position
        item = list?.get(position)
    }

    open fun updateItemState() {

    }
}