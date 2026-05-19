package com.example.plantmanager;

import static android.content.Context.MODE_PRIVATE;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;


public class PlantDB {
    private static SQLiteDatabase db;

    public static void connectToDB(Context context) {
        db = context.openOrCreateDatabase("plants.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS \"plant_conditions\" (\n" +
                "\t\"id\" INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\t\"plant_id\"\tINTEGER,\n" +
                "\t\"assessment_date\"\tTEXT,\n" +
                "\t\"earth_dryness\"\tINTEGER,\n" +
                "\t\"leafs_condition\"\tTEXT,\n" +
                "\t\"branches_condition\"\tTEXT,\n" +
                "\tFOREIGN KEY(\"plant_id\") REFERENCES \"plants\"(\"id\"),\n" +
                "\tCHECK(\"earth_dryness\" >= 0 AND \"earth_dryness\" <= 5)\n" +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"plants\" (\n" +
                "\t\"id\" INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\t\"name\"\tTEXT UNIQUE,\n" +
                "\t\"age\"\tINTEGER,\n" +
                "\t\"type\"\tTEXT,\n" +
                "\t\"indoor_outdoor\"\tTEXT,\n" +
                "\tCHECK(\"indoor_outdoor\" = 'i' OR \"indoor_outdoor\" = 'o')\n" +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"plants_care_rules\" (\n" +
                "\t\"id\" INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\t\"plant_type\"\tTEXT UNIQUE,\n" +
                "\t\"watering_interval_days\"\tINTEGER,\n" +
                "\t\"fertilizer_interval_days\"\tINTEGER,\n" +
                "\t\"lighting_type\"\tTEXT,\n" +
                "\t\"spraying_interval_days \"\tINTEGER,\n" +
                "\t\"min_temperature\"\tINTEGER,\n" +
                "\t\"max_tempreature\"\tINTEGER,\n" +
                "\t\"description\"\tINTEGER\n" +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"schedule\" (\n" +
                "\t\"id\" INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\t\"plant_id\"\tINTEGER,\n" +
                "\t\"is_done\"\tINTEGER NOT NULL DEFAULT 0,\n" +
                "\t\"action_date\"\tINTEGER,\n" +
                "\t\"action_type\"\tTEXT,\n" +
                "\t\"supplies_amount\"\tINTEGER,\n" +
                "\tFOREIGN KEY(\"plant_id\") REFERENCES \"plants\"(\"id\"),\n" +
                "\tCHECK(\"is_done\" = 1 OR \"is_done\" = 0)\n" +
                ");");
    }

    public static void closeConnection() {
        db.close();
    }

    public static ArrayList<Plant> getAllPlants(Context context) {
        connectToDB(context);
        String sql = "SELECT * FROM plants";
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<Plant> readPlants = new ArrayList<>();
        while (cursor.moveToNext()) {
            Plant currentPlant = new Plant(cursor.getInt(0),
                    cursor.getString(1), cursor.getInt(2),
                    cursor.getString(3), cursor.getString(4));
            readPlants.add(currentPlant);
        }
        cursor.close();
        closeConnection();
        return readPlants;
    }

    public static void addPlant(Plant plant, Context context) {
        connectToDB(context);
        String sql = "INSERT INTO plants (name, age, type, indoor_outdoor) VALUES (?, ?, ?, ?)";
        Object[] args = {plant.getName(), plant.getAge(), plant.getType(), plant.getIndoor_outdoor()};
        db.execSQL(sql, args);
        closeConnection();
    }

    public static void deletePlant(Plant plant, Context context) {
        connectToDB(context);
        Integer id = plant.getId();
        String sql = "DELETE FROM plants WHERE id = ?";
        Object[] args = {id};
        db.execSQL(sql, args);
        closeConnection();
    }

    public static void editPlant(Plant plant, Context context) {
        connectToDB(context);
        String sql = "UPDATE plants SET name=?, age=?, type=?, indoor_outdoor=? WHERE id = ?";
        Object[] args = {plant.getName(), plant.getAge(), plant.getType(), plant.getIndoor_outdoor(), plant.getId()};
        db.execSQL(sql, args);
        closeConnection();
    }

    public static void addAction(PlantCareAction action, Context context) {
        connectToDB(context);
        String sql = "INSERT INTO schedule (plant_id, action_date, action_type, supplies_amount) VALUES (?, ?, ?, ?)";
        Object[] args = {action.getPlantId(), action.getActionDate(), action.getActionType(), action.getSuppliesAmount()};
        db.execSQL(sql, args);
        closeConnection();
    }

    public static ArrayList<PlantCareAction> getAllActions(Context context) {
        connectToDB(context);
        String sql = "SELECT * FROM schedule";
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<PlantCareAction> readActions = new ArrayList<>();
        while (cursor.moveToNext()) {
            PlantCareAction currentAction = new PlantCareAction(cursor.getInt(0),
                    cursor.getInt(1), cursor.getInt(2),
                    new Date(cursor.getLong(3)), cursor.getString(4),
                    cursor.getInt(5)
                );
            readActions.add(currentAction);
        }
        cursor.close();
        closeConnection();
        return readActions;
    }
}
