package kr.ac.snu.hcil.datahalo.utils

import kr.ac.snu.hcil.datahalo.notificationdata.EnhancementPattern
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotification
import java.util.*

class NotificationRandomGenerator{
    companion object Factory {
        fun newRandomNotification(id: Int, initTime: Long, naturalDecay: Long): EnhancedNotification {
            val firsttrend = Random().nextInt() % 3
            val secondtrend = Random().nextInt() % 3

            return EnhancedNotification(
                id,
                "default",
                initTime,
                naturalDecay
            ).apply{
                when(firsttrend){
                    0 -> {
                        firstPattern = EnhancementPattern.INC
                    }
                    1 -> {
                        firstPattern = EnhancementPattern.DEC
                        enhanceOffset = 1.0
                        currEnhancement = enhanceOffset
                    }
                    2 -> {
                        firstPattern = EnhancementPattern.EQ
                        enhanceOffset = 0.5
                        currEnhancement = enhanceOffset
                    }
                }

                when(secondtrend){
                    0 -> {firstPattern = EnhancementPattern.INC}
                    1 -> {firstPattern = EnhancementPattern.DEC}
                    2 -> {firstPattern = EnhancementPattern.EQ}
                }
            }
        }
    }
}