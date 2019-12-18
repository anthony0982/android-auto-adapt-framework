package im.clazz.extension

/**
 * Created by Wang on 2017/7/1.
 */


fun <T> ArrayList<T>.clearAll(): ArrayList<T> {
    this.clear()
    return this
}