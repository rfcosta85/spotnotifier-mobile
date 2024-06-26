package pt.ipca.spotnotifier

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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

    private lateinit var sharedPreferences: SharedPreferences
    private val PREFERENCE_NAME = "spot_notifier_prefs"
    private val KEY_ACTIVITY_STARTED = "spot_valuation_activity_started"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Check if the SpotValuationActivity has been started before.
         * If not, start the SpotValuationActivity after a delay of 10 seconds.
         * The delay is handled using a Handler and the shared preference is updated to prevent
         * repeated starts.
         */
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        if (!sharedPreferences.getBoolean(KEY_ACTIVITY_STARTED, false)) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, SpotValuationActivity::class.java)
                startActivity(intent)
                sharedPreferences.edit().putBoolean(KEY_ACTIVITY_STARTED, true).apply()
            }, 10000)
        }

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

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()

        val autoCompleteFragment = AutocompleteSupportFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.autocomplete_fragment_container, autoCompleteFragment)
            .commit()

        autoCompleteFragment
            .setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        autoCompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
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
            val inputText = it.findViewById<EditText>(com.google.android.libraries
                .places.R.id.places_autocomplete_search_input)
            inputText.setBackgroundResource(R.drawable.border_input)
            inputText.hint = getString(R.string.input_hint_maps)
            inputText.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_geolocation,
                0,
                R.drawable.ic_microphone,
                0
            )

            val userIcon = ImageView(this).apply {
                setImageResource(R.drawable.ic_user)
                setPadding(8, 8, 8, 8)
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
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
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

    /**
     * Navigate to the selected marker's position using Google Maps or a browser.
     *
     * @param marker The marker that was clicked.
     */
    private fun navigateToMarker(marker: Marker) {
        val position = marker.position
        val gmmIntentUri =
            Uri.parse("google.navigation:q=${position.latitude}, ${position.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            val browserIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            startActivity(browserIntent)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SpotValuationActivity::class.java)
            startActivity(intent)
        }, 10000)
    }

    /**
     * Add markers around the user's location in a circular pattern.
     *
     * @param userLocation The user's current location.
     */
    private fun addMarkersAroundUserLocation(userLocation: LatLng) {
        val numMarkers = 6
        val radius = 1000

        val angleIncrement = 360f / numMarkers
        for (i in 0 until numMarkers) {
            val angle = i * angleIncrement
            val offsetX = (radius * cos(Math.toRadians(angle.toDouble()))).toFloat()
            val offsetY = (radius * sin(Math.toRadians(angle.toDouble()))).toFloat()
            val newLat = userLocation.latitude + offsetY / 111111
            val newLng =
                userLocation.longitude + offsetX / (111111 * cos(Math.toRadians(userLocation.latitude)))

            val markerPosition = LatLng(newLat, newLng)
            mMap.addMarker(MarkerOptions().position(markerPosition).title("Marker $i"))
        }
    }

    /**
     * Get the last known location of the user and update the map.
     * Adds a marker to the user's position and moves the camera to it.
     */
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

    /**
     * Find a view with the specified type within a given root view.
     *
     * @param T The type of the view to find.
     * @param root The root view to search within.
     * @param viewType The class of the view type to find.
     * @return The view of the specified type, or null if not found.
     */
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

    /**
     * Check if the app has location permission.
     * If not, request the permission from the user.
     */
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

    /**
     * Handle the profile click event and navigate to the ProfileActivity.
     *
     * @param view The view that was clicked.
     */
    fun onProfileClick(view: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    /**
     * Handle the result of permission requests.
     *
     * @param requestCode The request code passed in requestPermissions().
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
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
