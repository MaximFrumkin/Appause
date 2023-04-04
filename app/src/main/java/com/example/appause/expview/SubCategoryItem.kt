package com.example.appause.expview

class SubCategoryItem {
    private var categoryId: String? = null
    private var subId: String? = null
    private var subCategoryName: String? = null
    private var isChecked: String? = null

    fun getCategoryId(): String? {
        return categoryId
    }

    fun setCategoryId(categoryId: String?) {
        this.categoryId = categoryId
    }

    fun getSubId(): String? {
        return subId
    }

    fun setSubId(subId: String?) {
        this.subId = subId
    }

    fun getSubCategoryName(): String? {
        return subCategoryName
    }

    fun setSubCategoryName(subCategoryName: String?) {
        this.subCategoryName = subCategoryName
    }

    fun getIsChecked(): String? {
        return isChecked
    }

    fun setIsChecked(isChecked: String?) {
        this.isChecked = isChecked
    }
}
