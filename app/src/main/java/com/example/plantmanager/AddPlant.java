package com.example.plantmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Objects;

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

        Spinner plantTypeSpinner = findViewById(R.id.plantTypeSpinner);
        ArrayList<String> plantTypes = PlantDB.getAllPlantTypes(this);
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, plantTypes);
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plantTypeSpinner.setAdapter(typesAdapter);
    }

    public void addPlantAndClosePage(View v) {
        TextInputEditText nameField = findViewById(R.id.nameField);
        Spinner plantTypeSpinner = findViewById(R.id.plantTypeSpinner);
        TextInputEditText ageField = findViewById(R.id.ageField);
        Switch indoor_outdoor_switch = findViewById(R.id.indoor_outdoor_switch);
        String name;
        String type;
        int age;
        String indoor_outdoor;
        if (nameField.getText() != null && plantTypeSpinner.getSelectedItem() != null
                && ageField.getText() != null && indoor_outdoor_switch != null) {
            name = nameField.getText().toString();
            if (Objects.equals("", name)) {
                Toast.makeText(v.getContext(), "Введите название", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (name.length() > 16) {
                Toast.makeText(v.getContext(), "Слишком длинное название", Toast.LENGTH_SHORT).show();
                return;
            }
            type = plantTypeSpinner.getSelectedItem().toString();

            if (Objects.equals(ageField.getText().toString(), "")) {
                Toast.makeText(v.getContext(), "Введите возраст", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                age = Integer.parseInt(ageField.getText().toString());
            }
            catch (NumberFormatException e) {
                Toast.makeText(v.getContext(), "В поле возраста могут быть только цифры", Toast.LENGTH_SHORT).show();
                return;
            }


            if (indoor_outdoor_switch.isChecked()) {
                indoor_outdoor = "i";
            }
            else {
                indoor_outdoor = "o";
            }
            Plant plant = new Plant(-1, name, age, type, indoor_outdoor);
            PlantDB.addPlant(plant, this);
            PlantsListActivity.adapter.setPlants(PlantDB.getAllPlants(this));
            MainActivity.selfLink.changeMakeTodayBlock();
            finish();
        }
    }

    public void closePage(View v) {
        finish();
    }
}