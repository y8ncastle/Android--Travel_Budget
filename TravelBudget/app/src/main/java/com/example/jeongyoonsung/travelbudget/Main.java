package com.example.jeongyoonsung.travelbudget;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Main extends Activity {
    SQLiteDatabase db;
    String sql;
    String DBname = "TravelBudget";
    String tableName = "BudgetData";
    String tableName2 = "ConsumeList";
    int DBcount = 0;
    Cursor resultset;

    File file;
    FileWriter writer;
    BufferedWriter bwriter;

    ListView list;

    DecimalFormat format = new DecimalFormat("###,###");

    Dialog add_dialog, reset_dialog, change_dialog, split_dialog, backup_dialog, list_change_dialog;

    private final long finish_time = 2000;
    private long back_pressed_time = 0;

    TextView main_add, main_reset, main_budget, main_currency_end, main_budget_info, main_budget_info_plus, main_record_count;
    TextView main_budget_percent, main_daily_budget_set, main_budget_change, main_backup, main_list_cover;

    String currency_end = "";
    String daily_origin = "";
    String daily_check = "";

    int budget = 0;
    int daily = 0;
    int daily_budget = 0;
    int daily_init = 0;

    String[] dates;
    String[] contents;
    int[] prices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main_display);

        main_add = (TextView)findViewById(R.id.main_add_button);
        main_reset = (TextView)findViewById(R.id.main_reset);
        main_budget = (TextView)findViewById(R.id.left_budget);
        main_currency_end = (TextView)findViewById(R.id.main_currency_end);
        main_budget_info = (TextView)findViewById(R.id.daily_budget_info);
        main_budget_info_plus = (TextView)findViewById(R.id.daily_budget_info_plus);
        main_record_count = (TextView)findViewById(R.id.budget_record_count);
        main_budget_percent = (TextView)findViewById(R.id.budget_percentage);
        main_daily_budget_set = (TextView)findViewById(R.id.daily_budget_set_button);
        main_budget_change = (TextView)findViewById(R.id.budget_change);
        main_backup = (TextView)findViewById(R.id.main_backup);
        main_list_cover = (TextView)findViewById(R.id.main_list_cover);

        dataSet();

        main_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_dialog = new Dialog(Main.this);
                add_dialog.setContentView(R.layout.dialog_main_add);
                add_dialog.setCancelable(false);
                add_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                add_dialog.show();

                TextView dialog_main_add_to_list = (TextView)add_dialog.findViewById(R.id.dialog_add_button);
                TextView dialog_main_add_cancel = (TextView)add_dialog.findViewById(R.id.dialog_cancel_button);

                final EditText dialog_main_add_content = (EditText)add_dialog.findViewById(R.id.dialog_add_content);
                final EditText dialog_main_add_price = (EditText)add_dialog.findViewById(R.id.dialog_add_price);

                dialog_main_add_to_list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            String temp_content = dialog_main_add_content.getText().toString();
                            String temp_price = dialog_main_add_price.getText().toString();
                            int temp_add_price = Integer.parseInt(temp_price);

                            if (temp_content.length() > 0 && temp_price.length() > 0) {
                                 db = openOrCreateDatabase(DBname, MODE_PRIVATE, null);

                                 try {
                                     long now = System.currentTimeMillis();
                                     Date date = new Date(now);
                                     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  |  kk:mm:ss");
                                     String getTime = sdf.format(date);

                                     sql = "INSERT INTO " + tableName2 + " (date, content, price) VALUES ('" + getTime + "', '" + temp_content + "', " + temp_add_price + ");";
                                     db.execSQL(sql);

                                     dialog_main_add_content.setText("");
                                     dialog_main_add_price.setText("");
                                     dataSet();
                                     Toast.makeText(getApplicationContext(), "내용이 추가되었습니다", Toast.LENGTH_SHORT).show();

                                     db.close();
                                 } catch (Exception e) {
                                     e.printStackTrace();
                                 }
                            } else {
                                Toast.makeText(getApplicationContext(), "입력사항들을 채워주세요", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "입력사항들을 채워주세요", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });

                dialog_main_add_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        add_dialog.dismiss();
                    }
                });
            }
        });

        main_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset_dialog = new Dialog(Main.this);
                reset_dialog.setContentView(R.layout.dialog_main_reset);
                reset_dialog.setCancelable(false);
                reset_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                reset_dialog.show();
            }
        });

        main_daily_budget_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "예산 금액을 0으로 설정하는 경우\n예산을 일 수에 맞게 자동분할 합니다\n또한, 모든 값을 0으로 설정시 초기화됩니다", Toast.LENGTH_SHORT).show();

                split_dialog = new Dialog(Main.this);
                split_dialog.setContentView(R.layout.dialog_main_split_budget);
                split_dialog.setCancelable(false);
                split_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                split_dialog.show();

                TextView dialog_main_split_budget = (TextView)split_dialog.findViewById(R.id.dialog_split_button);
                TextView dialog_main_split_cancel = (TextView)split_dialog.findViewById(R.id.dialog_split_cancel_button);

                final EditText dialog_main_split_day = (EditText)split_dialog.findViewById(R.id.dialog_split_day);
                final EditText dialog_main_split_price = (EditText)split_dialog.findViewById(R.id.dialog_split_price);

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                final String getTime = sdf.format(date);

                dialog_main_split_budget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            String temp_day = dialog_main_split_day.getText().toString();
                            int temp_split_day = Integer.parseInt(temp_day);

                            String temp_price = dialog_main_split_price.getText().toString();
                            int temp_split_price = Integer.parseInt(temp_price);

                            if (temp_day.length() > 0 && temp_price.length() > 0) {
                                db = openOrCreateDatabase(DBname, MODE_PRIVATE, null);

                                try {
                                    if (temp_split_day == 0 && temp_split_price == 0) {
                                        sql = "UPDATE " + tableName + " SET daily=0;";
                                        db.execSQL(sql);

                                        sql = "UPDATE " + tableName + " SET daily_budget=0";
                                        db.execSQL(sql);

                                        sql = "UPDATE " + tableName + " SET daily_init=0";
                                        db.execSQL(sql);

                                        sql = "UPDATE " + tableName + " SET daily_origin='ADMIN';";
                                        db.execSQL(sql);

                                        sql = "UPDATE " + tableName + " SET daily_check='ADMIN';";
                                        db.execSQL(sql);

                                        dataSet();
                                        split_dialog.dismiss();

                                        Toast.makeText(getApplicationContext(), "분할 설정이 초기화되었습니다", Toast.LENGTH_SHORT).show();
                                    }
                                    else if (temp_split_price == 0) {
                                        sql = "UPDATE " + tableName + " SET daily=" + temp_split_day + ";";
                                        db.execSQL(sql);

                                        sql = "UPDATE " + tableName + " SET daily_budget=" + (budget / temp_split_day) + ";";
                                        db.execSQL(sql);

                                        sql = "UPDATE " + tableName + " SET daily_init=1;";
                                        db.execSQL(sql);

                                        sql = "UPDATE " + tableName + " SET daily_origin='" + getTime + "';";
                                        db.execSQL(sql);

                                        sql = "UPDATE " + tableName + " SET daily_check='" + getTime + "';";
                                        db.execSQL(sql);

                                        dataSet();
                                        split_dialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "예산 분할 설정이 완료되었습니다", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (temp_split_price <= budget && temp_split_day > 0) {
                                            sql = "UPDATE " + tableName + " SET daily=" + temp_split_day + ";";
                                            db.execSQL(sql);

                                            sql = "UPDATE " + tableName + " SET daily_budget=" + temp_split_price + ";";
                                            db.execSQL(sql);

                                            sql = "UPDATE " + tableName + " SET daily_init=1;";
                                            db.execSQL(sql);

                                            sql = "UPDATE " + tableName + " SET daily_origin='" + getTime + "';";
                                            db.execSQL(sql);

                                            sql = "UPDATE " + tableName + " SET daily_check='" + getTime + "';";
                                            db.execSQL(sql);

                                            dataSet();
                                            split_dialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "예산 분할 설정이 완료되었습니다", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "여행 기간은 1일 이상이어야 하며\n분할 금액은 예산보다 적거나 같아야 합니다", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    db.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "입력사항들을 채워주세요", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "입력사항들을 채워주세요", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });

                dialog_main_split_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        split_dialog.dismiss();
                    }
                });
            }
        });

        main_budget_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_dialog = new Dialog(Main.this);
                change_dialog.setContentView(R.layout.dialog_main_change_budget);
                change_dialog.setCancelable(false);
                change_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                change_dialog.show();

                TextView dialog_main_change_budget = (TextView)change_dialog.findViewById(R.id.dialog_change_button);
                TextView dialog_main_change_cancel = (TextView)change_dialog.findViewById(R.id.dialog_change_cancel_button);

                final EditText dialog_main_initial_budget = (EditText)change_dialog.findViewById(R.id.dialog_change_budget);
                final EditText dialog_main_change_price = (EditText)change_dialog.findViewById(R.id.dialog_change_price);

                dialog_main_initial_budget.setText(format.format(budget));

                dialog_main_change_budget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            String temp_price = dialog_main_change_price.getText().toString();
                            int temp_change_price = Integer.parseInt(temp_price);

                            if (temp_price.length() > 0) {
                                db = openOrCreateDatabase(DBname, MODE_PRIVATE, null);

                                try {
                                    sql = "UPDATE " + tableName + " SET initial_budget=" + temp_change_price;
                                    db.execSQL(sql);

                                    sql = "UPDATE " + tableName + " SET daily=0;";
                                    db.execSQL(sql);

                                    sql = "UPDATE " + tableName + " SET daily_budget=0";
                                    db.execSQL(sql);

                                    sql = "UPDATE " + tableName + " SET daily_init='ADMIN'";
                                    db.execSQL(sql);

                                    sql = "UPDATE " + tableName + " SET daily_origin='ADMIN';";
                                    db.execSQL(sql);

                                    sql = "UPDATE " + tableName + " SET daily_check='ADMIN';";
                                    db.execSQL(sql);

                                    dataSet();
                                    change_dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "예산이 변경되었습니다", Toast.LENGTH_SHORT).show();

                                    db.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "변경할 예산을 입력해주세요", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "변경할 예산을 입력해주세요", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });

                dialog_main_change_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        change_dialog.dismiss();
                    }
                });
            }
        });

        main_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backup_dialog = new Dialog(Main.this);
                backup_dialog.setContentView(R.layout.dialog_main_backup);
                backup_dialog.setCancelable(false);
                backup_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                backup_dialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        long temp_time = System.currentTimeMillis();
        long interval_time = temp_time - back_pressed_time;

        if (0 <= interval_time && finish_time >= interval_time) {
            super.onBackPressed();
        }
        else
        {
            back_pressed_time = temp_time;
            Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }

    public class CustomList extends ArrayAdapter<String> {
        private final Activity context;
        public CustomList(Activity context) {
            super(context, R.layout.consume_list_item, dates);
            this.context = context;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.consume_list_item, null, true);
            TextView date = (TextView)rowView.findViewById(R.id.consume_date);
            TextView content = (TextView)rowView.findViewById(R.id.consume_content);
            TextView price = (TextView)rowView.findViewById(R.id.consume_price);

            date.setText("[ " + dates[position] + " ]");
            content.setText(contents[position]);
            price.setText(format.format(prices[position]) + currency_end);

            return rowView;
        }
    }

    public void dataSet() {
        db = openOrCreateDatabase(DBname, MODE_PRIVATE, null);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String getTime = sdf.format(date);

        try {
            sql = "SELECT initial_budget, currency_end, daily, daily_budget, daily_init, daily_origin, daily_check FROM " + tableName + ";";

            resultset = db.rawQuery(sql, null);
            resultset.moveToNext();

            int temp_budget = resultset.getInt(0);
            String temp_currency_end = resultset.getString(1);
            int temp_daily = resultset.getInt(2);
            int temp_daily_budget = resultset.getInt(3);
            int temp_daily_init = resultset.getInt(4);
            String temp_daily_origin = resultset.getString(5);
            String temp_daily_check = resultset.getString(6);

            budget = temp_budget;
            currency_end = temp_currency_end;
            daily = temp_daily;
            daily_budget = temp_daily_budget;
            daily_init = temp_daily_init;
            daily_origin = temp_daily_origin;
            daily_check = temp_daily_check;

            int temp_budget_check = budget;

            sql = "SELECT date, content, price FROM " + tableName2 + " ORDER BY date DESC;";

            resultset = db.rawQuery(sql, null);
            DBcount = resultset.getCount();

            int temp_budget_cal = 0;

            if (DBcount != 0) {
                dates = new String[DBcount];
                contents = new String[DBcount];
                prices = new int[DBcount];

                for (int i=0; i<DBcount; i++) {
                    resultset.moveToNext();

                    String temp_date = resultset.getString(0);
                    String temp_content = resultset.getString(1);
                    int temp_price = resultset.getInt(2);

                    dates[i] = temp_date;
                    contents[i] = temp_content;
                    prices[i] = temp_price;

                    temp_budget_cal += prices[i];
                    temp_budget_check -= prices[i];

                    if (daily_init != 0) {
                        String temp_date_check = "";

                        for (int j=0; j<10; j++) {
                            temp_date_check += dates[i].toCharArray()[j];
                        }

                        if (temp_date_check.equals(getTime) == true)
                            daily_budget -= prices[i];
                    }
                }
            }

            if (daily_init != 0) {
                daily_check = getTime;

                if (daily_origin.equals(daily_check) == false) {
                    daily_origin = getTime;
                    daily_init += 1;

                    if (daily >= daily_init) {
                        db = openOrCreateDatabase(DBname, MODE_PRIVATE, null);

                        try {
                            sql = "UPDATE " + tableName + " SET daily_init=" + daily_init + ";";
                            db.execSQL(sql);

                            sql = "UPDATE " + tableName + " SET daily_origin='" + getTime + "';";
                            db.execSQL(sql);

                            sql = "UPDATE " + tableName + " SET daily_check='" + getTime + "';";
                            db.execSQL(sql);

                            db.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        db = openOrCreateDatabase(DBname, MODE_PRIVATE, null);

                        try {
                            sql = "UPDATE " + tableName + " SET daily=0;";
                            db.execSQL(sql);

                            sql = "UPDATE " + tableName + " SET daily_budget=0";
                            db.execSQL(sql);

                            sql = "UPDATE " + tableName + " SET daily_init=0";
                            db.execSQL(sql);

                            sql = "UPDATE " + tableName + " SET daily_origin='ADMIN';";
                            db.execSQL(sql);

                            sql = "UPDATE " + tableName + " SET daily_check='ADMIN';";
                            db.execSQL(sql);

                            db.close();

                            daily = 0;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                main_budget_info.setText(" " + format.format(Math.abs(daily_budget)) + currency_end);

                if (daily_budget >= 0) {
                    main_budget_info_plus.setText("  남았어요");
                    main_budget_info_plus.setVisibility(View.VISIBLE);
                } else {
                    main_budget_info_plus.setText("  초과했어요");
                    main_budget_info_plus.setVisibility(View.VISIBLE);
                }
            } else {
                main_budget_info.setText(" 정해지지 않았어요");
                main_budget_info_plus.setVisibility(View.GONE);
            }

            if (daily == 0) {
                main_budget_info.setText(" 정해지지 않았어요");
                main_budget_info_plus.setVisibility(View.GONE);
            }

            main_budget.setText(format.format(temp_budget_check));
            main_currency_end.setText(currency_end);

            main_record_count.setText(" " + format.format(DBcount));

            int temp_result = (int)(((float)temp_budget_cal / (float)budget) * 100);

            if (temp_result >= 0 && temp_result <= 100)
                main_budget_percent.setText(String.valueOf(temp_result) + " %");
            else
                main_budget_percent.setText("100 %");

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (DBcount != 0) {
            main_list_cover.setVisibility(View.INVISIBLE);

            CustomList adapter = new CustomList(Main.this);
            list = (ListView)findViewById(R.id.main_consume_list);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int position, long ld) {
                    list_change_dialog = new Dialog(Main.this);
                    list_change_dialog.setContentView(R.layout.dialog_list_change);
                    list_change_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    list_change_dialog.show();

                    TextView dialog_list_change = (TextView)list_change_dialog.findViewById(R.id.dialog_list_change_button);
                    TextView dialog_list_delete = (TextView)list_change_dialog.findViewById(R.id.dialog_list_delete_button);

                    final EditText dialog_list_change_content = (EditText)list_change_dialog.findViewById(R.id.dialog_list_change_content);
                    final EditText dialog_list_change_price = (EditText)list_change_dialog.findViewById(R.id.dialog_list_change_price);

                    dialog_list_change_content.setText(contents[position]);
                    dialog_list_change_price.setText(String.valueOf(prices[position]));

                    dialog_list_change.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                String temp_content = dialog_list_change_content.getText().toString();
                                String temp_price = dialog_list_change_price.getText().toString();
                                int temp_change_price = Integer.parseInt(temp_price);

                                if (temp_content.length() > 0 && temp_price.length() > 0) {
                                    db = openOrCreateDatabase(DBname, MODE_PRIVATE, null);

                                    try {
                                        sql = "UPDATE " + tableName2 + " SET content='" + temp_content + "' WHERE date='" + dates[position] + "';";
                                        db.execSQL(sql);

                                        sql = "UPDATE " + tableName2 + " SET price=" + temp_change_price + " WHERE date='" + dates[position] + "';";
                                        db.execSQL(sql);

                                        dataSet();
                                        list_change_dialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "내용이 변경되었습니다", Toast.LENGTH_SHORT).show();

                                        db.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "입력사항들을 채워주세요", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "입력사항들을 채워주세요", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    });

                    dialog_list_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            db = openOrCreateDatabase(DBname, MODE_PRIVATE, null);

                            try {
                                sql = "DELETE FROM " + tableName2 + " WHERE date='" + dates[position] + "';";
                                db.execSQL(sql);

                                dataSet();
                                list_change_dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "삭제가 완료되었습니다\n정상적으로 삭제가 안될 시 재실행해주세요", Toast.LENGTH_SHORT).show();

                                db.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        } else {
            main_list_cover.setVisibility(View.VISIBLE);
        }
    }

    public void main_reset_control(View v) {
        switch (v.getId()) {
            case R.id.dialog_reset_button:
                db = openOrCreateDatabase(DBname, MODE_PRIVATE, null);

                try {
                    sql = "DROP TABLE IF EXISTS " + tableName + ";";
                    db.execSQL(sql);

                    sql = "DROP TABLE IF EXISTS " + tableName2 + ";";
                    db.execSQL(sql);

                    reset_dialog.dismiss();

                    Toast.makeText(Main.this, "초기화가 완료되었습니다\n어플을 재시작해주세요", Toast.LENGTH_SHORT).show();

                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.dialog_reset_cancel_button:
                reset_dialog.dismiss();
                break;
        }
    }

    public void main_backup_control(View v) {
        switch (v.getId()) {
            case R.id.dialog_backup_button:
                if (DBcount != 0) {
                    TedPermission.with(this)
                            .setPermissionListener(permissionlistener)
                            .setRationaleMessage("데이터 백업을 위해 내장메모리 접근 권한이 필요합니다.")
                            .setDeniedMessage("백업을 진행하시려면\n[설정] > [애플리케이션] 에서 권한을 허용해주세요.")
                            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .check();

                    db = openOrCreateDatabase(DBname, MODE_PRIVATE, null);

                    try {
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                        String getTime = sdf.format(date);

                        file = new File(Environment.getExternalStorageDirectory(), "Travel_Budget_Backup_" + getTime + ".txt");
                        writer = null;
                        bwriter = null;

                        writer = new FileWriter(file, false);
                        bwriter = new BufferedWriter(writer);

                        bwriter.write("Travel Budget (" + getTime + ") History\n\n");
                        bwriter.flush();

                        for (int i=DBcount-1; i>=0; i--) {
                            bwriter.write("[" + dates[i] + "] " + contents[i] + " (" + prices[i] + currency_end + ")\n");
                        }
                        bwriter.flush();

                        backup_dialog.dismiss();

                        Toast.makeText(Main.this, "백업이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (bwriter != null)
                                bwriter.close();
                            if (writer != null)
                                writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(Main.this, "백업할 예산 사용 내역이 없습니다", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.dialog_backup_cancel_button:
                backup_dialog.dismiss();
                break;
        }
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        }
    };
}