package im.clazz.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import com.halo.base.BaseLightActivity
import com.halo.component.Dialog
import com.halo.component.TextWatcher
import com.halo.helper.ResourcesHelper
import com.halo.util.InputMethodUtil
import im.clazz.R
import im.clazz.extension.EditCallback
import im.clazz.extension.onClick
import im.clazz.ui.component.SubmittingView
import kotlinx.android.synthetic.main.common_edit_activity.*

open class CommonEditActivity : BaseLightActivity() {
    var originText: String? = ""

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.common_edit_activity)

        var title = intent.getStringExtra("title")
        var hint = intent.getStringExtra("hint")
        var content = intent.getStringExtra("content")
        originText = content

        mTxtTitle.text = title
        mEditor.hint = hint
        mEditor.setText(content)

        mEditor.requestFocusForcely()
        InputMethodUtil.showInputMethod(mEditor)

        mImgBack.onClick {
            InputMethodUtil.hideInputMethod(mEditor)
            finish()
        }

        mImgConfirm.onClick {
            if (onConfirmCallback != null) {
                showSendingDialog()
                onConfirmCallback?.invoke(mEditor.text.toString(), this::onSuceess, this::onFail)
                return@onClick
            }
            var intent = Intent()
            intent.putExtra("content", mEditor.text.toString())
            setResult(Activity.RESULT_OK, intent)
            InputMethodUtil.hideInputMethod(mEditor)
            finish()
        }
        mImgConfirm.isEnabled = false

        mEditor.addTextChangedListener(object : TextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                var text = mEditor.text.toString()
                mImgConfirm.isEnabled = originText != text && text.isNotEmpty()
            }
        })
    }

    var dialog: Dialog? = null

    fun showSendingDialog() {
        InputMethodUtil.hideInputMethod(currentFocus)

        var submittingView = SubmittingView(this)
        submittingView!!.setBackgroundColor(ResourcesHelper.getColor(R.color.transparent))
        submittingView.setTip(R.string.saving)
        dialog = Dialog(window.decorView, submittingView)
        dialog!!.setOnDismissListener { }
        dialog!!.show()
    }

    private fun onSuceess() {
        dialog?.dismiss()
        finish()
    }

    private fun onFail() {
        dialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        onConfirmCallback = null
    }

    companion object {
        var onConfirmCallback: EditCallback? = null
    }
}