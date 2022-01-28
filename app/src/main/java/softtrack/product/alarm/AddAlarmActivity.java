package softtrack.product.alarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddAlarmActivity extends AppCompatActivity {

    @SuppressLint("WrongConstant") public SQLiteDatabase db;
    public String alarmDate;
    public String alarmTime;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        db = openOrCreateDatabase("alarms-database.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        Button addAlarmFooterCancelBtn = findViewById(R.id.addAlarmFooterCancelBtn);
        addAlarmFooterCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddAlarmActivity.this, MainActivity.class);
                intent.putExtra("created", false);
                AddAlarmActivity.this.startActivity(intent);
            }
        });
        Button addAlarmFooterCreateBtn = findViewById(R.id.addAlarmFooterCreateBtn);
        addAlarmFooterCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText addAlarmSignalName = findViewById(R.id.addAlarmSignalName);
                CharSequence rawSignalName = addAlarmSignalName.getText();
                String signalName = rawSignalName.toString();
                db.execSQL("INSERT INTO \"alarms\"(time, date, isEnabled, name) VALUES (\"" + alarmTime + "\", \"" + alarmDate + "\", " + true + ", \"" + signalName + "\");");

                // пока будильних делает переход между activity
                /*AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                PendingIntent pi = PendingIntent.getActivity(AddAlarmActivity.this, 111, new Intent(AddAlarmActivity.this, MainActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                am.setInexactRepeating(AlarmManager.RTC, SystemClock.elapsedRealtime() + 5000, 10000, pi
                );*/
                // пока будильних делает переход между activity
                setAlarm();
                Intent intent = new Intent(AddAlarmActivity.this, MainActivity.class);
                intent.putExtra("created", true);
                AddAlarmActivity.this.startActivity(intent);
            }
        });

        Button addAlarmDateInput = findViewById(R.id.addAlarmDateInput);
        addAlarmDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                DatePickerDialog picker = new DatePickerDialog(AddAlarmActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        alarmDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    }
                }, year, month, day);
                picker.show();
            }
        });

        ScrollView addAlarmTimeInputMinutesScroll = findViewById(R.id.addAlarmTimeInputMinutesScroll);
        addAlarmTimeInputMinutesScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                alarmTime = String.valueOf(i) + ":" + String.valueOf(i1);
            }
        });
        ScrollView addAlarmTimeInputHoursScroll = findViewById(R.id.addAlarmTimeInputHoursScroll);
        addAlarmTimeInputHoursScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                alarmTime = String.valueOf(i2) + ":" + String.valueOf(i3);
            }
        });

    }

    public void setAlarm() {
        AlarmManager mAlarmManager;
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long millis = 1000l;

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + millis, pendingIntent);
        mAlarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + millis, 20000, pendingIntent);
    }


}
