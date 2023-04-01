package com.example.appause.ui.reports

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appause.R
import androidx.compose.runtime.Composable
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.google.firebase.annotations.PreviewApi
import java.util.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*


class DetailedUsageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val perCategoryUsageArray = intent.getLongArrayExtra("perCategoryUsageList")
        // this will give original perCategoryUsageList sent with the intent
        var perCategoryUsageList = perCategoryUsageArray?.toList()

        var categoryList = intent.getStringArrayListExtra("categoryList")?.toList()
        var totalGoalTime = intent.getLongExtra("totalGoalTime", 0)

        var goalName = intent.getStringExtra("goalName")


        // todo remove this block later for dummy data
        ///////////////////////////////////////////////////////////////////////////
        totalGoalTime = 100
        categoryList = listOf("Whatsapp", "Instagram", "Snapchat")
        perCategoryUsageList = listOf(50, 25, 75)
        ///////////////////////////////////////////////////////////////////////////

        setContent {
            customListView(goalName.toString(), categoryList, perCategoryUsageList, totalGoalTime)
        }

    }


    @Composable
    fun customListView(usageDescription: String, categoryList: List<String>, perCategoryUsageList: List<Long>, totalGoalTime: Long) {

        // lazy column for displaying a list view.
        LazyColumn {

            item {
                Text(text = usageDescription, style = MaterialTheme.typography.h6, modifier = Modifier.padding(16.dp))
            }

            itemsIndexed(categoryList) { index, _ ->
                Card(
                    modifier = Modifier.padding(8.dp),
                    elevation = 8.dp
                )
                {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()


                    ) {
                        Spacer(modifier = Modifier.width(5.dp))
                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = categoryList[index],
                            modifier = Modifier.padding(4.dp),
                            color = Color.Black, textAlign = TextAlign.Center
                        )
                        customProgressBar(totalGoalTime, perCategoryUsageList[index])
                    }
                }
            }
        }
    }

    @Composable
    fun customProgressBar(totalGoalTime: Long, categoryUsage: Long) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .fillMaxHeight(),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            var progress: Int = 75;
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .height(30.dp)
                    .background(Color.Gray)
                    .width(300.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(15.dp))
                        .height(30.dp)
                        .background(
                            // adding brush for background color.
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF0F9D58),
                                    Color(0xF055CA4D)
                                )
                            )
                        )
                        .width(300.dp * (categoryUsage.toFloat() / totalGoalTime))
                )
                Text(
                    text = "$categoryUsage h",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

        }
    }

}