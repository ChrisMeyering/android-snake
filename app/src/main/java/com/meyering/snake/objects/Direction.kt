package com.meyering.snake.objects

enum class Direction {
    RIGHT,
    DOWN,
    LEFT,
    UP,
    NONE;

    companion object {
        fun fromAngle(angle: Float): Direction {
            if (angle in -40f..40f || angle in 320f..360f)
                return RIGHT
            if (angle in -130f..-50f)
                return DOWN
            if (angle in 140f..180f || angle in -180f..-140f)
                return LEFT
            if (angle in 50f..130f)
                return UP
            return NONE

        }

        fun complement(dir: Direction): Direction {
            return when(dir) {
                RIGHT -> LEFT
                DOWN -> UP
                LEFT -> RIGHT
                UP -> DOWN
                NONE -> NONE
            }
        }
    }
}

