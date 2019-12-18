package com.halo.base

import android.os.Bundle
import com.halo.util.ActivityUtil

open class BaseFullScreenActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtil.showTransparentStatusBar(this)
    }


    override fun shouldSetStatusBarColor(): Boolean {
        return false
    }
}
