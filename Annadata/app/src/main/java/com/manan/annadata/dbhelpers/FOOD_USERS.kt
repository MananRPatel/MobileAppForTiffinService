package com.manan.annadata.dbhelpers

import com.google.firebase.firestore.GeoPoint

class FOOD_USERS (
    var USER_NAME: String? = null,
    var ID: String? = null,
    var ADDRESS: String? = null,
    var MAIL: String? = null,
    var PHOTO_URL: String? = null,
    var TIFFIN_LINK: String? = null,
    var FCM_TOKEN: String? = null,
    var USER_LOCATION: GeoPoint? = null)