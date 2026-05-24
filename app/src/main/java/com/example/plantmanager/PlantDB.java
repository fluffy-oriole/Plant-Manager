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
                "\t\"type\"\tTEXT,\n" +
                "\t\"indoor_outdoor\"\tTEXT,\n" +
                "\tCHECK(\"indoor_outdoor\" = 'i' OR \"indoor_outdoor\" = 'o')\n" +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"plants_care_rules\" (\n" +
                "\t\"id\" INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\t\"plant_type\"\tTEXT UNIQUE,\n" +
                "\t\"watering_interval_days\"\tINTEGER,\n" +
                "\t\"fertilizer_interval_days\"\tINTEGER,\n" +
                "\t\"spraying_interval_days\"\tINTEGER\n" +
                ");");

        int countOfTypes;
        String sql = "SELECT count(*) FROM plants_care_rules";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        countOfTypes = cursor.getInt(0);
        cursor.close();
        if (countOfTypes < 2) {
            db.execSQL("INSERT INTO plants_care_rules (plant_type, watering_interval_days, fertilizer_interval_days, spraying_interval_days) " +
                    "VALUES ('Роза', 5, 10, 15)");
            db.execSQL("INSERT INTO plants_care_rules (plant_type, watering_interval_days, fertilizer_interval_days, spraying_interval_days) " +
                    "VALUES ('Орхидея', 7, 14, 18)");
        }

        db.execSQL("CREATE TABLE IF NOT EXISTS \"schedule\" (\n" +
                "\t\"id\" INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\t\"plant_id\"\tINTEGER,\n" +
                "\t\"is_done\"\tINTEGER NOT NULL DEFAULT 0,\n" +
                "\t\"action_date\"\tINTEGER,\n" +
                "\t\"action_type\"\tTEXT,\n" +
                "\t\"is_one_time\"\tINTEGER NOT NULL DEFAULT 0,\n" +
                "\tFOREIGN KEY(\"plant_id\") REFERENCES \"plants\"(\"id\"),\n" +
                "\tCHECK(\"is_done\" = 1 OR \"is_done\" = 0)\n" +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS plant_care_intervals (\n" +
                "plant_id INTEGER UNIQUE,\n" +
                "watering_interval_days INTEGER,\n" +
                "fertilizer_interval_days INTEGER,\n" +
                "spraying_interval_days INTEGER,\n" +
                "FOREIGN KEY(plant_id) REFERENCES plants(id)\n" +
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
                    cursor.getString(1), cursor.getString(2),
                    cursor.getString(3));
            readPlants.add(currentPlant);
        }
        cursor.close();
        closeConnection();
        return readPlants;
    }

    public static void addPlant(Plant plant, Context context) {
        connectToDB(context);

        String insertSql = "INSERT INTO plants (name, type, indoor_outdoor) VALUES (?, ?, ?)";
        Object[] insertArgs = {plant.getName(), plant.getType(), plant.getIndoor_outdoor()};
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
                    new String[]{plant.getType()}
            );

            if (rulesCursor.moveToFirst()) {
                int wateringInterval    = rulesCursor.getInt(0);
                int fertilizerInterval  = rulesCursor.getInt(1);
                int sprayingInterval    = rulesCursor.getInt(2);

                if (Objects.equals(plant.getIndoor_outdoor(), "o")) {
                    wateringInterval -= (int)(wateringInterval * 0.2);
                }

                db.execSQL(
                        "INSERT INTO plant_care_intervals (plant_id, watering_interval_days, fertilizer_interval_days, spraying_interval_days) VALUES (?, ?, ?, ?)",
                        new Object[]{newPlantId, wateringInterval, fertilizerInterval, sprayingInterval}
                );

                long today = Calendar.getInstance().getTimeInMillis();
                String sql2 = "INSERT INTO schedule (plant_id, action_date, action_type) VALUES (?, ?, ?)";

                db.execSQL(sql2, new Object[]{newPlantId, today, "Полив"});
                db.execSQL(sql2, new Object[]{newPlantId, today, "Удобрение"});
                if (Objects.equals(plant.getIndoor_outdoor(), "i")) {
                    db.execSQL(sql2, new Object[]{newPlantId, today, "Опрыскивание"});
                }
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
                    cursor.getString(2), cursor.getString(3));
        }
        cursor.close();
        closeConnection();
        return plant;
    }

    public static PlantCareAction getActionById(int actionId, Context context) {
        connectToDB(context);
        Cursor cursor = db.rawQuery("SELECT * FROM schedule WHERE id = ?",
                new String[]{String.valueOf(actionId)});
        PlantCareAction action = null;
        if (cursor.moveToFirst()) {
            action = new PlantCareAction(cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    new Date(cursor.getLong(3)),
                    cursor.getString(4));
        }
        cursor.close();
        closeConnection();
        return action;
    }

    private static void generateScheduleEntries(int plantId, String actionType, int intervalDays, int isOneTime) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, intervalDays);

        db.execSQL(
                "INSERT INTO schedule (plant_id, action_date, action_type, is_one_time) VALUES (?, ?, ?, ?)",
                new Object[]{plantId, cal.getTimeInMillis(), actionType, isOneTime}
        );
    }

    public static void deletePlant(Plant plant, Context context) {
        connectToDB(context);
        Integer id = plant.getId();
        db.execSQL("DELETE FROM schedule WHERE plant_id = ?", new Object[]{id});
        db.execSQL("DELETE FROM plant_care_intervals WHERE plant_id = ?", new Object[]{id});
        db.execSQL("DELETE FROM plants WHERE id = ?", new Object[]{id});
        closeConnection();
    }

    public static void editPlant(Plant plant, Context context) {
        connectToDB(context);

        // Читаем старые значения до обновления
        Cursor oldCursor = db.rawQuery(
                "SELECT type, indoor_outdoor FROM plants WHERE id = ?",
                new String[]{String.valueOf(plant.getId())}
        );
        String oldType = null;
        String oldIndoorOutdoor = null;
        if (oldCursor.moveToFirst()) {
            oldType           = oldCursor.getString(0);
            oldIndoorOutdoor  = oldCursor.getString(1);
        }
        oldCursor.close();

        db.execSQL("UPDATE plants SET name=?, type=?, indoor_outdoor=? WHERE id = ?",
                new Object[]{plant.getName(), plant.getType(), plant.getIndoor_outdoor(), plant.getId()});

        boolean scheduleOutdated = !Objects.equals(oldType, plant.getType())
                || !Objects.equals(oldIndoorOutdoor, plant.getIndoor_outdoor());

        if (scheduleOutdated) {
            db.execSQL("DELETE FROM schedule WHERE plant_id = ? AND is_done = 0 AND is_one_time = 0",
                    new Object[]{plant.getId()});

            Cursor rulesCursor = db.rawQuery(
                    "SELECT watering_interval_days, fertilizer_interval_days, spraying_interval_days " +
                            "FROM plants_care_rules WHERE plant_type = ?",
                    new String[]{plant.getType()}
            );
            if (rulesCursor.moveToFirst()) {
                int wateringInterval   = rulesCursor.getInt(0);
                int fertilizerInterval = rulesCursor.getInt(1);
                int sprayingInterval   = rulesCursor.getInt(2);
                boolean isOutdoor = Objects.equals(plant.getIndoor_outdoor(), "o");

                if (isOutdoor) {
                    wateringInterval -= (int)(wateringInterval * 0.2);
                }

                db.execSQL(
                        "UPDATE plant_care_intervals SET watering_interval_days=?, fertilizer_interval_days=?, spraying_interval_days=? WHERE plant_id=?",
                        new Object[]{wateringInterval, fertilizerInterval, sprayingInterval, plant.getId()}
                );

                long today = Calendar.getInstance().getTimeInMillis();
                String insertSql = "INSERT INTO schedule (plant_id, action_date, action_type) VALUES (?, ?, ?)";
                db.execSQL(insertSql, new Object[]{plant.getId(), today, "Полив"});
                db.execSQL(insertSql, new Object[]{plant.getId(), today, "Удобрение"});
                if (!isOutdoor) {
                    db.execSQL(insertSql, new Object[]{plant.getId(), today, "Опрыскивание"});
                }
            }
            rulesCursor.close();
        }

        closeConnection();
    }

    public static ArrayList<PlantCareAction> getAllActions(Context context) {
        connectToDB(context);
        String sql = "SELECT * FROM schedule WHERE is_done = 0 ORDER BY action_date";
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<PlantCareAction> readActions = new ArrayList<>();
        while (cursor.moveToNext()) {
            PlantCareAction currentAction = new PlantCareAction(cursor.getInt(0),
                    cursor.getInt(1), cursor.getInt(2),
                    new Date(cursor.getLong(3)), cursor.getString(4));
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

        String sqlGet = "SELECT plant_id, action_type, is_one_time FROM schedule WHERE id = ?";
        Cursor cursor = db.rawQuery(sqlGet, new String[]{String.valueOf(actionId)});
        cursor.moveToFirst();

        int plantId = cursor.getInt(0);
        String actionType = cursor.getString(1);
        int isOneTime = cursor.getInt(2);

        if (Objects.equals(isOneTime, 0)) {
            String intervalColumn;
            if (Objects.equals(actionType, "Полив"))
                intervalColumn = "watering_interval_days";
            else if (Objects.equals(actionType, "Удобрение"))
                intervalColumn = "fertilizer_interval_days";
            else
                intervalColumn = "spraying_interval_days";

            String intervalsSql = "SELECT " + intervalColumn + " FROM plant_care_intervals WHERE plant_id = ?";
            Cursor rulesCursor = db.rawQuery(intervalsSql, new String[]{Integer.toString(plantId)});

            if (rulesCursor.moveToFirst()) {
                int interval = rulesCursor.getInt(0);
                generateScheduleEntries(plantId, actionType, interval, 0);
            }
            rulesCursor.close();
        }
        cursor.close();
        closeConnection();
    }

    public static ArrayList<String> getAllPlantTypes(Context context) {
        connectToDB(context);
        Cursor cursor = db.rawQuery("SELECT plant_type FROM plants_care_rules", null);
        ArrayList<String> types = new ArrayList<>();
        while (cursor.moveToNext()) {
            types.add(cursor.getString(0));
        }
        cursor.close();
        closeConnection();
        return types;
    }

    public static ArrayList<PlantCareAction> getTodayActions(Context context) {
        connectToDB(context);

        Calendar startOfDay = Calendar.getInstance();
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        startOfDay.set(Calendar.MILLISECOND, 0);

        Calendar endOfDay = Calendar.getInstance();
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);
        endOfDay.set(Calendar.MILLISECOND, 999);

        String sql = "SELECT * FROM schedule WHERE is_done = 0 AND action_date >= ?" +
                " AND action_date <= ? ORDER BY action_date";
        String startOfDay_s = String.valueOf(startOfDay.getTimeInMillis());
        String endOfDay_s = String.valueOf(endOfDay.getTimeInMillis());
        Cursor cursor = db.rawQuery(sql, new String[]{startOfDay_s, endOfDay_s});

        ArrayList<PlantCareAction> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            result.add(new PlantCareAction(
                    cursor.getInt(0), cursor.getInt(1), cursor.getInt(2),
                    new Date(cursor.getLong(3)), cursor.getString(4)));
        }
        cursor.close();
        closeConnection();
        return result;
    }

    public static void updateIntervals(int plantId, Context context, String action_type) {
        connectToDB(context);

        String sql = "SELECT assessment_date, earth_dryness, leafs_condition, branches_condition " +
                "FROM plant_conditions WHERE plant_id = ? ORDER BY assessment_date DESC LIMIT 3";
        Cursor assessmentsCursor = db.rawQuery(sql, new String[]{String.valueOf(plantId)});

        ArrayList<PlantCondition> conditions = new ArrayList<>();
        while (assessmentsCursor.moveToNext()) {
            conditions.add(new PlantCondition(
                    assessmentsCursor.getInt(1),
                    assessmentsCursor.getString(2),
                    assessmentsCursor.getString(3)));
        }
        assessmentsCursor.close();

        if (conditions.isEmpty()) {
            closeConnection();
            return;
        }

        String sqlInterval = "SELECT watering_interval_days, fertilizer_interval_days, spraying_interval_days " +
                            "FROM plant_care_intervals WHERE plant_id = ?";
        Cursor intervalCursor = db.rawQuery(sqlInterval, new String[]{String.valueOf(plantId)});

        intervalCursor.moveToFirst();
        int wateringInterval  = intervalCursor.getInt(0);
        int fertilizerInterval = intervalCursor.getInt(1);
        int sprayingInterval  = intervalCursor.getInt(2);
        intervalCursor.close();

        float avgDryness = 0;

        int yellowLeafs = 0;
        int brownLeafs = 0;
        int holesLeafs = 0;
        int stickyPlaque = 0;
        int whitePlaque = 0;

        int droppingBranches = 0;
        int dryBranches = 0;
        int pests = 0;
        int pale = 0;
        int dark = 0;
        for (PlantCondition condition : conditions) {
            avgDryness += condition.getEarthDryness();
            switch (condition.getLeafsCondition()) {
                case "Жёлтый":
                    yellowLeafs++; break;
                case "Коричневый":
                    brownLeafs++; break;
                case "Дырки на листьях":
                    holesLeafs++; break;
                case "Липкий налёт":
                    stickyPlaque++; break;
                case "Белый налёт":
                    whitePlaque++; break;
            }
            switch (condition.getBranchesCondition()) {
                case "Поникшие":
                    droppingBranches++; break;
                case "Сухие":
                    dryBranches++; break;
                case "Следы вредителей":
                    pests++; break;
                case "Бледные":
                    pale++; break;
                case "Темные":
                    dark++; break;
            }
        }

        boolean important = false;
        boolean waterIntervalChanged = false;

        avgDryness /= conditions.size();
        if (avgDryness >= 3.0 && wateringInterval - (int)(wateringInterval * 0.15) >= 1) {
            wateringInterval -= (int)(wateringInterval * 0.15);
            waterIntervalChanged = true;
        } else if (avgDryness <= 1.0 && wateringInterval + (int)(wateringInterval * 0.15) <= 30) {
            wateringInterval += (int)(wateringInterval * 0.15);
            waterIntervalChanged = true;
        }

        if (yellowLeafs >= 2  && !waterIntervalChanged) {
            wateringInterval -= (int)(wateringInterval * 0.15);
            waterIntervalChanged = true;
        }
        if (brownLeafs >= 2  && !waterIntervalChanged) {
            wateringInterval += (int)(wateringInterval * 0.15);
            sprayingInterval += (int)(sprayingInterval * 0.15);
            waterIntervalChanged = true;
        }
        if (holesLeafs >= 1) {
            generateScheduleEntries(plantId, "Обработать инсектицидом", 0, 1);
        }
        if (stickyPlaque >= 1) {
            generateScheduleEntries(plantId, "Промыть мыльным раствором и обработать инсектицидом", 0, 1);
            important = true;
        }
        if (whitePlaque >= 1) {
            generateScheduleEntries(plantId, "Удалить поражённые листья и обработать фунгицидом", 0, 1);
            important = true;
        }
        if (droppingBranches >= 2  && !waterIntervalChanged) {
            wateringInterval -= (int)(wateringInterval * 0.15);
            waterIntervalChanged = true;
        }
        if (dryBranches >= 2  && !waterIntervalChanged) {
            wateringInterval += (int)(wateringInterval * 0.15);
            waterIntervalChanged = true;
            generateScheduleEntries(plantId, "Удалить сухие ветки", 0, 1);
        }
        if (pests >= 1) {
            generateScheduleEntries(plantId, "Удалить вредителей и обработать инсектицидом", 0, 1);
            important = true;
        }
        if (pale >= 2) {
            generateScheduleEntries(plantId, "Переставить в более светлое место", 0, 1);
        }
        if (dark >= 1 && !waterIntervalChanged) {
            wateringInterval += (int)(wateringInterval * 0.15);
            generateScheduleEntries(plantId, "Удалить пораженные ветки и обработать фунгицидом", 0, 1);
            important = true;
        }

        if (conditions.size() < 3 || important) {
            closeConnection();
            return;
        }

        String updateSql = "UPDATE plant_care_intervals SET watering_interval_days = ?, fertilizer_interval_days = ?, spraying_interval_days = ? WHERE plant_id = ?";
        db.execSQL(updateSql, new Object[]{wateringInterval, fertilizerInterval, sprayingInterval, plantId});
        closeConnection();
    }

    public static boolean isTodayAssessmentDone(int plantId, Context context) {
        connectToDB(context);

        Calendar startOfDay = Calendar.getInstance();
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        startOfDay.set(Calendar.MILLISECOND, 0);

        Calendar endOfDay = Calendar.getInstance();
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);
        endOfDay.set(Calendar.MILLISECOND, 999);

        String sql = "SELECT count(*) FROM plant_conditions WHERE plant_id = ? AND assessment_date >= ? AND assessment_date <= ?";
        String plantId_s = String.valueOf(plantId);
        String startOfDay_s = String.valueOf(startOfDay.getTimeInMillis());
        String endOfDay_s = String.valueOf(endOfDay.getTimeInMillis());

        Cursor cursor = db.rawQuery(sql, new String[]{plantId_s, startOfDay_s, endOfDay_s});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        closeConnection();

        if (count > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public static void addNewType(Context context, String name, int waterDays, int fertilizerDays, int sprayingDays) {
        connectToDB(context);
        String sql = "INSERT INTO plants_care_rules (plant_type, watering_interval_days, fertilizer_interval_days, spraying_interval_days) VALUES (?, ?, ?, ?)";
        Object[] args = {name, waterDays, fertilizerDays, sprayingDays};
        db.execSQL(sql, args);
        closeConnection();
    }

    public static void postponeWatering(int plantId, int days, Context context) {
        connectToDB(context);

        String sql = "UPDATE schedule SET action_date = action_date + ? WHERE id = (" +
                    " SELECT id FROM schedule WHERE plant_id = ? AND action_type = 'Полив'" +
                    "AND is_done = 0 ORDER BY action_date ASC LIMIT 1)";
        db.execSQL(sql ,new Object[]{(long) days * 24 * 60 * 60 * 1000, plantId}
        );

        closeConnection();
    }
}