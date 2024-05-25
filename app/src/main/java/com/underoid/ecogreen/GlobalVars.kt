package com.underoid.ecogreen

object GlobalVars {
    var dialogName: String = ""
    var dialogLocation: String = ""
    var dialogPhotoURI: String = ""
    var latLngListSize: Int = 0

    fun setDiName(name: String){
        dialogName = name
    }
    fun setDiLocation(location: String){
        dialogLocation = location
    }
    fun setDiPhotoURI(uri: String){
        dialogPhotoURI = uri
    }

    fun setLatLngListSizee(size: Int){
        latLngListSize = size
    }

    fun getDiName(): String{
        return dialogName
    }
    fun getDiLocation(): String{
        return dialogLocation
    }
    fun getDiURI(): String{
        return dialogPhotoURI
    }
    fun getLatLngListSizee() : Int{
        return latLngListSize
    }

}