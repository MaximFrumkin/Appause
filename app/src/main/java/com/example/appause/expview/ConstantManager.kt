package com.example.appause.expview

class ConstantManager {
//    val CHECK_BOX_CHECKED_TRUE = "YES"
//    val CHECK_BOX_CHECKED_FALSE = "NO"

//    var childItems: ArrayList<ArrayList<HashMap<String, String>>> = ArrayList()
//    var parentItems: ArrayList<HashMap<String, String>> = ArrayList()


    object Parameter {
        const val IS_CHECKED = "is_checked"
        const val SUB_CATEGORY_NAME = "sub_category_name"
        const val CATEGORY_NAME = "category_name"
        const val CATEGORY_ID = "category_id"
        const val SUB_ID = "sub_id"
        const val CHECK_BOX_CHECKED_TRUE = "YES"
        const val CHECK_BOX_CHECKED_FALSE = "NO"
        var childItems: ArrayList<ArrayList<HashMap<String, String?>>> = ArrayList()
        var parentItems: ArrayList<HashMap<String, String?>> = ArrayList()
    }
}
