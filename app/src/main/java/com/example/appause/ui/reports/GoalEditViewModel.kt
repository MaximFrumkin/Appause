package com.example.appause.ui.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GoalEditViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is goal edit Fragment"
    }
    val text: LiveData<String> = _text
}