package com.halo.util

import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.halo.helper.ResourcesHelper
import com.readystatesoftware.systembartint.SystemBarTintManager
import im.clazz.R

object ActivityUtil {

    fun showStatusBar(activity: Activity) {
        val decorView = activity.window.decorView
        val uiOptions = decorView.systemUiVisibility
        var newUiOptions = uiOptions
        newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
        decorView.systemUiVisibility = newUiOptions
    }

    fun hideStatusBar(activity: Activity) {
        val decorView = activity.window.decorView
        val uiOptions = decorView.systemUiVisibility
        var newUiOptions = uiOptions
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = newUiOptions
    }

    fun setStatusBarColor(activity: Activity, colorResId: Int) {
        val color = ResourcesHelper.getColor(colorResId)
        val navigationBarColor = ResourcesHelper.getColor(R.color.navigation_bar_tint_color)

        activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val tintManager: SystemBarTintManager
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            tintManager = SystemBarTintManager(activity)
            tintManager.isStatusBarTintEnabled = true
            tintManager.setNavigationBarTintEnabled(true)
            tintManager.setTintColor(color)
            tintManager.setNavigationBarTintColor(navigationBarColor)
        }
    }

    fun setStatusBarColorOnly(activity: Activity, colorResId: Int) {
        val color = ResourcesHelper.getColor(colorResId)
        val navigationBarColor = ResourcesHelper.getColor(R.color.navigation_bar_tint_color)

        val tintManager: SystemBarTintManager
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            tintManager = SystemBarTintManager(activity)
            tintManager.isStatusBarTintEnabled = true
            tintManager.setNavigationBarTintEnabled(true)
            tintManager.setTintColor(color)
            tintManager.setNavigationBarTintColor(navigationBarColor)
        }
    }

    fun setStatusBarColor(activity: Activity) {
        setStatusBarColor(activity, R.color.white)
    }

//    fun setFullScreen(activity: Activity) {
//        val systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//
//        activity.window.decorView.systemUiVisibility = systemUiVisibility
//    }

    fun hideStatusBarAndTitleBar(activity: Activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE)// 去掉标题栏
        activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)// 去掉信息栏
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }

    /**
     * 将虚拟按键变为三个小圆点<br></br>
     * 必须是先调用setContentView之后在调用该方法，不然没有效果，请知晓！

     * @param activity
     */
    fun enterLightsOutMode(activity: Activity) {
        val params = activity.window.attributes
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
        activity.window.attributes = params
    }

    fun showTransluntStatusBarAndNavigationBar(activity: Activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            activity.window.attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val window = activity.window
            // Translucent status bar
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // Translucent navigation bar
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
    }

    fun showTransparentStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window = activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    fun showTransluntNavigationBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            activity.window.attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val window = activity.window
            // Translucent navigation bar
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
    }

    fun showNavigationBar(activity: Activity) {
        LogUtil.d()
        val window = activity.window ?: return

        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)

        val decorView = window.decorView

        if (decorView != null) {
            var uiOptions = decorView.systemUiVisibility

            if (Build.VERSION.SDK_INT >= 14) {
                uiOptions = uiOptions and View.SYSTEM_UI_FLAG_LOW_PROFILE.inv()
            }

            if (Build.VERSION.SDK_INT >= 16) {
                uiOptions = uiOptions and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()
            }

            if (Build.VERSION.SDK_INT >= 19) {
                uiOptions = uiOptions and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
            }

            decorView.systemUiVisibility = uiOptions
        }
    }

    fun finish(activity: Activity?) {
        if (activity == null)
            return
        activity.finish()
    }

    val navigationBarHeight: Int
        get() {
            var navigationHeight = ResourcesHelper.get().getIdentifier("navigation_bar_height", "dimen", "android")
            navigationHeight = ResourcesHelper.get().getDimensionPixelSize(navigationHeight)
            return navigationHeight
        }

    fun getStatusHeight(activity: Activity): Int {
        var statusHeight = 0
        val localRect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(localRect)
        statusHeight = localRect.top
        if (0 == statusHeight) {
            val localClass: Class<*>
            try {
                localClass = Class.forName("com.android.internal.R\$dimen")
                val localObject = localClass.newInstance()
                val i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString())
                statusHeight = activity.resources.getDimensionPixelSize(i5)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: SecurityException) {
                e.printStackTrace()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            }

        }
        return statusHeight
    }

    fun checkDeviceHasNavigationBar(): Boolean {
        var hasNavigationBar = false
        val rs = ApplicationUtil.context!!.resources
        val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id)
        }
        try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val m = systemPropertiesClass.getMethod("get", String::class.java)
            val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
            if ("1" == navBarOverride) {
                hasNavigationBar = false
            } else if ("0" == navBarOverride) {
                hasNavigationBar = true
            }
        } catch (e: Exception) {
        }

        return hasNavigationBar
    }

    fun smoothSwitchScreen(activity: Activity) {
        // 5.0以上修复了此bug
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            val rootView = activity.findViewById(android.R.id.content) as ViewGroup
            val resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android")
            val statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId)
            rootView.setPadding(0, statusBarHeight, 0, 0)
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    fun setUnFullScreen(activity: Activity?) {
        if (activity == null)
            return
        val lp = activity.getWindow().getAttributes()
        lp.flags = lp.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
        activity.getWindow().setAttributes(lp)
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        showNavigationBar(activity)
    }

    fun setFullScreen(activity: Activity) {
        val lp = activity.getWindow().getAttributes()
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
        activity.getWindow().setAttributes(lp)
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        hideNavigationBar(activity)
    }


    fun hideNavigationBar(activity: Activity) {
        val window = activity.window
        var flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            flags = flags or View.SYSTEM_UI_FLAG_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            flags = flags or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = flags
    }
}
