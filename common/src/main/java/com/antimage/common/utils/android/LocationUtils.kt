package com.antimage.common.utils.android

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.TextUtils
import androidx.core.app.ActivityCompat

/**
 * Created by xuyuming on 2019-08-28
 */
object LocationUtils {

    private val REFRESH_TIME = 5000L
    private val METER_POSITION = 0.0f
    private var mLocationListener: ILocationListener? = null
    private var listener: LocationListener? = MyLocationListener()

    private class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {//定位改变监听
            if (mLocationListener != null) {
                mLocationListener!!.onSuccessLocation(location)
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {//定位状态监听

        }

        override fun onProviderEnabled(provider: String) {//定位状态可用监听

        }

        override fun onProviderDisabled(provider: String) {//定位状态不可用监听

        }
    }


    /**
     * GPS获取定位方式
     */
    fun getGPSLocation(context: Context): Location? {
        var location: Location? = null
        val manager = getLocationManager(context)
        //高版本的权限检查
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//是否支持GPS定位
            //获取最后的GPS定位信息，如果是第一次打开，一般会拿不到定位信息，一般可以请求监听，在有效的时间范围可以获取定位信息
            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        return location
    }

    /**
     * network获取定位方式
     */
    fun getNetWorkLocation(context: Context): Location? {
        var location: Location? = null
        val manager = getLocationManager(context)
        //高版本的权限检查
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }
        if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {//是否支持Network定位
            //获取最后的network定位信息
            location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        return location
    }


    /**
     * 获取最好的定位方式
     */
    fun getBestLocation(context: Context, criteria: Criteria?): Location? {
        var criteria = criteria
        val location: Location?
        val manager = getLocationManager(context)
        if (criteria == null) {
            criteria = Criteria()
        }
        val provider = manager.getBestProvider(criteria, true)
        if (TextUtils.isEmpty(provider)) {
            //如果找不到最适合的定位，使用network定位
            location = getNetWorkLocation(context)
        } else {
            //高版本的权限检查
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                return null
            }
            //获取最适合的定位方式的最后的定位权限
            location = manager.getLastKnownLocation(provider)
        }
        return location
    }


    /**
     * 定位监听
     */
    fun addLocationListener(context: Context, provider: String, locationListener: ILocationListener) {

        addLocationListener(context, provider, REFRESH_TIME, METER_POSITION, locationListener)
    }

    /**
     * 定位监听
     */
    fun addLocationListener(
        context: Context,
        provider: String,
        time: Long,
        meter: Float,
        locationListener: ILocationListener?
    ) {
        if (locationListener != null) {
            mLocationListener = locationListener
        }
        if (listener == null) {
            listener = MyLocationListener()
        }
        val manager = getLocationManager(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        manager.requestLocationUpdates(provider, time, meter, listener)
    }


    /**
     * 取消定位监听
     */
    fun unRegisterListener(context: Context) {
        if (listener != null) {
            val manager = getLocationManager(context)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            //移除定位监听
            manager.removeUpdates(listener)
        }
    }

    private fun getLocationManager(context: Context): LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    /**
     * 自定义接口
     */
    interface ILocationListener {
        fun onSuccessLocation(location: Location)
    }
}