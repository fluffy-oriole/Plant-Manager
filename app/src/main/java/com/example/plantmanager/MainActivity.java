package com.example.plantmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static MainActivity selfLink;
    public static PlantCareActionsAdapter todayAdapter = new PlantCareActionsAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        RecyclerView todayRecycler = findViewById(R.id.todayActionsList);
        todayRecycler.setLayoutManager(new LinearLayoutManager(this));
        todayRecycler.setAdapter(todayAdapter);
        changeMakeTodayBlock();

        selfLink = this;
    }

    public void openSchedule(View v) {
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }

    public void openPlantsList(View v) {
        Intent intent = new Intent(this, PlantsListActivity.class);
        startActivity(intent);
    }

    public void changeMakeTodayBlock() {
        ArrayList<PlantCareAction> todayList = PlantDB.getTodayActions(this);
        todayAdapter.setActionsList(todayList);
        TextView makeTodayText = findViewById(R.id.todayTitle);
        RecyclerView todayRecycler = findViewById(R.id.todayActionsList);
        if (Objects.equals(todayList.size(), 0)) {
            makeTodayText.setVisibility(View.GONE);
            todayRecycler.setVisibility(View.GONE);
        }
        else {
            makeTodayText.setVisibility(View.VISIBLE);
            todayRecycler.setVisibility(View.VISIBLE);
        }
    }
}