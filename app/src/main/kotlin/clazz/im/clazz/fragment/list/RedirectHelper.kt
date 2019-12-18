package im.clazz.fragment.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import chat.ContactUtil
import im.clazz.campus.group.GroupType
import im.clazz.ui.group.GroupActivity
import im.clazz.ui.student.UserActivity


object RedirectHelper {

    fun redirectWithContactId(context: Context? = null, groupType: GroupType? = null, contactId: String?) {
        redirect(context, groupType, ContactUtil.extractId(contactId!!).toString())
    }

    fun redirect(context: Context? = null, groupType: GroupType? = null, id: String?) {
        redirect(context, groupType, id?.toInt())
    }

    fun redirect(context: Context? = null, groupType: GroupType? = null, id: Int?) {
        when (groupType) {
            GroupType.Clazz, GroupType.Club, GroupType.Circle -> {
                GroupActivity.groupType = groupType
                GroupActivity.id = id!!
                val intent = Intent(context, GroupActivity::class.java)
                if (!(context is Activity))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(intent)
                return
            }

            GroupType.User -> {
                val intent = Intent(context, UserActivity::class.java)
                intent.putExtra("uid", id.toString())
                if (!(context is Activity))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(intent)
            }
        }
    }
}