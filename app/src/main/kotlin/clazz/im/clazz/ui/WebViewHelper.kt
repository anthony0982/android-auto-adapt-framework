package im.clazz.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.webkit.*
import com.halo.util.FileType
import com.halo.util.SelectFileUtil
import im.clazz.extension.log
import im.clazz.extension.tip
import im.clazz.extension.uploadLog
import im.clazz.helper.InnerWebViewClient
import java.io.File

/**
 * Created by Wang on 2017/12/7.
 */
class WebViewHelper {

    var webviewActivity: Activity? = null
    var webview: WebView? = null
    var filePathCallback: ValueCallback<Array<Uri>>? = null

    fun init() {
        var settings = webview?.getSettings()
        settings?.setDefaultTextEncodingName("utf-8")
        settings?.setUseWideViewPort(true)

        settings?.setDomStorageEnabled(true)// 设置可以使用localStorage
        settings?.setAllowFileAccess(true)// 可以读取文件缓存(manifest生效)
        settings?.setAppCacheEnabled(false)
        settings?.setSupportZoom(false)
        settings?.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS)
        settings?.setMediaPlaybackRequiresUserGesture(false)

        settings?.javaScriptEnabled = true
        settings?.cacheMode = WebSettings.LOAD_NO_CACHE

//        webview?.addJavascriptInterface(this, "android")

        webview?.setWebViewClient(object : InnerWebViewClient() {
            override fun onPageFinished(webview: WebView, url: String) {
                if (finishedUrls.contains(url))
                    return
                finishedUrls.add(url)
//                js("")
//                "slideState?.stateString : ${slideState?.stateString}".uploadLog()
            }
        })

        webview?.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                "consoleMessage : ${consoleMessage?.message()}".uploadLog()
                return super.onConsoleMessage(consoleMessage)
            }

            override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                this@WebViewHelper.filePathCallback = filePathCallback
                chooseFile()
                return true
            }
        }
    }

    var selectFileUtil: SelectFileUtil? = null

    fun chooseFile() {
        selectFileUtil = SelectFileUtil()
        selectFileUtil?.activity = webviewActivity
        selectFileUtil?.callback = this::onGetPath
        selectFileUtil?.selectFile()
    }

    fun onGetPath(path: String?) {
        "onActivityResult path : $path".log
        var type = FileType.get(path)
        "type : $type".tip()
        var uri = Uri.fromFile(File(path))
        var array = arrayOf(uri)
        filePathCallback?.onReceiveValue(array)
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        selectFileUtil?.onActivityResult(requestCode, resultCode, data!!)
    }
}