package im.clazz.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import com.halo.base.BaseActivity
import com.halo.util.ActivityUtil
import im.clazz.R
import im.clazz.extension.onClick
import im.clazz.extension.tint
import im.clazz.extension.unShieldTouch
import im.clazz.helper.DrawingViewHelper
import im.clazz.helper.SlideHelper
import im.clazz.lesson.LessonActivity
import im.clazz.video.ChangeOrientationHandler
import im.clazz.video.OrientationSensorListener
import kotlinx.android.synthetic.main.slide_only_activity.*

class SlideOnlyActivity : BaseActivity() {

    var mBackPressed: Boolean = false

    var handler: Handler? = null
    var listener: OrientationSensorListener? = null
    var sm: SensorManager? = null
    var sensor: Sensor? = null
    var slideHelper: SlideHelper? = null
    var webViewCanvasHelper: DrawingViewHelper? = null
    var drawingViewHelper: DrawingViewHelper? = null

    companion object {
        var url: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.slide_only_activity)

        handler = ChangeOrientationHandler(this)
        sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sm!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        listener = OrientationSensorListener(handler)
        sm!!.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)

        initSlide()
    }

    override fun shouldSetStatusBarColor(): Boolean {
        return false
    }

    fun initSlide() {
        slideHelper = SlideHelper()
        drawingViewHelper = DrawingViewHelper()
        slideHelper?.activity = this
        slideHelper?.webview = mWebView
        slideHelper?.webviewLayout = mSlideLayout
        slideHelper?.url = url
        slideHelper?.loadingView = mLoadingView
        slideHelper?.drawingViewHelper = drawingViewHelper

        mDrawingView.disableTouchDraw = true

        drawingViewHelper?.activity = LessonActivity.activity
        drawingViewHelper?.darwingViewBelow = mDrawingView
        drawingViewHelper?.darwingViewAbove = mDrawingViewAbove
        drawingViewHelper?.drawingViewContainer = mSlideLayout
        drawingViewHelper?.onSetDrawingView()
        drawingViewHelper?.init()

        slideHelper?.init()
        initWebViewHelper()
    }

    private fun initWebViewHelper() {
        slideHelper?.activity = this
        slideHelper?.newContainer = mSlideLayout
        slideHelper?.autoSlideButton = mBtnAutoSlide
        slideHelper?.playAudioButton = mBtnPlayAudio
        slideHelper?.eraseButton = mBtnErase
        slideHelper?.buttonLeft = mBtnLeft
        slideHelper?.buttonRight = mBtnRight
        slideHelper?.buttonUp = mBtnUp
        slideHelper?.buttonDown = mBtnDown

        slideHelper?.onSetDirectionButton()
        slideHelper?.onSetAutoSlideButton()
        slideHelper?.onSetPlayAudioButton()
        slideHelper?.onSetEraseButton()
        mBtnFullScreen.onClick { slideHelper?.unFullscreen() }
        mBtnRefresh.onClick { slideHelper?.refresh() }

        mDrawingView?.unShieldTouch()
        mDrawingViewAbove?.unShieldTouch()
        webViewCanvasHelper?.activity = this
        webViewCanvasHelper?.newContainer = mSlideLayout

        mBtnDrawMode.onClick {
            var drawMode = slideHelper?.phoenixChannelHelper?.drawMode
            slideHelper?.phoenixChannelHelper?.drawMode = !drawMode!!
            updateDrawModeState()
        }
        updateDrawModeState()
    }

    fun updateDrawModeState() {
        var drawMode = slideHelper?.phoenixChannelHelper?.drawMode!!
        mBtnDrawMode.tint(R.drawable.ic_pencil_slide, if (drawMode) R.color.black else R.color.gray_f0)
    }

    override fun onResume() {
        sm!!.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
        super.onResume()
        ActivityUtil.hideNavigationBar(this)
        slideHelper?.onResume()
        webViewCanvasHelper?.onResume()
    }

    override fun onBackPressed() {
        mBackPressed = true
        slideHelper?.unFullscreen()
        webViewCanvasHelper?.unFullscreen()
    }

    override fun onPause() {
        sm!!.unregisterListener(listener)
        super.onPause()
    }
}
