package com.halo.util

import android.os.Bundle
import com.halo.model.Mergable
import java.util.*

/**
 * 集合对象工具类
 */
object CollectionUtil {

    /**
     * 获取集合大小

     * @param collection
     * *            集合
     * *
     * @return
     */
    fun <T> size(collection: Collection<T>?): Int {
        return collection?.size ?: 0
    }

    /**
     * 检查集合元素是否存在

     * @param collection
     * *            集合
     * *
     * @return
     */
    fun isAvailable(collection: Collection<Any?>, index: Int): Boolean {
        if (isEmpty(collection))
            return false
        return index >= 0 && index < collection.size
    }

    /**
     * 检查集合元素是否为空

     * @param collection
     * *            集合
     * *
     * @return
     */
    fun isEmpty(collection: Collection<Any?>?): Boolean {
        return collection == null || collection.isEmpty()
    }

    fun isEmpty(collection: ArrayList<Any?>?): Boolean {
        return collection == null || collection.isEmpty()
    }

    /**
     * 检查数组元素是否为空

     * @param array
     * *            数组
     * *
     * @return
     */
    fun <T> isEmpty(array: Array<T>?): Boolean {
        return array == null || array.size == 0
    }

    /**
     * 检查Map元素是否为空

     * @param map
     * *            Map
     * *
     * @return
     */
    fun isEmpty(map: Map<*, *>?): Boolean {
        return map == null || map.isEmpty()
    }

    fun isEmpty(array: IntArray?): Boolean {
        return array == null || array.size == 0
    }

    fun inCollection(obj: Any, collection: Set<Any>): Boolean {
        return collection.contains(obj)
    }

    fun isEmpty(bundle: Bundle?): Boolean {
        return bundle == null || bundle.isEmpty
    }

    fun <T> size(array: Array<T>?): Int {
        return array?.size ?: 0
    }

    /**
     * 检查是否包含指定元素

     * @param array
     * *            数组
     * *
     * @return
     */
    fun contains(array: Array<Any>, obj: Any): Boolean {
        return !isEmpty(array) && Arrays.asList(*array).contains(obj)
    }

    /**
     * 检查是否包含指定元素

     * @param array
     * *            数组
     * *
     * @return
     */
    fun contains(array: IntArray, obj: Int): Boolean {
        if (isEmpty(array))
            return false
        for (i in array) {
            if (i == obj)
                return true
        }
        return false
    }

    /**
     * 检查是否包含指定元素

     * @param set
     * *            集合
     * *
     * @return
     */
    fun contains(set: Set<Any>, obj: Any): Boolean {
        return !isEmpty(set) && set.contains(obj)
    }

    fun contains(collection: Array<String>, string: String): Boolean {
        if (isEmpty(collection) || TextUtil.isEmpty(string))
            return false
        for (item in collection) {
            if (item == string)
                return true
        }
        return false
    }

    fun contains(collection: Collection<Any?>?, obj: Any?): Boolean {
        if (isEmpty(collection) || obj == null)
            return false
        collection?.forEach {
            if (it != null && it == obj)
                return true
        }
        return false
    }

    operator fun <T> get(collection: Collection<T>?, t: T?): T? {
        if (isEmpty(collection) || t == null)
            return null
        collection?.forEach {
            if (it != null && it == t)
                return@get it
        }
        return null
    }

    fun <T : Mergable<T>> merge(collection: Collection<T>?, source: T?): T? {
        val target = get(collection, source) ?: return null
        target.merge(source)
        return target
    }

    @JvmStatic
    fun print(collection: Array<String>) {
        for (string in collection) {
            LogUtil.d(CollectionUtil::class.java.simpleName, string)
        }
    }

    fun toString(collection: Array<Any>): String? {
        if (isEmpty(collection))
            return null
        val sb = StringBuilder()
        if (collection.size > 1)
            sb.append("[")
        for (obj in collection) {
            sb.append(obj)
            sb.append(",")
        }
        var str = sb.toString()
        str = str.substring(0, str.length - 1)
        if (collection.size > 1)
            str += "]"
        return str
    }

    fun isEmpty(array: LongArray?): Boolean {
        return array == null || array.size == 0
    }

    fun size(map: Map<Any?, Any?>?): Int {
        if (map == null)
            return 0
        return map.size
    }

    fun <T> addAllAfterClear(c1: MutableCollection<T>?, c2: Collection<T>?): Boolean {
        if (c1 == null || c2 == null)
            return false
        c1.clear()
        if (c2.isEmpty())
            return false
        c1.addAll(c2)
        return true
    }

    @SafeVarargs
    fun <T> addAll(c1: MutableCollection<T>, vararg collections: Collection<T>?) {
        collections?.forEach {
            addAllItems(c1, it)
        }
    }

    fun <T> addAllItems(c1: MutableCollection<T>?, c2: Collection<T>?): Boolean {
        if (c1 == null || c2 == null)
            return false
        if (c2.isEmpty())
            return false
        c1.addAll(c2)
        return true
    }
}