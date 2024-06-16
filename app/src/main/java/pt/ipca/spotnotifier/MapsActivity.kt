package pt.ipca.spotnotifier

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import pt.ipca.spotnotifier.databinding.ActivityMapsBinding
import kotlin.math.cos
import kotlin.math.sin

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userLocation: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val googleMapsApiKey = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
            .metaData.getString("com.google.android.geo.API_KEY")

        if (googleMapsApiKey != null) {
            Places.initialize(applicationContext, googleMapsApiKey)
        } else {
            Log.e("MapsActivity", "API keys not found in meta-data")
        }
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Places.initialize(applicationContext, "@string/google_maps_key")
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()

        val autoCompleteFragment = AutocompleteSupportFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.autocomplete_fragment_container, autoCompleteFragment)
            .commit()

        autoCompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        autoCompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLng = place.latLng
//                if (latLng != null) {
//                    mMap.addMarker(MarkerOptions().position(latLng).title(place.name))
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
//                }
                place.latLng?.let {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 13f))
                    addMarkersAroundUserLocation(it)
                }
            }
            override fun onError(status: com.google.android.gms.common.api.Status) {
                Log.d("MEI", "DocumentSnapshot added with ID: $status")
            }
        })

        autoCompleteFragment.view?.let {
            val inputText = it.findViewById<EditText>(com.google.android.libraries.places.R.id.places_autocomplete_search_input)
            inputText.setBackgroundResource(R.drawable.border_input)
            inputText.hint = "Pesquise aqui"
            inputText.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_geolocation,
                0,
                R.drawable.ic_microphone,
                0
            )

            val userIcon = ImageView(this).apply {
                setImageResource(R.drawable.ic_user)
                setPadding(8,8,8,8)
            }

            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val inputLayout = findViewWithType(it, LinearLayout::class.java)
            inputLayout?.addView(userIcon, layoutParams)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMinZoomPreference(13.0f)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                userLocation = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13f))
                addMarkersAroundUserLocation(userLocation)
            }
        }
        mMap.setOnMarkerClickListener { marker ->
            navigateToMarker(marker)
            true
        }
    }

//    private fun navigateToMarker(destination: LatLng) {
//        val gmmIntentUri = Uri.parse("google.navigation:q${destination.latitude}, ${destination.longitude}")
//        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//        mapIntent.setPackage("com.google.android.apps.maps")
//        if (mapIntent.resolveActivity(packageManager) != null) {
//            startActivity(mapIntent)
//        } else {
//            val browserIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//            startActivity(browserIntent)
//        }
//    }

    private fun navigateToMarker(marker: Marker) {
        val position = marker.position
        val gmmIntentUri = Uri.parse("google.navigation:q=${position.latitude}, ${position.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            val browserIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            startActivity(browserIntent)
        }
    }

    private fun addMarkersAroundUserLocation(userLocation: LatLng) {
        val numMarkers = 6
        val radius = 1000

        val angleIncrement = 360f / numMarkers
        for(i in 0 until numMarkers) {
            val angle = i * angleIncrement
            val offsetX = (radius * cos(Math.toRadians(angle.toDouble()))).toFloat()
            val offsetY = (radius * sin(Math.toRadians(angle.toDouble()))).toFloat()
            val newLat = userLocation.latitude + offsetY / 111111
            val newLng = userLocation.longitude + offsetX / (111111 * cos(Math.toRadians(userLocation.latitude)))

            val markerPosition = LatLng(newLat, newLng)
            mMap.addMarker(MarkerOptions().position(markerPosition).title("Marker $i"))
        }
    }

    private fun getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val userLatLng = LatLng(location.latitude, location.longitude)
                        mMap.addMarker(MarkerOptions().position(userLatLng).title("You are here"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                    }
                }
        }
    }
    private fun <T : View> findViewWithType(root: View, viewType: Class<T>): T? {
        if (viewType.isInstance(root)) {
            return viewType.cast(root)
        }
        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                val child = root.getChildAt(i)
                val result = findViewWithType(child, viewType)
                if (result != null) {
                    return result
                }
            }
        }
        return null
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, show rationale or request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission has already been granted
            getLastKnownLocation()
        }
    }

    fun onProfileClick(view: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted
                    getLastKnownLocation()
                } else {
                    // Permission denied
                    // Show message to the user about the importance of the permission
                }
                return
            }
        }
    }
}