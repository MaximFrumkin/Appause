package com.example.appause.expview

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import com.example.appause.AppCategory
import com.example.appause.AppTimer
import com.example.appause.CheckedActivity
import com.example.appause.R
import kotlinx.coroutines.runBlocking


class OnboardingActivity : AppCompatActivity() {
    private var btn: Button? = null
    private var lvCategory: ExpandableListView? = null
    private var arCategory: ArrayList<DataItem>? = null
    private var arSubCategory: ArrayList<SubCategoryItem?>? = null
    private val arSubCategoryFinal: ArrayList<ArrayList<SubCategoryItem>>? = null
    private var parentItems: ArrayList<HashMap<String, String?>>? = null
    private var childItems: ArrayList<ArrayList<HashMap<String, String?>>>? = null
    private var myCategoriesExpandableListAdapter: MyCategoriesExpandableListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        val btn = findViewById<Button>(R.id.btn)
        btn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, CheckedActivity::class.java)
            startActivity(intent)
        })
        setupReferences()
    }

    private fun setupReferences() {
        val pm = packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val categoryToAppsMap = emptyMap<AppCategory, MutableList<String>>().toMutableMap()

        for (packageInfo in packages) {
            val packageName = packageInfo.packageName
            val appNameId = packageInfo.labelRes
            val appName = pm.getApplicationLabel(packageInfo).toString()
            var category: AppCategory? = null
            runBlocking {
                category = AppTimer.AppCategoryService().fetchCategory(packageName)
            }

            if (category != null && category!! != AppCategory.OTHER) {
                if (categoryToAppsMap.containsKey(category)) {
                    categoryToAppsMap[category]?.add(appName)
                } else {
                    categoryToAppsMap[category!!] = mutableListOf<String>()
                    categoryToAppsMap[category]?.add(appName)
                }
            }
        }

        Log.v("ONBOARDING>>>>>>", "$categoryToAppsMap")


        lvCategory = findViewById<ExpandableListView>(R.id.lvCategory)
        arCategory = ArrayList()
        arSubCategory = ArrayList()
        parentItems = ArrayList()
        childItems = ArrayList()

        for ((categoryId, appNames) in categoryToAppsMap) {
            var dataItem = DataItem()
            dataItem.setCategoryId(categoryId.ordinal.toString())
            dataItem.setCategoryName(categoryId.toString())
            arSubCategory = ArrayList()
            for (appName in appNames) {
                val subCategoryItem = SubCategoryItem()
                subCategoryItem.setCategoryId(categoryId.toString())
                subCategoryItem.setIsChecked(ConstantManager.Parameter.CHECK_BOX_CHECKED_FALSE)
                subCategoryItem.setSubCategoryName(appName)
                arSubCategory!!.add(subCategoryItem)
            }
            dataItem.setSubCategory(arSubCategory)
            arCategory!!.add(dataItem)
        }
        Log.d("TAG", "setupReferences: " + arCategory!!.size)
        for (data in arCategory!!) {
            val childArrayList = ArrayList<HashMap<String, String?>>()
            val mapParent = HashMap<String, String?>()
            mapParent[ConstantManager.Parameter.CATEGORY_ID] = data.getCategoryId()
            mapParent[ConstantManager.Parameter.CATEGORY_NAME] = data.getCategoryName()
            var countIsChecked = 0
            for (subCategoryItem in data.getSubCategory()!!) {
                val mapChild = HashMap<String, String?>()
                mapChild[ConstantManager.Parameter.SUB_ID] = subCategoryItem!!.getSubId()
                mapChild[ConstantManager.Parameter.SUB_CATEGORY_NAME] =
                    subCategoryItem.getSubCategoryName()
                mapChild[ConstantManager.Parameter.CATEGORY_ID] = subCategoryItem.getCategoryId()
                mapChild[ConstantManager.Parameter.IS_CHECKED] = subCategoryItem.getIsChecked()
                if (subCategoryItem.getIsChecked()
                        .equals(ConstantManager.Parameter.CHECK_BOX_CHECKED_TRUE, ignoreCase = true)
                ) {
                    countIsChecked++
                }
                childArrayList.add(mapChild)
            }
            if (countIsChecked == data.getSubCategory()!!.size) {
                data.setIsChecked(ConstantManager.Parameter.CHECK_BOX_CHECKED_TRUE)
            } else {
                data.setIsChecked(ConstantManager.Parameter.CHECK_BOX_CHECKED_FALSE)
            }
            mapParent[ConstantManager.Parameter.IS_CHECKED] = data.getIsChecked()
            childItems!!.add(childArrayList)
            parentItems!!.add(mapParent)
        }
        ConstantManager.Parameter.parentItems = parentItems!!
        ConstantManager.Parameter.childItems = childItems!!
        myCategoriesExpandableListAdapter =
            MyCategoriesExpandableListAdapter(this, parentItems!!, childItems!!, false)
        lvCategory?.setAdapter(myCategoriesExpandableListAdapter)
    }
}