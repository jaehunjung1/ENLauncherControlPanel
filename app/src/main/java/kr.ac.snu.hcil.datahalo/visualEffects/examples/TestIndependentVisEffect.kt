package kr.ac.snu.hcil.datahalo.visualEffects.examples

import androidx.constraintlayout.widget.ConstraintLayout
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.datahalo.visualEffects.AbstractIndependentVisEffect
import kr.ac.snu.hcil.datahalo.visualEffects.HeightOnlyIndependentObject
import kr.ac.snu.hcil.datahalo.visualEffects.IndependentVisObject

class TestIndependentVisEffect(appHaloConfig: AppHaloConfig)
    : AbstractIndependentVisEffect(
        effectID = "TestIndependentEffect",
        independentVisObjects = listOf(
                // TODO change this to testVisObject (height-only change)
                HeightOnlyIndependentObject(
                        visualMapping = appHaloConfig.independentVisualMappings[0],
                        importanceEnhancementPatterns = appHaloConfig.keywordGroupPatterns,
                        dataParameters = appHaloConfig.independentDataParameters[0],
                        visualParameters = appHaloConfig.independentVisualParameters[0],
                        animationParameters = appHaloConfig.independentAnimationParameters[0]
                )
        ),
        visualParameters = appHaloConfig.independentVisEffectVisParams
)