package im.clazz.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import chat.ContactHelper
import com.halo.audio.local.AudioController
import com.halo.base.BaseLightActivity
import com.halo.helper.TimeHelper
import com.halo.listener.ListenerHub
import com.halo.util.ActivityUtil
import com.halo.util.LogUtil
import com.halo.util.TextUtil
import im.clazz.R
import im.clazz.account.LoginActivity
import im.clazz.account.RegisterHelper
import im.clazz.chat.ChatActivity
import im.clazz.fragment.helper.MainActivityFragmentHelper
import im.clazz.fragment.helper.MainFragmentStudentHelper
import im.clazz.helper.AtMeHelper
import im.clazz.helper.Session
import im.clazz.helper.ScanHelper
import im.clazz.provider.Provider
import im.clazz.provider.ProviderAction
import im.clazz.provider.ProviderUri
import im.clazz.schedule.TermHelper
import im.clazz.service.IMController
import im.clazz.ui.component.TabBar
import im.clazz.util.WhiteListUtil
import org.jetbrains.anko.contentView

class MainActivity : BaseLightActivity() {

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        instance = this
        requestPermissions(*PERMISSIONS)

        IMController.checkStartIMService()
        AudioController.checkStartAudioService()

        ActivityUtil.smoothSwitchScreen(this)

        setContentView(R.layout.main_activity_layout)
        MainActivityFragmentHelper.init(supportFragmentManager)
        MainActivityFragmentHelper.showMainFragmentStudent()

        WhiteListUtil.checkWhiteList()
        registerLoginProviderListener()
        ContactHelper.load()
        AtMeHelper.checkLoad()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (ACTION_CHAT == action)
            redirectToChatActivity(contactId)
        if (ACTION_SHOW_SYSTEM_MESSAGE == action)
            showSystemView()
        if (ACTION_SHOW_AUDIO == action)
            markShowAudio(audioId)

        reset()

//        updateSlideState()
    }

    fun reset() {
        contactId = null
        action = null
        audioId = -1
    }

    override fun shouldObserveKeyboard(): Boolean {
        return true
    }

    override fun onKeyboardShow() {
        super.onKeyboardShow()
        MainActivityFragmentHelper.currentFragment?.onKeyboardShow()
    }

    override fun onKeyboardHide() {
        super.onKeyboardHide()
        MainActivityFragmentHelper.currentFragment?.onKeyboardHide()
    }

    private fun markShowAudio(audioId: Int) {
        MainActivityFragmentHelper.action = ACTION_SHOW_AUDIO
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        onNewIntent(intent)
    }

    override fun onRequestPermissions() {
        TimeHelper.correctTime()
        TermHelper.init()
    }

    public override fun onResume() {
        super.onResume()
        LogUtil.d()
    }

    override fun onRestart() {
        super.onRestart()
        LogUtil.d()
    }

    override fun onStop() {
        super.onStop()
        LogUtil.d()
    }

    public override fun onPause() {
        super.onPause()
        LogUtil.d()
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.d()
        instance = null
        Session.removeLogoutListener(this.javaClass)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ListenerHub.notifyOnMainActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val content = data.getStringExtra("QR_CODE")
            if (!TextUtil.isEmpty(content))
                ScanHelper.onScan(this@MainActivity, content)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN
                && event.repeatCount == 0) {
//            if (slideHelper!!.onBackPressed())
//                return true
            if (MainActivityFragmentHelper.onBackPressed())
                return true
            exitToHome()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun exitToHome() {
        val intent = Intent()
        intent.action = Intent.ACTION_MAIN
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    companion object {
        private val ACTION_CHAT = "chat"
        val ACTION_SHOW_AUDIO = "show_audio"
        val ACTION_SHOW_SYSTEM_MESSAGE = "system_message"
        private var instance: MainActivity? = null


        var contactId: String? = null
        var action: String? = null
        var audioId = -1

        val SCANNIN_GREQUEST_CODE = 0

        private val PERMISSIONS = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

        fun redirectToChatActivity(contactId: String?) {
            val intent = Intent(MainActivity.activity, ChatActivity::class.java)
            ChatActivity.contactId = contactId
            MainActivity.activity?.startActivity(intent)
            MainActivity.activity?.overridePendingTransition(R.anim.slide_right_in_fast, R.anim.slide_left_out)
        }

        fun showSystemView() {
            TabBar.get().select(3)
            Provider.notify(ProviderUri.RES_SHOW_SYSTEM_MESSAGE_VIEW, ProviderAction.UPDATE)
        }

        val activity: MainActivity?
            get() = instance
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (MainActivityFragmentHelper.onKeyUp(keyCode, event))
            return true
        return super.onKeyUp(keyCode, event)
    }

    fun registerLoginProviderListener() {
        Session.addLogoutListener(this.javaClass) {
            if (front) {
                var flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                RegisterHelper.startActivityFromLeft(this@MainActivity, LoginActivity::class.java, flags)
            }
            contentView?.post {
                MainFragmentStudentHelper.onDestroy()
                MainActivityFragmentHelper.onDestroy()
                finish()
            }
        }
    }
}
