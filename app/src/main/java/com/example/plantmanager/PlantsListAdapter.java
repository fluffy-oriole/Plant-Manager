package com.example.plantmanager;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class PlantsListAdapter extends RecyclerView.Adapter<PlantsListAdapter.PlantViewHolder> {
    private static ArrayList<Plant> plants = new ArrayList<>();

    public void setPlants(ArrayList<Plant> newPlants) {
        plants = newPlants;
        notifyDataSetChanged();
    }

    public static ArrayList<Plant> getPlants() {
        return plants;
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant, parent, false);
        return new PlantViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant currentPlant = getPlants().get(position);
        holder.plant = currentPlant;
        holder.plantName.setText(currentPlant.getName());
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    public static class PlantViewHolder extends RecyclerView.ViewHolder {
        TextView plantName;
        ImageButton editButton;
        ImageButton deleteButton;
        Plant plant;
        PlantsListAdapter parent;
        public PlantViewHolder(View view, PlantsListAdapter adapter) {
            super(view);
            parent = adapter;
            plantName = view.findViewById(R.id.actionType);
            editButton = view.findViewById(R.id.openActionButton);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), EditActivity.class);
                    intent.putExtra("plant_id", plant.getId().toString());
                    intent.putExtra("plant_name", plant.getName());
                    intent.putExtra("plant_age", plant.getAge().toString());
                    intent.putExtra("plant_type", plant.getType());
                    intent.putExtra("indoor_outdoor", plant.getIndoor_outdoor());
                    v.getContext().startActivity(intent);
                }
            });

            deleteButton = view.findViewById(R.id.deletePlantButton);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlantDB.deletePlant(plant, v.getContext());
                    parent.setPlants(PlantDB.getAllPlants(v.getContext()));
                    MainActivity.selfLink.changeMakeTodayBlock();
                }
            });
        }
    }
}