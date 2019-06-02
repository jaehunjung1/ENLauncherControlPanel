package kr.ac.snu.hcil.datahalo.utils

class CoordinateConverter {
    companion object{

        fun defaultToCenterBasedCartesianCoordinate(x:Double, y:Double, w:Double, h: Double): Pair<Double, Double>{
            //input: left top (0,0) -> right bottom(width, height)
            val cX = w / 2
            val cY = h / 2
            //output: left top (-cX, -cY) -> right bottom(cX, cY)
            return Pair(x - cX, y - cY)
        }

        fun centerBasedToDefaultCartesianCoordinate(xFromCenter:Double, yFromCenter: Double, w:Double, h: Double): Pair<Double, Double>{
            //input: left top (-cX, -cY) -> right bottom(cX, cY)
            val cX = w / 2
            val cY = h / 2
            //output: left top (0, 0) -> right bottom(width, height)
            return Pair(xFromCenter + cX, yFromCenter + cY)
        }

        fun centerBasedCartesianToPolarCoordinate(xFromCenter: Double, yFromCenter: Double): Pair<Double, Double>{
            //x, y는 중심 기준
            //degree는 꼭대기 기준으로 시계방향임
            val r = Math.sqrt(xFromCenter * xFromCenter + yFromCenter * yFromCenter)
            val degree = (Math.atan2( yFromCenter, xFromCenter) * 180.0 / Math.PI) + 90.0
            val degreePositive = if(degree < 0.0) 360.0 + degree else if(degree > 360.0) degree - 360.0 else degree
            return Pair(r, degreePositive)
        }

        fun polarToCenterBasedCartesianCoordinate(r: Double, degree: Double): Pair<Double, Double>{
            //input: 꼭대기 기준 degree
            val rad = degree * Math.PI / 180.0

            //return (x, y)도 중심 기준 좌표
            return Pair(r * Math.sin(rad), -1.0 * r * Math.cos(rad))
        }
    }
}