package com.manan.annadata.dbhelpers

import com.google.firebase.firestore.GeoPoint

class BUY_HISTORY {
    var FOOD_ID: String? = null
    var RETAILER_ID: String? = null
    var FOOD_NAME: String? = null
    var FOOD_WEIGHT = 0
    var TOTAL_FOOD = 0
    var FOOD_PRICE = 0
    var FOOD_POINT = 0
    var FOOD_INFO: String? = null
    var ORDER_STATUS = 0
    var RATING_FOOD = 0

    companion object {
        const val ORDER_CONFORM = 1
        const val ORDER_CANCEL = 0
        const val ORDER_PLACED = 2
    }
}