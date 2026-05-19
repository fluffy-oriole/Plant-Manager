package com.example.plantmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class AddPlant extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_plant);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editPlantLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void addPlantAndClosePage(View v) {
        TextInputEditText nameField = findViewById(R.id.nameField);
        TextInputEditText typeField = findViewById(R.id.typeField);
        TextInputEditText ageField = findViewById(R.id.ageField);
        Switch indoor_outdoor_switch = findViewById(R.id.indoor_outdoor_switch);
        String name;
        String type;
        int age;
        String indoor_outdoor;
        if (nameField.getText() != null && typeField.getText() != null
                && ageField.getText() != null && indoor_outdoor_switch != null) {
            name = nameField.getText().toString();
            type = typeField.getText().toString();
            age = Integer.parseInt(ageField.getText().toString());
            if (indoor_outdoor_switch.isChecked()) {
                indoor_outdoor = "i";
            }
            else {
                indoor_outdoor = "o";
            }
            Plant plant = new Plant(-1, name, age, type, indoor_outdoor);
            PlantDB.addPlant(plant, this);
            MainActivity.adapter.setPlants(PlantDB.getAllPlants(this));
            finish();
        }
    }

    public void closeAddPage(View v) {
        finish();
    }
}