package com.cylin.clutils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.collections.ArrayList

class GeoUtils {

    companion object {
        private val TAG = GeoUtils::class.java.simpleName

        private var locationManager : LocationManager? = null

        enum class GeoType(val key: String) {
            GPS(LocationManager.GPS_PROVIDER),
            NETWORK(LocationManager.NETWORK_PROVIDER),
        }

        fun setLocationUpdate(@NonNull context: Context, type: GeoType, locationListener: LocationListener) {
            if (locationManager == null) {
                locationManager = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager?
            }

            try {
                // Request location updates
                locationManager?.requestLocationUpdates(type.key, 0L, 0f, locationListener)
            } catch (ex: SecurityException) {
                android.util.Log.d(TAG, ">> Security Exception, no location available.\n ${ex.message}")
            }
        }

        fun removeUpdate(@NonNull locationListener: LocationListener) {
            locationManager?.removeUpdates(locationListener)
        }

        fun geo2Address(context: Context, lat: Double, lon: Double): List<Address> {
            var addresses: List<Address> = ArrayList()
            val geocoder = Geocoder(context, Locale.getDefault())

            android.util.Log.d(TAG, ">> lat: $lat, lon: $lon")
            try {
                addresses = geocoder.getFromLocation(lat, lon, 1)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                return addresses
            }
        }

        fun address2Geo(context: Context, address: String, maxResults: Int): List<Address> {
            val coder = Geocoder(context)
            return coder.getFromLocationName(address, maxResults)
        }

        fun convertPostcode2Three(postcode: String): String {
            return if (postcode.length > 3) {
                postcode.substring(0, 3)
            } else {
                postcode
            }
        }
    }
}