package kr.ac.snu.hcil.datahalo.manager

import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedAppNotifications
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotification
import kr.ac.snu.hcil.datahalo.notificationdata.NotiHierarchy
import kr.ac.snu.hcil.datahalo.utils.WGBFilterManager
import kr.ac.snu.hcil.datahalo.visconfig.*

class VisDataConverter {
    companion object{

        private fun constructFilters(visConfig: AppHaloConfig): Map<Options, WGBFilterManager.WGBFilter<out Any>>{
            return mapOf(
                    Options.IMPORTANCE to WGBFilterManager.buildCustomRangeFilter(
                            visConfig.filterImportanceConfig[WGBFilterVar.ACTIVE] as Boolean,
                            visConfig.filterImportanceConfig[WGBFilterVar.WHITE_COND] as Double,
                            visConfig.filterImportanceConfig[WGBFilterVar.BLACK_COND] as Double
                    ),
                    Options.OBSERVATION_WINDOW to WGBFilterManager.buildCustomRangeFilter(
                            visConfig.filterObservationWindowConfig[WGBFilterVar.ACTIVE] as Boolean,
                            visConfig.filterObservationWindowConfig[WGBFilterVar.WHITE_COND] as Long,
                            visConfig.filterObservationWindowConfig[WGBFilterVar.BLACK_COND] as Long
                    ) {
                        ref, white, black ->
                        when {
                            ref <= white -> Response.WHITE
                            ref > black -> Response.BLACK
                            else -> Response.GRAY
                        }
                    },
                    Options.CHANNEL to WGBFilterManager.buildCustomSetFilter(
                            visConfig.filterChannelConfig[WGBFilterVar.ACTIVE] as Boolean,
                            visConfig.filterChannelConfig[WGBFilterVar.WHITE_COND] as Set<NotiHierarchy>,
                            visConfig.filterChannelConfig[WGBFilterVar.BLACK_COND] as Set<NotiHierarchy>
                    ),
                    Options.KEYWORD to WGBFilterManager.buildCustomSetFilter(
                            visConfig.filterKeywordConfig[WGBFilterVar.ACTIVE] as Boolean,
                            visConfig.filterKeywordConfig[WGBFilterVar.WHITE_COND] as Set<String>,
                            visConfig.filterKeywordConfig[WGBFilterVar.BLACK_COND] as Set<String>
                    )
            )
        }

        fun convert(data: EnhancedAppNotifications, visConfig: AppHaloConfig): Map<NotificationType, List<EnhancedNotification>>{
            val optionFilters = constructFilters(visConfig)
            val sampleMax = visConfig.maxNumOfIndependentNotifications
            return filterAndCategorizeEnhancedNotifications(data, optionFilters, sampleMax)
        }

        private fun filterAndCategorizeEnhancedNotifications(
                data: EnhancedAppNotifications,
                optionFilters: Map<Options, WGBFilterManager.WGBFilter<out Any>>,
                sampleMax:Int
        ): Map<NotificationType, List<EnhancedNotification>> {
            val activatedFilters = optionFilters.filter { optionFilter -> optionFilter.value.enabled }

            //TODO(whiteAndGray 확인)
            //Filter Black Out
            var whiteAndGray:List<EnhancedNotification> = data.notificationData
            activatedFilters.forEach{
                optionFilter ->
                val option = optionFilter.key
                val wgbFilter = optionFilter.value
                when(option){
                    Options.IMPORTANCE ->{
                        whiteAndGray = whiteAndGray.filterNot{
                            notiData ->
                            (wgbFilter as WGBFilterManager.WGBFilter<Double>).determineCondition(notiData.currEnhancement) == Response.BLACK
                        }
                    }
                    Options.OBSERVATION_WINDOW -> {
                        whiteAndGray = whiteAndGray.filterNot{
                            notiData ->
                            (wgbFilter as WGBFilterManager.WGBFilter<Long>).determineCondition(System.currentTimeMillis() - notiData.initTime) == Response.BLACK
                        }
                    }
                    Options.KEYWORD -> {
                        val blackKeywords = (wgbFilter as WGBFilterManager.WGBSetFilter<String>).blackCondition
                        whiteAndGray = whiteAndGray.filterNot{ notiData ->
                            blackKeywords.fold(false){ acc: Boolean, keyword: String ->
                                val result = acc || (notiData.notiContent.title.contains(keyword) || notiData.notiContent.content.contains(keyword))
                                result
                            }
                        }
                    }
                    Options.CHANNEL -> {
                        val blackChannels = (wgbFilter as WGBFilterManager.WGBSetFilter<NotiHierarchy>).blackCondition
                        whiteAndGray = whiteAndGray.filterNot{ notiData ->
                            blackChannels.contains(notiData.channelHiearchy)
                        }
                    }
                }
            }

            //Calculate Whiteness
            activatedFilters.forEach{
                optionFilter ->
                val option = optionFilter.key
                val wgbFilter = optionFilter.value
                when(option){
                    Options.IMPORTANCE ->{
                        whiteAndGray.filter{
                            notiData ->
                            (wgbFilter as WGBFilterManager.WGBFilter<Double>).determineCondition(notiData.currEnhancement) == Response.WHITE
                        }.map{
                            notiData -> notiData.whiteRank += 1
                        }
                    }
                    Options.OBSERVATION_WINDOW ->{
                        whiteAndGray.filter{
                            notiData ->
                            (wgbFilter as WGBFilterManager.WGBFilter<Long>).determineCondition(System.currentTimeMillis() - notiData.initTime) == Response.WHITE
                        }.map{
                            notiData -> notiData.whiteRank += 1
                        }
                    }
                    Options.KEYWORD -> {
                        val whiteKeywords = (wgbFilter as WGBFilterManager.WGBSetFilter<String>).whiteCondition
                        whiteAndGray.map{ notiData ->
                            notiData.whiteRank += whiteKeywords.fold(0){ acc: Int, keyword: String ->
                                if(notiData.notiContent.title.contains(keyword) || notiData.notiContent.content.contains(keyword)) acc + 1 else acc }
                        }
                    }
                    Options.CHANNEL -> {
                        val whiteChannels = (wgbFilter as WGBFilterManager.WGBSetFilter<NotiHierarchy>).whiteCondition
                        whiteAndGray.map{ notiData ->
                            notiData.whiteRank += whiteChannels.fold(0){ acc: Int, notiHierarchy: NotiHierarchy ->
                                if(notiHierarchy.equals(notiData.channelHiearchy)) acc + 1 else acc }
                        }
                    }
                }
            }

            val fromWhiteToGray = whiteAndGray.sortedWith(compareBy({it.whiteRank}, {it.currEnhancement}, {it.initTime}))
            val sampleCount = if(fromWhiteToGray.size > sampleMax) sampleMax else fromWhiteToGray.size
            val independentNotis = if(fromWhiteToGray.isNotEmpty()) fromWhiteToGray.subList(0, sampleCount - 1) else emptyList()
            val aggregatedNotis = if(fromWhiteToGray.size > sampleCount) fromWhiteToGray.subList(sampleCount, fromWhiteToGray.size - 1) else emptyList()

            return mapOf(
                    NotificationType.INDEPENDENT to independentNotis,
                    NotificationType.AGGREGATED to aggregatedNotis
            )
        }

    }

}