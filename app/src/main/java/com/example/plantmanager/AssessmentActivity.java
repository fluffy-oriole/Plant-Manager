package com.example.plantmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AssessmentActivity extends AppCompatActivity {
    private int plantId = -1;
    private int actionId = -1;
    private boolean needToBePostponed = true;
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
                "Влажная", "Слегка влажная", "Нормальная", "Слегка сухая", "Сухая"
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

        TextView actionHintText = findViewById(R.id.textView8);
        PlantCareAction currentAction = PlantDB.getActionById(actionId, this);


        leafsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                makeOffsetIfNeed(currentAction, actionHintText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        drynessSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                makeOffsetIfNeed(currentAction, actionHintText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
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
            if (needToBePostponed)
                PlantDB.postponeWatering(plantId, 2, this);
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

    private void makeOffsetIfNeed(PlantCareAction currentAction, TextView actionHintText) {
        boolean isWatering = false;
        if (currentAction.getActionType().equals("Полив"))
            isWatering = true;

        boolean soilTooWet = false;
        Spinner drynessSpinner = findViewById(R.id.spinner2);
        if (drynessSpinner.getSelectedItemPosition() <= 1)
            soilTooWet = true;

        boolean leafsAreYellow = false;
        Spinner leafsSpinner = findViewById(R.id.spinner);
        int leafsPosition = leafsSpinner.getSelectedItemPosition();
        if (leafsPosition == 1)
            leafsAreYellow = true;

        if (isWatering && (soilTooWet || leafsAreYellow)) {
            actionHintText.setText("Отложить на 2 дня:");
            needToBePostponed = true;
        }
        else {
            actionHintText.setText("Выполните:");
            needToBePostponed = false;
        }
    }
}