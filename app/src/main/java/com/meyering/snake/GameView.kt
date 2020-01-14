package com.meyering.snake

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.meyering.snake.objects.Direction
import com.meyering.snake.objects.OnSwipeListener
import com.meyering.snake.objects.Snake
import kotlin.math.min

class GameView(context: Context, attrs: AttributeSet?) : View(context, attrs), OnSwipeListener {
    companion object {
        const val SMALLEST_DIMENSION = 20 // 20 snake body parts will fit in width/height
    }

    lateinit var snake: Snake
    private val gestureDetector: GestureDetector = GestureDetector(this)

    @SuppressLint("NewApi")
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(android.R.color.holo_red_dark)
        style = Paint.Style.FILL
    }

    var height: Int? = null
    var width: Int? = null
    var radius: Int? = null
    var borderHorizontal = 0
    var borderVertical = 0
    var gameStarted = false
    var mhandler: Handler = Handler()
    lateinit  var runnable: Runnable

    init {
        runnable = Runnable {
            if (!gameStarted) {
                if (width != null && height != null) {
                    init_game()
                    gameStarted = true
                }
            } else {
                moveSnake()
            }
            mhandler.postDelayed(runnable, 300)
        }
        runnable.run()
    }

    fun init_game() {
        radius = min(width!!, height!!) / (2 * SMALLEST_DIMENSION)
        snake = Snake(context, radius!!)
        snake.init(Point(width!!/2, height!!/2))
        initBorders()
    }
    fun isOutOfBounds(p: Point): Boolean {
        return p.x > width!! - (radius!! + borderHorizontal)
                || p.y > height!! - (radius!! + borderVertical)
                || p.y < radius!! + borderVertical
                || p.x < radius!! + borderHorizontal
    }

    fun moveSnake() {
        if (isOutOfBounds(snake.move())) {
            mhandler.removeCallbacks(runnable)
            gameStarted = false
            Toast.makeText(context, "GAME OVER", Toast.LENGTH_SHORT).show()
            mhandler.postDelayed(runnable, 2000)
        }
        invalidate()

    }

    fun initBorders() {
        borderHorizontal = width!!/2 - radius!!

        while (borderHorizontal> 2 * radius!!) {
            borderHorizontal-= 2 * radius!!
        }
        borderVertical = height!!/2 - radius!!
        while (borderVertical > 2 * radius!!) {
            borderVertical-= 2 * radius!!
        }
    }

    fun drawBorders(canvas: Canvas) {
        canvas.apply {
            canvas.drawRect(0f,0f,borderHorizontal.toFloat(),height.toFloat(), borderPaint)
            canvas.drawRect(0f,0f,width.toFloat(), borderVertical.toFloat(), borderPaint)
            canvas.drawRect(width.toFloat() - borderHorizontal, 0f, width.toFloat(), height.toFloat(), borderPaint)
            canvas.drawRect(0f,height.toFloat() - borderVertical,width.toFloat(),height.toFloat(), borderPaint)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBorders(canvas)
        if (::snake.isInitialized) {
            snake.draw(canvas)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        width = measuredWidth
        height = measuredHeight
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    override fun onSwipe(d: Direction): Boolean {
        return snake.updateDirection(d)
    }


}