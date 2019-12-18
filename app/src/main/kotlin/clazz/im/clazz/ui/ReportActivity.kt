package im.clazz.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.halo.base.BaseActivity
import com.halo.component.Dialog
import com.halo.helper.ResourcesHelper
import com.halo.model.Data
import com.halo.net.Request
import com.halo.net.ResponseXListener
import com.halo.util.InputMethodUtil
import im.clazz.R
import im.clazz.extension.onClick
import im.clazz.extension.tip
import im.clazz.ui.component.SubmittingView
import im.clazz.view.SettingsItemView
import kotlinx.android.synthetic.main.report_activity.*
import org.jetbrains.anko.forEachChild

class ReportActivity : BaseActivity() {
    var submittingView: SubmittingView? = null
    var dialog: Dialog? = null

    companion object {
        var type: String? = null
        var id: Int? = null
        var title: String? = null
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.report_activity)
        initViews()
        mImgConfirm.isEnabled = false
    }


    fun initViews() {
        mImgBack.onClick {
            back()
        }
        mImgConfirm.onClick {
            publish()
        }
        mImgConfirm.isEnabled = false
        mLayoutContent.forEachChild {
            var item = it as SettingsItemView
            item.activity = this
            item.onClick {
                select(item)
                mImgConfirm.isEnabled = true
            }
        }
    }

    var reason: String? = null

    private fun select(selected: SettingsItemView) {
        mLayoutContent.forEachChild {
            var item = it as SettingsItemView
            if (item == selected) {
                item.check()
                reason = item.title
            } else
                item.uncheck()
        }
    }


    override fun shouldObserveKeyboard(): Boolean {
        return true
    }

    override fun onBackPressed() {
        back()
    }

    fun back() {
        finish()
    }


    fun publish() {
        showSendingDialog()
        Request.report(id, type, reason, object : ResponseXListener<Data>() {
            override fun onSuccess(t: Data?) {
                super.onSuccess(t)
                dialog?.dismiss()
                R.string.reported.tip
                finish()
            }

            override fun onFail(t: Data?) {
                super.onFail(t)
                dialog?.dismiss()

                if (t?.status == -1001)
                    R.string.report_duplicate.tip
                else
                    R.string.operation_fail.tip
            }
        })
    }


    fun showSendingDialog() {
        InputMethodUtil.hideInputMethod(currentFocus)

        submittingView = SubmittingView(this)
        submittingView!!.setBackgroundColor(ResourcesHelper.getColor(R.color.transparent))
        submittingView!!.setTip(R.string.submiting)

        dialog = Dialog(window.decorView, submittingView)
        dialog!!.setBackgroundColorResId(R.color.dialog_background)
        dialog!!.setOnDismissListener { }
        dialog!!.setAutoCancel(false)
        dialog!!.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK)
            return

        mLayoutContent.forEachChild {
            var item = it as? SettingsItemView
            item?.onActivityResult(requestCode, resultCode, data)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        id = null
        ReportActivity.title = null
        type = null
    }
}