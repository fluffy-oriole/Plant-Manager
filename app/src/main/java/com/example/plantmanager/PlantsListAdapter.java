package com.example.plantmanager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlantsListAdapter extends RecyclerView.Adapter<PlantsListAdapter.PlantViewHolder> {

    private static final ArrayList<Plant> plants = new ArrayList<>();

    public ArrayList<Plant> getPlants() {
        return plants;
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant currentPlant = plants.get(position);
        holder.plantName.setText(currentPlant.getName());
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    public static class PlantViewHolder extends RecyclerView.ViewHolder {
        TextView plantName;
        public PlantViewHolder(View view) {
            super(view);
            plantName = view.findViewById(R.id.plantName);
        }
    }
}