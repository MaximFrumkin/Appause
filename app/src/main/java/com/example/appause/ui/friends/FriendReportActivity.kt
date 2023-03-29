package com.example.appause.ui.reports

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.appause.MainActivity
import com.example.appause.R
import com.example.appause.databinding.FragmentReportsBinding

class FriendReportFragment : Fragment(){
    private var _binding: FragmentReportsBinding? = null
    private lateinit var adapter: RecyclerAdapter

    private var app = this.getActivity()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        adapter.updateData()

        return view

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}