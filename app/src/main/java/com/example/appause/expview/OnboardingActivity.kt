package com.example.appause.expview

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import com.example.appause.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


class OnboardingActivity : AppCompatActivity() {
    private var lvCategory: ExpandableListView? = null
    private var arCategory: ArrayList<DataItem>? = null
    private var arSubCategory: ArrayList<SubCategoryItem?>? = null
    private var parentItems: ArrayList<HashMap<String, String?>>? = null
    private var childItems: ArrayList<ArrayList<HashMap<String, String?>>>? = null
    private var myCategoriesExpandableListAdapter: MyCategoriesExpandableListAdapter? = null

    private fun addGoalToGoalTracker() {
        // Add the current goal to the goal tracker!
        var appCategories : MutableList<String> = mutableListOf()
        var appNames : MutableList<String> = mutableListOf()
        for (i in MyCategoriesExpandableListAdapter.parentItems!!.indices) {
            val isChecked: String = MyCategoriesExpandableListAdapter.parentItems!!.get(i)
                .get(ConstantManager.Parameter.IS_CHECKED)!!
            if (isChecked.equals(ConstantManager.Parameter.CHECK_BOX_CHECKED_TRUE, ignoreCase = true)) {
                appCategories.add(MyCategoriesExpandableListAdapter.parentItems!!.get(i).get(ConstantManager.Parameter.CATEGORY_NAME).toString())
            } else {
                for (j in MyCategoriesExpandableListAdapter.childItems!!.get(i).indices) {
                    val isChildChecked: String =
                        MyCategoriesExpandableListAdapter.childItems!!.get(i)[j]
                            .get(ConstantManager.Parameter.IS_CHECKED)!!
                    if (isChildChecked.equals(
                            ConstantManager.Parameter.CHECK_BOX_CHECKED_TRUE,
                            ignoreCase = true
                        )
                    ) {
                        appNames.add(MyCategoriesExpandableListAdapter.childItems!!.get(i)[j].get(ConstantManager.Parameter.SUB_CATEGORY_NAME).toString())
                    }
                }
            }
        }
        Log.v("ONBOARDING", ">>>>>>CATEGORIES: $appCategories")
        Log.v("ONBOARDING", ">>>>>>APPNAMES: $appNames")


        val goalName = findViewById<EditText>(R.id.editTextTextGoalName).text.toString()
        GoalTracker.addGoal(goalName, 0, appNames, appCategories)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        val confirmBtn = findViewById<Button>(R.id.btn)
        confirmBtn.setOnClickListener(View.OnClickListener {
            addGoalToGoalTracker()

            // The GoalTracker now has ALL the goals this user has!
            // We can now update firebase
            runBlocking {
                Firebase.firestore.collection("users")
                    .document(getUserDocIdBlocking(CurrentUser.user))
                    .update(
                        mutableMapOf(
                            "totalGoals" to GoalTracker.goals.size
                        ) as Map<String, Any>
                    ).await()
            }
            Log.v("ONBOARDING", "UPDATED TOTAL GOALS ${GoalTracker.goals.size}")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })

        val addAnotherGoalButton = findViewById<Button>(R.id.btn2)
        addAnotherGoalButton.setOnClickListener(View.OnClickListener {
            addGoalToGoalTracker()
            val intent = Intent(this, OnboardingActivity::class.java)
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