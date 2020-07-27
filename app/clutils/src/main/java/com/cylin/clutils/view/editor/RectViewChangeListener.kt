package com.cylin.clutils.view.editor

interface RectViewChangeListener {

    fun onViewAdd(rectDrawingView: RectDrawingView)

    fun onViewRemoved(rectDrawingView: RectDrawingView)

    fun onStartDrawing()

    fun onStopDrawing()
}