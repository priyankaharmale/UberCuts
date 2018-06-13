package com.hnweb.ubercuts.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.adaptor.MonthAdaptor;
import com.hnweb.ubercuts.adaptor.YearAdaptor;
import com.hnweb.ubercuts.interfaces.OnCallBack;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Priyanka H on 13/06/2018.
 */

public class RegistrationActivityStepThree extends AppCompatActivity implements OnCallBack {
    Button btn_proceed;
    OnCallBack onCallBack;
    EditText et_mm, et_yyyy,et_cvv,et_cardNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerstepthree);
        onCallBack = RegistrationActivityStepThree.this;
        btn_proceed = (Button) findViewById(R.id.btn_proceed);
        et_mm = (EditText) findViewById(R.id.et_mm);
        et_yyyy = (EditText) findViewById(R.id.et_yyyy);
        et_cvv=(EditText) findViewById(R.id.et_cvv);
        et_cardNo=(EditText) findViewById(R.id.et_cardNo);

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_cardNo.getText().toString().equals("")) {
                    et_cardNo.setError("Please Enter the Card Number");
                } else if (et_mm.getText().toString().equals("")) {
                    et_mm.setError("Please Select the Month");
                } else if (et_yyyy.getText().toString().equals("")) {
                    et_yyyy.setError("Please Select the Year");
                } else if (et_cvv.getText().toString().equals("")) {
                    et_cvv.setError("Please Enter CVV Number");
                }  else
                {
                    Intent intent = new Intent(RegistrationActivityStepThree.this, RegistrationActivityStepTwo.class);
                    startActivity(intent);
                }

            }
        });
        et_yyyy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogYear();
            }
        });
        et_mm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMonth();
            }
        });
        et_cardNo.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';
            boolean isDelete = true;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before == 0)
                    isDelete = false;
                else
                    isDelete = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String source = editable.toString();
                int length = source.length();

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(source);

                if (length > 0 && length % 5 == 0) {
                    if (isDelete)
                        stringBuilder.deleteCharAt(length - 1);
                    else
                        stringBuilder.insert(length - 1, " ");

                    et_cardNo.setText(stringBuilder);
                    et_cardNo.setSelection(et_cardNo.getText().length());

                }
            }
        });
    }

    public void dialogYear() {
        Dialog dialog = new Dialog(RegistrationActivityStepThree.this);
        dialog.setContentView(R.layout.dialog_month);

        ListView lv = (ListView) dialog.findViewById(R.id.lv);
        dialog.setCancelable(true);
        dialog.setTitle("Year");

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i <= 2060; i++) {
            years.add(Integer.toString(i));
        }
        YearAdaptor adapter = new YearAdaptor(RegistrationActivityStepThree.this, years, onCallBack, dialog);
        lv.setAdapter(adapter);

        dialog.show();
    }

    public void dialogMonth() {
        Dialog dialog = new Dialog(RegistrationActivityStepThree.this);
        dialog.setContentView(R.layout.dialog_month);

        ListView lv = (ListView) dialog.findViewById(R.id.lv);
        dialog.setCancelable(true);
        dialog.setTitle("Month");

        ArrayList<String> years = new ArrayList<String>();
        years.add("01");
        years.add("02");
        years.add("03");
        years.add("04");
        years.add("05");
        years.add("06");
        years.add("07");
        years.add("08");
        years.add("09");
        years.add("10");
        years.add("11");
        years.add("12");

        MonthAdaptor adapter = new MonthAdaptor(RegistrationActivityStepThree.this, years, onCallBack, dialog);
        lv.setAdapter(adapter);

        dialog.show();
    }

    @Override
    public void callback(String count) {
        et_mm.setText(count);
    }

    @Override
    public void callbackYear(String count) {
        et_yyyy.setText(count);

    }

    @Override
    public void callbackList(String id, String name) {

    }
}
