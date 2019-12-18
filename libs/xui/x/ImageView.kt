package x

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.NinePatchDrawable
import android.util.AttributeSet
import com.halo.util.ApplicationUtil
import com.halo.util.XmlAttibuteHelper
import im.clazz.R

open class ImageView : android.widget.ImageView {

    private var originalHeight: Float = 0.toFloat()
    private var originalWidth: Float = 0.toFloat()

    var resId: Int? = null
    var disabledResId: Int? = null

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = ApplicationUtil.context!!.obtainStyledAttributes(attrs, R.styleable.x)
        var typedValue = typedArray.peekValue(R.styleable.x_src)
        resId = typedValue?.resourceId
        var disabledTypedValue = typedArray.peekValue(R.styleable.x_src_disabled)
        disabledResId = disabledTypedValue?.resourceId
        var drawable = XmlAttibuteHelper.getBackground(attrs)
        if (drawable != null)
            setBackgroundDrawable(drawable)
        drawable = XmlAttibuteHelper.getImageDrawable(attrs)
        if (drawable != null)
            setImageDrawable(drawable)
        typedArray.recycle()
    }

    constructor(context: Context) : super(context)

//    override fun setEnabled(enabled: Boolean) {
//        super.setEnabled(enabled)
//        if (resId == null)
//            return
//        if (enabled || disabledResId == null)
//            setImageResource(resId!!)
//        else
//            setImageResource(disabledResId!!)
//
//    }

    override fun setBackgroundResource(resid: Int) {
        val drawable = XmlAttibuteHelper.getDrawable(resid)
        setBackgroundDrawable(drawable)
    }

    override fun setImageResource(resId: Int) {
        val drawable = XmlAttibuteHelper.getDrawable(resId)
        setImageDrawable(drawable)
    }

    override fun onDraw(canvas: Canvas) {
        if (originalHeight == 0f && originalWidth == 0f || drawable is NinePatchDrawable) {
            super.onDraw(canvas)
            return
        }
        var scaleX = 1f
        var scaleY = 1f
        if (originalWidth != 0f)
            scaleX = originalWidth / width.toFloat()
        if (originalHeight != 0f)
            scaleY = originalHeight / height.toFloat()
        canvas.save()
        canvas.scale(scaleX, scaleY)
        canvas.clipRect(0, 0, width, height)
        val d = drawable
        d?.draw(canvas)
        canvas.restore()
    }

    fun setOriginalHeight(originalHeight: Float) {
        this.originalHeight = originalHeight
    }

    fun setOriginalWidth(originalWidth: Float) {
        this.originalWidth = originalWidth
    }
}
