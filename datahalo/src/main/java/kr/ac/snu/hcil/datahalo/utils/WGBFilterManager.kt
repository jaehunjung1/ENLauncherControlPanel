package kr.ac.snu.hcil.datahalo.utils

import kr.ac.snu.hcil.datahalo.visconfig.Response


class WGBFilterManager{
    companion object{

        inline fun <reified T:Comparable<T>> buildCustomRangeFilter(
                enabled: Boolean,
                whiteCondition: T,
                blackCondition: T): WGBRangeFilter<T> {
            return object: WGBRangeFilter<T>(enabled, whiteCondition, blackCondition){}
        }

        inline fun <reified T:Comparable<T>> buildCustomRangeFilter(
                enabled: Boolean,
                whiteCondition: T,
                blackCondition: T,
                crossinline determination: (T, T, T) -> Response): WGBRangeFilter<T> {
            return object: WGBRangeFilter<T>(enabled, whiteCondition, blackCondition){
                override fun determineCondition(ref: T): Response {
                    return determination(ref, this.whiteCondition, this.blackCondition)
                }
            }
        }

        inline fun <reified T> buildCustomSetFilter(
                enabled: Boolean,
                whiteCondition: Set<T>,
                blackCondition: Set<T>): WGBSetFilter<T> {
            return object: WGBSetFilter<T>(enabled, whiteCondition, blackCondition){}
        }

        inline fun <reified T> buildCustomSetFilter(
                enabled: Boolean,
                whiteCondition: Set<T>,
                blackCondition: Set<T>,
                crossinline determination: (T, Set<T>, Set<T>) -> Response): WGBSetFilter<T> {
            return object: WGBSetFilter<T>(enabled, whiteCondition, blackCondition){
                override fun determineCondition(ref: T): Response {
                    return determination(ref, this.whiteCondition, this.blackCondition)
                }
            }
        }
    }

    interface WGBFilter<T>{
        var enabled: Boolean
        fun determineCondition(ref: T): Response
    }

    abstract class WGBRangeFilter<T>(
            override var enabled: Boolean,
            open var whiteCondition: T,
            open var blackCondition: T): WGBFilter<T>
    where T: Comparable<T>{

        override fun determineCondition(ref: T): Response {
            return when{
                ref >= whiteCondition -> Response.WHITE
                ref < blackCondition -> Response.BLACK
                else -> Response.GRAY
            }
        }
    }

    abstract class WGBSetFilter<T>(
            override var enabled: Boolean,
            open var whiteCondition: Set<T>,
            open var blackCondition: Set<T>): WGBFilter<T> {

        override fun determineCondition(ref: T): Response {
            return when(ref) {
                in whiteCondition -> Response.WHITE
                in blackCondition -> Response.BLACK
                else -> Response.GRAY
            }
        }
    }
}