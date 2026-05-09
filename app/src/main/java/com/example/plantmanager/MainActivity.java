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

public class MainActivity extends AppCompatActivity {

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


        RecyclerView recyclerView = findViewById(R.id.plantsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PlantsListAdapter adapter = new PlantsListAdapter();
        adapter.getPlants().add(new Plant(0, "Ромашка", 4, "Ромашка обыкновенная", "o"));
        adapter.getPlants().add(new Plant(1, "Тюльпан", 2, "Тюльпан обыкновенный", "i"));
        recyclerView.setAdapter(adapter);
    }

    public void openAddPage(View v) {
        Intent intent = new Intent(this, AddPlant.class);
        startActivity(intent);
    }
}