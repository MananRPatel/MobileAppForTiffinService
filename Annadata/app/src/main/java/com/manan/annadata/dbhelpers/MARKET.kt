package com.manan.annadata.dbhelpers


data class MARKET (
    var RETAILER_ID: String? = null,
    var FOOD_NAME: String? = null,
    var FOOD_PRICE: Int = 0,
    var FOOD_POINT: Int = 0,
    var FOOD_WEIGHT: Int = 0,
    var FOOD_INFO: String? = null,
    var TOTAL_FOOD: Int = 0,
    var FOOD_TIME: String? = null,
    var FOOD_DATE: String? = null,
    var NO_FOOD_LEFT: Boolean = false,
    var LEFT_ORDER: Int = 0,
    var PHOTO_URL: String? = null,
    var CITY_NAME: String? = null,
    var TIFFIN_LINK: String? = null,
    var ALL_RATING: List<Int>? = null,
    var ALL_REVIEW: List<String>? = null,
    var VEG: Boolean? = null
)