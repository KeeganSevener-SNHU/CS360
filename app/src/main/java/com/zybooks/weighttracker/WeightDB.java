package com.zybooks.weighttracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class WeightDB extends SQLiteOpenHelper {

    private static final String DATABASE = "weightData.db";
    private static final int version = 1;
    public WeightDB(Context context) {
        super(context, DATABASE, null, version);
    }


    // Table names and columns for the database.
    private static final class WeightTable {
        private static final String TABLE_USERS = "logins";
        private static final String COL_ID1 = "_id";
        private static final String COL_NAME = "username";
        private static final String COL_PASS = "password";

        private static final String TABLE_LOGS = "logs";
        private static final String COL_ID2 = "_id";
        private static final String COL_WEIGHT = "weight";
        private static final String COL_DATE = "date";
        private static final String COL_MES = "measure";

        private static final String TABLE_GOAL = "goal";
        private static final String COL_ID3 = "_id";
        private static final String COL_GOAL = "goalweight";
    }

    // Constructor for the data base.
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create the USERS table,
        db.execSQL("create table " + WeightTable.TABLE_USERS + " (" +
                WeightTable.COL_ID1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WeightTable.COL_NAME + " text, "
                + WeightTable.COL_PASS + " text)" );

        // Create the LOGS table.
        db.execSQL("create table " + WeightTable.TABLE_LOGS + " (" +
                WeightTable.COL_ID2 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WeightTable.COL_WEIGHT + " float, "
                + WeightTable.COL_DATE + " text, "
                + WeightTable.COL_MES + " text)" );

        // Create the GOAL table.
        db.execSQL("create table " + WeightTable.TABLE_GOAL + " (" +
                WeightTable.COL_ID3 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WeightTable.COL_GOAL + " float)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("drop table if exists " + WeightTable.TABLE_USERS);
        db.execSQL("drop table if exists " + WeightTable.TABLE_LOGS);
        db.execSQL("drop table if exists " + WeightTable.TABLE_GOAL);
        onCreate(db);
    }


    //------------------------- Goal Functions
    // Function for adding a goal.
    private void addGoal(float goal) {
        // Add values for new account
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("goalWeight", goal);
        db.insert("goal", null, values);
    }

    // Function for Updating the goal.
    public String updateGoal(float goal) {
        // Update the goal weight
        long id = 1;
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase dbR = this.getReadableDatabase();
        int rowCount = -1;

        String str = "";
        //Log.d("updateGoal", "weightGoal = " + goal + ",lol");

        // Get cursor object with database result.
        Cursor cursor = dbR.rawQuery("select count(*) from "
                + WeightTable.TABLE_GOAL, null);
        if (cursor.moveToFirst()) {
            rowCount = cursor.getInt(0);
        }
        if (rowCount > 0) {
            ContentValues values = new ContentValues();
            values.put(WeightTable.COL_GOAL, goal);

            int rowsUpdated = db.update(WeightTable.TABLE_GOAL, values, " " +
                            WeightTable.COL_ID3 + " = 1",
                    null); //new String[] { Long.toString(id) }
            Log.d("updatedCount", "count = " + rowsUpdated);
            str = "Goal updated";
        }
        else {
            addGoal(goal);
        }
        cursor.close();
        return str;
    }

    // Function for retrieving the current goal weight.
    public String getGoal() {
        SQLiteDatabase db = getReadableDatabase();
        float wGoal = 0;

        String sql = "select * from " + WeightTable.TABLE_GOAL;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            long idd = cursor.getLong(0);
            wGoal = cursor.getFloat(1);
            Log.d("getGoal", "weightGoal = " + wGoal + ",lol- " + idd);
        }
        cursor.close();
        return wGoal + "";
    }

    //------------------------------------------------



    //------------------------- Account functions

    // Function for adding a new login.
    public void addLogin(String name, String pass) {
        // Add values for new account
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", name);
        values.put("password", pass);
        db.insert(WeightTable.TABLE_USERS, null, values);
    }

    // Function for returning a user.
    public boolean getUser(String mUser, String mPass) {
        SQLiteDatabase db = getReadableDatabase();

        boolean found = false;

        String sql = "select * from " + WeightTable.TABLE_USERS + " where username = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {mUser});
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            String user = cursor.getString(1);
            String pass = cursor.getString(2);
            Log.d("getUser", "Username = " + user + ", " + id + ", " + pass);
            if (user.equals(mUser) && pass.equals(mPass))
                found = true;
        }
        cursor.close();
        // Return confirmation of the user in database.
        return found;
    }




    //---------------------------- Weight log functions
    // Function for adding a new weight log.
    public void addLog(float weight, String date) {
        // Add values for new account
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("weight", weight);
        values.put("date", date);
        values.put("measure", "lbs");
        db.insert("logs", null, values);
    }

    // Delete a log
    public void deleteId(long id) {
        // Delete a log by ID
        SQLiteDatabase db = getReadableDatabase();

        String sql = "delete from " + WeightTable.TABLE_LOGS + " where _id = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {String.valueOf(id)});
        cursor.moveToFirst();
        cursor.close();
    }

    // Get a log by its ID.
    public String getDateById(long id) {
        // Find a log by ID.
        SQLiteDatabase db = getReadableDatabase();
        String gimme = "not Found";

        String sql = "select * from " + WeightTable.TABLE_LOGS + " where _id = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {String.valueOf(id)});
        if (cursor.moveToFirst()) {
            long foundId = cursor.getLong(0);
            String date = cursor.getString(2);
            if (foundId == id)
                gimme = date;
        }
        cursor.close();
        return gimme;
    }

    // Function for getting an array of the logs.
public ArrayList<LogDocs> getLogs() {
        // Open the database
        SQLiteDatabase db = getReadableDatabase();
        // Store the logs in objects for dynamic use.
        ArrayList<LogDocs> stringList = new ArrayList<LogDocs>();

        // SQL command for getting the logs.
        String sql = "select * from " + WeightTable.TABLE_LOGS + " order by date desc";
        Cursor cursor = db.rawQuery(sql, new String[] {});

    if (cursor.moveToFirst() && !cursor.isNull(0)) {
        do {
            long id = cursor.getLong(0);
            float weight = cursor.getFloat(1);
            String date = cursor.getString(2);
            String measure = cursor.getString(3);

            // Create logDoc object and add to ArrayList.
            LogDocs logDoc = new LogDocs(id, weight, date, measure);
            stringList.add(logDoc);

        } while (cursor.moveToNext());
    }
        return stringList;
}

    // Function for updating the logs weight type
    public String updateWeightType(String type) {
        SQLiteDatabase db = getReadableDatabase();

        String confirm = "none";

        // IF statement for the lbs option.
        if (type.equals("lbs")) {
            String sql = "select * from " + WeightTable.TABLE_LOGS;
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                do {
                    String check = cursor.getString(3);
                    // Check to make sure the measurement, COL_MES,
                    // is not already lbs.
                    if (check.equals("lbs"))
                        return "Already in lbs";

                    long id = cursor.getLong(0);
                    float weight = cursor.getFloat(1);
                    weight = weight * 2.2F;

                    ContentValues values = new ContentValues();
                    values.put(WeightTable.COL_WEIGHT, weight);
                    values.put(WeightTable.COL_MES, "lbs");

                    db.update(WeightTable.TABLE_LOGS, values, " " +
                                    WeightTable.COL_ID2 + " = ?",
                            new String[] {String.valueOf(id)});

                } while (cursor.moveToNext());
            }
            cursor.close();

            // Updates the GOAL to LBS.
            sql = "select * from " + WeightTable.TABLE_GOAL;
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();

            float goal = cursor.getFloat(1);
            goal = goal * 2.2F;

            ContentValues values = new ContentValues();
            values.put(WeightTable.COL_GOAL, goal);

            db.update(WeightTable.TABLE_GOAL, values, " " +
                            WeightTable.COL_ID3 + " = 1",
                    null);
            cursor.close();


            confirm = "Updated to lbs";
        }
        //
        else if (type.equals("kg")) {

            // Updates the LOGs to KG.
            String sql = "select * from " + WeightTable.TABLE_LOGS;
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                do {
                    String check = cursor.getString(3);
                    // Check to make sure the measurement, COL_MES,
                    // is not already kg.
                    if (check.equals("kg"))
                        return "Already in kg";

                    long id = cursor.getLong(0);
                    float weight = cursor.getFloat(1);
                    weight = weight / 2.2F;

                    ContentValues values = new ContentValues();
                    values.put(WeightTable.COL_WEIGHT, weight);
                    values.put(WeightTable.COL_MES, "kg");

                    db.update(WeightTable.TABLE_LOGS, values, " " +
                                    WeightTable.COL_ID2 + " = ?",
                            new String[] {String.valueOf(id)});

                } while (cursor.moveToNext());
            }
            cursor.close();


            // Updates the GOAL to KG.
            sql = "select * from " + WeightTable.TABLE_GOAL;
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();

            float goal = cursor.getFloat(1);
            goal = goal / 2.2F;

            ContentValues values = new ContentValues();
            values.put(WeightTable.COL_GOAL, goal);

            db.update(WeightTable.TABLE_GOAL, values, " " +
                            WeightTable.COL_ID3 + " = 1",
                    null);
            cursor.close();

            confirm = "Updated to kg";
        }
        return confirm;
    }

    // Update Log in database.
    public String updateLog(String date, float weight, long id) {
        String retStr = "";
        SQLiteDatabase db = getReadableDatabase();

        // Create the SQL query.
        String sql = "select * from " + WeightTable.TABLE_LOGS + " where " + WeightTable.COL_ID2
                + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {String.valueOf(id)});
        cursor.moveToFirst();

        // Assign new values.
        ContentValues values = new ContentValues();
        values.put(WeightTable.COL_DATE, date);
        values.put(WeightTable.COL_WEIGHT, weight);

        // Update the appropriate log with the new values via ID.
        db.update(WeightTable.TABLE_LOGS, values, " " +
                        WeightTable.COL_ID2 + " = ?",
                new String[] {String.valueOf(id)});
        cursor.close();

        // Return confirmation of log update.
        retStr = "Updated log";
        return retStr;
    }

    public LogDocs getALog(long id) {

        SQLiteDatabase db = getReadableDatabase();
        LogDocs log;

        // Updates the GOAL to KG.
        String sql = "select * from " + WeightTable.TABLE_LOGS + " where " + WeightTable.COL_ID2
                + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {String.valueOf(id)});
        cursor.moveToFirst();

        // get variables and assing it to a LogDocs object.
        float weight = cursor.getFloat(1);
        String date = cursor.getString(2);
        String mes = cursor.getString(3);

        log = new LogDocs(id, weight, date, mes);

        cursor.close();
        return log;
    }

}

