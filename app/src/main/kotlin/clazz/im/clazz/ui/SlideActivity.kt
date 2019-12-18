package im.clazz.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.webkit.WebView
import com.halo.base.BaseActivity
import com.halo.util.ActivityUtil
import com.vilyever.drawingview.DrawingView
import im.clazz.R
import im.clazz.extension.onClick
import im.clazz.extension.tint
import im.clazz.extension.unShieldTouch
import im.clazz.helper.DrawingViewHelper
import im.clazz.helper.SlideHelper
import im.clazz.model.SlideState
import im.clazz.video.ChangeOrientationHandler
import im.clazz.video.OrientationSensorListener
import kotlinx.android.synthetic.main.slide_activity.*

class SlideActivity : BaseActivity() {

    var mBackPressed: Boolean = false

    var handler: Handler? = null
    var listener: OrientationSensorListener? = null
    var sm: SensorManager? = null
    var sensor: Sensor? = null

    companion object {
        var slideState: SlideState? = null
        var webview: WebView? = null
        var drawingView: DrawingView? = null
        var drawingViewAbove: DrawingView? = null
        var slideHelper: SlideHelper? = null
        var webViewCanvasHelper: DrawingViewHelper? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.slide_activity)

        handler = ChangeOrientationHandler(this)
        sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sm!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        listener = OrientationSensorListener(handler)
        sm!!.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
        mSlideLayout.addView(webview)
        mSlideLayout.addView(drawingView)
        mSlideLayout.addView(drawingViewAbove)

        initWebViewHelper()
    }

    override fun shouldSetStatusBarColor(): Boolean {
        return false
    }

    private fun initWebViewHelper() {
        webview?.unShieldTouch()
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

        drawingView?.unShieldTouch()
        drawingViewAbove?.unShieldTouch()
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
