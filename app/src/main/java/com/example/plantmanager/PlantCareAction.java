package com.example.plantmanager;

import java.util.Date;
import java.text.SimpleDateFormat;

public class PlantCareAction {
    private final Integer id;
    private final Integer plantId;
    private final Integer isDone;
    private final Date actionDate;
    private final String actionType;

    public PlantCareAction(Integer identifier, Integer actionPlantId, Integer isActionDone, Date plannedDate, String type) {
        id = identifier;
        plantId = actionPlantId;
        isDone = isActionDone;
        actionDate = plannedDate;
        actionType = type;
    }

    public Integer getId() {
        return id;
    }

    public Integer getPlantId() {
        return plantId;
    }

    public String getActionType() {
        return actionType;
    }

    public String getActionDate_s() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        return sdf.format(actionDate);
    }

    public Date getActionDate() {
        return actionDate;
    }

}
