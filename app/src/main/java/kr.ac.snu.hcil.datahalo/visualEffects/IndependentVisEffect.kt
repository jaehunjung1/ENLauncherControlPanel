package kr.ac.snu.hcil.datahalo.visualEffects

import android.animation.Animator
import android.animation.AnimatorSet
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View
import android.widget.ImageView
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotification
import kr.ac.snu.hcil.datahalo.visconfig.NotiProperty
import kr.ac.snu.hcil.datahalo.visconfig.IndependentVisEffectVisParams
import kr.ac.snu.hcil.datahalo.visconfig.KeywordGroupImportancePatterns.Companion.ELSE_KEYWORD_GROUP

interface InterfaceIndependentVisEffect{

    val effectID: String
    val independentVisObjects: List<AbstractIndependentVisObject>
    val localPivotID: Int

    fun getMappedNotificationID(): Int
    fun getDrawables(): List<Drawable>
    fun getLocalLayoutParams(): List<ConstraintLayout.LayoutParams>
    fun getAnimatorSet(): AnimatorSet

    fun setEnhancedNotification(enhancedNotification: EnhancedNotification)
    fun setLocalStructure(localLayoutParams: List<ConstraintLayout.LayoutParams>, visParams: IndependentVisEffectVisParams)

    fun placeVisObjectsInLayout(constraintLayout: ConstraintLayout, pivotLayoutParams: ConstraintLayout.LayoutParams)
    fun deleteVisObjectsInLayout(constraintLayout: ConstraintLayout)
}

abstract class AbstractIndependentVisEffect(
        final override val effectID: String,
        final override val independentVisObjects: List<AbstractIndependentVisObject>,
        var visualParameters: IndependentVisEffectVisParams
): InterfaceIndependentVisEffect
{
    final override val localPivotID: Int = View.generateViewId()
    //private val maxEffectSize = 100
    private val drawables: MutableList<Drawable> = mutableListOf()
    private val localLayoutParams: List<ConstraintLayout.LayoutParams>
            = List(independentVisObjects.size){ ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)}
    private var animatorSet: AnimatorSet = AnimatorSet()
    private var currentCenterPolar: Pair<Int, Float> = Pair(-1, 0f)
    private var mappedNotificationID: Int = -1

    init{
        independentVisObjects.forEach{ abstractIndependentVisObject ->
            abstractIndependentVisObject.setID(View.generateViewId())
        }
    }

    final override fun getMappedNotificationID() = mappedNotificationID
    final override fun getDrawables(): List<Drawable> = drawables.toList()
    final override fun getAnimatorSet() = animatorSet
    final override fun getLocalLayoutParams(): List<ConstraintLayout.LayoutParams> = localLayoutParams.toList()

    final override fun setEnhancedNotification(enhancedNotification: EnhancedNotification) {

        mappedNotificationID = enhancedNotification.id
        drawables.clear()

        val animatorCollection = mutableListOf<Animator>()
        independentVisObjects.forEach{ visObject ->
            val (drawable, animator) = visObject.getDrawableWithAnimator(
                    mapOf(
                            NotiProperty.IMPORTANCE to enhancedNotification.currEnhancement,
                            NotiProperty.LIFE_STAGE to enhancedNotification.lifeCycle,
                            NotiProperty.CONTENT to (enhancedNotification.keywordGroup)
                    )
            )

            drawables.add(drawable)
            animatorCollection.addAll(
                    animator.childAnimations.map{anim->
                        anim.also{it.setTarget(drawable)}
                    }
            )
        }

        animatorSet.playTogether(animatorCollection)
    }

    override fun setLocalStructure(localLayoutParams: List<ConstraintLayout.LayoutParams>, visParams: IndependentVisEffectVisParams) {
        val unitAngle = 360f / localLayoutParams.size
        localLayoutParams.forEachIndexed{ index, layoutParam ->
            layoutParam.apply{
                circleConstraint = localPivotID
                circleRadius = visParams.radius[index]
                circleAngle = visParams.offsetAngle + (unitAngle * index)
            }
        }
    }

    override fun placeVisObjectsInLayout(constraintLayout: ConstraintLayout, pivotLayoutParams: ConstraintLayout.LayoutParams) {
        var localPivotView = constraintLayout.findViewById<View>(localPivotID) as ImageView?

        if(localPivotView == null){
            //r: global pivot -> local pivot 사이의 거리, theta: 꼭대기 기준으로 r과 이루는 각도
            currentCenterPolar = Pair(pivotLayoutParams.circleRadius, pivotLayoutParams.circleAngle)
            localPivotView = ImageView(constraintLayout.context).also{
                it.id = localPivotID
                //it.setBackgroundColor(Color.YELLOW)
                it.layoutParams = pivotLayoutParams
            }

            constraintLayout.addView(localPivotView)
        }

        if (currentCenterPolar.first != pivotLayoutParams.circleRadius || currentCenterPolar.second != pivotLayoutParams.circleAngle){
            currentCenterPolar = Pair(pivotLayoutParams.circleRadius, pivotLayoutParams.circleAngle)
            localPivotView.layoutParams = pivotLayoutParams
        }

        setLocalStructure(localLayoutParams, visualParameters)

        independentVisObjects.forEachIndexed{ index, visObj ->
            val objectView: ImageView? = constraintLayout.findViewById(visObj.getID())

            objectView?.let{
                it.setImageDrawable(drawables[index])
                it.rotation = pivotLayoutParams.circleAngle
                it.layoutParams = localLayoutParams[index]

            }?: run{
                val imageView = ImageView(constraintLayout.context).also{
                    it.id = visObj.getID()
                    it.setImageDrawable(drawables[index])
                    it.rotation = pivotLayoutParams.circleAngle
                }
                constraintLayout.addView(imageView, localLayoutParams[index])
            }
        }
    }

    final override fun deleteVisObjectsInLayout(constraintLayout: ConstraintLayout) {
        independentVisObjects.forEach{
            constraintLayout.removeView(
                    constraintLayout.findViewById(it.getID())
            )
        }

        constraintLayout.removeView(
                constraintLayout.findViewById(localPivotID)
        )
    }
}