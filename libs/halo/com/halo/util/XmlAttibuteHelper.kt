package com.halo.util

import android.annotation.SuppressLint
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.util.Xml
import android.widget.ImageView.ScaleType
import com.halo.helper.ResourcesHelper
import im.clazz.R
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import x.StateListDrawableWrapper
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * 布局文件Xml属性读取、修改帮助类

 * @author wangzengyang@gmail.com
 * *
 * @since 2014-3-20
 */
object XmlAttibuteHelper {
    val NAMESPACE_X_VIEW = "http://schemas.android.com/apk/res/" + ApplicationUtil.currentPackageName
    val ATTRIBUTE_BACKGROUND = "background"
    val ATTRIBUTE_SRC = "src"

    fun getBackgroundTintColor(attrs: AttributeSet): Int {
        val typedArray = ApplicationUtil.context!!.obtainStyledAttributes(attrs, R.styleable.x)
        return typedArray.getColor(R.styleable.x_backgoundTint, 0xFFFFFFFF.toInt())
    }

    fun getBackground(attrs: AttributeSet): Drawable? {
        val typedArray = ApplicationUtil.context!!.obtainStyledAttributes(attrs, R.styleable.x)
        val typedValue = typedArray.peekValue(R.styleable.x_bg)
        if (typedValue == null || typedValue.string == null)
            return null
        val name = typedValue.string.toString()
        val resId = typedValue.resourceId
        typedArray.recycle()

        if (resId == 0x0)
            return null

        if (name.endsWith(".9.png")) {
            val d = getNinePatchDrawable(typedValue)

            if (d != null) {
                return d
            }
        } else if (name.endsWith(".png") || name.endsWith(".jpg")) {
            val bitmap = getResource(typedValue)
            if (bitmap != null)
                return BitmapDrawable(bitmap)
        }

        val r = ApplicationUtil.context!!.resources
        if (name.endsWith(".xml")) {
            val parser = r.getXml(resId)
            val isSelector = parseSelector(resId, name)
            if (isSelector) {
                val d = StateListDrawableWrapper()
                d.inflate(r, parser, attrs)
                return d
            }
            try {
                return Drawable.createFromXml(r, parser)
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return r.getDrawable(resId)
    }

    fun parseSelector(xmlId: Int, name: String): Boolean {
        val xml = ResourcesHelper.getXml(xmlId)
        var eventType = -1
        try {
            xml.next()
            eventType = xml.eventType
        } catch (e1: XmlPullParserException) {
            e1.printStackTrace()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        while (eventType != XmlPullParser.END_DOCUMENT) {

            // 到达title节点时标记一下
            if (eventType == XmlPullParser.START_TAG) {
                if (xml.name == "selector") {
                    return true
                }
            }

            // 如过到达标记的节点则取出内容
            // if (eventType == XmlPullParser.TEXT && inTitle) {
            // ((TextView) findViewById(R.id.txXml)).setText(xml.getText());
            // }

            try {
                xml.next()
                eventType = xml.eventType
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return false
    }

    fun getImageDrawable(attrs: AttributeSet): Drawable? {
        val typedArray = ApplicationUtil.context!!.obtainStyledAttributes(attrs, R.styleable.x)
        val typedValue = typedArray.peekValue(R.styleable.x_src) ?: return null
        val name = typedValue.string.toString()
        val resId = typedValue.resourceId
        typedArray.recycle()

        if (resId == 0x0)
            return null

        if (name.endsWith(".9.png")) {

            val d = getNinePatchDrawable(typedValue)
            if (d != null)
                return d
        } else if (name.endsWith(".png") || name.endsWith(".jpg")) {
            val bitmap = getResource(typedValue)
            return BitmapDrawable(bitmap)
        }
        val r = ApplicationUtil.context!!.resources
        if (name.endsWith(".xml")) {
            val parser = r.getXml(resId)
            if ("selector" == parser.name) {
                val d = StateListDrawableWrapper()
                d.inflate(r, parser, attrs)
                return d
            }
            try {
                return Drawable.createFromXml(r, parser)
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return r.getDrawable(resId)
    }

    fun getDrawable(resId: Int): Drawable? {
        val typedValue = TypedValue()
        val r = ApplicationUtil.context!!.resources
        r.getValue(resId, typedValue, true)
        if (typedValue.string == null)
            return null
        val name = typedValue.string.toString()

        if (resId == 0x0)
            return null

        if (name.endsWith(".9.png")) {
            val d = getNinePatchDrawable(typedValue)

            if (d != null)
                return d
        } else if (name.endsWith(".png") || name.endsWith(".jpg")) {
            val bitmap = getResource(typedValue)
            return BitmapDrawable(bitmap)
        }

        if (name.endsWith(".xml")) {
            val parser = r.getXml(resId)

            val isSelector = parseSelector(resId, name)

            if (isSelector) {
                val d = StateListDrawableWrapper()
                val attrX = Xml.asAttributeSet(parser)
                d.inflate(r, parser, attrX)
                return d
            }
            try {
                return Drawable.createFromXml(r, parser)
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return r.getDrawable(resId)
    }

    private fun getResource(typedValue: TypedValue): Bitmap? {
        val name = typedValue.string.toString()
        val resId = typedValue.resourceId
        val `is` = getImageInputStream(resId, name)
        val bitmap = ImageUtil.createSuitableDrawable(`is`)
        return bitmap
    }

    private fun getImageInputStream(resId: Int, name: String): InputStream {
        val resources = ApplicationUtil.context!!.resources
        val assetManager = resources.assets
        var `is`: InputStream? = null
        try {
            `is` = assetManager.open(name)
        } catch (e: IOException) {
            try {
                `is` = resources.openRawResource(resId)
            } catch (ex: NullPointerException) {
                ex.printStackTrace()
            } catch (ex: NotFoundException) {
                ex.printStackTrace()
            }

        }

        return `is`!!
    }

    private fun getBackgroundResourceId(attrs: AttributeSet): Int {
        return attrs.getAttributeResourceValue(NAMESPACE_X_VIEW, ATTRIBUTE_BACKGROUND, -1)
    }

    @SuppressLint("NewApi")
    private fun getNinePatchDrawable(typedValue: TypedValue): NinePatchDrawable? {

        val r = ApplicationUtil.context!!.resources
        val rid = typedValue.resourceId

        val srcNinePatchBmp = BitmapFactory.decodeResource(r, rid) ?: return null

        var tarNinePatchBmp: Bitmap? = null
        var tarNinePatchDra: NinePatchDrawable? = null

        val ratio = WindowUtil.scaleRatio
        val tarWidth = srcNinePatchBmp.width * ratio
        val tarHeight = srcNinePatchBmp.height * ratio

        var chunk = srcNinePatchBmp.ninePatchChunk
        chunk = newChunk(chunk, ratio)

        tarNinePatchBmp = ImageUtil.scale(srcNinePatchBmp, tarWidth, tarHeight, ScaleType.FIT_XY, true)

        tarNinePatchDra = NinePatchDrawable(r, tarNinePatchBmp, chunk, Rect(), null)

        return tarNinePatchDra
    }

    private fun newChunk(chunk: ByteArray, ratio: Float): ByteArray? {

        val xDivs: IntArray
        val yDivs: IntArray

        val byteBuffer = ByteBuffer.wrap(chunk).order(ByteOrder.nativeOrder())

        val serialized = byteBuffer.get()
        if (serialized.toInt() == 0)
            return null

        val tarByteBuffer = ByteBuffer.allocate(chunk.size).order(ByteOrder.nativeOrder())

        tarByteBuffer.put(serialized)

        xDivs = IntArray(byteBuffer.get().toString().toInt())
        yDivs = IntArray(byteBuffer.get().toString().toInt())

        tarByteBuffer.put(xDivs.size.toByte())
        tarByteBuffer.put(yDivs.size.toByte())

        tarByteBuffer.put(byteBuffer.get())

        tarByteBuffer.putInt(byteBuffer.int)
        tarByteBuffer.putInt(byteBuffer.int)
        tarByteBuffer.putInt(byteBuffer.int)
        tarByteBuffer.putInt(byteBuffer.int)
        tarByteBuffer.putInt(byteBuffer.int)
        tarByteBuffer.putInt(byteBuffer.int)
        tarByteBuffer.putInt(byteBuffer.int)

        newDivs(xDivs, byteBuffer, ratio)
        newDivs(yDivs, byteBuffer, ratio)

        putDivs(xDivs, tarByteBuffer)
        putDivs(yDivs, tarByteBuffer)

        while (byteBuffer.hasRemaining()) {
            tarByteBuffer.put(byteBuffer.get())
        }

        return tarByteBuffer.array()
    }

    private fun putDivs(divs: IntArray, byteBuffer: ByteBuffer) {
        for (index in divs.indices) {
            byteBuffer.putInt(divs[index])
        }
    }

    private fun newDivs(divs: IntArray, byteBuffer: ByteBuffer, ratio: Float) {
        var start: Int
        var end: Int
        var width: Int
        var index = 0
        while (index < divs.size) {
            start = byteBuffer.int
            end = byteBuffer.int
            width = end - start
            start = (start * ratio).toInt()
            if (width != 1)
                width = (width * ratio).toInt()
            end = start + width
            divs[index] = start
            divs[index + 1] = end
            index += 2
        }
    }
}
