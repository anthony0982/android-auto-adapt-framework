package im.clazz.ui

import android.content.Intent
import android.os.Bundle
import com.halo.base.BaseLightActivity
import im.clazz.R
import im.clazz.extension.onClick
import kotlinx.android.synthetic.main.service_protocol_layout.*

class ServiceProtocolActivity : BaseLightActivity() {

    var webViewHelper = WebViewHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.service_protocol_layout)

        mImgBack.onClick { onBackPressed() }

        initWebViewHelper()
    }

    private fun initWebViewHelper() {
        webViewHelper.webviewActivity = this
        webViewHelper.webview = mWebView
        webViewHelper.init()
        mWebView.loadUrl("file:///android_asset/service_protocol.html")
    }

    override fun onResume() {
        super.onResume()
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
