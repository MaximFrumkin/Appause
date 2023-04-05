package com.example.appause.ui.reports

import android.content.Intent
import android.content.Intent.getIntent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appause.Goal
import com.example.appause.GoalTracker
import com.example.appause.MainActivity
import com.example.appause.R
import com.example.appause.databinding.FragmentReportsBinding

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private lateinit var adapter: ReportsRecyclerAdapter
    lateinit var goalTracker : GoalTracker
    private var app = this.getActivity()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reports, container, false)

        val listener = object : ReportsRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(goal: Goal) {

                // TODO: delete perCategoryUsage later.
                //  I am creating this bcz I am assuming goal will have list<Long> for time used per category
                val perCategoryUsage = listOf<Long>(3, 4, 5)

                println("Item clicked: $goal")
                val intent = Intent(context, DetailedUsageActivity::class.java).apply {
                    putStringArrayListExtra("categoryList", ArrayList(goal.categoryList))
                    putExtra("perCategoryUsageList", (perCategoryUsage.toLongArray()))
                    putExtra("totalGoalTime", goal.goalTime)
                    putExtra("goalName", goal.goalName)
                    putExtra("GOALTRACKER", goalTracker)
                }
                startActivity(intent)
            }
        }


        adapter = ReportsRecyclerAdapter(activity as MainActivity, listener)
        goalTracker = adapter.mainActivity.goalTracker

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        adapter.updateData()

        return view

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
