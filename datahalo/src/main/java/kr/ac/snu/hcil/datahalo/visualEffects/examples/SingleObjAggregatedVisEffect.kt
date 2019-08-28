package kr.ac.snu.hcil.datahalo.visualEffects.examples

import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.datahalo.visualEffects.AbstractAggregatedVisEffect

class SingleObjAggregatedVisEffect(appHaloConfig: AppHaloConfig)
    : AbstractAggregatedVisEffect(
        effectID = "SingleObjAggregatedVisEffect",
        mappingRules = appHaloConfig.aggregatedVisualMappings,
        effectParameters = appHaloConfig.aggregatedVisEffectVisParams,
        objVisualParameters = appHaloConfig.aggregatedVisualParameters,
        objDataParameters = appHaloConfig.aggregatedDataParameters,
        objAnimationParameters = appHaloConfig.aggregatedAnimationParameters,
        keywordGroupImportancePatterns = appHaloConfig.keywordGroupPatterns
)
