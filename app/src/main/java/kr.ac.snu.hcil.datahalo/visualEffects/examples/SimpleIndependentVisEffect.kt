package kr.ac.snu.hcil.datahalo.visualEffects.examples

import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.datahalo.visualEffects.AbstractIndependentVisEffect
import kr.ac.snu.hcil.datahalo.visualEffects.IndependentVisObject


class SimpleIndependentVisEffect(appHaloConfig: AppHaloConfig)
    : AbstractIndependentVisEffect(
        effectID = "SingleVisObjIndependentEffect",
        independentVisObjects = listOf(
                IndependentVisObject(
                        visualMapping = appHaloConfig.independentVisualMappings[0],
                        dataParameters = appHaloConfig.independentDataParameters[0],
                        visualParameters = appHaloConfig.independentVisualParameters[0],
                        animationParameters = appHaloConfig.independentAnimationParameters[0]
                )
        ),
        visualParameters = appHaloConfig.independentVisEffectVisParams[0]
)