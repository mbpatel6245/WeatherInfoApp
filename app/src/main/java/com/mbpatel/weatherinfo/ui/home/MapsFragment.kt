package com.mbpatel.weatherinfo.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mbpatel.weatherinfo.R
import com.mbpatel.weatherinfo.ui.history.HistoryFragmentDirections
import com.mbpatel.weatherinfo.ui.login.LoginActivity
import com.mbpatel.weatherinfo.utils.*
import com.mbpatel.weatherinfo.utils.permission.askRuntimePermission
import kotlinx.android.synthetic.main.fragment_maps.*
import java.util.*


class MapsFragment : Fragment() {
    private lateinit var mMap: GoogleMap
    private var mLocation: Location? = null

    private val mapViewModel: MapViewModel by viewModels {
        InjectorUtils.provideMapViewModelFactory(this)
    }
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        checkPermission()
        initLocation()
        mMap.setOnCameraIdleListener {
            val center: LatLng = mMap.cameraPosition.target
            val geocoder = Geocoder(activity, Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(center.latitude, center.longitude, 1)
            if (addresses.isNotEmpty()) {
                val cityName: String =
                    if (!TextUtils.isEmpty(addresses[0].subLocality)) addresses[0].subLocality + "," else ""
                val stateName: String =
                    if (!TextUtils.isEmpty(addresses[0].locality)) addresses[0].locality + "," else ""
                val countryName: String =
                    if (!TextUtils.isEmpty(addresses[0].adminArea)) addresses[0].adminArea + "" else ""

                tvAddress.text = "$cityName $stateName $countryName"
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        btnChoose.setOnClickListener {
            if (tvAddress.text.toString().isEmpty())
                showToast(requireActivity(), getString(R.string.empty_address))
            else {
                mapViewModel.addHistory(
                    mMap.cameraPosition.target.latitude,
                    mMap.cameraPosition.target.longitude,
                    tvAddress.text.toString()
                )
                val direction =
                    MapsFragmentDirections.actionMapFragmentToWeatherFragment(
                        mMap.cameraPosition.target.latitude.toString(),
                        mMap.cameraPosition.target.longitude.toString()
                    )
                view.findNavController().navigate(direction)
            }
        }
    }

    /**
     * Init current location with helper of utils class
     */
    private fun initLocation() {
        val cLoc = getPreference(requireActivity(), Constants.KEY_CURRENT_LOCATION)
        if (mLocation == null && cLoc.isNotEmpty()) {
            val mLoc = cLoc.split(",").toTypedArray()
            setCurrentLocationCamera(LatLng(mLoc[0].toDouble(), mLoc[1].toDouble()))
        }
        // Initialize Location Helper
        LocationHelper.instance?.initialize(requireContext())
        LocationHelper.instance?.initListener(
            LocationHelper.GpsAccuracy.BALANCED.accuracy,
            object : LocationHelper.OnLocationChanged {
                override fun onLocationReceived(loc: Location?) {
                    loc?.let {
                        savePreference(
                            requireActivity(),
                            Constants.KEY_CURRENT_LOCATION,
                            "${it.latitude},${it.longitude}"
                        )
                        setCurrentLocationCamera(LatLng(it.latitude, it.longitude))
                    }
                }
            })

        LocationHelper.instance?.initPermissionListener(object : LocationHelper.OnPermissionCheck {
            override fun onAllow(message: String) {
                checkPermission()
            }

            override fun onDeny(message: String) {
            }

            override fun onNeverAskAgain(message: String) {
            }

            override fun onGpsEnable(message: String) {
                checkPermission()
            }

            override fun onGpsDisable(message: String) {
            }
        })
    }

    private fun setCurrentLocationCamera(loc: LatLng?) {
        if (loc != null) {
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    loc, 15f
                )
            )
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null)
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        LocationHelper.instance?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * Init current location with helper of utils class
     */
    @SuppressLint("MissingPermission")
    private fun checkPermission() {
        askRuntimePermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
        }.onDeclined { e ->
            if (e.hasDenied()) {
                AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
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
                // you need to open setting manually if you really need it
                e.goToSettings()
            }
        }
    }
}