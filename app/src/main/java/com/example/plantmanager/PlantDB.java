package com.example.plantmanager;

import static android.content.Context.MODE_PRIVATE;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


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
                "\t\"spraying_interval_days\"\tINTEGER,\n" +
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

        int countOfTypes = 0;
        String sql = "SELECT count(*) FROM plants_care_rules";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        countOfTypes = cursor.getInt(0);
        if (Objects.equals(countOfTypes, 0)) {
            db.execSQL("INSERT INTO plants_care_rules (plant_type, watering_interval_days, fertilizer_interval_days, lighting_type, spraying_interval_days, min_temperature, max_tempreature, description) " +
                    "VALUES ('роза', 5, 10, 'Прямой солнечный свет', 15, -5, 5, 'Описание')");
        }
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

        String insertSql = "INSERT INTO plants (name, age, type, indoor_outdoor) VALUES (?, ?, ?, ?)";
        Object[] insertArgs = {plant.getName(), plant.getAge(), plant.getType(), plant.getIndoor_outdoor()};
        db.execSQL(insertSql, insertArgs);

        Cursor idCursor = db.rawQuery("SELECT last_insert_rowid()", null);
        int newPlantId = -1;
        if (idCursor.moveToFirst()) {
            newPlantId = idCursor.getInt(0);
        }
        idCursor.close();

        if (newPlantId != -1) {
            Cursor rulesCursor = db.rawQuery(
                    "SELECT watering_interval_days, fertilizer_interval_days, spraying_interval_days " +
                            "FROM plants_care_rules WHERE plant_type = ?",
                    new String[]{plant.getType().toLowerCase()}
            );

            if (rulesCursor.moveToFirst()) {
                int wateringInterval   = rulesCursor.getInt(0);
                int fertilizerInterval = rulesCursor.getInt(1);
                int sprayingInterval   = rulesCursor.getInt(2);

                generateScheduleEntries(newPlantId, "Полив", wateringInterval);
                generateScheduleEntries(newPlantId, "Удобрение", fertilizerInterval);
                generateScheduleEntries(newPlantId, "Опрыскивание", sprayingInterval);
            }
            rulesCursor.close();
        }
        closeConnection();
    }

    public static Plant getPlantById(int plantId, Context context) {
        connectToDB(context);
        Cursor cursor = db.rawQuery("SELECT * FROM plants WHERE id = ?",
                new String[]{String.valueOf(plantId)});
        Plant plant = null;
        if (cursor.moveToFirst()) {
            plant = new Plant(cursor.getInt(0), cursor.getString(1),
                    cursor.getInt(2), cursor.getString(3), cursor.getString(4));
        }
        cursor.close();
        closeConnection();
        return plant;
    }

    private static void generateScheduleEntries(int plantId, String actionType, int intervalDays) {
        if (intervalDays <= 0)
            return;

        String sql = "INSERT INTO schedule (plant_id, action_date, action_type, supplies_amount) VALUES (?, ?, ?, ?)";

        Calendar cal = Calendar.getInstance();

        long msInTwoWeeks = (long) 14 * 24 * 60 * 60 * 1000;
        long endTime = cal.getTimeInMillis() + msInTwoWeeks;

        while (cal.getTimeInMillis() <= endTime) {
            db.execSQL(sql, new Object[]{
                    plantId,
                    cal.getTimeInMillis(),
                    actionType,
                    0 // TODO: сделать в бд количество ресурсов
            });
            cal.add(Calendar.DAY_OF_YEAR, intervalDays);
        }
    }

    public static void deletePlant(Plant plant, Context context) {
        connectToDB(context);
        Integer id = plant.getId();
        db.execSQL("DELETE FROM schedule WHERE plant_id = ?", new Object[]{id});
        db.execSQL("DELETE FROM plants WHERE id = ?", new Object[]{id});
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
        Object[] args = {action.getPlantId(), action.getActionDate().getTime(), action.getActionType(), action.getSuppliesAmount()};
        db.execSQL(sql, args);
        closeConnection();
    }

    public static ArrayList<PlantCareAction> getAllActions(Context context) {
        connectToDB(context);
        String sql = "SELECT * FROM schedule ORDER BY action_date";
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

    public static void addCondition(int plantId, PlantCondition condition, Context context) {
        connectToDB(context);
        String sql = "INSERT INTO plant_conditions (plant_id, assessment_date, earth_dryness, leafs_condition, branches_condition) " +
                "VALUES (?, ?, ?, ?, ?)";
        db.execSQL(sql, new Object[]{
                plantId,
                condition.getAssessmentDate().getTime(),
                condition.getEarthDryness(),
                condition.getLeafsCondition(),
                condition.getBranchesCondition()
        });
        closeConnection();
    }

    public static void markActionDone(int actionId, Context context) {
        connectToDB(context);
        db.execSQL("UPDATE schedule SET is_done = 1 WHERE id = ?", new Object[]{actionId});
        closeConnection();
    }
}