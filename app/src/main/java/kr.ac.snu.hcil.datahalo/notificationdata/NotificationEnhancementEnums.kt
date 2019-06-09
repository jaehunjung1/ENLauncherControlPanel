package kr.ac.snu.hcil.datahalo.notificationdata

enum class EnhancedNotificationLife{
    STATE_1_JUST_TRIGGERED, //Just Triggered
    STATE_2_TRIGGERED_NOT_INTERACTED, //Triggered but Not Interacted
    STATE_3_JUST_INTERACTED, //Just Interacted
    STATE_4_INTERACTED_NOT_DECAYED, //Interacted Not Decaying
    STATE_5_DECAYING, // Decaying
}

enum class EnhancementPattern{
    EQ, INC, DEC
}