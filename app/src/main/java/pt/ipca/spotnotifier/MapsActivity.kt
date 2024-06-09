package pt.ipca.spotnotifier

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import pt.ipca.spotnotifier.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Places.initialize(applicationContext, "AIzaSyDANa7oAPs8AA-PjFGIEOqBsZR6eNiwmQ8")
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val autoCompleteFragment = AutocompleteSupportFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.autocomplete_fragment_container, autoCompleteFragment)
            .commit()

        autoCompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        autoCompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLng = place.latLng
                if (latLng != null) {
                    mMap.addMarker(MarkerOptions().position(latLng).title(place.name))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
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

        val barcelos = LatLng(41.53345229690391, -8.622321031592374)
        mMap.addMarker(MarkerOptions().position(barcelos).title("Cidade de Barcelos"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(barcelos))

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

//    private fun checkLocationPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED) {
//            // Permission is not granted, show rationale or request permission
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        } else {
//            // Permission has already been granted
//            getLastKnownLocation()
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        when (requestCode) {
//            LOCATION_PERMISSION_REQUEST_CODE -> {
//                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    // Permission granted
//                    getLastKnownLocation()
//                } else {
//                    // Permission denied
//                    // Show message to the user about the importance of the permission
//                }
//                return
//            }
//        }
//    }

}