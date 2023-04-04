package com.example.appause.expview

class DataItem {
    private var categoryId: String? = null
    private var categoryName: String? = null
    private var isChecked = "NO"
    private var subCategory: ArrayList<SubCategoryItem?>? = null

    fun DataItem() {}

    fun getCategoryId(): String? {
        return categoryId
    }

    fun setCategoryId(categoryId: String?) {
        this.categoryId = categoryId
    }

    fun getCategoryName(): String? {
        return categoryName
    }

    fun setCategoryName(categoryName: String?) {
        this.categoryName = categoryName
    }

    fun getIsChecked(): String? {
        return isChecked
    }

    fun setIsChecked(isChecked: String) {
        this.isChecked = isChecked
    }

    fun getSubCategory(): ArrayList<SubCategoryItem?>? {
        return subCategory
    }

    fun setSubCategory(subCategory: ArrayList<SubCategoryItem?>?) {
        this.subCategory = subCategory
    }
}