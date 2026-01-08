package com.example.drawingapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    // A list to store all the paths drawn so far
    private val paths = mutableListOf<CustomPath>()

    private var drawPath: CustomPath? = null
    private var canvasBitmap:Bitmap? = null
    private var drawPaint: Paint? = null
    private var canvasPaint: Paint? = null
    private var color = Color.BLACK
    private var brushSize: Float = 0.toFloat()
    private var canvas:Canvas? = null


    init {
        setupDrawing()
    }

    private fun setupDrawing() {
        drawPaint = Paint()
        drawPath = CustomPath(color,brushSize)
        drawPaint!!.color = color
        drawPaint!!.style = Paint.Style.STROKE
        drawPaint!!.strokeJoin = Paint.Join.ROUND
        drawPaint!!.strokeCap = Paint.Cap.ROUND
        canvasPaint = Paint(Paint.DITHER_FLAG)
        brushSize = 20.toFloat()
        drawPaint!!.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1. Draw all previously finished paths
        for (path in paths) {
            drawPaint.strokeWidth = path.brushThickness
            drawPaint.color = path.color
            canvas.drawPath(path, drawPaint)
        }

        // 2. Draw the path currently being drawn
        drawPath?.let {
            drawPaint.strokeWidth = it.brushThickness
            drawPaint.color = it.color
            canvas.drawPath(it, drawPaint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath = CustomPath(color, brushSize)
                drawPath?.moveTo(touchX, touchY)
            }

            MotionEvent.ACTION_MOVE -> {
                drawPath?.lineTo(touchX, touchY)
            }

            MotionEvent.ACTION_UP -> {
                // Add the finished path to our list and reset
                drawPath?.let { paths.add(it) }
                drawPath = null
            }
            else -> return false
        }

        invalidate() // Tells the view to redraw
        return true
    }

    /**
     * Call this from your Activity/Fragment to change the brush size
     */
    fun setSizeForBrush(newSize: Float) {
        brushSize = newSize
    }

    /**
     * Call this from your Activity/Fragment to change color
     */
    fun setColor(newColor: Int) {
        color = newColor
    }

    /**
     * Logic to undo the last action
     */
    fun onClickUndo() {
        if (paths.size > 0) {
            paths.removeAt(paths.size - 1)
            invalidate()
        }
    }

    // Custom Path class that keeps track of its own color and size
    internal inner class CustomPath(var color: Int,
                                    var brushThickness: Float) : Path()
}