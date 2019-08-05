package kr.ac.snu.hcil.datahalo.visconfig

enum class Response{
    WHITE,
    GRAY,
    BLACK
}

enum class Options{
    IMPORTANCE,
    OBSERVATION_WINDOW,
    CHANNEL,
    KEYWORD
}

enum class NotificationType{
    INDEPENDENT,
    AGGREGATED
}

enum class NotiProperty(val value: String){
    IMPORTANCE("importance"),
    LIFE_STAGE("life_stage"),
    //NOTIFICATION_CHANNEL("notification_channel"),
    //ELAPSED_TIME("elapsed_time"),
    CONTENT("notification_content")
}

enum class NotiAggregationType{
    MEAN_NUMERIC,
    MAX_NUMERIC,
    MIN_NUMERIC,
    MOST_FREQUENT_NOMINAL,
    COUNT,
}

enum class NotiVisVariable{
    MOTION,
    SHAPE,
    COLOR,
    SIZE,
    POSITION
}

enum class VisVarCustomizability{
    PREDEFINED,
    CUSTOMIZABLE
}

enum class MappingState{
    PREDEFINED,
    BOUND,
    TRANSPARENT
}

enum class WGBFilterVar{
    ACTIVE,
    WHITE_COND,
    BLACK_COND,
}




