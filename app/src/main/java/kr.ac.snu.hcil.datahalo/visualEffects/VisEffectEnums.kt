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

enum class VisShapeType(val explanation: String){
    RECT("Rectangle Shaped Drawable"),
    OVAL("Circle Shaped Drawable"),
    PATH("Path Shaped Drawable"),
    TEXT("Raw Data Drawable"),
    IMAGE("Image Drawable")
}

data class VisObjectShape(val type: VisShapeType, val drawable: Drawable)

