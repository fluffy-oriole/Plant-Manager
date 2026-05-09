package com.example.plantmanager;

import java.sql.*;
import java.util.ArrayList;

public class PlantDB {
    private static Connection con;
    private static Statement stab;
    private static ResultSet result;

    public static void connectToDB() throws ClassNotFoundException, SQLException  {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:PlantsDB.db");
    }

    public static void closeConnection() throws SQLException {
        result.close();
        stab.close();
        con.close();
    }

    public static ArrayList<Plant> readPlantsDB()
    {
        try {
            connectToDB();
            stab = con.createStatement();
            result = stab.executeQuery("SELECT * FROM plants");
            ArrayList<Plant> plantsList = new ArrayList<>();
            while(result.next()) {
                Plant newPlant = new Plant(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getInt("age"),
                        result.getString("type"),
                        result.getString("indoor_outdoor"));
                plantsList.add(newPlant);
            }
            closeConnection();
            return plantsList;
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void addPlantDB(Plant plantToAdd) {
        try {
            connectToDB();
            stab = con.createStatement();
            stab.executeUpdate(String.format("INSERT INTO plants (name, age, type) VALUES ('%s', %d, '%s')",
                    plantToAdd.getName(), plantToAdd.getAge(), plantToAdd.getType()));
            closeConnection();
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deletePlantDB(Plant plantToDelete) {
        try {
            connectToDB();
            stab = con.createStatement();
            stab.executeUpdate(String.format("DELETE FROM plants WHERE name = '%s'", plantToDelete.getName()));
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void addAssessmentDB(Plant assessmentPant, PlantCondition condition) {
        try {
            connectToDB();
            stab = con.createStatement();
            result = stab.executeQuery(String.format("SELECT id FROM plants WHERE name = '%s'", assessmentPant.getName()));
            String sql = "INSERT INTO plant_conditions (plant_id, assessment_date, earth_dryness, " +
                    "leafs_condition, branches_condition) VALUES (%d, '%s', %d, '%s', '%s')";
            stab.executeUpdate(String.format(sql, result.getInt("id"), condition.getAssessmentDate(),
                    condition.getEarthDryness(), condition.getLeafsCondition(), condition.getBranchesCondition()));
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void addCareAction(Plant carePlant, PlantCareAction action) {
        try {
            connectToDB();
            stab = con.createStatement();
            String sql = "INSERT INTO schedule (plant_id, action_date, action_type, supplies_amount) VALUES '%d', %s, %s, '%d'";
            stab.executeUpdate(String.format(sql, carePlant.getId(), action.getActionDate().getTime(),
                    action.getActionType(), action.getSuppliesAmount()));
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void changePlant(Plant changedPlant) {
        try {
            connectToDB();
            stab = con.createStatement();
            String sql = "UPDATE TABLE plants SET name = '%s', age = '%d', type = '%s', indoor_outdoor = '%s' WHERE id = '%d'";
            result = stab.executeQuery(String.format(sql, changedPlant.getName(), changedPlant.getAge(),
                    changedPlant.getType(), changedPlant.getIndoor_outdoor(), changedPlant.getId()));
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void setActionMark(PlantCareAction action) {
        try {
            connectToDB();
            stab = con.createStatement();
            String sql = "UPDATE TABLE schedule SET is_done = 1 WHERE id = '%d'";
            stab.executeUpdate(String.format(sql, action.getId()));
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static ArrayList<PlantCareAction> readActionsByPlant(Plant plant) {
        try {
            connectToDB();
            stab = con.createStatement();
            String sql = "SELECT * FROM schedule WHERE plant_id = '%d'";
            result = stab.executeQuery(String.format(sql, plant.getId()));
            ArrayList<PlantCareAction> actions = new ArrayList<>();
            while(result.next()) {
                actions.add(new PlantCareAction(
                        result.getInt("id"),
                        result.getInt("is_done"),
                        new Date(result.getLong("action_date")),
                        result.getString("action_type"),
                        result.getInt("supplies_amount")));
            }
            return actions;
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
