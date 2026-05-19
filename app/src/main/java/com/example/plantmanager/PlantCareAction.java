package com.example.plantmanager;

import java.util.Date;

public class PlantCareAction {
    Integer id;
    Integer plantId;
    Integer isDone;
    Date actionDate;
    String actionType;
    Integer suppliesAmount;

    public PlantCareAction(Integer identifier, Integer actionPlantId, Integer isActionDone, Date plannedDate, String type, Integer amount) {
        id = identifier;
        plantId = actionPlantId;
        isDone = isActionDone;
        actionDate = plannedDate;
        actionType = type;
        suppliesAmount = amount;
    }

    public Integer getId() {
        return id;
    }

    public Integer getPlantId() {
        return plantId;
    }

    public Integer getIsDone() {
        return isDone;
    }

    public Integer getSuppliesAmount() {
        return suppliesAmount;
    }

    public String getActionType() {
        return actionType;
    }

    public Date getActionDate() {
        return actionDate;
    }
}
