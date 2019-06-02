package kr.ac.snu.hcil.datahalo.notificationdata

enum class EnhancedNotificationLife{
    DEFAULT,
    STATE_1, //Just Triggered
    STATE_2, //Triggered but Not Interacted
    STATE_3, //Just Interacted
    STATE_4, //Interacted Not Decaying
    STATE_5, // Decaying
}

enum class EnhancementPattern{
    EQ, INC, DEC
}