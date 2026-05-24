package com.example.plantmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class AddNewType extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_type);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void closePage(View v) {
        finish();
    }

    public void addNewType(View v) {
        TextInputEditText inputName = findViewById(R.id.inputName);
        TextInputEditText inputWater = findViewById(R.id.inputWater);
        TextInputEditText inputFertilizer= findViewById(R.id.inputFertilizer);
        TextInputEditText inputSpraying = findViewById(R.id.inputSpraying);

        String name = inputName.getText().toString();
        String water = inputWater.getText().toString();
        String fertilizer = inputFertilizer.getText().toString();
        String spraying = inputSpraying.getText().toString();

        if (Objects.equals(name, "") || Objects.equals(water, "") ||
                Objects.equals(fertilizer, "") || Objects.equals(spraying, "")) {
            Toast.makeText(v.getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                int waterDays = Integer.parseInt(water);
                int fertilizerDays = Integer.parseInt(fertilizer);
                int sprayingDays = Integer.parseInt(spraying);

                PlantDB.addNewType(v.getContext(), name, waterDays, fertilizerDays, sprayingDays);
                AddPlant.selfLink.updatePlantTypes();
                finish();
            }
            catch (NumberFormatException e) {
                Toast.makeText(v.getContext(), "Допустимы только числа", Toast.LENGTH_SHORT).show();
            }
        }
    }
}