package com.halo.base

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.alexvasilkov.gestures.sample.utils.DecorUtils
import com.halo.helper.KeyboardHeightHelper
import com.halo.util.ActivityUtil
import com.halo.util.CollectionUtil
import com.halo.util.WindowUtil
import net.tsz.afinal.FinalActivity
import org.jetbrains.anko.contentView
import java.util.*

/**
 * Activity的基类，实现基本的自动缩放

 * @author WangZengYang
 * *
 * @since 2013-8-26
 */
open class BaseActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    var keyboardHeightHelper: KeyboardHeightHelper? = null
    var keyboardHeight = 0
    var isKeyboardShown = false

    var front = false


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (shouldSetStatusBarColor())
            ActivityUtil.setStatusBarColor(this)

        if (this.shouldBeAddedToLastActivityArray())
            lastActivityArray.add(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        observeKeyboard()
    }


    open fun shouldSetStatusBarColor(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
    }

    open fun shouldBeAddedToLastActivityArray(): Boolean {
        return true
    }

    override fun setContentView(layoutResID: Int) {
        val view = layoutInflater.inflate(layoutResID, null)
        this.setContentView(view)
    }

    override fun setContentView(view: View) {
        resizeView(view)
        super.setContentView(view)
        FinalActivity.initInjectedView(this)
        setDecorMargin(view)
    }

    private fun setDecorMargin(contentView: View) {
        if (!shouldMarginForStatusBar())
            return
        DecorUtils.marginForStatusBar(contentView)
    }

    protected fun shouldMarginForStatusBar(): Boolean {
        return false
    }

    /**
     * 重新计算View及子View的宽高、边距

     * @param view
     */
    fun resizeView(view: View) {
        WindowUtil.resizeRecursively(view)
    }

    open fun shouldObserveKeyboard(): Boolean {
        return false
    }

    private fun observeKeyboard() {
        if (!shouldObserveKeyboard())
            return
        keyboardHeightHelper = KeyboardHeightHelper(this)
        contentView?.post({ keyboardHeightHelper?.start() })
    }

    override fun onPause() {
        super.onPause()
        front = false
        keyboardHeightHelper?.observer = null
    }

    override fun onResume() {
        super.onResume()
        front = true
        keyboardHeightHelper?.observer = this::onKeyboardHeightChanged
    }

    override fun onDestroy() {
        super.onDestroy()
        lastActivityArray.remove(this)
        keyboardHeightHelper?.close()
    }

    fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        if (keyboardHeight != height)
            onKeyboardHeightChange(height, orientation)
        keyboardHeight = height
        if (height == 0 && isKeyboardShown)
            onKeyboardHide()
        else if (height > 0 && !isKeyboardShown)
            onKeyboardShow()
    }

    open fun onKeyboardShow() {
        isKeyboardShown = true
    }

    open fun onKeyboardHide() {
        isKeyboardShown = false
    }

    open fun onKeyboardHeightChange(height: Int, orientation: Int) {

    }

    fun requestPermissions(vararg permissions: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return
        val permissionList = ArrayList<String>()

        for (permission in permissions) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, permission)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED)
                continue
            permissionList.add(permission)
        }

        if (CollectionUtil.isEmpty(permissionList)) {
            onRequestPermissions()
            return
        }
        requestPermissions(permissionList.toTypedArray(), REQUEST_PERMISSIONS)
    }

    open fun onRequestPermissions() {
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onRequestPermissions()
        }
    }

    companion object {

        val lastActivityArray = ArrayList<BaseActivity>()

        val topActivity: BaseActivity?
            get() {
                if (lastActivityArray.isEmpty())
                    return null
                return lastActivityArray[lastActivityArray.size - 1]
            }

        private val REQUEST_PERMISSIONS = 0
        private val REQUEST_READ_PHONE_STATE = 1
        private val REQUEST_STORAGE = 2
        private val REQUEST_LOCATION = 3
        private val REQUEST_CAMERA = 4
        private val REQUEST_MICROPHONE = 5
    }
}
