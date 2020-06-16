package com.cylin.clutils

import android.widget.EditText

fun EditText.text2String(): String {
    return text.toString()
}

fun EditText.isTextEmpty(): Boolean {
    return text.trim().isEmpty()
}