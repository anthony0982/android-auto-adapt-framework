package im.clazz.ui

import android.content.Intent
import android.os.Bundle
import com.halo.base.BaseActivity
import com.halo.util.ActivityUtil
import im.clazz.R
import im.clazz.extension.log
import kotlinx.android.synthetic.main.web_view_layout.*

class WebViewActivity : BaseActivity() {


    companion object {
        var url: String? = null
    }

    var webViewHelper = WebViewHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_view_layout)

        initWebViewHelper()
    }

    override fun shouldSetStatusBarColor(): Boolean {
        return false
    }

    private fun initWebViewHelper() {
        webViewHelper.webviewActivity = this
        webViewHelper.webview = mWebView
        webViewHelper.init()
        "loadUrl : $url".log
        mWebView.loadUrl(url)
    }

    override fun onResume() {
        super.onResume()
        ActivityUtil.hideNavigationBar(this)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onPause() {
        super.onPause()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        webViewHelper?.onActivityResult(requestCode, resultCode, data)
    }
}
