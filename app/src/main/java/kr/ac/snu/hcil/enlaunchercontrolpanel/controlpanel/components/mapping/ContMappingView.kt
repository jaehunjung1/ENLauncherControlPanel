package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.mapping

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.View

class ContMappingView(context: Context, leftX: Float, rightX: Float) : View(context) {
    var wallpaint: Paint
    var wallpath: Path

    var coordX: FloatArray
    var coordY: FloatArray

    init {

        wallpaint = Paint()
        wallpaint.isAntiAlias = true
        wallpaint.color = Color.RED
        wallpaint.alpha = 150
        wallpaint.style = Paint.Style.FILL

        wallpath = Path()

        coordX = floatArrayOf(leftX, rightX, rightX, leftX)
        //coordY = floatArrayOf(0f, 100f, 0f, 100f)
        coordY = floatArrayOf(45f, 640f, 45f, 640f)

    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        wallpath.reset() // only needed when reusing this path for a new build
        wallpath.moveTo(coordX[0], coordY[0]) // used for first point
        wallpath.lineTo(coordX[1], coordY[1])
        wallpath.lineTo(coordX[2], coordY[2])
        wallpath.lineTo(coordX[3], coordY[3])
        wallpath.close()

        canvas.drawPath(wallpath, wallpaint)
    }

    fun invalidateSize(thumbPos: FloatArray) {
        coordY = thumbPos
        this.invalidate()
    }
}
