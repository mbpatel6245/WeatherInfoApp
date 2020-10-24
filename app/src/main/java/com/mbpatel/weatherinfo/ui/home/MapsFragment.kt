package com.mbpatel.weatherinfo.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mbpatel.weatherinfo.utils.LocationHelper
import com.mbpatel.weatherinfo.MainActivity
import com.mbpatel.weatherinfo.R
import kotlinx.android.synthetic.main.fragment_maps.*
import java.util.*


class MapsFragment : Fragment() {
    private lateinit var mMap: GoogleMap
//    private val mapViewModel: MapViewModel by viewModels {
//        InjectorUtils.provideMapViewModelFactory(this)
//    }
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        initLocation()
        mMap.setOnMapClickListener {
            val geocoder = Geocoder(activity, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            val cityName: String = if(!TextUtils.isEmpty(addresses[0].subLocality)) addresses[0].subLocality+ "," else ""
            val stateName: String =  if(!TextUtils.isEmpty(addresses[0].locality))addresses[0].locality + "," else ""
            val countryName: String = if(!TextUtils.isEmpty(addresses[0].adminArea))addresses[0].adminArea + "" else ""

            Log.e("MEHUL", "$cityName,$stateName,$countryName")
            val clickedLocation = LatLng(it.latitude, it.longitude)
            googleMap.addMarker(
                MarkerOptions().position(clickedLocation).title("$cityName,$stateName,$countryName")
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(clickedLocation))
            //mapViewModel.addBookmark(it.latitude, it.longitude,"$cityName$stateName$countryName")

            //showToast(requireActivity(),getString(R.string.bookmark_saved))
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

//        (activity as MainActivity).supportActionBar?.title = getString(R.string.title_location)

        btnDone.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    /**
     * Init current location with helper of utils class
     */
    private fun initLocation() {
        // Initialize Location Helper
        LocationHelper.instance?.initialize(activity)
        LocationHelper.instance?.initListener(
            LocationHelper.GpsAccuracy.BALANCED.accuracy,
            object : LocationHelper.OnLocationChanged {
                override fun onLocationReceived(loc: Location?) {
                    if (loc != null) {
                        mMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(loc.latitude, loc.longitude),
                                8f
                            )
                        )
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null)
                    }
                }
            })

        LocationHelper.instance?.initPermissionListener(object : LocationHelper.OnPermissionCheck {
            override fun onAllow(message: String) {
                setMyLocation()
            }

            override fun onDeny(message: String) {
            }

            override fun onNeverAskAgain(message: String) {
            }

            override fun onGpsEnable(message: String) {
                setMyLocation()
            }

            override fun onGpsDisable(message: String) {
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun setMyLocation() {
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        LocationHelper.instance?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}