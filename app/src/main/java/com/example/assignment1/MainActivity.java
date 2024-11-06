package com.example.assignment1;

import static android.provider.BaseColumns._ID;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

import static android.provider.BaseColumns._ID;
import static com.example.assignment1.Constants.BMI;
import static com.example.assignment1.Constants.Result;
import static com.example.assignment1.Constants.TABLE_NAME;
import static com.example.assignment1.Constants.WEIGHT;
import static com.example.assignment1.Constants.TIME;

public class MainActivity extends AppCompatActivity {

    private EventsData events;
    boolean isHistoryLayout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        final TextView bmi = findViewById(R.id.bmi);
        final Button calculate_btn = findViewById(R.id.calculate);
        final TextView result = findViewById(R.id.result);

        EditText edit_text = (EditText)findViewById(R.id.editText);
        edit_text.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        EditText edit_text2 = (EditText)findViewById(R.id.editText2);
        edit_text2.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});

        bmi.setBackgroundColor(getResources().getColor(R.color.grey));
        result.setBackgroundColor(getResources().getColor(R.color.grey));

        calculate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float resultCalculate;
                float weightValue = Float.parseFloat(edit_text.getText().toString());
                float heightValue = Float.parseFloat(edit_text2.getText().toString()) / 100;
                resultCalculate = weightValue / (heightValue * heightValue);
                bmi.setText(formatter.format(resultCalculate));

                if(resultCalculate < 16){
                    result.setText(R.string.severethinness);
                    result.setTextColor(getResources().getColor(R.color.black));
                    result.setBackgroundColor(getResources().getColor(R.color.chili_red));
                }
                else if(resultCalculate >= 16 && resultCalculate <= 17){
                    result.setText(R.string.moderatethinness);
                    result.setTextColor(getResources().getColor(R.color.black));
                    result.setBackgroundColor(getResources().getColor(R.color.dusty_rose));
                }
                else if(resultCalculate >= 17 && resultCalculate <= 18.5){
                    result.setText(R.string.mildthinness);
                    result.setTextColor(getResources().getColor(R.color.black));
                    result.setBackgroundColor(getResources().getColor(R.color.yellow));
                }
                else if(resultCalculate >= 18.5 && resultCalculate <= 25){
                    result.setText(R.string.normal);
                    result.setTextColor(getResources().getColor(R.color.black));
                    result.setBackgroundColor(getResources().getColor(R.color.green));
                }
                else if(resultCalculate >= 25 && resultCalculate <= 30){
                    result.setText(R.string.overweight);
                    result.setTextColor(getResources().getColor(R.color.black));
                    result.setBackgroundColor(getResources().getColor(R.color.yellow));
                }
                else if(resultCalculate >= 30 && resultCalculate <= 35){
                    result.setText(R.string.obeseclass1);
                    result.setTextColor(getResources().getColor(R.color.black));
                    result.setBackgroundColor(getResources().getColor(R.color.dusty_rose));
                }
                else if(resultCalculate >= 35 && resultCalculate <= 40){
                    result.setText(R.string.obeseclass2);
                    result.setTextColor(getResources().getColor(R.color.black));
                    result.setBackgroundColor(getResources().getColor(R.color.chili_red));
                }
                else if(resultCalculate > 40){
                    result.setText(R.string.obeseclass3);
                    result.setTextColor(getResources().getColor(R.color.white));
                    result.setBackgroundColor(getResources().getColor(R.color.black_red));
                }


                /*asg2*/
                events = new EventsData(MainActivity.this);
                try {
                    addEvent();

                } catch (Exception error) {
                    error.printStackTrace();
                } finally {
                    events.close();
                }



            }
        });

        if(isHistoryLayout){
            ImageView backButton = findViewById(R.id.imageView3);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setContentView(R.layout.activity_main);
                    isHistoryLayout = false;
                }
            });
        } else {
            ImageView historyButton = findViewById(R.id.imageView);
            historyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setContentView(R.layout.activity_history);
                    Cursor cursor = getEvents();
                    showEvents(cursor);
                    isHistoryLayout = true;
                }
            });
        }


    }



    DecimalFormat formatter = new DecimalFormat("#,###.##");

    class DecimalDigitsInputFilter implements InputFilter {
        private Pattern mPattern;
        DecimalDigitsInputFilter(int digits, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digits - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) +
                    "})?)||(\\.)?");
        }
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        final TextView txtView=findViewById(R.id.textView1);
        final TextView txtView2=findViewById(R.id.textView2);
        final TextView txtView3=findViewById(R.id.textView3);
        final TextView txtView4=findViewById(R.id.textView4);
        final TextView txtView5=findViewById(R.id.textView5);
        txtView.setTextSize(newConfig.fontScale*32);
        txtView2.setTextSize(newConfig.fontScale*24);
        txtView3.setTextSize(newConfig.fontScale*24);
        txtView4.setTextSize(newConfig.fontScale*24);
        txtView5.setTextSize(newConfig.fontScale*24);

        final EditText editText=findViewById(R.id.editText);
        final EditText editText2=findViewById(R.id.editText2);
        final TextView bmi=findViewById(R.id.bmi);
        final TextView result=findViewById(R.id.result);
        final Button calculate_btn=findViewById(R.id.calculate);

        editText.setTextSize(newConfig.fontScale*24);
        editText2.setTextSize(newConfig.fontScale*24);
        bmi.setTextSize(newConfig.fontScale*24);
        result.setTextSize(newConfig.fontScale*24);
        calculate_btn.setTextSize(newConfig.fontScale*24);
    }

    private void addEvent() {
        EditText et1 = findViewById(R.id.editText);
        TextView et2 = findViewById(R.id.bmi);
        TextView et3 = findViewById(R.id.result);

        String weight_asg2 = et1.getText().toString();
        String bmi_asg2 = et2.getText().toString();
        String result_asg2 = et3.getText().toString();

        SQLiteDatabase db = events.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIME, System.currentTimeMillis());
        values.put(WEIGHT, weight_asg2);
        values.put(BMI, bmi_asg2);
        values.put(Result, result_asg2);
        db.insert(TABLE_NAME, null, values);
    }//end addEvent

    private Cursor getEvents() {
        String[] FROM = {_ID, TIME, WEIGHT, BMI, Result};
        String ORDER_BY = TIME + " DESC";
        SQLiteDatabase db = events.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, FROM, null, null, null, null, ORDER_BY);
        return cursor;
    }//end getEvents

    private void showEvents(Cursor cursor) {

        final ListView listView = findViewById(R.id.listView);
        final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;

        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());

        while(cursor.moveToNext()) {
            map = new HashMap<String, String>();
            long timeInMillis = cursor.getLong(cursor.getColumnIndex(TIME));;
            String formattedTime = sdf.format(new Date(timeInMillis));
            map.put(TIME, formattedTime);
            map.put(WEIGHT, cursor.getString(2));
            map.put(BMI, cursor.getString(3));
            map.put(Result, cursor.getString(4));
            MyArrList.add(map);
        }
        SimpleAdapter sAdap;
        sAdap = new SimpleAdapter( MainActivity.this, MyArrList, R.layout.activity_column,
                new String[] {"time", "weight", "BMI", "Result"},
                new int[] {R.id.col_date_head, R.id.col_weight_head, R.id.col_BMI_head, R.id.col_criteria_head} );
        listView.setAdapter(sAdap);
    }//end showEvents



}