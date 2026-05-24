package com.example.plantmanager;

public class Plant {
    private final Integer id;
    private String name;
    private String type;
    private String indoor_outdoor;

    public Plant(Integer plantId, String plantName, String plantType, String plantIndoor_outdoor) {
        id = plantId;
        name = plantName;
        type = plantType;
        indoor_outdoor = plantIndoor_outdoor;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getIndoor_outdoor() {
        return indoor_outdoor;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIndoor_outdoor(String indoor_outdoor) {
        this.indoor_outdoor = indoor_outdoor;
    }
}
