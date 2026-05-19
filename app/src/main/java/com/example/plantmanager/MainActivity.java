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

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static PlantsListAdapter adapter = new PlantsListAdapter();

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
        adapter.setPlants(PlantDB.getAllPlants(this));
        recyclerView.setAdapter(adapter);

        PlantCareAction action1 = new PlantCareAction(0, 0, 0,
                new Date(), "Полив", 100);
        PlantDB.addAction(action1, this);

        PlantCareAction action2 = new PlantCareAction(1, 2, 0,
                new Date(), "Удобрение", 50);
        PlantDB.addAction(action2, this);

        PlantCareAction action3 = new PlantCareAction(3, 3, 0,
                new Date(), "Опрыскивание", 100);
        PlantDB.addAction(action3, this);

        Plant rose = new Plant(0, "Роза красная", 2,
                "Роза обыкновенная", "o");
        PlantDB.addPlant(rose, this);

        Plant violet = new Plant(0, "Фиалки", 2,
                "Фиалка синяя", "o");
        PlantDB.addPlant(violet, this);

        Plant sunflower = new Plant(0, "Подсолнух", 2,
                "Подсолнух обыкновенный", "o");
        PlantDB.addPlant(sunflower, this);

    }

    public void openAddPage(View v) {
        Intent intent = new Intent(this, AddPlant.class);
        startActivity(intent);
    }

    public void openSchedule(View v) {
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }
}