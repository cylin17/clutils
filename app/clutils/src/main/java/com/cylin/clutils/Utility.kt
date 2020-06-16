package com.cylin.clutils

import java.util.*

object Utility {

    // Output an e.g. #f22e9a string
    fun getRandomColorHex(): String {
        val rndInt = Random().nextInt(0xffffff + 1)
        return String.format("#%06x", rndInt)
    }
}