package kr.ac.snu.hcil.datahalo.utils

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.Paint.Align


class TextDrawable(text: CharSequence, textSize: Float): Drawable() {

    private val DEFAULT_COLOR = Color.WHITE
    private val DEFAULT_TEXTSIZE = 15f
    private var mPaint: Paint? = null
    private var mText: CharSequence? = null
    private var mIntrinsicWidth: Int = 0
    private var mIntrinsicHeight: Int = 0

    init{
        mText = text
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG).also{
            it.color = DEFAULT_COLOR
            it.textAlign = Align.CENTER
            it.textSize = textSize

            mIntrinsicWidth = Math.round(it.measureText(text, 0, text.length) + .5).toInt()
            mIntrinsicHeight = it.getFontMetricsInt(null)
        }
    }

    fun setText(cs: CharSequence, textSize: Float = DEFAULT_TEXTSIZE, textColor: Int = DEFAULT_COLOR){
        mText = cs
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG).also{
            it.color = textColor
            it.textAlign = Align.CENTER
            it.textSize = textSize

            mIntrinsicWidth = Math.round(it.measureText(cs, 0, cs.length) + .5).toInt()
            mIntrinsicHeight = it.getFontMetricsInt(null)
        }
        invalidateSelf()
    }

    fun setColor(color: Int){
        mPaint?.let{
            it.color = color
        }
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        val bounds = bounds
        mPaint?.let{paint ->
            mText?.let{ text ->
                canvas.drawText(text, 0, text.length, bounds.exactCenterX(), bounds.exactCenterY(), paint)
            }
        }
    }


    override fun getOpacity(): Int{
        return mPaint?.let{
            it.alpha
        }?: run{
            0
        }
    }

    override fun setAlpha(alpha: Int) {
        mPaint?.let{
            it.alpha = alpha
        }
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint?.let{
            it.colorFilter = colorFilter
        }
    }

    override fun getIntrinsicWidth(): Int = mIntrinsicWidth

    override fun getIntrinsicHeight(): Int = mIntrinsicHeight

    override fun applyTheme(t: Resources.Theme) {
        super.applyTheme(t)
    }
}