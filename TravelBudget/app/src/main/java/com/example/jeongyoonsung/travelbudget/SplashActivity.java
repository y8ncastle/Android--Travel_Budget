package com.example.jeongyoonsung.travelbudget;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class SplashActivity extends Activity {
    SQLiteDatabase db;
    String sql;
    String DBname = "TravelBudget";
    String tableName = "BudgetData";
    String tableName2 = "ConsumeList";
    int DBcount;
    Cursor resultset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        db = openOrCreateDatabase(DBname, MODE_PRIVATE, null);

        try {
            sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(initial_budget INTEGER, currency_end VARCHAR2(10), daily INTEGER, " +
                    "daily_budget INTEGER, daily_init INTEGER, daily_origin VARCHAR2(11), daily_check VARCHAR2(11), info VARCHAR2(11));";
            db.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS " + tableName2 + " (date VARCHAR2(40), content VARCHAR2(91), price INTEGER);";
            db.execSQL(sql);

            sql = "SELECT initial_budget FROM " + tableName + " WHERE info='ADMIN';";
            resultset = db.rawQuery(sql, null);
            DBcount = resultset.getCount();

            if (DBcount == 0) {
                sql = "INSERT INTO " + tableName + " (initial_budget, currency_end, daily, daily_budget, daily_init, daily_origin, " +
                        "daily_check, info) VALUES (0, '원', 0, 0, 0, 'ADMIN', 'ADMIN', 'ADMIN');";
                db.execSQL(sql);

                Intent intent = new Intent(SplashActivity.this, InitProcess.class);
                startActivity(intent);
                finish();
            } else {
                resultset.moveToNext();

                int temp_budget = resultset.getInt(0);

                if (temp_budget != 0) {
                    Intent intent = new Intent(SplashActivity.this, Main.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, InitProcess.class);
                    startActivity(intent);
                    finish();
                }
            }

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
