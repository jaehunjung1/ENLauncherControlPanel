package kr.ac.snu.hcil.datahalo.visualEffects

import android.graphics.drawable.Drawable

enum class ColorSwatches{
    LIGHT_VIBRANT,
    VIBRANT,
    DARK_VIBRANT,
    LIGHT_MUTED,
    MUTED,
    DARK_MUTED
}

enum class AnimationTypes {
    ALPHA,
    ROTATION,
    SCALE_X,
    SCALE_Y,
    TRANSLATION_X,
    TRANSLATION_Y
}

enum class NewVisShape{
    RECT,
    OVAL,
    PATH,
    IMAGE,
    RAW
}

data class VisObjectShape(val type: NewVisShape, val drawable: Drawable)

