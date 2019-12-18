package im.clazz.global

import com.halo.handler.UiThreadHandler
import im.clazz.extension.Callback


fun runInUiThread(f: Callback?) {
    UiThreadHandler.post {
        f?.invoke()
    }
}

fun runInUiThread(delay: Long = 0, f: Callback?) {
    UiThreadHandler.postDelayed(delay) {
        f?.invoke()
    }
}