package kr.ac.snu.hcil.datahalo.utils

import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.Paint.Align
import kotlin.math.roundToInt


class TextDrawable(text: CharSequence, textSize: Float): Drawable() {

    companion object{
        private const val DEFAULT_COLOR = Color.WHITE
        private const val DEFAULT_TEXT_SIZE = 15f
    }

    private var mPaint: Paint? = null
    private var mText: CharSequence? = null
    private var rotation: Float? = null
    private var mIntrinsicWidth: Int = 0
    private var mIntrinsicHeight: Int = 0

    init{
        mText = text
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG).also{
            it.color = DEFAULT_COLOR
            it.textAlign = Align.CENTER
            it.textSize = textSize

            mIntrinsicWidth = (it.measureText(text, 0, text.length) + .5).roundToInt()
            mIntrinsicHeight = it.getFontMetricsInt(Paint.FontMetricsInt())
        }
    }

    fun setText(cs: CharSequence, textSize: Float = DEFAULT_TEXT_SIZE, textColor: Int = DEFAULT_COLOR, rotate: Float? = null){
        rotation = rotate
        mText = cs
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG).also{
            it.color = textColor
            it.textAlign = Align.CENTER
            it.textSize = textSize

            mIntrinsicWidth = (it.measureText(cs, 0, cs.length) + .5).roundToInt()
            mIntrinsicHeight = it.getFontMetricsInt(Paint.FontMetricsInt())
        }
        invalidateSelf()
    }

    fun setColor(color: Int){
        mPaint?.let{ it.color = color }
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        mPaint?.let{ paint ->
            mText?.let{ text ->
                rotation?.let{ angle ->
                    canvas.apply{
                        save()
                        rotate(angle, bounds.exactCenterX(), bounds.exactCenterY())
                        drawText(text, 0, text.length, bounds.exactCenterX(),bounds.exactCenterY(), paint)
                        restore()
                    }
                }?: run{
                    canvas.drawText(text, 0, text.length, bounds.exactCenterX(), bounds.exactCenterY(), paint)
                }
            }
        }
    }

    override fun getOpacity(): Int{
        return mPaint?.alpha ?: 0
    }

    override fun setAlpha(alpha: Int) {
        mPaint?.let{ it.alpha = alpha }
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint?.let{
            it.colorFilter = colorFilter
        }
    }

    override fun getIntrinsicWidth(): Int = mIntrinsicWidth
    override fun getIntrinsicHeight(): Int = mIntrinsicHeight

}