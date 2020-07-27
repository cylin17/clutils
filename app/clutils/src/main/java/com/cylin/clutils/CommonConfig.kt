package com.cylin.clutils

import java.util.*

/**
 * @author Charlie
 * @Date on 2020-July-24
 * @Description
 */
object CommonConfig {
}

val Any.TAG: String
    get() = this.javaClass.simpleName

/**
 * Output an e.g. #f22e9a string
 */
fun generateRandomColorHex(): String {
    val rndInt = Random().nextInt(0xffffff + 1)
    return String.format("#%06x", rndInt)
}
