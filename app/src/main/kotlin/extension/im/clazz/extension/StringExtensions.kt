package im.clazz.extension

import com.artifex.mupdflib.MD5
import com.halo.helper.UiHelper
import com.halo.util.Base64Util
import com.halo.util.LogUtil
import com.halo.util.TimeUtil
import datetime.DateTime
import java.util.*

/**
 * Created by Wang on 2017/6/12.
 */


fun String.toTimeDiff() =
        TimeUtil.getTimeBetweenString(DateTime(this))

fun String.toTime(format: String) = DateTime(this).toString(format)

fun String.toDateTime() = DateTime(this)

fun String.toDateTimeFromElixir(): DateTime {
    var parts = this.split(".")
    var mss = parts.get(1).substring(0, 3)
    var time = parts.get(0) + ".$mss+0000"
    return DateTime(time)
}

fun String.toOnlyTime(): DateTime {
    var time = DateTime().zero()
    var parts = this.split(":")
    var hour = parts.get(0).toInt()
    var minute = parts.get(1).toInt()
    time.hour = hour
    time.minute = minute
    return time
}

fun String.toDate(): DateTime {
    var time = DateTime().zero()
    var parts = this.split("-")
    var year = parts.get(0).toInt()
    var month = parts.get(1).toInt()
    var day = parts.get(2).toInt()
    time.year = year
    time.month = month
    time.day = day
    return time
}

fun String.toDeadlineString() =
        TimeUtil.getDeadlineString(DateTime(this))


fun String.isTimeUp() =
        TimeUtil.isTimeUp(DateTime(this))

fun String.toTimestamp() =
        DateTime(this).timeInMillis


fun String.trimMultiBlank() = this.replace("\\n+".toRegex(), "\n").replace(" +".toRegex(), " ")


enum class UsernameType {
    PHONE, EMAIL, OTHER
}

fun String.isValidName(): Boolean {
    return this.length >= 2
}

fun String.isValidUsername(): Boolean {
    return this.getUsernameType() != UsernameType.OTHER
}

fun String.getUsernameType(): UsernameType {
    val emailRegex = Regex(pattern = "^.+@.+\\..+$")
//    val phoneRegex = Regex(pattern = "1[34578][012356789]\\d{8}|134[012345678]\\d{7}")
    val phoneRegex = Regex(pattern = "\\d{11}")

    return when {
        this.matches(emailRegex) -> UsernameType.EMAIL
        this.matches(phoneRegex) -> UsernameType.PHONE
        else -> UsernameType.OTHER
    }
}

fun String.isValidPassword(): Boolean {
    return this.length >= 6
}

fun String.md5(): String {
    return MD5.MD5Hash(this)
}

val String.log: Unit
    get() {
        return LogUtil.d(this)
    }

val String.base64: String
    get() {
        return Base64Util.encode(this)
    }
val String.base64Decode: String
    get() {
        return String(Base64Util.decode(this))
    }

val String.logLocal: Unit
    get() {
        return LogUtil.logLocal(this)
    }

fun String.uploadLog() {
//    return LogUtil.upload(this)
}

fun String.clip(length: Int): String {
    if (this.length < length)
        return this
    return this.substring(0, length)
}

fun String.tip() {
    return UiHelper.showTip(this)
}

fun Calendar.toOnlyTime(): DateTime {
    return DateTime(this.time)
}