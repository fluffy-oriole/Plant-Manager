package com.example.plantmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class PlantCareActionsAdapter extends RecyclerView.Adapter<PlantCareActionsAdapter.ActionViewHolder> {
    ArrayList<PlantCareAction> actionsList = new ArrayList<>();

    public void setActionsList(ArrayList<PlantCareAction> newActions) {
        actionsList = newActions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlantCareActionsAdapter.ActionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_action, parent, false);
        return new PlantCareActionsAdapter.ActionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantCareActionsAdapter.ActionViewHolder holder, int position) {
        PlantCareAction currentAction = actionsList.get(position);
        holder.action = currentAction;
        holder.actionType.setText(currentAction.getActionType());
        String currentPlantName = "";
        for (Plant plant : PlantsListAdapter.getPlants()) {
            if (Objects.equals(plant.getId(), currentAction.getId())) {
                currentPlantName = plant.getName();
                break;
            }
        }
        holder.actionPlant.setText(currentPlantName);
    }

    @Override
    public int getItemCount() {
        return actionsList.size();
    }
    public static class ActionViewHolder extends RecyclerView.ViewHolder {
        PlantCareAction action;
        TextView actionType;
        TextView actionPlant;
        ImageButton openActionButton;

        public ActionViewHolder(@NonNull View itemView) {
            super(itemView);
            actionType = itemView.findViewById(R.id.actionType);
            actionPlant = itemView.findViewById(R.id.actionPlant);
            openActionButton = itemView.findViewById(R.id.openActionButton);

            openActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }
}
