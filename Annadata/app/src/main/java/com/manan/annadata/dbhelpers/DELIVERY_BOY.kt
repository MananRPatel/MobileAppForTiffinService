package com.manan.annadata.dbhelpers

import com.google.firebase.firestore.GeoPoint

data class DELIVERY_BOY (
    var BUYER_ID: String? = null,
    var FOOD_ID: String? = null,
    var FOOD_NAME: String? = null,
    var TOTAL_ORDER: Int = 0,
    var FOOD_POINT: Int = 0,
    var FOOD_MONEY: Int = 0,
    var DELIVERY_TIME: String? = null,
    var CONFORM_DELIVERY: Boolean = false,
    var CANCEL_DELIVERY: Boolean = false,
    var BUYER_HISTORY_ID: String? = null,
    var DELIVERY_ADDRESS: GeoPoint? = null
)