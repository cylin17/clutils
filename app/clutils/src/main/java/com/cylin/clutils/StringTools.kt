package com.cylin.clutils

import java.security.MessageDigest

/**
 * 將字串加密成 MD5
 */fun String.md5(): String {
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