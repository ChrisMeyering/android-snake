package com.meyering.snake.objects

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import com.meyering.snake.R
import java.util.LinkedList
import kotlin.random.Random

class Food (context: Context, val dimensions: Point, val baseCoords: Point, val step: Int){
    var food: LinkedList<Point> = LinkedList()
    val foodDrawable = context.getDrawable(R.drawable.food).apply {
        this!!.bounds = Rect(-step/2,-step/2, step/2, step/2)
    }


    fun generateFood() : Point {
        var newFood: Point
        do {
            val food_x = baseCoords.x + Random.nextInt(0,dimensions.x) * step
            val food_y = baseCoords.y + Random.nextInt(0,dimensions.y) * step
            newFood = Point(food_x , food_y)
        } while (food.contains(newFood))
        return newFood
    }

    fun addFood(p: Point) {
        food.add(p)
    }

    fun consumeFood(p: Point) {
        food.remove(p)
    }

    fun draw(canvas: Canvas) {
        canvas.apply {
            food.forEach {
                canvas.translate(it.x.toFloat(), it.y.toFloat())
                foodDrawable!!.draw(canvas)
                canvas.translate(-it.x.toFloat(), -it.y.toFloat())
            }
        }

    }

}