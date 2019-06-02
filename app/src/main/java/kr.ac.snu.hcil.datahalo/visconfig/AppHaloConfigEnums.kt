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

enum class NotiProperty(value: String){
    IMPORTANCE("importance"),
    LIFE_STAGE("life_stage"),
    NOTIFICATION_CHANNEL("notification_channel"),
    ELAPSED_TIME("elapsed_time"),
    CONTENT("notification_content")
}

enum class AggregatedNotiProperty(value: String){
    AGG_IMPORTANCE("importance"),
    AGG_LIFE_STAGE("life_stage"),
    AGG_NOTIFICATION_CHANNEL("notification_channel"),
    AGG_ELAPSED_TIME("elapsed_time"),
    AGG_CONTENT("notification_content"),
    COUNT("notification_count")
}

enum class NotiVisVariable{
    POSITION_X,
    POSITION_Y,
    SHAPE,
    SIZE_X,
    SIZE_Y,
    COLOR,
    MOTION
}

enum class NotiAggregationType{
    MEAN_NUMERIC,
    MAX_NUMERIC,
    MIN_NUMERIC,
    MOST_FREQUENT_NOMINAL,
    COUNT,
}

enum class NuNotiVisVariable{
    POSITION,
    SHAPE,
    SIZE,
    COLOR,
    MOTION,
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

enum class BinQuartile(ratio: Double){
    FIRST_QUARTILE(0.0),
    SECOND_QUARTILE(0.25),
    THIRD_QUARTILE(0.5),
    FOURTH_QUARTILE(0.75),
    OVER_FIFTH_QUARTILE(1.0)
}



