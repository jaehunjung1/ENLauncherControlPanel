package kr.ac.snu.hcil.datahalo.visualEffects.examples

import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.datahalo.visualEffects.AbstractIndependentVisEffect
import kr.ac.snu.hcil.datahalo.visualEffects.IndependentVisObject

class DoubleObjIndependentVisEffect(appHaloConfig: AppHaloConfig)
    : AbstractIndependentVisEffect(
        effectID = "DoubleVisObjIndependentEffect",
        independentVisObjects = listOf(
                IndependentVisObject(
                        visualMapping = appHaloConfig.independentVisualMappings[0],
                        dataParameters = appHaloConfig.independentDataParameters[0],
                        visualParameters = appHaloConfig.independentVisualParameters[0],
                        animationParameters = appHaloConfig.independentAnimationParameters[0]
                ),
                IndependentVisObject(
                        visualMapping = appHaloConfig.independentVisualMappings[1],
                        dataParameters = appHaloConfig.independentDataParameters[1],
                        visualParameters = appHaloConfig.independentVisualParameters[1],
                        animationParameters = appHaloConfig.independentAnimationParameters[1]
                )
        ),
        visualParameters = appHaloConfig.independentVisEffectVisParams[0]
)