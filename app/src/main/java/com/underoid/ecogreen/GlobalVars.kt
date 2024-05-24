package com.underoid.ecogreen

object GlobalVars {
    var dialogName: String = ""
    var dialogLocation: String = ""
    var dialogPhotoURI: String = ""

    fun setDiName(name: String){
        dialogName = name
    }
    fun setDiLocation(location: String){
        dialogLocation = location
    }
    fun setDiPhotoURI(uri: String){
        dialogPhotoURI = uri
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

}