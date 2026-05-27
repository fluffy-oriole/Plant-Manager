package com.example.plantmanager.controller;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantmanager.R;
import com.example.plantmanager.model.PlantDB;

public class ScheduleActivity extends AppCompatActivity {
    public static PlantCareActionsAdapter adapter = new PlantCareActionsAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scheduleMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RecyclerView recyclerView = findViewById(R.id.CareActionsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setActionsList(PlantDB.getAllActions(this));
        recyclerView.setAdapter(adapter);
    }

    public void closePage(View v) {
        finish();
    }
}