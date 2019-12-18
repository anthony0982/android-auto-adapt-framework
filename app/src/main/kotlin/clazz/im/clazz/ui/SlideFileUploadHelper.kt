package im.clazz.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.webkit.*
import com.halo.upload.Base64UploadHelper
import com.halo.upload.UploadListener
import com.halo.util.SelectFileUtil
import im.clazz.extension.Callback
import im.clazz.extension.IntCallback
import im.clazz.extension.log
import im.clazz.extension.md5
import im.clazz.helper.InnerWebViewClient
import org.json.JSONObject
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Wang on 2017/12/7.
 */
class SlideFileUploadHelper {

    var activity: Activity? = null
    var webview: WebView? = null
    var filePathCallback: ValueCallback<Array<Uri>>? = null
    var progressCallback: IntCallback? = null
    var startUploadCallback: Callback? = null
    var url: String? = null

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
        settings?.setAllowFileAccessFromFileURLs(true)

        settings?.javaScriptEnabled = true
        settings?.cacheMode = WebSettings.LOAD_NO_CACHE

        webview?.addJavascriptInterface(this, "android")

        webview?.setWebViewClient(object : InnerWebViewClient() {
            override fun onPageFinished(webview: WebView, url: String) {
                if (finishedUrls.contains(url))
                    return
                finishedUrls.add(url)
            }
        })

        webview?.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
//                "consoleMessage : ${consoleMessage?.message()}".uploadLog()
                return super.onConsoleMessage(consoleMessage)
            }

            override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                this@SlideFileUploadHelper.filePathCallback = filePathCallback
                chooseFile()
                return true
            }
        }
    }

    var sections = CopyOnWriteArrayList<JSONObject>()

    var images = CopyOnWriteArrayList<JSONObject>()

    @JavascriptInterface
    fun onSlide(json: String) {
        var jsonObject = JSONObject(json)
        sections.add(jsonObject)
        "onSlide index: $json".log
        var sectionProgress = jsonObject.optDouble("progress") / 100 * processSectionsPart
        progressCallback?.invoke(sectionProgress.toInt())
    }

    @JavascriptInterface
    fun loadImage(json: String) {
        var jsonObject = JSONObject(json)
        images.add(jsonObject)
        "loadImage json: $json".log
    }

    var css = ""
    @JavascriptInterface
    fun onCss(css: String) {
        this.css = css
        if (images.isEmpty()) {
            uploadImagePart = 0
            mergeHtml()
            return
        }
        uploadImages()
    }

    var width = ""
    var height = ""

    @JavascriptInterface
    fun onSize(width: String, height: String) {
        this.width = width
        this.height = height
    }

    var processSectionsPart = 10
    var uploadImagePart = 0
    var progressArray: FloatArray? = null
    fun uploadImages() {
        uploadImagePart = 60
        progressArray = FloatArray(images.size)

        images.forEachIndexed { index, it ->
            uploadImage(index, it)
        }
    }

    private fun uploadImage(index: Int, jsonObject: JSONObject) {
        var sectionIndex = jsonObject.optInt("section")
        var base64 = jsonObject.optString("data")
        var uuid = jsonObject.optString("uuid")
        var mime = jsonObject.optString("mime")
        var fileType = mime.split("/").last()
        var listener = object : UploadListener {
            override fun progress(ratio: Float, size: Long) {

                progressArray!![index] = ratio
                var progress = progressArray!!.sum() / progressArray!!.size
                "progressCallback : $progress".log
                progressCallback?.invoke(processSectionsPart + (progress * 60).toInt())
            }

            override fun onSuccess(url: String?) {
                var section = sections.get(sectionIndex)
                var html = section.optString("html").replace(uuid, url!!)
                section.put("html", html)
                "uploadImage result : $url".log


                progressArray!![index] = 1.0f
                var progress = progressArray!!.sum() / progressArray!!.size
                "progressCallback : $progress".log
                progressCallback?.invoke(processSectionsPart + (progress * 60).toInt())

                images.remove(jsonObject)

                if (images.isEmpty())
                    mergeHtml()
            }

            override fun onFail(message: String?) {
            }
        }
        "UploadListener : ${listener.hashCode()} ".log
        Base64UploadHelper.upload(base64, fileType, listener)
    }

    private fun mergeHtml() {
        var uploadHtmlPart = 100 - uploadImagePart - processSectionsPart
        var html = StringBuilder()
        sections.forEachIndexed { index, it ->
            "mergeHtml index : $index id : ${it.optInt("id")} name : ${it.getString("name")}".log
            html.appendln(it.optString("html"))
        }
        html.append(css)

        var htmlString = html.toString()
        Base64UploadHelper.uploadSlide(htmlString, "html", object : UploadListener {
            override fun progress(progress: Float, size: Long) {
                var part = uploadImagePart + processSectionsPart + (progress * uploadHtmlPart).toInt()
                if (part != 100)
                    progressCallback?.invoke(part)
            }

            override fun onSuccess(url: String?) {
                "mergeHtml md5 : ${htmlString.md5()} result : $url".log
                reset()
                this@SlideFileUploadHelper.url = url
                progressCallback?.invoke(uploadImagePart + processSectionsPart + (1.0f * uploadHtmlPart).toInt())
            }

            override fun onFail(message: String?) {
            }
        })
    }

    fun reset() {
        progressArray = null
        sections.clear()
        images.clear()
    }


    var selectFileUtil: SelectFileUtil? = null

    fun chooseFile() {
        selectFileUtil = SelectFileUtil()
        selectFileUtil?.activity = activity
        selectFileUtil?.callback = this::onGetPath
        selectFileUtil?.selectFile()
    }

    fun onGetPath(path: String?) {
        "onActivityResult path : $path".log
        var array = if (path != null) arrayOf(Uri.fromFile(File(path))) else null
        try {
            filePathCallback?.onReceiveValue(array)
        } catch (e: IllegalStateException) {

        }
        if (path != null)
            startUploadCallback?.invoke()
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        selectFileUtil?.onActivityResult(requestCode, resultCode, data)
    }
}