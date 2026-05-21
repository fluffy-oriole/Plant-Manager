package com.example.plantmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Objects;

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editPlantLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputEditText nameEdit = findViewById(R.id.nameField);
        nameEdit.setText(getIntent().getStringExtra("plant_name"));

        TextInputEditText ageEdit = findViewById(R.id.ageField);
        ageEdit.setText(getIntent().getStringExtra("plant_age"));

        Spinner plantTypeSpinner = findViewById(R.id.plantTypeSpinner);
        ArrayList<String> plantTypes = PlantDB.getAllPlantTypes(this);
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, plantTypes);
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plantTypeSpinner.setAdapter(typesAdapter);

// Выбираем текущий тип растения
        String currentType = getIntent().getStringExtra("plant_type");
        int currentTypeIndex = plantTypes.indexOf(currentType);
        if (currentTypeIndex != -1) {
            plantTypeSpinner.setSelection(currentTypeIndex);
        }

        Switch typeSwitch = findViewById(R.id.indoor_outdoor_switch);
        if (Objects.equals(getIntent().getStringExtra("indoor_outdoor"), "i")) {
            typeSwitch.setChecked(true);
        }
        else if (Objects.equals(getIntent().getStringExtra("indoor_outdoor"), "o")) {
            typeSwitch.setChecked(false);
        }
    }

    public void editPlant(View v) {
        Plant currentPlant = null;
        for (Plant plant : PlantsListAdapter.getPlants()) {
            if (Objects.equals(plant.getId().toString(), getIntent().getStringExtra("plant_id"))) {
                currentPlant = plant;
                break;
            }
        }

        TextInputEditText nameEdit = findViewById(R.id.nameField);
        TextInputEditText ageEdit = findViewById(R.id.ageField);
        Spinner plantTypeSpinner = findViewById(R.id.plantTypeSpinner);
        Switch indoorOutdoorSwitch = findViewById(R.id.indoor_outdoor_switch);

        String newName;
        int newAge;
        String newType;
        String newIndoorOutdoor;
        newName = nameEdit.getText().toString();
        newAge = Integer.parseInt(ageEdit.getText().toString());
        newType = plantTypeSpinner.getSelectedItem().toString();
        if (indoorOutdoorSwitch.isChecked()) {
            newIndoorOutdoor = "i";
        }
        else {
            newIndoorOutdoor = "o";
        }

        currentPlant.setName(newName);
        currentPlant.setAge(newAge);
        currentPlant.setType(newType);
        currentPlant.setIndoor_outdoor(newIndoorOutdoor);
        PlantDB.editPlant(currentPlant, this);
        PlantsListActivity.adapter.setPlants(PlantDB.getAllPlants(v.getContext()));
        finish();
    }

    public void closePage(View v) {
        finish();
    }
}