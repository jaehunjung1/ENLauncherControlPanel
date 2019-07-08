package kr.ac.snu.hcil.datahalo.notificationdata

enum class EnhancedNotificationLife{
    JUST_TRIGGERED, //Just Triggered
    TRIGGERED_NOT_INTERACTED, //Triggered but Not Interacted
    JUST_INTERACTED, //Just Interacted
    INTERACTED_NOT_DECAYING, //Interacted Not Decaying
    DECAYING, // Decaying
}

enum class EnhancementPattern{
    EQ,
    INC,
    DEC
}