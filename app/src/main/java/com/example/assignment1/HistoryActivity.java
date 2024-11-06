package com.example.assignment1;

import static android.provider.BaseColumns._ID;
import static com.example.assignment1.Constants.BMI;
import static com.example.assignment1.Constants.Result;
import static com.example.assignment1.Constants.TABLE_NAME;
import static com.example.assignment1.Constants.TIME;
import static com.example.assignment1.Constants.WEIGHT;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {
    private EventsData events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainHistory), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        events = new EventsData(this);
        Cursor cursor = getEvents();
        showEvents(cursor);

        ImageView backButton = findViewById(R.id.imageView3);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

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

            String value = cursor.getString(4);

            int intValue = Integer.parseInt(value);
            String resultString = getString(intValue);

            map.put(Result, resultString);
            MyArrList.add(map);
        }
        SimpleAdapter sAdap;
        sAdap = new SimpleAdapter( HistoryActivity.this, MyArrList, R.layout.activity_column,
                new String[] {"time", "weight", "BMI", "Result"},
                new int[] {R.id.col_date_head, R.id.col_weight_head, R.id.col_BMI_head, R.id.col_criteria_head} );
        listView.setAdapter(sAdap);
    }//end showEvents
}