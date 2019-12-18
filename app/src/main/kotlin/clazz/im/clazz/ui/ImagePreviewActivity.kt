package im.clazz.ui

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.halo.base.BaseFullScreenActivity
import com.halo.helper.UiHelper
import com.halo.util.ActivityUtil
import im.clazz.R
import im.clazz.ui.component.ImagePreviewer
import net.tsz.afinal.annotation.view.ViewInject
import java.util.*

class ImagePreviewActivity : BaseFullScreenActivity() {
    @ViewInject(id = R.id.chat_image_previewer, click = "cancel")
    private var mImagePreviewer: ImagePreviewer? = null
    private var rect: IntArray? = null
    private var topViewRect: IntArray? = null
    private var bottomViewRect: IntArray? = null
    private var targetRect: IntArray? = null

    private var shown: Boolean = false
    private var imageIndex: Int = 0
    private var urlList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_image_preview_layout)
        ActivityUtil.showTransluntNavigationBar(this)
        ActivityUtil.showTransparentStatusBar(this)
        val b = intent.extras
        urlList = b.getStringArrayList("urlList")
        imageIndex = b.getInt("imageIndex")
        rect = b.getIntArray("rect")
        topViewRect = b.getIntArray("topViewRect")
        bottomViewRect = b.getIntArray("bottomViewRect")
        targetRect = b.getIntArray("targetRect")
        mImagePreviewer!!.setActivity(this)
        mImagePreviewer!!.setViewPagerValues(urlList, imageIndex)
        mImagePreviewer!!.setAnimImageValues(rect, topViewRect, bottomViewRect, targetRect)
        mImagePreviewer!!.render()
    }

    override fun onResume() {
        super.onResume()
        if (shown)
            return
        shown = true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        ActivityUtil.showTransparentStatusBar(this)
    }

    override fun onPause() {
        super.onPause()
    }

    // 此事件无效待后期解决
    fun cancel(v: View) {
        exit()
    }

    private fun exit() {
        UiHelper.shieldTouchEvent(mImagePreviewer)
        mImagePreviewer!!.animateHide {
            finish()
            overridePendingTransition(0, 0)
        }
    }

    override fun onBackPressed() {
        exit()
    }

    override fun onDestroy() {
        super.onDestroy()
        glideDrawable = null
    }

    companion object {
        var glideDrawable: Drawable? = null
    }
}
