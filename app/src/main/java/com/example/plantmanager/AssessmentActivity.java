package com.example.plantmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.List;

public class AssessmentActivity extends AppCompatActivity {
    private int plantId = -1;
    private int actionId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_assessment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        plantId = getIntent().getIntExtra("plantId", -1);
        actionId = getIntent().getIntExtra("actionId", -1);

        TextView plantName = findViewById(R.id.plantNameText);
        plantName.setText(PlantDB.getPlantById(plantId, this).getName());

        TextView activityType = findViewById(R.id.activityType);
        activityType.setText(PlantDB.getActionById(actionId, this).getActionType());

        Spinner leafsSpinner = findViewById(R.id.spinner);
        List<String> leafsOptions = Arrays.asList(
                "Зелёный", "Жёлтый", "Коричневый", "Дырки на листьях", "Липкий налёт", "Белый налёт"
        );
        ArrayAdapter<String> leafsAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, leafsOptions);
        leafsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leafsSpinner.setAdapter(leafsAdapter);

        Spinner drynessSpinner = findViewById(R.id.spinner2);
        List<String> drynessOptions = Arrays.asList(
                "Влажная", "Слегка влажная", "Нейтральная", "Слегка сухая", "Сухая"
        );
        ArrayAdapter<String> drynessAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, drynessOptions);
        drynessAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drynessSpinner.setAdapter(drynessAdapter);

        Spinner branchesSpinner = findViewById(R.id.spinner3);
        List<String> branchesOptions = Arrays.asList(
                "Здоровые", "Поникшие", "Сухие", "Следы вредителей", "Бледные", "Темные"
        );
        ArrayAdapter<String> branchesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, branchesOptions);
        branchesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchesSpinner.setAdapter(branchesAdapter);
    }
    public void makeAssessmentAndMarkAction(View v) {
        Spinner leafsSpinner    = findViewById(R.id.spinner);
        Spinner drynessSpinner  = findViewById(R.id.spinner2);
        Spinner branchesSpinner = findViewById(R.id.spinner3);

        String leafsCondition    = leafsSpinner.getSelectedItem().toString();
        int earthDryness         = drynessSpinner.getSelectedItemPosition();
        String branchesCondition = branchesSpinner.getSelectedItem().toString();

        PlantCondition condition = new PlantCondition(earthDryness, leafsCondition, branchesCondition);

        if (plantId != -1) {
            PlantDB.addCondition(plantId, condition, this);
            PlantDB.updateIntervals(plantId, this, PlantDB.getActionById(actionId, this).getActionType());
        }
        if (actionId != -1) {
            PlantDB.markActionDone(actionId, this);
            ScheduleActivity.adapter.setActionsList(PlantDB.getAllActions(this));
        }
        MainActivity.selfLink.changeMakeTodayBlock();
        finish();
    }

    public void closePage(View v) {
        finish();
    }
}