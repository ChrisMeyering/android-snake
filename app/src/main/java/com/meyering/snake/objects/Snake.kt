package com.meyering.snake.objects

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import androidx.core.graphics.plus
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.meyering.snake.R
import java.util.*

class Snake(context: Context, radius: Int) {
    companion object {
        const val START_LENGTH = 12
    }

    var justAte = false
    val snakeBodyPart = context.getDrawable(R.drawable.snake_body_part).apply {
        this!!.bounds = Rect(-radius,-radius, radius, radius)
    }
    val smallRadius = radius * 80 / 100
    val snakeFace = VectorDrawableCompat.create(context.getResources(), R.drawable.snake_face, null).apply {
        this!!.bounds = Rect(-smallRadius ,-smallRadius, smallRadius, smallRadius)
    }

    val body: Queue<Point> = LinkedList()
    lateinit var lastHead: Point
    val directions = arrayOf(
        Point(2 * radius, 0), Point(0, 2 * radius),
        Point(-2 * radius, 0), Point(0, -2 * radius)
    )
    var direction: Direction = Direction.RIGHT
    var badDirection = Direction.complement(direction)

    fun init(start: Point) {
        lastHead = start
        repeat(START_LENGTH) {
            body.add(lastHead)
        }
    }

    fun step(): Point {
        return directions[direction.ordinal]
    }

    fun updateDirection(d: Direction): Boolean {
        if (d != badDirection && d != Direction.NONE) {
            direction = d
            return true
        }
        return false
    }

    fun draw(canvas: Canvas) {
        canvas.apply {
            body.forEach {
                canvas.translate(it.x.toFloat(), it.y.toFloat())
                snakeBodyPart!!.draw(canvas)
                canvas.translate(-it.x.toFloat(), -it.y.toFloat())
            }
        }
        canvas.translate(lastHead.x.toFloat(), lastHead.y.toFloat())
        snakeFace!!.draw(canvas)
        canvas.translate(-lastHead.x.toFloat(), -lastHead.y.toFloat())
    }

    fun move(): Point? {
        badDirection = Direction.complement(direction)
        if (!body.isEmpty() && !justAte) {
            body.remove()
        } else if (justAte) {
            justAte = false
        }
        lastHead += step()
        if (body.contains(lastHead))
            return null
        body.add(lastHead)
        return lastHead
    }
}
