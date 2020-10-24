package com.mbpatel.weatherinfo.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.mbpatel.weatherinfo.R
import com.mbpatel.weatherinfo.utils.permission.askRuntimePermission
import java.lang.ref.WeakReference

class LocationHelper private constructor() : ConnectionCallbacks, OnConnectionFailedListener{

    companion object {

        val TAG = LocationHelper::class.java.simpleName

        private const val INTERVAL = 20 * 1000 // 20 seconds
            .toLong()
        private const val FAST_INTERVAL: Long = 5000 // 5 seconds
        private const val DISTANCE = 5 //meters

        /**
         * Constant used in the location settings dialog.
         */
        private const val REQUEST_CHECK_SETTINGS = 0x11
        private const val REQUEST_ENABLE_GPS = 0x12

        private const val MESSAGE_PERMISSION_ALLOW = "Runtime permission allowed by user"
        private const val MESSAGE_PERMISSION_DENY = "Runtime permission Deny by user"
        private const val MESSAGE_PERMISSION_NEVER_ASK = "onNeverAskAgain() option selected by user while apply Runtime permission.\n" +
                "     User will be redirect to setting page, user can manually enable from setting page."
        private const val MESSAGE_GPS_ENABLE = "GPS enabled by user"
        private const val MESSAGE_GPS_DISABLE = "GPS disable by user"

        @Volatile
        private var INSTANCE: LocationHelper? = null
        val instance: LocationHelper?
            get() {
                if (INSTANCE == null) {
                    synchronized(LocationHelper::class.java) {
                        if (INSTANCE == null) {
                            INSTANCE = LocationHelper()
                        }
                    }
                }
                return INSTANCE
            }
    }

    private var contextWeakReference: WeakReference<Context?>? = null
    private var googleApiClient: GoogleApiClient? = null

    private var listener: OnLocationChanged? = null
    private var mGpsAccuracy: Int? = null
    private var permissionListener: OnPermissionCheck? = null
    private var singleListener: OnLocationChanged? = null

    private var context: Context? = null

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Provides access to the Location Settings API.
     */
    private var mSettingsClient: SettingsClient? = null

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private var mLocationRequest: LocationRequest? = null

    /**
     * Callback for Location events.
     */
    private var mLocationCallback: LocationCallback? = null

    /**
     * Represents a geographical location.
     */
    private var mCurrentLocation: Location? = null

    /**
     * Init location & google client object
     */
    fun initialize(context: Context?) {
        contextWeakReference = WeakReference(context)
        this.context = context
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(context!!)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        }
        checkPermission()
    }

    /**
     * This listener return current location to user.
     * These will ask user to allow GPS and runtime permission
     * 1. GPS accuracy - i.e. LocationHelper.GpsAccuracy.HIGH.accuracy
     * 2. listener - Return current location
     */
    fun initListener(mGpsAccuracy: Int, listener: OnLocationChanged?) {
        this.listener = listener
        this.mGpsAccuracy = mGpsAccuracy
    }

    /**
     *  This is used to notify possible status of permission to user
     *  This is Optional listener
     *  1. onAllow() - Runtime permission allowed by user
     *  2. onDeny() - Runtime permission Deny by user
     *  3. onNeverAskAgain() - onNeverAskAgain option selected by user while apply Runtime permission.
     *                         User will be redirect to setting page, user can manually enable from setting page.
     *  4. onGpsEnable() - GPS enabled by user
     *  5. onGpsDisable() - GPS disable by user
     */
    fun initPermissionListener(listener: OnPermissionCheck?) {
        this.permissionListener = listener
    }

    /**
     * This function will help to enable runtime permission after 6.0 devices
     * Make sure you have entered these permissions into manifest file
     *  Required permissions
     *  1.ACCESS_FINE_LOCATION
     *  2.ACCESS_COARSE_LOCATION
     */
    private fun checkPermission() {
        (context as FragmentActivity).askRuntimePermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) {
            //all permissions already granted or just granted
            permissionListener?.onAllow(MESSAGE_PERMISSION_ALLOW)
            connect()
        }.onDeclined { e ->
            if (e.hasDenied()) {
                permissionListener?.onDeny(MESSAGE_PERMISSION_DENY)
                AlertDialog.Builder(context as FragmentActivity, R.style.AlertDialogTheme)
                    .setMessage("To access location, you need to allow these permissions")
                    .setPositiveButton("Allow") { dialog, which ->
                        e.askAgain()
                    } //ask again
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }

            if (e.hasForeverDenied()) {
                permissionListener?.onNeverAskAgain(MESSAGE_PERMISSION_NEVER_ASK)
                // you need to open setting manually if you really need it
                e.goToSettings()
            }
        }
    }

    // Connect to google client
    private fun connect() {
        if (!googleApiClient!!.isConnected) {
            googleApiClient!!.connect()
        }
    }

    /* Listeners */
    override fun onConnected(bundle: Bundle?) {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        mSettingsClient = LocationServices.getSettingsClient(context!!)

        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()
        startUpdates()
    }

    /**
     * Creates a callback for receiving location events.
     */
    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                try {
                    mCurrentLocation = locationResult.lastLocation

                    /**
                     * Return location every time till location change
                     */
                    if (listener != null && mCurrentLocation != null) {
                        listener!!.onLocationReceived(mCurrentLocation)
                    }

                    /**
                     * Return location Only one time and then after remove location listener
                     */
                    if (singleListener != null && mCurrentLocation != null) {
                        singleListener!!.onLocationReceived(mCurrentLocation)
                        singleListener = null
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest.create()
        mLocationRequest?.priority = mGpsAccuracy!!
        mLocationRequest?.smallestDisplacement = DISTANCE.toFloat()
        mLocationRequest?.interval = INTERVAL
        mLocationRequest?.fastestInterval = FAST_INTERVAL
    }

    /**
     * Check GPS enable or not, if disable then make request to enable it.
     */
    private fun buildLocationSettingsRequest() {
        try {
            val builder = LocationSettingsRequest.Builder()
            builder.setAlwaysShow(true)
            builder.addLocationRequest(mLocationRequest!!)

            val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(context!!)
                .checkLocationSettings(builder.build())

            result.addOnSuccessListener(
                context as FragmentActivity,
                OnSuccessListener<LocationSettingsResponse?> {
                    // All location settings are satisfied. The client can initialize
                    // location requests here.
                    permissionListener?.onGpsEnable(MESSAGE_GPS_ENABLE)
                    startLocationUpdates()

                })

            result.addOnFailureListener(context as FragmentActivity, OnFailureListener { e ->
                if (e is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                    try {
                        permissionListener?.onGpsDisable(MESSAGE_GPS_DISABLE)

                        /**
                         *  Show the dialog by calling startResolutionForResult(),
                         *  and check the result in onActivityResult().
                         */
                        val resolvable = e as ResolvableApiException
                        resolvable.startResolutionForResult(
                            context as FragmentActivity,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (sendEx: SendIntentException) {
                        sendEx.printStackTrace()
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (googleApiClient != null) {
            if (!googleApiClient!!.isConnected) {
                googleApiClient!!.connect()
                return
            }

            if (contextWeakReference == null || contextWeakReference!!.get() == null
                || ActivityCompat.checkSelfPermission(
                    contextWeakReference!!.get()!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    contextWeakReference!!.get()!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            mFusedLocationClient?.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback, Looper.myLooper()
            )
        }
    }

    /**
     *
     * Removes location updates from the FusedLocationApi.
     * It is a good practice to remove location requests when the activity is in a paused or
     * stopped state. Doing so helps battery performance and is especially
     * recommended in applications that request frequent location updates.
     *
     */
    private fun stopLocationUpdates() {

        try {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener {
                    googleApiClient = null
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun removeListener() {
        listener = null
        stopLocationUpdates()
    }

    private fun startUpdates() {
        startLocationUpdates()
    }

    override fun onConnectionSuspended(i: Int) {
        Handler().postDelayed({ connect() }, INTERVAL)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Handler().postDelayed({ connect() }, 5 * INTERVAL)
    }

    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.i(TAG, "User agreed to make required location settings changes.")
                    startLocationUpdates()
                    permissionListener?.onGpsEnable("User agreed to make required location settings changes.")
                }
                Activity.RESULT_CANCELED -> {
                    Log.i(TAG, "User choose not to make required location settings changes.")
                    openGpsEnableSetting()
                    permissionListener?.onGpsDisable("User choose not to make required location settings changes.")
                }
            }
            REQUEST_ENABLE_GPS -> {
                val locationManager =
                    context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
                val isGpsEnabled =
                    locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                if (!isGpsEnabled) {
                    permissionListener?.onGpsDisable("GPS still disable, GPS is not enabled from setting page")
                    /**
                     *  Remove below comment if force fully redirect to setting page till user enable it
                     */
                    //openGpsEnableSetting()
                } else {
                    startLocationUpdates()
                    permissionListener?.onGpsEnable("GPS enabled from setting page")
                }
            }
        }
    }

    /**
     * Open setting page with GPS enable/disable page
     */
    private fun openGpsEnableSetting() {
        try {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            (context as FragmentActivity).startActivityForResult(intent, REQUEST_ENABLE_GPS)
        } catch (e: Exception) {
        }
    }


    fun setSingleListener(listener: OnLocationChanged?) {
        singleListener = listener
    }

    interface OnLocationChanged {
        fun onLocationReceived(loc: Location?)
    }

    interface OnPermissionCheck {
        fun onAllow(message: String)
        fun onDeny(message: String)
        fun onNeverAskAgain(message: String)
        fun onGpsEnable(message: String)
        fun onGpsDisable(message: String)
    }

    enum class GpsAccuracy(val accuracy: Int) {
        HIGH(LocationRequest.PRIORITY_HIGH_ACCURACY),
        BALANCED(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY),
        LOW(LocationRequest.PRIORITY_LOW_POWER),
        NO_POWER(LocationRequest.PRIORITY_NO_POWER)
    }
}