package com.example.plantmanager;

import java.util.Date;
import java.text.SimpleDateFormat;

public class PlantCareAction {
    Integer id;
    Integer plantId;
    Integer isDone;
    Date actionDate;
    String actionType;

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

    public Integer getIsDone() {
        return isDone;
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
