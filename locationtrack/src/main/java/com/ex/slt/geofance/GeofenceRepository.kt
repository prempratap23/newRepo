package com.ex.slt.geofance

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class GeofenceRepository(val context: Context) : ContextWrapper(context) {

    private val geofencePendingIntent: PendingIntent
        get() {
            val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
            return PendingIntent.getBroadcast(
                context,
               1110,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }


    private fun buildGeofence(Id: String, location: LatLng, redius: Float, trasType: Int): Geofence {
        Log.e("Tag",Id)
        return Geofence.Builder()
            .setCircularRegion(location.latitude, location.longitude, redius)
            .setRequestId(Id)
            .setTransitionTypes(trasType)
            .setLoiteringDelay(5000)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()

    }

    private fun buildGeofencingRequest(geofence: Geofence): GeofencingRequest {
        Log.e("Tag","buildGeofencingRequest")
        return GeofencingRequest.Builder()
            .addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT)
            .build()
        Log.e("Tag","Trigger")
    }

    fun add(latLng: LatLng, redius: Float) {

        var geofance = buildGeofence("Sanjay", latLng, redius,  Geofence.GEOFENCE_TRANSITION_EXIT)
        var geoReq = buildGeofencingRequest(geofance)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        Log.e("Tag","Trigger")
        var geofencingClient = LocationServices.getGeofencingClient(context)

        geofencingClient.addGeofences(geoReq,geofencePendingIntent).addOnSuccessListener {

            Log.e("Tag","Success")

        }.addOnFailureListener {
            Log.e("Tag","EDX")

            var apicode = it as ApiException
            when(apicode.statusCode)
            {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE->
                {
                    Log.e("Tag","GEOFENCE_NOT_AVAILABLE")
                }
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES->
                {
                    Log.e("Tag","GEOFENCE_TOO_MANY_GEOFENCES")
                }
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS->
                {
                    Log.e("Tag","GEOFENCE_TOO_MANY_PENDING_INTENTS")
                }
            }

        }

     }

}