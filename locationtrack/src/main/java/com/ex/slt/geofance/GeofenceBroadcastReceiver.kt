package com.ex.slt.geofance

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent


class GeofenceBroadcastReceiver : BroadcastReceiver() {



    override fun onReceive(context: Context?, intent: Intent?) {

        if (context == null) {
            return
        }

        val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: return
        if (geofencingEvent.hasError()) {
            return
        }
        val geofenceTransition = geofencingEvent.geofenceTransition
        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {

            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                val local = Intent()
                local.action = "service.to.activity.transfer"
                local.putExtra("number","Out Side Location" )
                context.sendBroadcast(local)
            }

        }

    }
}