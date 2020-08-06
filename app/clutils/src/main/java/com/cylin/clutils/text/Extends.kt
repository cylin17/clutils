package com.cylin.clutils.text

import android.content.ClipboardManager
import android.content.Context
import android.util.Base64
import java.security.MessageDigest

/**
 * 將字串加密成 MD5
 */
fun String.md5(): String {
    return hashString(this, "MD5")
}

/**
 * 將字串加密成 SHA-256
 */
fun String.sha256(): String {
    return hashString(this, "SHA-256")
}

private fun hashString(input: String, algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray())
        .fold("", { str, it -> str + "%02x".format(it) })
}

/**
 * 檢查網址結尾是否有「/」，若沒有則加入！
 */
fun String.urlCheck(): String {
    return if (this.endsWith("/", false)) this else "$this/"
}

/**
 * Decode base64 string.
 */
fun String.decodeBase64(): String {
    return String(Base64.decode(this, Base64.DEFAULT))
}

/**
 * encode to base64 string.
 */
fun String.base64(): String {
    return Base64.encodeToString(this.toByteArray(), Base64.DEFAULT)
}

/**
 * 複製文字到剪貼簿
 */
fun String.copy(context: Context): Boolean {
    try {
        if (this.isNullOrEmpty()) {
            return false
        }

        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.text = this
        return true
    } catch (e: Exception) {
        return false
    }
}

/**
 * 字串轉 Double
 *
 * 遇空字串 返回 0.0
 * NumberFormatException 返回 0.0
 */
fun String.cast2Double(): Double {
    if (this.isEmpty()) {
        return 0.0
    }
    return try {
        this.toDouble()
    } catch (e: NumberFormatException) {
        0.0
    }
}

/**
 * 字串轉 Float
 *
 * 遇空字串 返回 0f
 * NumberFormatException 返回 0f
 */
fun String.cast2Float(): Float {
    if (this.isEmpty()) {
        return 0f
    }
    return try {
        this.toFloat()
    } catch (e: NumberFormatException) {
        0f
    }
}

/**
 * 字串轉 Int
 *
 * 遇空字串 返回 0
 * NumberFormatException 返回 0
 */
fun String.cast2Int(): Int {
    if (this.isEmpty()) {
        return 0
    }
    return try {
        this.toInt()
    } catch (e: NumberFormatException) {
        0
    }
}

