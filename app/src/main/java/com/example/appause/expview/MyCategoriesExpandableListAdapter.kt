package com.example.appause.expview

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.appause.R


class MyCategoriesExpandableListAdapter (
    _activity: Activity,
    _parentItems: ArrayList<HashMap<String, String?>>?,
    _childItems: ArrayList<ArrayList<HashMap<String, String?>>>?,
    _isFromMyCategoriesFragment: Boolean
        )  : BaseExpandableListAdapter(){

    companion object{
        lateinit var activity: Activity
        var parentItems: ArrayList<HashMap<String, String?>>? = null
        var childItems: ArrayList<ArrayList<HashMap<String, String?>>>? = null
        var isFromMyCategoriesFragment: Boolean = false
    }
    init {
        activity = _activity
        parentItems = _parentItems
        childItems = _childItems
        isFromMyCategoriesFragment = _isFromMyCategoriesFragment
    }
//    private val childItems: ArrayList<ArrayList<HashMap<String, String>>>? = null
//    private var parentItems: ArrayList<HashMap<String, String>>? = null

    //    private final ArrayList<HashMap<String, String>> childItems;
    private var inflater: LayoutInflater? = null
//    private var activity: Activity? = null
    private var child: HashMap<String, String?>? = null
    private var count = 0
//    private var isFromMyCategoriesFragment = false

    init {
        inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getGroupCount(): Int {
        return parentItems!!.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return childItems!![groupPosition].size
    }

    override fun getGroup(i: Int): Any? {
        return null
    }

    override fun getChild(i: Int, i1: Int): Any? {
        return null
    }

    override fun getGroupId(i: Int): Long {
        return 0
    }

    override fun getChildId(i: Int, i1: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        b: Boolean,
        convertView: View?,
        viewGroup: ViewGroup?
    ): View? {
        var convertView: View? = convertView
        val viewHolderParent: ViewHolderParent
        if (convertView == null) {
            if (isFromMyCategoriesFragment) {
                convertView = inflater?.inflate(R.layout.group_list_layout_my_categories, null)
            } else {
                convertView = inflater?.inflate(R.layout.group_list_layout_choose_categories, null)
            }
            viewHolderParent = ViewHolderParent()
            viewHolderParent.tvMainCategoryName = convertView?.findViewById(R.id.tvMainCategoryName)
            viewHolderParent.cbMainCategory = convertView?.findViewById(R.id.cbMainCategory)
            viewHolderParent.ivCategory = convertView?.findViewById(R.id.ivCategory)
            convertView?.setTag(viewHolderParent)
        } else {
            viewHolderParent = convertView.getTag() as ViewHolderParent
        }
        if (parentItems!![groupPosition][ConstantManager.Parameter.IS_CHECKED]?.uppercase() ==
                ConstantManager.Parameter.CHECK_BOX_CHECKED_TRUE
        ) {
            viewHolderParent.cbMainCategory!!.isChecked = true
            notifyDataSetChanged()
        } else {
            viewHolderParent.cbMainCategory!!.isChecked = false
            notifyDataSetChanged()
        }
        viewHolderParent.cbMainCategory!!.setOnClickListener{
                if (viewHolderParent.cbMainCategory!!.isChecked) {
                    parentItems!![groupPosition][ConstantManager.Parameter.IS_CHECKED] =
                        ConstantManager.Parameter.CHECK_BOX_CHECKED_TRUE
                    for (i in 0 until childItems!![groupPosition].size) {
                        childItems!![groupPosition][i][ConstantManager.Parameter.IS_CHECKED] =
                            ConstantManager.Parameter.CHECK_BOX_CHECKED_TRUE
                    }
                    notifyDataSetChanged()
                } else {
                    parentItems!![groupPosition][ConstantManager.Parameter.IS_CHECKED] =
                        ConstantManager.Parameter.CHECK_BOX_CHECKED_FALSE
                    for (i in 0 until childItems!![groupPosition].size) {
                        childItems!![groupPosition][i][ConstantManager.Parameter.IS_CHECKED] =
                            ConstantManager.Parameter.CHECK_BOX_CHECKED_FALSE
                    }
                    notifyDataSetChanged()
                }
            }

        ConstantManager.Parameter.childItems = childItems!!
        ConstantManager.Parameter.parentItems = parentItems!!
        viewHolderParent.tvMainCategoryName!!.text =
            parentItems!![groupPosition][ConstantManager.Parameter.CATEGORY_NAME]
        return convertView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        b: Boolean,
        convertView: View?,
        viewGroup: ViewGroup?
    ): View? {
        var convertView: View? = convertView
        val viewHolderChild: ViewHolderChild
        child = childItems!![groupPosition][childPosition]
        if (convertView == null) {
            convertView = inflater?.inflate(R.layout.child_list_layout_choose_category, null)
            viewHolderChild = ViewHolderChild()
            viewHolderChild.tvSubCategoryName = convertView?.findViewById(R.id.tvSubCategoryName)
            viewHolderChild.cbSubCategory = convertView?.findViewById(R.id.cbSubCategory)
            viewHolderChild.viewDivider = convertView?.findViewById(R.id.viewDivider)
            convertView?.setTag(viewHolderChild)
        } else {
            viewHolderChild = convertView.getTag() as ViewHolderChild
        }
        if (childItems!![groupPosition][childPosition][ConstantManager.Parameter.IS_CHECKED]?.uppercase() ==
                ConstantManager.Parameter.CHECK_BOX_CHECKED_TRUE
        ) {
            viewHolderChild.cbSubCategory!!.isChecked = true
            notifyDataSetChanged()
        } else {
            viewHolderChild.cbSubCategory!!.isChecked = false
            notifyDataSetChanged()
        }
        viewHolderChild.tvSubCategoryName!!.text =
            child!![ConstantManager.Parameter.SUB_CATEGORY_NAME]
        viewHolderChild.cbSubCategory!!.setOnClickListener {
                if (viewHolderChild.cbSubCategory!!.isChecked) {
                    count = 0
                    childItems!![groupPosition][childPosition][ConstantManager.Parameter.IS_CHECKED] =
                        ConstantManager.Parameter.CHECK_BOX_CHECKED_TRUE
                    notifyDataSetChanged()
                } else {
                    count = 0
                    childItems!![groupPosition][childPosition][ConstantManager.Parameter.IS_CHECKED] =
                        ConstantManager.Parameter.CHECK_BOX_CHECKED_FALSE
                    notifyDataSetChanged()
                }
                for (i in 0 until childItems!![groupPosition].size) {
                    if (childItems!![groupPosition][i][ConstantManager.Parameter.IS_CHECKED]?.uppercase() ==
                            ConstantManager.Parameter.CHECK_BOX_CHECKED_TRUE
                    ) {
                        count++
                    }
                }
                if (count == childItems!![groupPosition].size) {
                    parentItems!![groupPosition][ConstantManager.Parameter.IS_CHECKED] =
                        ConstantManager.Parameter.CHECK_BOX_CHECKED_TRUE
                    notifyDataSetChanged()
                } else {
                    parentItems!![groupPosition][ConstantManager.Parameter.IS_CHECKED] =
                        ConstantManager.Parameter.CHECK_BOX_CHECKED_FALSE
                    notifyDataSetChanged()
                }
                ConstantManager.Parameter.childItems = childItems!!
                ConstantManager.Parameter.parentItems = parentItems!!
            }
        return convertView
    }

    override fun isChildSelectable(i: Int, i1: Int): Boolean {
        return false
    }

    override fun onGroupCollapsed(groupPosition: Int) {
        super.onGroupCollapsed(groupPosition)
    }

    override fun onGroupExpanded(groupPosition: Int) {
        super.onGroupExpanded(groupPosition)
    }

    private class ViewHolderParent {
        var tvMainCategoryName: TextView? = null
        var cbMainCategory: CheckBox? = null
        var ivCategory: ImageView? = null
    }

    private class ViewHolderChild {
        var tvSubCategoryName: TextView? = null
        var cbSubCategory: CheckBox? = null
        var viewDivider: View? = null
    }
}