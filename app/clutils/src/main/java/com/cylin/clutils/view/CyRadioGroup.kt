package com.cylin.clutils.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RadioGroup

class CyRadioGroup : RadioGroup {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //获取最大宽度
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec)
        //获取Group中的Child数量
        val childCount = childCount
        //设置Group的左边距，下面也会使用x计算每行所占的宽度
        var x = 0
        //设置Group的上边距，下面也会使用y计算Group所占的高度
        var y = 30
        //设置Group的行数
        var row = 0
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            if (child.visibility != View.GONE) {
                child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
                //重新计算child的宽高
                var width = child.measuredWidth
                val height = child.measuredHeight
                //添加到X中，(width+10) 设置child左边距
                x += width + 10
                //行数*child高度+这次child高度=现在Group的高度,(height + 10)设置child上边距
                y = row * (height + 10) + (height + 10)
                //当前行宽X大于Group的最大宽度时，进行换行
                if (x > maxWidth) {
                    //当index不为0时，进行row++，防止FirstChild出现大于maxWidth时,提前进行row++
                    if (index != 0) row++
                    //child的width大于maxWidth时，重新设置child的width为最大宽度
                    if (width >= maxWidth) {
                        width = maxWidth - 30
                    }
                    //重新设置当前X
                    x = width + 20
                    //重新设置现在Group的高度
                    y = row * (height + 10) + (height + 10)
                }
            }
        }
        // 设置容器所需的宽度和高度
        setMeasuredDimension(maxWidth, y)
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        val childCount = childCount
        val maxWidth = r - l
        var x = 10
        var y = 0
        var row = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                var width = child.measuredWidth
                val height = child.measuredHeight
                x += width + 10
                y = row * (height + 10) + (height + 10)
                if (x > maxWidth) {
                    if (i != 0) row++
                    if (width >= maxWidth) {
                        width = maxWidth - 30
                    }
                    x = width + 20
                    y = row * (height + 10) + (height + 10)
                }
                child.layout(x - width, y - height, x, y)
            }
        }
    }
}