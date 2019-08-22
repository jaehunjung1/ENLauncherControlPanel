package kr.ac.snu.hcil.datahalo.visconfig

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.util.Property
import android.view.View
import kr.ac.snu.hcil.datahalo.manager.VisDataManager
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancementPattern
import kr.ac.snu.hcil.datahalo.notificationdata.NotiHierarchy
import kr.ac.snu.hcil.datahalo.utils.MapFunctionUtilities
import kr.ac.snu.hcil.datahalo.visualEffects.VisShapeType
import kr.ac.snu.hcil.datahalo.visualEffects.VisObjectShape
import kotlin.math.roundToLong

data class IndependentVisObjectVisParams(
        var selectedPos: Double = 1.0,
        var selectedPosRange: Pair<Double, Double> = Pair(0.0, 1.0),
        var selectedShape: VisObjectShape = VisObjectShape(VisShapeType.RECT, null),
        var selectedShapeList: List<VisObjectShape> = listOf(),
        var selectedMotion: AnimatorSet = AnimatorSet(),
        var selectedMotionList: List<AnimatorSet> = listOf(),
        var selectedColor: Int = Color.BLACK,
        var selectedColorList: List<Int> = listOf(),
        var selectedSize: Double = 1.0,
        var selectedSizeRange: Pair<Double, Double> = Pair(0.0, 1.0),
        var additional: Map<String, Any> = emptyMap()
){
    private var _selectedPosRangeList: MutableList<Pair<Double, Double>> = mutableListOf()
    private var _selectedSizeRangeList: MutableList<Pair<Double, Double>> = mutableListOf()

    fun getSelectedPosRangeList(binNum: Int): List<Pair<Double, Double>> {
        return if(binNum != _selectedPosRangeList.size){ MapFunctionUtilities.bin(selectedPosRange, binNum)
        } else{ _selectedPosRangeList }
    }

    fun setSelectedPosRangeList(rangeList: List<Pair<Double, Double>>){
        _selectedPosRangeList.clear()
        _selectedPosRangeList.addAll(rangeList)
    }

    fun getSelectedSizeRangeList(binNum: Int): List<Pair<Double, Double>>{
        return if(binNum != _selectedSizeRangeList.size){ MapFunctionUtilities.bin(selectedSizeRange, binNum)
        } else{ _selectedSizeRangeList }
    }

    fun setSelectedSizeRangeList(rangeList: List<Pair<Double, Double>>){
        _selectedSizeRangeList.clear()
        _selectedSizeRangeList.addAll(rangeList)
    }
}

data class IndependentVisObjectDataParams(
        var binNums: Int = 5,
        var selectedImportanceRange: Pair<Double, Double> = Pair(0.0, 1.0),
        var tSaturation: Long = -1L, //3hrs
        var additional: Map<String, Any> = emptyMap()
){
    val givenLifeList: List<EnhancedNotificationLife> = EnhancedNotificationLife.values().toList()
    val selectedImportanceRangeList: List<Pair<Double, Double>>
        get() = MapFunctionUtilities.bin(selectedImportanceRange, binNums)
    val givenImportanceRange: Pair<Double, Double> = Pair(0.0, 1.0)
}

data class IndependentVisObjectAnimParams(
        var property: Property<View, Float>,
        var values: Array<Float>,
        var duration: Long,
        var interpolator: TimeInterpolator,
        var sustained: List<EnhancedNotificationLife> = listOf(
                EnhancedNotificationLife.JUST_TRIGGERED,
                EnhancedNotificationLife.TRIGGERED_NOT_INTERACTED,
                EnhancedNotificationLife.JUST_INTERACTED,
                EnhancedNotificationLife.INTERACTED_NOT_DECAYING,
                EnhancedNotificationLife.DECAYING
        ),
        var repeatCount: Int = ObjectAnimator.INFINITE,
        var repeatMode: Int = ObjectAnimator.REVERSE
){
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

data class IndependentVisEffectVisParams(
        val radius: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0),
        var offsetAngle: Float = 0f
)

data class AggregatedVisObjectVisParams(
        var selectedPos: Double = 1.0,
        var selectedPosRange: Pair<Double, Double> = Pair(0.0, 1.0),
        var selectedShape: VisObjectShape = VisObjectShape(VisShapeType.OVAL, null),
        var selectedShapeList: List<VisObjectShape> = listOf(),
        var selectedMotion: AnimatorSet = AnimatorSet(),
        var selectedMotionList: List<AnimatorSet> = listOf(),
        var selectedColor: Int = Color.BLACK,
        var selectedColorList: List<Int> = listOf(),
        var selectedSize: Double = 1.0,
        var selectedSizeRange: Pair<Double, Double> = Pair(0.0, 1.0),
        var additional: Map<String, Any> = emptyMap()
){
    private var _selectedPosRangeList: MutableList<Pair<Double, Double>> = mutableListOf()
    private var _selectedSizeRangeList: MutableList<Pair<Double, Double>> = mutableListOf()

    fun getSelectedPosRangeList(binNum: Int): List<Pair<Double, Double>> {
        return if(binNum != _selectedPosRangeList.size){ MapFunctionUtilities.bin(selectedPosRange, binNum)
        } else{ _selectedPosRangeList }
    }

    fun setSelectedPosRangeList(rangeList: List<Pair<Double, Double>>){
        _selectedPosRangeList.clear()
        _selectedPosRangeList.addAll(rangeList)
    }

    fun getSelectedSizeRangeList(binNum: Int): List<Pair<Double, Double>>{
        return if(binNum != _selectedSizeRangeList.size){ MapFunctionUtilities.bin(selectedSizeRange, binNum)
        } else{ _selectedSizeRangeList }
    }

    fun setSelectedSizeRangeList(rangeList: List<Pair<Double, Double>>) {
        _selectedSizeRangeList.clear()
        _selectedSizeRangeList.addAll(rangeList)
    }
}

data class AggregatedVisObjectDataParams(
        var binNums: Int = 5,
        var countThreshold: Int = 10,
        var selectedImportanceRange: Pair<Double, Double> = Pair(0.0, 1.0),

        var keywordGroupMap: Map<String, MutableList<String>> = emptyMap(),
        var tSaturation: Long = -1L, //3hrs
        var additional: Map<String, Any> = emptyMap()
){
    val givenLifeList: List<EnhancedNotificationLife> = EnhancedNotificationLife.values().toList()
    val keywordGroups: List<String>
        get() = keywordGroupMap.keys.toList()
    val givenImportanceRange: Pair<Double, Double> = Pair(0.0, 1.0)
    val selectedImportanceRangeList: List<Pair<Double, Double>>
        get() = MapFunctionUtilities.bin(selectedImportanceRange, binNums)
}
data class AggregatedVisObjectAnimParams(
        var property: Property<View, Float>,
        var values: Array<Float>,
        var duration: Long,
        var interpolator: TimeInterpolator,
        var sustained: List<EnhancedNotificationLife> = listOf(
                EnhancedNotificationLife.JUST_TRIGGERED,
                EnhancedNotificationLife.TRIGGERED_NOT_INTERACTED,
                EnhancedNotificationLife.JUST_INTERACTED,
                EnhancedNotificationLife.INTERACTED_NOT_DECAYING,
                EnhancedNotificationLife.DECAYING
        ),
        var repeatCount: Int = ObjectAnimator.INFINITE,
        var repeatMode: Int = ObjectAnimator.REVERSE
){
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

data class AggregatedVisEffectVisParams(
        var groupNumber: Int = 5,
        var contentGroupMap: Map<String, List<String>> = emptyMap()
)

data class NotificationFilteringParams(
        var filterImportanceConfig: Map<WGBFilterVar, Any> = mapOf(
                WGBFilterVar.ACTIVE to true,
                WGBFilterVar.WHITE_COND to 0.5,
                WGBFilterVar.BLACK_COND to 0.0
        ),
        var filterObservationWindowConfig: Map<WGBFilterVar, Any> = mapOf(
                WGBFilterVar.ACTIVE to true,
                WGBFilterVar.WHITE_COND to 60 * 60 * 1000L,
                WGBFilterVar.BLACK_COND to 6 * 60 * 60 * 1000L
        ),
        var filterChannelConfig: Map<WGBFilterVar, Any> = mapOf(
                WGBFilterVar.ACTIVE to false,
                WGBFilterVar.WHITE_COND to setOf<NotiHierarchy>(),
                WGBFilterVar.BLACK_COND to setOf<NotiHierarchy>()
        ),
        var filterKeywordConfig: Map<WGBFilterVar, Any> = mapOf(
                WGBFilterVar.ACTIVE to true,
                WGBFilterVar.WHITE_COND to setOf<String>(),
                WGBFilterVar.BLACK_COND to setOf<String>()
        ),
        var maxNumOfIndependentNotifications: Int = 3

)

data class KeywordGroupImportance(
        val group: String,
        val keywords: MutableSet<String>,
        var rank: Int = -1,
        var type: String = VisDataManager.DEFAULT_PATTERN,
        var enhancementParam: NotificationEnhacementParams
){
    companion object{
        private var currentLastID: Long = 0L
        private fun assignID(): Long =
                if(currentLastID < Long.MAX_VALUE) { currentLastID++ }
                else {
                    currentLastID = 0
                    currentLastID
                }
    }

    val id: Long = assignID()
}

class KeywordGroupImportancePatterns(
       keywordGroupPatterns: Map<String, Pair<Set<String>, String>>,
       elseKeywordGroupPattern: String = VisDataManager.DEFAULT_PATTERN
){
    companion object{
        const val ELSE_KEYWORD_GROUP = "Remainder"
    }

    private val keywordGroupPatterns: MutableList<KeywordGroupImportance> = mutableListOf()
    private var elsePattern: KeywordGroupImportance = KeywordGroupImportance(
            group = ELSE_KEYWORD_GROUP,
            rank = Int.MAX_VALUE,
            keywords = mutableSetOf(),
            type = elseKeywordGroupPattern,
            enhancementParam = VisDataManager.getExampleSaturationPattern(elseKeywordGroupPattern)!!
    )

    init{
        keywordGroupPatterns.toList().forEachIndexed { index, pair ->
            this.keywordGroupPatterns.add(
                    KeywordGroupImportance(
                            group = pair.first,
                            rank = index,
                            keywords = pair.second.first.toMutableSet(),
                            type = pair.second.second,
                            enhancementParam = VisDataManager.getExampleSaturationPattern(pair.second.second)!!
                    )
            )
        }
    }

    fun getOrderedKeywordGroupImportancePatterns(): List<KeywordGroupImportance> = keywordGroupPatterns
    fun getOrderedKeywordGroupImportancePatternsWithRemainder(): List<KeywordGroupImportance> = List(keywordGroupPatterns.size + 1){
        if(it == keywordGroupPatterns.size) elsePattern else keywordGroupPatterns[it]
    }

    fun getOrderedKeywordGroups(): List<String> = keywordGroupPatterns.map{it.group}

    fun getRemainderKeywordGroupPattern(): KeywordGroupImportance = elsePattern
    fun setRemainderKeywordGroupEnhancementParams(param: NotificationEnhacementParams) {
        elsePattern.type = VisDataManager.CUSTOM_PATTERN
        elsePattern.enhancementParam = param
    }

    fun setRemainderKeywordGroupEnhancementParams(type: String) {
        elsePattern.type = type
        elsePattern.enhancementParam = VisDataManager.getExampleSaturationPattern(type)!!
    }

    fun addKeywordGroup(
            group: String,
            keywords: Set<String> = emptySet(),
            rank: Int = keywordGroupPatterns.size){
        addKeywordGroup(group, keywords, rank, VisDataManager.DEFAULT_PATTERN, VisDataManager.getExampleSaturationPattern(VisDataManager.DEFAULT_PATTERN)!!)
    }

    fun addKeywordGroup(
            group: String,
            keywords: Set<String> = emptySet(),
            rank: Int = keywordGroupPatterns.size,
            type: String){
        addKeywordGroup(group, keywords, rank, type, VisDataManager.getExampleSaturationPattern(type)!!)
    }

    fun addKeywordGroup(
            group: String,
            keywords: Set<String> = emptySet(),
            rank: Int = keywordGroupPatterns.size,
            type: String,
            enhancementParam: NotificationEnhacementParams) {

        if(group !in keywordGroupPatterns.map{it.group}){
            keywordGroupPatterns.add(
                    KeywordGroupImportance(
                            group = group,
                            rank = rank,
                            keywords = keywords.toMutableSet(),
                            type = type,
                            enhancementParam = enhancementParam
                    )
            )
            keywordGroupPatterns.sortBy{it.rank}
        }
    }

    fun deleteKeywordGroup(group: String){
        keywordGroupPatterns.find{it.group == group}?.let{ item ->
            keywordGroupPatterns.remove(item)
        }
        keywordGroupPatterns.forEachIndexed{index, keywordGroupImportance ->
            keywordGroupImportance.rank = index
        }
        elsePattern.rank = keywordGroupPatterns.size
    }

    fun swapRankOfGroup(rank1: Int, rank2: Int){
        val el1 = getGroupOfRank(rank1)
        val el2 = getGroupOfRank(rank2)
        el2?.rank = rank1
        el1?.rank = rank2
        keywordGroupPatterns.sortBy{it.rank}
    }

    fun changeRankOfGroup(group: String, changedRank: Int){
        keywordGroupPatterns.find{it.group == group}?.let{ itemToChangeRank ->
            val prevRank = itemToChangeRank.rank

            if(changedRank > prevRank){
                //prev -> changed rank가 내려감 prevRank 1, changedRank 3이면 ->  0 1 2 3 4 5 -> 0 2 3 1 4 5
                keywordGroupPatterns.filter{it.rank in (prevRank + 1)..changedRank}.forEach{it.rank--}
            }
            else if(changedRank < prevRank){
                //prevRank 3, changedRank 1이면 ->  0 1 2 3 4 5 -> 0 3 1 2 4 5
                keywordGroupPatterns.filter{it.rank in changedRank until prevRank}.forEach{it.rank++}
            }
            else{}
            itemToChangeRank.rank = changedRank
            keywordGroupPatterns.sortBy{it.rank}
        }
    }

    fun getGroupOfRank(rank: Int): KeywordGroupImportance? = keywordGroupPatterns.find{it.rank == rank}

    fun getKeywordsOfGroup(group: String): Set<String>? = keywordGroupPatterns.find{it.group == group}?.keywords
    fun addKeywordToGroup(group: String, keyword: String){
        keywordGroupPatterns.find{it.group == group}?.keywords?.add(keyword)
    }
    fun deleteKeywordInGroup(group: String, keyword: String){
        keywordGroupPatterns.find{it.group == group}?.keywords?.remove(keyword)
    }

    fun getEnhancementParamOfGroup(group: String): NotificationEnhacementParams? = keywordGroupPatterns.find{it.group == group}?.enhancementParam

    fun setEnhancementParamOfGroup(group: String, params: NotificationEnhacementParams){
        keywordGroupPatterns.find{it.group == group}?.let{
            it.type = VisDataManager.CUSTOM_PATTERN
            it.enhancementParam = params
        }
    }
    fun setEnhancementParamOfGroup(group: String, type: String){
        keywordGroupPatterns.find{it.group == group}?.let{
            it.type = type
            it.enhancementParam = VisDataManager.getExampleSaturationPattern(type)!!
        }
    }

    fun assignGroupToNotification(title: String, content: String): String{
        //rank가 위일 수록 먼저 할당될 가능성이 있음
        keywordGroupPatterns.forEach{ item ->
            item.keywords.forEach{ keyword ->
                if(title.contains(keyword) || content.contains(keyword)){return item.group}
            }
        }
        return ELSE_KEYWORD_GROUP
    }
}

data class NotificationEnhacementParams(
        var initialImportance: Double = 0.5,
        var lifespan: Long = 1000L * 60 * 60 * 6,
        var importanceRange: Pair<Double, Double> = Pair(0.0, 1.0),
        var firstPattern: EnhancementPattern = EnhancementPattern.EQ,
        var secondPattern: EnhancementPattern = EnhancementPattern.EQ,
        var firstSaturationTime: Long = (lifespan * 0.5).roundToLong(),
        var secondSaturationTime: Long= (lifespan * 0.5).roundToLong()
){
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}