package com.company.carracegame

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class GameView(var c: Context, var gameTask: GameTask) : View(c) {
    private var myPoint: Paint? = null
    private var speed = 1
    private var life = 2
    private var time = 0
    private var score = 0
    private var myCarPosition = 0
    var viewWidth = 0
    var viewHeight = 0

    private val otherCars = ArrayList<HashMap<String, Any>>()

    init {
        myPoint = Paint()
    }

    private fun reset(){
        life = 2
        speed = 1
        time = 0
        score = 0
        myCarPosition = 0
        viewWidth = 0
        viewHeight = 0
    }

    @SuppressLint("UseCompatLoadingForDrawables", "DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas);
        viewWidth = this.measuredWidth
        viewHeight = this.measuredHeight

        if (time % 700 < 10 + speed) {
            val map = HashMap<String, Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            otherCars.add(map)
        }

        time += 10 + speed
        val carWidth = viewWidth / 5
        val carHeight = carWidth + 10
        myPoint!!.style = Paint.Style.FILL

        val d = resources.getDrawable(R.drawable.blue_car, null)

        d.setBounds(
            myCarPosition * viewWidth / 3 + viewWidth / 15 + 25,
            viewHeight - 2 - carHeight,
            myCarPosition * viewWidth / 3 + viewWidth / 15 + carWidth - 25,
            viewHeight - 2
        )

        d.draw(canvas!!)

        myPoint!!.color = Color.GREEN

        var highScore = 0

        for (i in otherCars.indices) {
            try {
                val carX = otherCars[i]["lane"] as Int * viewWidth / 3 + viewWidth / 15
                val carY = time - otherCars[i]["startTime"] as Int
                val d2 = resources.getDrawable(R.drawable.red_car, null)

                d2.setBounds(
                    carX + 25, carY - carHeight, carX + carWidth - 25, carY
                )
                d2.draw(canvas)

                if (otherCars[i]["lane"] as Int == myCarPosition) {
                    if (carY > viewHeight - 2 - carHeight && carY < viewHeight - 2) {
                        if (life == 1) {
                            gameTask.closeGame(score)
                            reset()
                        } else {
                            otherCars.removeAt(i)
                            myCarPosition = 0
                            life--
                        }
                    }
                }
                if (carY > viewHeight + carHeight) {
                    otherCars.removeAt(i)
                    score++
                    speed = 1 + abs(score / 8)
                    highScore = score.coerceAtLeast(highScore)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        myPoint!!.color = Color.WHITE
        myPoint!!.textSize = 40f
        canvas.drawText("Life : $life", viewWidth - 180f, 40f, myPoint!!)
        canvas.drawText("Speed : $speed", 80f, 120f, myPoint!!)
        canvas.drawText("Score : $score", 80f, 40f, myPoint!!)
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val x1 = event.x
                if (x1 < viewWidth / 2 && myCarPosition > 0) {
                    myCarPosition--
                } else if (x1 > viewWidth / 2 && myCarPosition < 2) {
                    myCarPosition++
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {

            }
        }
        return true
    }
}