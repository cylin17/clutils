package com.cylin.clutils

import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.pow

/**
 * @author Charlie
 * @Date on 2020-August-06
 * @Description
 */

/**
 * 格式化數字, 無條件捨去, 保留幾位由 n 決定
 *
 * 將輸入的 double value, 無條件捨去至小數點以下 n 位
 * 預設為無條件捨去取整數
 *
 * @param n 小數點以下 n 位, 預設為 0
 * @param pattern 可設定顯示的樣式為 "0.##"
 * @return 格式化目標數值, 到小數點以下n位, 無條件捨去
 */
fun Double.floorWithDigit(n: Int = 0, pattern: String? = null): String {
    // 設定次方(保留小數點以下幾位)
    val pow = if (n > 0) {
        10f.pow(n)
    } else {
        1f
    }
    // 無條件捨去
    val tmp: Double = floor(this * pow) / pow
    // 格式化要顯示幾位小數
    return if (pattern == null) {
        val formatString = "%.${n}f"
        String.format(formatString, tmp)
    } else {
        tmp.decimalFormat(pattern)
    }
}

/**
 * 格式化數字, 四捨五入, 保留幾位由 format pattern 決定
 *
 * 常用 pattern
 * 0.##: 小數點以下兩位, 不足位不補 0, 超過兩位四捨五入
 * 0.00: 小數點以下兩位, 不足位補 0, 超過兩位四捨五入
 *
 * @param pattern 預設為 "0.##"
 */
fun Double.decimalFormat(pattern: String = "0.##"): String {
    return DecimalFormat(pattern).format(this)
}
