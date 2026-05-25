package com.example.plantmanager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

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
        String plantName = PlantDB.getPlantById(holder.action.getPlantId(), holder.itemView.getContext()).getName();
        holder.actionPlant.setText(plantName);
        holder.actionDate.setText(currentAction.getActionDate_s());
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        if (currentAction.getActionDate().before(today.getTime())) {
            holder.actionDate.setTextColor(Color.RED);
        }
        holder.openActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isTodayAssessmentDone = PlantDB.isTodayAssessmentDone(holder.action.getPlantId(), v.getContext());
                if (isTodayAssessmentDone) {
                    PlantDB.markActionDone(holder.action.getId(), v.getContext());
                    ScheduleActivity.adapter.setActionsList(PlantDB.getAllActions(v.getContext()));
                    MainActivity.selfLink.changeMakeTodayBlock();
                }
                else {
                    Intent intent = new Intent(v.getContext(), AssessmentActivity.class);
                    intent.putExtra("plantId", holder.action.getPlantId());
                    intent.putExtra("actionId", holder.action.getId());
                    v.getContext().startActivity(intent);
                }
            }
        });

        Calendar endOfDay = Calendar.getInstance();
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);
        endOfDay.set(Calendar.MILLISECOND, 999);

        if (currentAction.getActionDate().after(endOfDay.getTime())) {
            holder.openActionButton.setEnabled(false);
            holder.openActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));

        }
        else {
            holder.openActionButton.setEnabled(true);
            holder.openActionButton.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.accentColor));
        }

    }

    @Override
    public int getItemCount() {
        return actionsList.size();
    }
    public static class ActionViewHolder extends RecyclerView.ViewHolder {
        PlantCareAction action;
        TextView actionType;
        TextView actionPlant;
        TextView actionDate;
        ImageButton openActionButton;

        public ActionViewHolder(@NonNull View itemView) {
            super(itemView);
            actionType = itemView.findViewById(R.id.actionType);
            actionPlant = itemView.findViewById(R.id.actionPlant);
            openActionButton = itemView.findViewById(R.id.openActionButton);
            actionDate = itemView.findViewById(R.id.actionDate);
        }
    }
}
