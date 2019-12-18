package im.clazz.extension

import com.halo.util.TimeUtil
import datetime.DateTime

/**
 * Created by Wang on 2018/3/23.
 */


fun DateTime.toTimeDiff() =
        TimeUtil.getTimeBetweenString(this)