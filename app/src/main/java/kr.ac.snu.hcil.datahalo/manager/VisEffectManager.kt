package kr.ac.snu.hcil.datahalo.manager

import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
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
                = mapOf("example" to "ExampleAggregatedVisEffect")

        val availableIndependentVisEffects: List<String>
            get() = registeredIndependentVisEffects.keys.toList()

        val availableAggregatedVisEffects: List<String> = listOf("example")

        fun createNewIndependentVisEffect(visEffectID: String, visConfig: AppHaloConfig): AbstractIndependentVisEffect{
            if(visEffectID in availableIndependentVisEffects){
                return Class.forName("$PATH.${registeredIndependentVisEffects[visEffectID]}").getConstructor(AppHaloConfig::class.java).newInstance(visConfig)
                        as AbstractIndependentVisEffect
            }
            else{
                throw exceptionVisEffectNotMatched(visEffectID)
            }
        }

        fun createNewIndependentVisEffect(visEffectID: String, visObjects: List<AbstractIndependentVisObject>){
            TODO()
        }


        /*
        fun createIndependentVisEffects(data: List<EnhancedNotification>)
                : Map<Int, AbstractVisEffect>{
            val examplePalette = Palette.from(
                    Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888).also{
                        it.eraseColor(Color.CYAN)
                    }).generateVisObjectUID()

            return mapOf(1 to
                object: AbstractVisEffect(examplePalette, null, mapOf(), mapOf()) {
                    override fun drawVisualEffect(data: VisObject, canvas: Canvas) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                }
            )
        }


        private fun createAggregatedVisEffect(data: List<EnhancedNotification>): Pair<List<Int>, AbstractVisEffect?>{
            return Pair(listOf(), null)
        }
        */

    }
}