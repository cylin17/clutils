package com.cylin.clutils

import android.widget.EditText

fun EditText.textString(): String {
    return text.toString()
}

fun EditText.isTextEmpty(): Boolean {
    return text.trim().isEmpty()
}