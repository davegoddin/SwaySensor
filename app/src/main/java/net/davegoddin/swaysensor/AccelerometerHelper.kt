package net.davegoddin.swaysensor

import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.sqrt

class AccelerometerHelper {
    fun calculateLeanAngle(input : FloatArray) : Float
    {
        val xComponent = -input[0]
        val zComponent = input[2]

        val theta = atan(xComponent/zComponent)
        val magnitude = sqrt(xComponent*xComponent + zComponent*zComponent)
//        Log.d("Theta", "${radToDeg(theta)}, ${radToDeg(magnitude)}")

        return radToDeg(theta)


    }

    fun radToDeg(radians : Float) : Float{
        return radians * (180f / PI.toFloat())
    }
}