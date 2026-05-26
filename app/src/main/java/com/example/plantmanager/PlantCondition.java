package com.example.plantmanager;

import java.util.Date;

public class PlantCondition {
    private final Date assessmentDate;
    private final Integer earthDryness;
    private final String leafsCondition;
    private final String branchesCondition;

    public PlantCondition(Integer dryness, String leafs, String branches) {
        assessmentDate = new Date();
        earthDryness = dryness;
        leafsCondition = leafs;
        branchesCondition = branches;
    }

    public Date getAssessmentDate() {
        return assessmentDate;
    }

    public Integer getEarthDryness() {
        return earthDryness;
    }

    public String getLeafsCondition() {
        return leafsCondition;
    }

    public String getBranchesCondition() {
        return branchesCondition;
    }
}
