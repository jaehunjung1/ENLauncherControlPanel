package kr.ac.snu.hcil.datahalo.ui.viewmodel

import android.app.Application
import android.arch.lifecycle.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Handler
import android.support.v7.graphics.Palette
import android.util.Log
import kr.ac.snu.hcil.datahalo.notificationdata.*
import java.util.*

class HomeScreenDataHaloViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val TAG = "AURA_VIEW_MODEL"
        const val UPDATE_INTERVAL = 1000L * 15
    }

    //LiveData
    private var appNotificationLiveData: MutableLiveData<MutableMap<String, EnhancedAppNotifications>> = MutableLiveData()
    private var currentScreenNumber: MutableLiveData<Int> = MutableLiveData()



    private val mHandler = Handler()
    private var lastUpdateInMillis = Calendar.getInstance().timeInMillis


    private fun updateNotiEnhancement(notiData: EnhancedNotification, updateInterval: Long): EnhancedNotification {
        when(notiData.lifeCycle){
            EnhancedNotificationLife.STATE_2 -> {
                if(notiData.timeElapsed >= notiData.lifeSpan)
                    notiData.lifeCycle = EnhancedNotificationLife.STATE_5
                else {
                    when (notiData.firstPattern) {
                        EnhancementPattern.DEC -> {
                            notiData.currEnhancement -= notiData.slope * updateInterval
                            if (notiData.currEnhancement < notiData.lowerBound)
                                notiData.currEnhancement = notiData.lowerBound
                        }
                        EnhancementPattern.INC -> {
                            notiData.currEnhancement += notiData.slope * updateInterval
                            if (notiData.currEnhancement > notiData.upperBound)
                                notiData.currEnhancement = notiData.upperBound
                        }
                        EnhancementPattern.EQ -> {}
                    }
                }
            }
            EnhancedNotificationLife.STATE_4 -> {
                if(notiData.timeElapsed >= notiData.lifeSpan)
                    notiData.lifeCycle = EnhancedNotificationLife.STATE_5
                else{
                    when(notiData.secondPattern){
                        EnhancementPattern.DEC -> {
                            notiData.currEnhancement -= notiData.slope * updateInterval
                            if(notiData.currEnhancement < notiData.lowerBound)
                                notiData.currEnhancement = notiData.lowerBound
                        }
                        EnhancementPattern.INC -> {
                            notiData.currEnhancement += notiData.slope * updateInterval
                            if(notiData.currEnhancement > notiData.upperBound)
                                notiData.currEnhancement = notiData.upperBound
                        }
                        EnhancementPattern.EQ -> {}
                    }
                }
            }
            EnhancedNotificationLife.STATE_1 -> {
                notiData.lifeCycle = EnhancedNotificationLife.STATE_2
                when(notiData.firstPattern){
                    EnhancementPattern.INC -> notiData.slope = (notiData.upperBound - notiData.enhanceOffset) / notiData.firstSaturationTime
                    EnhancementPattern.DEC -> notiData.slope = (notiData.enhanceOffset - notiData.lowerBound) / notiData.firstSaturationTime
                    EnhancementPattern.EQ -> notiData.slope = 0.0
                }
            }
            EnhancedNotificationLife.STATE_3 -> {
                notiData.lifeCycle = EnhancedNotificationLife.STATE_4
                when(notiData.secondPattern){
                    EnhancementPattern.INC -> notiData.slope = (notiData.upperBound - notiData.currEnhancement) / notiData.secondSaturationTime
                    EnhancementPattern.DEC -> notiData.slope = (notiData.currEnhancement - notiData.lowerBound) / notiData.secondSaturationTime
                    EnhancementPattern.EQ -> notiData.slope = 0.0
                }
            }
            EnhancedNotificationLife.STATE_5 -> {
                notiData.currEnhancement = notiData.lowerBound
            }
        }
        notiData.timeElapsed += updateInterval
        return notiData
    }
    private val autoUpdateRunnable = object: Runnable{
        override fun run() {
            val nowInMillis = System.currentTimeMillis()
            val prevData = appNotificationLiveData.value
            val newData = prevData?.mapValues{
                it.value.apply{
                    notificationData.apply{
                        map { datum -> updateNotiEnhancement(datum, nowInMillis - lastUpdateInMillis) }
                            .apply{ filter{ datum -> datum.lifeCycle != EnhancedNotificationLife.STATE_5 || datum.currEnhancement > 0.0} }
                    }
                }
            }?.toMutableMap()
            lastUpdateInMillis = nowInMillis
            appNotificationLiveData.value = newData
            Log.d(TAG, "ViewModel Updated at $nowInMillis")
            mHandler.postDelayed(this, UPDATE_INTERVAL)
        }
    }

    var paletteMap: MutableMap<String, Palette> = mutableMapOf()
    var drawableMap: MutableMap<String, Drawable> = mutableMapOf()

    private val bWidth = 80
    private val bHeight = 80

    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        val bmp = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return Bitmap.createScaledBitmap(bmp, bWidth, bHeight, false)
    }

    init{
    }

    fun getCurrentScreenNumber():LiveData<Int>{
        return currentScreenNumber
    }

    fun setCurrentScreenNumber(position: Int){
        currentScreenNumber.value = position
    }

    fun getNotificationByApps(): LiveData<MutableMap<String, EnhancedAppNotifications>>{
        return appNotificationLiveData
    }

    fun setNotificationByApps(data: MutableMap<String, EnhancedAppNotifications>){
        appNotificationLiveData.value = data
        mHandler.removeCallbacks(autoUpdateRunnable)
        mHandler.post(autoUpdateRunnable)
    }

    fun setNotificationByApps(data: HashMap<String, EnhancedAppNotifications>){
        appNotificationLiveData.value = data.toMutableMap()
        mHandler.removeCallbacks(autoUpdateRunnable)
        mHandler.post(autoUpdateRunnable)
    }

    fun getEnhancementDataInGivenScreen(position: Int): LiveData<MutableMap<String, EnhancedAppNotifications>> {
        Log.i("Duh", appNotificationLiveData.value?.filter{
            it.value.screenNumber == position
        }?.toMutableMap().toString())
        return Transformations.map(appNotificationLiveData) { enhancementMap ->
            //enhancementMap.forEach { (key, value) ->
            //Log.i("screenNumber", value.packageName+" "+value.screenNumber)
            //}

            enhancementMap.filter {
                it.value.screenNumber == position
            }.toMutableMap()
        }
    }

    override fun onCleared() {
        super.onCleared()
        mHandler.removeCallbacks(autoUpdateRunnable)
    }

}



