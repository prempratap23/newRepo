package com.e.task.ui.mainscreen

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.e.task.R
import com.e.task.base.BaseActivity
import com.e.task.utils.NotificationHelper
import com.e.task.utils.RequestPermission
import com.ex.slt.geofance.GeofenceRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MainActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    lateinit var geofenceClient: GeofencingClient

    lateinit var currentLocation: Location

    var fusedLocationProviderClient: FusedLocationProviderClient? = null

    var mCircle: Circle? = null

    var radiusInMeters  = 100.0F

    lateinit var gMap :GoogleMap

    private var mMyLocMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        geofenceClient= LocationServices.getGeofencingClient(this)

        setBroadcastReceiver(this)
    }

    @SuppressLint("MissingPermission")
    fun mapInitialize()
    {
        val task = fusedLocationProviderClient!!.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                Log.e("Tag ","Location"+currentLocation.longitude)
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this@MainActivity)

            }
        }


    }


    override fun onStart() {
        super.onStart()
        openRequestPermission()
    }

    private fun openRequestPermission() {
        if (requestPermission!!.checkPermission(RequestPermission.PERMISSION_CALL_PHONE)&&requestPermission!!.checkPermission(RequestPermission.PERMISSION_READ_STORAGE)&&requestPermission!!.checkPermission(
                RequestPermission.PERMISSION_WRITE_STORAGE
            )){
            mapInitialize()

        } else {
            requestPermission!!.permissionRequestShow(
                object : RequestPermission.PermissionCallBack {
                    override fun callBack(
                        grantAllPermission: Boolean,
                        deniedAllPermission: Boolean,
                        permissionResultList: List<Boolean>
                    ) {
                        if (grantAllPermission) {
                            mapInitialize()
                        }
                    }
                },

                RequestPermission.PERMISSION_CALL_PHONE, RequestPermission.PERMISSION_READ_STORAGE, RequestPermission.PERMISSION_WRITE_STORAGE
            )
        }
    }

    var updateUIReciver: BroadcastReceiver? = null

    fun setBroadcastReceiver(context: Context)
    {
        val filter = IntentFilter()
        filter.addAction("service.to.activity.transfer")
        updateUIReciver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null)
                    getData()
            }
        }
        registerReceiver(updateUIReciver, filter)
    }
    @SuppressLint("MissingPermission")
    fun getData() {
        Log.e("Tag ","Location Chnage"+currentLocation.longitude)
        val task = fusedLocationProviderClient!!.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                var notificationHelper= NotificationHelper(this)
                notificationHelper.sendHighPriorityNotification("Out Of Range","Out Of Range",MainActivity::class.java)
                drawCircle(LatLng(currentLocation.latitude,currentLocation.longitude))
            }
        }


    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onMapReady(googleMap: GoogleMap) {
        gMap=googleMap
        googleMap.isMyLocationEnabled=true
        gMap.setOnMapLongClickListener(this)
        var latLng=LatLng(currentLocation.latitude,currentLocation.longitude)
        drawCircle(latLng)

    }

    @SuppressLint("MissingPermission")
    private fun drawCircle(point: LatLng) {

        gMap.clear()
        if (gMap != null) {
            val options = MarkerOptions()
                .position(point)
            mMyLocMarker = gMap.addMarker(options)
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 17f))

        }


        mCircle = gMap.addCircle(
            CircleOptions()
                .center(LatLng(point.latitude, point.longitude))
                .radius(radiusInMeters.toDouble())
                .fillColor(0x44ff0000)
                .strokeColor(-0x10000)
                .strokeWidth(1f)
        )
        var geofenceRepository= GeofenceRepository(this)
        geofenceRepository.add(point, radiusInMeters)

    }

    override fun onMapLongClick(latLng: LatLng) {
        drawCircle(latLng)
    }

}