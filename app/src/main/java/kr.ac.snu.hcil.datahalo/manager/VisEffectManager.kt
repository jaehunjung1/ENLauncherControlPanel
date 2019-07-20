package kr.ac.snu.hcil.datahalo.manager

import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.datahalo.visualEffects.AbstractAggregatedVisEffect
import kr.ac.snu.hcil.datahalo.visualEffects.AbstractIndependentVisEffect
import kr.ac.snu.hcil.datahalo.visualEffects.AbstractIndependentVisObject

class VisEffectManager {
    companion object {
        private val PATH = "kr.ac.snu.hcil.datahalo.visualEffects.examples"
        private fun exceptionVisEffectNotMatched(effectName: String) = Exception("$effectName Does Not Exist")

        private val registeredIndependentVisEffects:Map<String, String>
                = mapOf(
                "SingleVisObjIndependentEffect" to "SingleObjIndependentVisEffect",
                "DoubleVisObjIndependentEffect" to "DoubleObjIndependentVisEffect"
        )

        private val registeredAggregatedVisEffects: Map<String, String>
                = mapOf(
                "SingleObjAggregatedVisEffect" to "SingleObjAggregatedVisEffect"
        )

        val availableIndependentVisEffects: List<String>
            get() = registeredIndependentVisEffects.keys.toList()

        val availableAggregatedVisEffects: List<String>
            get() = registeredAggregatedVisEffects.keys.toList()

        fun createNewIndependentVisEffect(visEffectID: String, visConfig: AppHaloConfig): AbstractIndependentVisEffect{
            if(visEffectID in availableIndependentVisEffects){
                return Class.forName("$PATH.${registeredIndependentVisEffects[visEffectID]}").getConstructor(AppHaloConfig::class.java).newInstance(visConfig)
                        as AbstractIndependentVisEffect
            }
            else{
                throw exceptionVisEffectNotMatched(visEffectID)
            }
        }

        fun createNewAggregatedVisEffect(visEffectID: String, visConfig: AppHaloConfig): AbstractAggregatedVisEffect{
            if(visEffectID in availableAggregatedVisEffects){
                return Class.forName("$PATH.${registeredAggregatedVisEffects[visEffectID]}").getConstructor(AppHaloConfig::class.java).newInstance(visConfig)
                        as AbstractAggregatedVisEffect
            }
            else{
                throw exceptionVisEffectNotMatched(visEffectID)
            }
        }
    }
}