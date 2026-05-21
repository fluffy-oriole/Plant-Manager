package com.example.plantmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PlantsListActivity extends AppCompatActivity {
    public static PlantsListAdapter adapter = new PlantsListAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plants_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.plantsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setPlants(PlantDB.getAllPlants(this));
        recyclerView.setAdapter(adapter);
    }

    public void openAddPage(View v) {
        Intent intent = new Intent(this, AddPlant.class);
        startActivity(intent);
    }

    public void closePage(View v) {
        finish();
    }
}