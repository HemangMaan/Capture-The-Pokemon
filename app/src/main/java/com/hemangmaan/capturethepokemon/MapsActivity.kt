package com.hemangmaan.capturethepokemon

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hemangmaan.capturethepokemon.databinding.ActivityMapsBinding
import java.lang.Exception
import kotlin.math.abs

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        checkPermission()
        loadPokemon()
    }
    private var accessLocation=123
    private fun checkPermission(){
//        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
//            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),accessLocation)
//            return
//        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),accessLocation)
            return
        }
        getUserLocation()
    }
    private fun getUserLocation(){
        Toast.makeText(this, "user location access on", Toast.LENGTH_SHORT).show()
        //TODO: will implement later
        val myLocation = MyLocationListener()
        val locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,myLocation)
        val myThread = MyThread()
        myThread.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            accessLocation-> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation()
                } else {
                    Toast.makeText(this, "We cannot access your location", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    var location:Location?=null
    //Get User Location
    inner class MyLocationListener :LocationListener {
        init {
            location= Location("Start")
            location!!.longitude=0.0
            location!!.latitude=0.0
        }
        override fun onLocationChanged(p0: Location) {
            location=p0
        }
    }

    var oldLocation:Location?=null
    inner class MyThread : Thread() {
        init {
            oldLocation= Location("Start")
            oldLocation!!.longitude=0.0
            oldLocation!!.latitude=0.0
        }

        override fun run() {
            while (true){
                try {
                    if(oldLocation!!.distanceTo(location)==0f){
                        continue
                    }
                    oldLocation=location
                    runOnUiThread {
                        mMap.clear()

                        //show me
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap.addMarker(
                            MarkerOptions().position(sydney).title("Me")
                                .snippet("Here is my Location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ash))
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12f))

                        //show Pokemon
                        for(newPokemon in listPokemon.iterator()){
                            if(!newPokemon.isCatch){
                                val pokemon = LatLng(newPokemon.lat,
                                    newPokemon.log)
                                mMap.addMarker(
                                    MarkerOptions().position(pokemon).title(newPokemon.name)
                                        .snippet(newPokemon.des + " Power: "+newPokemon.power)
                                        .icon(BitmapDescriptorFactory.fromResource(newPokemon.image))
                                )

                                if(abs(location!!.latitude-newPokemon.lat) <2 && abs(location!!.longitude-newPokemon.log)<2){
                                    newPokemon.isCatch=true
                                    playerPower= newPokemon.power
                                    Toast.makeText(applicationContext,
                                        "You catch new pokemon your new power is $playerPower", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }
                    sleep(1000)
                }catch (ex:Exception){}
            }
        }
    }
    var playerPower=0.0
    var listPokemon = ArrayList<Pokemon>()
    private fun loadPokemon(){
        listPokemon.add(Pokemon(R.drawable.p1,"Pikachu","here is the pikachu",55.0,37.3441, -122.2861)) //La Honda, CA 94020, USA
        listPokemon.add(Pokemon(R.drawable.p3,"Chikorita","here is the chikorita",33.5,37.40,-122.2861))
    }
}