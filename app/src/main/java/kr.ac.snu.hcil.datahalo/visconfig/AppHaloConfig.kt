package kr.ac.snu.hcil.datahalo.visconfig

import android.animation.AnimatorSet
import android.graphics.Color
import android.view.View
import android.view.animation.LinearInterpolator
import kr.ac.snu.hcil.datahalo.manager.AppHaloLayoutMethods
import kr.ac.snu.hcil.datahalo.manager.VisEffectManager
import kr.ac.snu.hcil.datahalo.notificationdata.NotiHierarchy
import kr.ac.snu.hcil.datahalo.visualEffects.AggregatedVisMappingRule

data class AppHaloConfig(val packageName: String){

    //Filter & Sample Methods Configs
    var filterImportanceConfig = mapOf(
            WGBFilterVar.ACTIVE to true,
            WGBFilterVar.WHITE_COND to 0.5,
            WGBFilterVar.BLACK_COND to 0.0
    )
    var filterObservationWindowConfig = mapOf(
            WGBFilterVar.ACTIVE to true,
            WGBFilterVar.WHITE_COND to 60 * 60 * 1000L,
            WGBFilterVar.BLACK_COND to 6 * 60 * 60 * 1000L
    )
    var filterChannelConfig = mapOf(
            WGBFilterVar.ACTIVE to false,
            WGBFilterVar.WHITE_COND to setOf<NotiHierarchy>(),
            WGBFilterVar.BLACK_COND to setOf<NotiHierarchy>()
    )
    var filterKeywordConfig = mapOf(
            WGBFilterVar.ACTIVE to true,
            WGBFilterVar.WHITE_COND to setOf<String>(),
            WGBFilterVar.BLACK_COND to setOf<String>()
    )
    var maxNumOfIndependentNotifications: Int = 3

    var notificationEnhancementParams: NotificationEnhacementParams = NotificationEnhacementParams()

    //AppHalo Layout & Visualization Methods
    var haloLayoutMethodName:String = AppHaloLayoutMethods.availiableLayouts[0]
    var independentVisEffectName: String = VisEffectManager.availableIndependentVisEffects[0]
    var aggregatedVisEffectName: String = VisEffectManager.availableAggregatedVisEffects[0]

    // 사용자 인풋으로 받고 아닌 경우에는 기본 설정으로 가야겠죠?
    val independentVisualMappings: MutableList<Map<NuNotiVisVariable, NotiProperty?>> = mutableListOf()
    var independentVisEffectVisParams: IndependentVisEffectVisParams = IndependentVisEffectVisParams()
    val independentVisualParameters: MutableList<IndependentVisObjectVisParams> = mutableListOf()
    val independentDataParameters: MutableList<IndependentVisObjectDataParams> = mutableListOf()
    val independentAnimationParameters: MutableList<List<IndependentVisObjectAnimParams>> = mutableListOf()

    val aggregatedVisualMappings: MutableList<AggregatedVisMappingRule> = mutableListOf()
    var aggregatedVisEffectVisParams: AggregatedVisEffectVisParams = AggregatedVisEffectVisParams()
    val aggregatedVisualParameters: MutableList<AggregatedVisObjectVisParams> = mutableListOf()
    val aggregatedDataParameters: MutableList<AggregatedVisObjectDataParams> = mutableListOf()
    val aggregatedAnimationParameters: MutableList<List<AggregatedVisObjectAnimParams>> = mutableListOf()

}