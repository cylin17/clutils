package com.cylin.clutils

import java.util.*

/**
 * @author Charlie
 * @Date on 2020-July-24
 * @Description
 */

val Any.TAG: String
    get() = this.javaClass.simpleName

/**
 * Output an e.g. #f22e9a string
 */
fun generateRandomColorHex(): String {
    val rndInt = Random().nextInt(0xffffff + 1)
    return String.format("#%06x", rndInt)
}

inline fun <reified T> T.className(): String {
    return T::class.java.simpleName
}

inline fun <reified T> T.clog(msg: Any?, prefix: String = "", isShowed: Boolean = true) {
    if (BuildConfig.DEBUG || isShowed) {
        val strPrefix = if (prefix.isNotEmpty()) "$prefix -> " else prefix
        android.util.Log.d(className(), ">> $strPrefix$msg")
    }
}