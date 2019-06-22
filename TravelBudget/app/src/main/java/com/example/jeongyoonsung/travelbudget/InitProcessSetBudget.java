package com.example.jeongyoonsung.travelbudget;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;

public class InitProcessSetBudget extends Activity {
    SQLiteDatabase db;
    String sql;
    String DBname = "TravelBudget";
    String tableName = "BudgetData";
    String tableName2 = "ConsumeList";

    NumberPicker numberPicker;
    SeekBar seekBar;

    DecimalFormat format = new DecimalFormat("###,###");

    TextView budget_price, budget_custom_price_info;

    EditText budget_custom_price;

    Button budget_complete;

    String temp_currency, currency_end = "원";

    int budget = 0, check_case;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.set_budget);

        final String[] currency = {"위아래로 스크롤하여 선택하세요", "대한민국 - 원 (KRW)", "미국 - 달러 (USD)", "유럽연합 - 유로 (EUR)", "일본 - 엔 (JPY)",
                "중국 - 위안 (CNY)", "홍콩 - 달러 (HKD)", "대만 - 달러 (TWD)", "영국 - 파운드 (GBP)", "캐나다 - 달러 (CAD)",
                "스위스 - 프랑 (CHF)", "스웨덴 - 크로나 (SEK)", "호주 - 달러 (AUD)", "뉴질랜드 - 달러 (NZD)", "체코 - 코루나 (CZK)", "터키 - 리라 (TRY)",
                "몽골 - 투그릭 (MNT)", "덴마크 - 크로네 (DKK)", "태국 - 바트 (THB)", "싱가포르 - 달러 (SGD)", "말레이시아 - 링깃 (MYR)",
                "인도네시아 - 루피아 (IDR)", "인도 - 루피 (INR)", "필리핀 - 페소 (PHP)", "멕시코 - 페소 (MXN)", "브라질 - 레알 (BRL)",
                "베트남 - 동 (VND)", "러시아 - 루블 (RUB)", "기타"};


        numberPicker = (NumberPicker)findViewById(R.id.budget_currency_picker);
        seekBar = (SeekBar)findViewById(R.id.budget_seekbar);

        budget_price = (TextView)findViewById(R.id.budget_price);
        budget_custom_price_info = (TextView)findViewById(R.id.budget_custom_price_info);

        budget_custom_price = (EditText)findViewById(R.id.budget_custom_price);

        budget_complete = (Button)findViewById(R.id.budget_button);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(currency.length - 1);
        numberPicker.setDisplayedValues(currency);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker.setWrapSelectorWheel(false);

        NumberPicker.OnValueChangeListener myValChangedListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                temp_currency = currency[newVal];

                switch (temp_currency) {
                    case "위아래로 스크롤하여 선택하세요":
                        currency_end = "";
                        break;
                    case "대한민국 - 원 (KRW)":
                        currency_end = " 원";
                        break;
                    case "미국 - 달러 (USD)":
                        currency_end = " $";
                        break;
                    case "유럽연합 - 유로 (EUR)":
                        currency_end = " €";
                        break;
                    case "일본 - 엔 (JPY)":
                        currency_end = " ￥";
                        break;
                    case "중국 - 위안 (CNY)":
                        currency_end = " 元";
                        break;
                    case "홍콩 - 달러 (HKD)":
                        currency_end = " HK$";
                        break;
                    case "대만 - 달러 (TWD)":
                        currency_end = " NT$";
                        break;
                    case "영국 - 파운드 (GBP)":
                        currency_end = " £";
                        break;
                    case "캐나다 - 달러 (CAD)":
                        currency_end = " C$";
                        break;
                    case "스위스 - 프랑 (CHF)":
                        currency_end = " CHF";
                        break;
                    case "스웨덴 - 크로나 (SEK)":
                        currency_end = " kr";
                        break;
                    case "호주 - 달러 (AUD)":
                        currency_end = " $";
                        break;
                    case "뉴질랜드 - 달러 (NZD)":
                        currency_end = " $";
                        break;
                    case "체코 - 코루나 (CZK)":
                        currency_end = " Kč";
                        break;
                    case "터키 - 리라 (TRY)":
                        currency_end = " YTL";
                        break;
                    case "몽골 - 투그릭 (MNT)":
                        currency_end = " ₮";
                        break;
                    case "덴마크 - 크로네 (DKK)":
                        currency_end = " kr";
                        break;
                    case "태국 - 바트 (THB)":
                        currency_end = " ฿";
                        break;
                    case "싱가포르 - 달러 (SGD)":
                        currency_end = " S$";
                        break;
                    case "말레이시아 - 링깃 (MYR)":
                        currency_end = " RM";
                        break;
                    case "인도네시아 - 루피아 (IDR)":
                        currency_end = " Rp";
                        break;
                    case "인도 - 루피 (INR)":
                        currency_end = " Rs.";
                        break;
                    case "필리핀 - 페소 (PHP)":
                        currency_end = " ₱";
                        break;
                    case "멕시코 - 페소 (MXN)":
                        currency_end = " $";
                        break;
                    case "브라질 - 레알 (BRL)":
                        currency_end = " R$";
                        break;
                    case "베트남 - 동 (VND)":
                        currency_end = " ₫";
                        break;
                    case "러시아 - 루블 (RUB)":
                        currency_end = " \u20BD";
                        break;
                    case "기타":
                        currency_end = " 원";
                        break;
                }
            }
        };

        numberPicker.setOnValueChangedListener(myValChangedListener);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = calculate(i);
                budget = progress;

                textVisibility();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                textVisibility();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textVisibility();
            }

            public void textVisibility () {
                try {
                    if (progress == -1) {
                        budget_price.setText("-");
                        budget_custom_price.setText("");
                        budget_custom_price_info.setText("먼저 통화를 선택해주세요");
                        budget_price.setVisibility(View.VISIBLE);
                        budget_custom_price.setVisibility(View.INVISIBLE);
                        budget_custom_price_info.setVisibility(View.VISIBLE);
                        check_case = 0;
                    }
                    else if (progress == -2) {
                        budget_price.setText("0");
                        budget_custom_price.setText("");
                        budget_custom_price_info.setText("직접입력");
                        budget_price.setVisibility(View.INVISIBLE);
                        budget_custom_price.setVisibility(View.VISIBLE);
                        budget_custom_price_info.setVisibility(View.VISIBLE);
                        check_case = 2;
                    }
                    else if (progress != maxCheck()) {
                        budget_price.setText(format.format(progress) + currency_end);
                        budget_price.setVisibility(View.VISIBLE);
                        budget_custom_price.setVisibility(View.INVISIBLE);
                        budget_custom_price_info.setVisibility(View.INVISIBLE);
                        check_case = 1;
                    }
                    else if (progress == maxCheck()){
                        budget_price.setText("0");
                        budget_custom_price.setText("");
                        budget_custom_price_info.setText(format.format(maxCheck()) + "+ 또는 직접입력");
                        budget_price.setVisibility(View.INVISIBLE);
                        budget_custom_price.setVisibility(View.VISIBLE);
                        budget_custom_price_info.setVisibility(View.VISIBLE);
                        check_case = 2;
                    }
                } catch (NullPointerException ne) {
                    budget_custom_price_info.setText("먼저 통화를 선택해주세요");
                    budget_custom_price_info.setVisibility(View.VISIBLE);
                    check_case = 0;

                    ne.fillInStackTrace();
                }
            }

            public int calculate (int i) {
                int temp_cal = i;

                try {
                    switch (temp_currency) {
                        case "위아래로 스크롤하여 선택하세요":
                            temp_cal = -1;
                            break;
                        case "대한민국 - 원 (KRW)":
                            temp_cal *= 10000;
                            break;
                        case "미국 - 달러 (USD)":
                            temp_cal *= 10;
                            break;
                        case "유럽연합 - 유로 (EUR)":
                            temp_cal *= 10;
                            break;
                        case "일본 - 엔 (JPY)":
                            temp_cal *= 1000;
                            break;
                        case "중국 - 위안 (CNY)":
                            temp_cal *= 60;
                            break;
                        case "홍콩 - 달러 (HKD)":
                            temp_cal *= 70;
                            break;
                        case "대만 - 달러 (TWD)":
                            temp_cal *= 270;
                            break;
                        case "영국 - 파운드 (GBP)":
                            temp_cal *= 5;
                            break;
                        case "캐나다 - 달러 (CAD)":
                            temp_cal *= 10;
                            break;
                        case "스위스 - 프랑 (CHF)":
                            temp_cal *= 10;
                            break;
                        case "스웨덴 - 크로나 (SEK)":
                            temp_cal *= 80;
                            break;
                        case "호주 - 달러 (AUD)":
                            temp_cal *= 10;
                            break;
                        case "뉴질랜드 - 달러 (NZD)":
                            temp_cal *= 15;
                            break;
                        case "체코 - 코루나 (CZK)":
                            temp_cal *= 200;
                            break;
                        case "터키 - 리라 (TRY)":
                            temp_cal *= 40;
                            break;
                        case "몽골 - 투그릭 (MNT)":
                            temp_cal *= 22000;
                            break;
                        case "덴마크 - 크로네 (DKK)":
                            temp_cal *= 55;
                            break;
                        case "태국 - 바트 (THB)":
                            temp_cal *= 300;
                            break;
                        case "싱가포르 - 달러 (SGD)":
                            temp_cal *= 10;
                            break;
                        case "말레이시아 - 링깃 (MYR)":
                            temp_cal *= 35;
                            break;
                        case "인도네시아 - 루피아 (IDR)":
                            temp_cal *= 128000;
                            break;
                        case "인도 - 루피 (INR)":
                            temp_cal *= 620;
                            break;
                        case "필리핀 - 페소 (PHP)":
                            temp_cal *= 490;
                            break;
                        case "멕시코 - 페소 (MXN)":
                            temp_cal *= 190;
                            break;
                        case "브라질 - 레알 (BRL)":
                            temp_cal *= 35;
                            break;
                        case "베트남 - 동 (VND)":
                            temp_cal *= 210000;
                            break;
                        case "러시아 - 루블 (RUB)":
                            temp_cal *= 580;
                            break;
                        case "기타":
                            temp_cal = -2;
                            break;
                    }
                } catch (NullPointerException ne) {
                    temp_cal *= 0;

                    ne.fillInStackTrace();
                }

                return temp_cal;
            }

            public int maxCheck () {
                int max = 0;

                switch (temp_currency) {
                    case "대한민국 - 원 (KRW)":
                        max = 10000000;
                        break;
                    case "미국 - 달러 (USD)":
                        max = 10000;
                        break;
                    case "유럽연합 - 유로 (EUR)":
                        max = 10000;
                        break;
                    case "일본 - 엔 (JPY)":
                        max = 1000000;
                        break;
                    case "중국 - 위안 (CNY)":
                        max = 60000;
                        break;
                    case "홍콩 - 달러 (HKD)":
                        max = 70000;
                        break;
                    case "대만 - 달러 (TWD)":
                        max = 270000;
                        break;
                    case "영국 - 파운드 (GBP)":
                        max = 5000;
                        break;
                    case "캐나다 - 달러 (CAD)":
                        max = 10000;
                        break;
                    case "스위스 - 프랑 (CHF)":
                        max = 10000;
                        break;
                    case "스웨덴 - 크로나 (SEK)":
                        max = 80000;
                        break;
                    case "호주 - 달러 (AUD)":
                        max = 10000;
                        break;
                    case "뉴질랜드 - 달러 (NZD)":
                        max = 15000;
                        break;
                    case "체코 - 코루나 (CZK)":
                        max = 200000;
                        break;
                    case "터키 - 리라 (TRY)":
                        max = 40000;
                        break;
                    case "몽골 - 투그릭 (MNT)":
                        max = 22000000;
                        break;
                    case "덴마크 - 크로네 (DKK)":
                        max = 55000;
                        break;
                    case "태국 - 바트 (THB)":
                        max = 300000;
                        break;
                    case "싱가포르 - 달러 (SGD)":
                        max = 10000;
                        break;
                    case "말레이시아 - 링깃 (MYR)":
                        max = 35000;
                        break;
                    case "인도네시아 - 루피아 (IDR)":
                        max = 128000000;
                        break;
                    case "인도 - 루피 (INR)":
                        max = 620000;
                        break;
                    case "필리핀 - 페소 (PHP)":
                        max = 490000;
                        break;
                    case "멕시코 - 페소 (MXN)":
                        max = 190000;
                        break;
                    case "브라질 - 레알 (BRL)":
                        max = 35000;
                        break;
                    case "베트남 - 동 (VND)":
                        max = 210000000;
                        break;
                    case "러시아 - 루블 (RUB)":
                        max = 580000;
                        break;
                }

                return max;
            }
        });

        budget_complete.setOnClickListener(new View.OnClickListener() {
            Toast toast;

            @Override
            public void onClick(View view) {
                try {
                    switch (check_case) {
                        case 0:
                            toast = Toast.makeText(getApplicationContext(), "통화 선택 후 예산을 설정해주세요", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, 320);
                            toast.show();
                            break;
                        case 1:
                            if (budget > 0) {
                                Intent intent = new Intent(InitProcessSetBudget.this, InitProcessComplete.class);

                                dataSet();

                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            } else {
                                toast = Toast.makeText(getApplicationContext(), "예산을 0보다 크게 설정해주세요", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.BOTTOM, 0, 320);
                                toast.show();
                            }
                            break;
                        case 2 :
                            String temp = budget_custom_price.getText().toString();

                            if (temp.length() > 0) {
                                budget = Integer.parseInt(temp);

                                if (budget > 0) {
                                    Intent intent = new Intent(InitProcessSetBudget.this, InitProcessComplete.class);

                                    dataSet();

                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                } else {
                                    toast = Toast.makeText(getApplicationContext(), "예산을 0보다 크게 설정해주세요", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.BOTTOM, 0, 320);
                                    toast.show();
                                }
                            } else {
                                toast = Toast.makeText(getApplicationContext(), "예산을 입력해주세요", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.BOTTOM, 0, 320);
                                toast.show();
                            }

                            break;
                    }
                } catch (NullPointerException ne) {
                    toast = Toast.makeText(getApplicationContext(), "통화 선택 후 예산을 설정해주세요", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 320);
                    toast.show();

                    ne.fillInStackTrace();
                }
            }
        });
    }

    public void dataSet() {
        db = openOrCreateDatabase(DBname, MODE_PRIVATE, null);

        try {
            sql = "UPDATE " + tableName + " SET initial_budget=" + budget + " WHERE info='ADMIN';";
            db.execSQL(sql);

            sql = "UPDATE " + tableName + " SET currency_end='" + currency_end + "' WHERE info='ADMIN';";
            db.execSQL(sql);

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
