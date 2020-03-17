package com.meyering.snake

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.meyering.snake.objects.Direction
import com.meyering.snake.objects.Food
import com.meyering.snake.objects.OnSwipeListener
import com.meyering.snake.objects.Snake
import kotlin.math.max
import kotlin.math.min

class GameView(context: Context, attrs: AttributeSet?) : View(context, attrs), OnSwipeListener {
    companion object {
        const val SMALLEST_DIMENSION = 16
        const val FOOD_GENERATION_FREQUENCY = 30
        const val MAX_MOVE_FREQUENCY = 100L
        const val START_MOVE_FREQUENCY = 400L
        const val ACCELERATE_FREQUENCY = 1000L
        const val ACCELERATION_RATE = 1
    }

    var moveFrequency = START_MOVE_FREQUENCY
    private var isPaused = false
    lateinit var snake: Snake
    lateinit var food: Food
    private val gestureDetector: GestureDetector = GestureDetector(context, this)

    @SuppressLint("NewApi")
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(android.R.color.black)
        style = Paint.Style.FILL
    }

    var lastFoodAge = 0
    var height: Int? = null
    var width: Int? = null
    var radius: Int? = null
    var borderHorizontal = 0
    var borderVertical = 0
    var mhandler: Handler = Handler()
    lateinit var runnable_init_game: Runnable
    lateinit var runnable_play_game: Runnable
    lateinit var runnable_accelerator: Runnable
    init {
        runnable_init_game = Runnable {
            if (width != null && height != null) {
                init_game()
                runnable_play_game.run()
                mhandler.postDelayed(runnable_accelerator, ACCELERATE_FREQUENCY)
            } else {
                mhandler.postDelayed(runnable_init_game, moveFrequency)
            }
        }

        runnable_play_game = Runnable {
            moveSnake()
        }
//        runnable = Runnable {
//            if (!gameStarted) {
//                if (width != null && height != null) {
//                    init_game()
//                    gameStarted = true
//                }
//            } else {
//                moveSnake()
//            }
//            mhandler.postDelayed(runnable, 300)
//        }
//        runnable.run()
        runnable_accelerator = Runnable {
            accelerate()
        }

        runnable_init_game.run()

    }

    fun accelerate() {
        moveFrequency = max(moveFrequency - ACCELERATION_RATE, MAX_MOVE_FREQUENCY)
        if (moveFrequency > MAX_MOVE_FREQUENCY) {
            mhandler.postDelayed(runnable_accelerator, ACCELERATE_FREQUENCY)
        }
    }

    fun getBoardDimensions(): Point {
        val dim_x = (width!! - borderHorizontal * 2) / (radius!! * 2)
        val dim_y = (height!! - borderVertical * 2) / (radius!! * 2)
        return Point(dim_x,dim_y)
    }

    fun init_game() {
        moveFrequency = START_MOVE_FREQUENCY
        radius = min(width!!, height!!) / (2 * SMALLEST_DIMENSION)
        snake = Snake(context, radius!!)
        snake.init(Point(width!!/2, height!!/2))
        initBorders()
        food = Food(context, getBoardDimensions(),Point(borderHorizontal + radius!!, borderVertical + radius!!), radius!! * 2)
        generateFood()

    }
    fun isOutOfBounds(p: Point): Boolean {
        return p.x > width!! - (radius!! + borderHorizontal)
                || p.y > height!! - (radius!! + borderVertical)
                || p.y < radius!! + borderVertical
                || p.x < radius!! + borderHorizontal
    }

    fun gameOver(msg: String) {
        mhandler.removeCallbacksAndMessages(runnable_play_game)
        Log.i("Ran into a wall", "GAME OVER")
        Toast.makeText(context, "GAME OVER: $msg", Toast.LENGTH_SHORT).show()
        mhandler.postDelayed(runnable_init_game, 2000)
    }

    fun generateFood() {
        var newFood: Point
        do {
            newFood = food.generateFood()
        } while (snake.body.contains(newFood))
        food.addFood(newFood)
        lastFoodAge = 0
    }

    fun moveSnake() {
        val headPosition = snake.move()
        when {
            headPosition == null -> gameOver("You ate yourself.")
            isOutOfBounds(headPosition) -> gameOver("You ran into a wall.")
            else -> {
                if (food.food.contains(headPosition)) {
                    food.consumeFood(headPosition)
                    snake.justAte = true
                    if (food.food.isEmpty()) {
                        generateFood()
                    }
                }
                if (lastFoodAge > FOOD_GENERATION_FREQUENCY) {
                    generateFood()
                } else {
                    lastFoodAge++
                }
                mhandler.postDelayed(runnable_play_game, moveFrequency)
            }
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
        if (::food.isInitialized) {
            food.draw(canvas)
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

    fun pause() {
        isPaused = true
        mhandler.removeCallbacksAndMessages(null)
    }

    fun resume() {
        if (isPaused) {
            isPaused = false
            mhandler.postDelayed(runnable_play_game, 1200)
            mhandler.postDelayed(runnable_accelerator, ACCELERATE_FREQUENCY)
        }
    }


}