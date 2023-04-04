package com.example.appause

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.appause.expview.ConstantManager
import com.example.appause.expview.MyCategoriesExpandableListAdapter

class CheckedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checked)
        val tvParent = findViewById<TextView>(R.id.parent)
        val tvChild = findViewById<TextView>(R.id.child)
//        val myCategoriesExpandableListAdapter = MyCategoriesExpandableListAdapter()
        for (i in MyCategoriesExpandableListAdapter.parentItems!!.indices) {
            val isChecked: String = MyCategoriesExpandableListAdapter.parentItems!!.get(i)
                .get(ConstantManager.Parameter.IS_CHECKED)!!
            if (isChecked.equals(ConstantManager.Parameter.CHECK_BOX_CHECKED_TRUE, ignoreCase = true)) {
                tvParent.setText(
                    tvParent.getText()
                        .toString() + MyCategoriesExpandableListAdapter.parentItems!!.get(i)
                        .get(ConstantManager.Parameter.CATEGORY_NAME)
                )
            }
            for (j in MyCategoriesExpandableListAdapter.childItems!!.get(i).indices) {
                val isChildChecked: String =
                    MyCategoriesExpandableListAdapter.childItems!!.get(i)[j]
                        .get(ConstantManager.Parameter.IS_CHECKED)!!
                if (isChildChecked.equals(
                        ConstantManager.Parameter.CHECK_BOX_CHECKED_TRUE,
                        ignoreCase = true
                    )
                ) {
                    tvChild.setText(
                        tvChild.getText()
                            .toString() + " , " + MyCategoriesExpandableListAdapter.parentItems!!.get(
                            i
                        ).get(ConstantManager.Parameter.CATEGORY_NAME) + " " + (j + 1)
                    )
                }
            }
        }
   }
}