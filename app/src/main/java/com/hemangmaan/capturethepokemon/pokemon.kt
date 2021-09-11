package com.hemangmaan.capturethepokemon

import android.location.Location

class Pokemon(var image: Int,
              var name: String, var des: String, var power: Double, var lat: Double, var log: Double) {
    var isCatch =false
    init {
        this.isCatch=false
    }
}