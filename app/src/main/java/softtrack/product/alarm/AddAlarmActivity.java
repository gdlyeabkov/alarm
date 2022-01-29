package softtrack.product.alarm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddAlarmActivity extends AppCompatActivity {

    @SuppressLint("WrongConstant") public SQLiteDatabase db;
    public String alarmDate = "28/01/2022";
    public String alarmTime = "18:10";
    public String oneCharPrefix = "0";
    public Switch soundAlarmSwitch;
    public Switch alarmPauseSwitch;
    public Switch vibrationAlarmSwitch;
    public String chosenRingtone = "";
    public String addAlarmHours = "00";
    public String addAlarmMinutes = "00";

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
                intent.putExtra("runed", false);
                intent.putExtra("id", 0);
                AddAlarmActivity.this.startActivity(intent);
            }
        });
        Button addAlarmFooterCreateBtn = findViewById(R.id.addAlarmFooterCreateBtn);
        addAlarmFooterCreateBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                EditText addAlarmSignalName = findViewById(R.id.addAlarmSignalName);
                CharSequence rawSignalName = addAlarmSignalName.getText();
                String signalName = rawSignalName.toString();
                boolean isVibrationEnabled = vibrationAlarmSwitch.isChecked();
                alarmTime = addAlarmHours + ":" + addAlarmMinutes;
                db.execSQL("INSERT INTO \"alarms\"(time, date, isEnabled, name, sound, isVibrationEnabled) VALUES (\"" + alarmTime + "\", \"" + alarmDate + "\", " + true + ", \"" + signalName + "\", \"" + chosenRingtone + "\", " + isVibrationEnabled + ");");

                Cursor alarmsCursor = db.rawQuery("Select * from alarms", null);
                alarmsCursor.moveToLast();
                int alarmId = alarmsCursor.getInt(0);
                setAlarm(alarmId);

                Intent intent = new Intent(AddAlarmActivity.this, MainActivity.class);
                intent.putExtra("created", true);
                intent.putExtra("runed", false);
                intent.putExtra("id", 0);
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

        ScrollView addAlarmTimeInputHoursScroll = findViewById(R.id.addAlarmTimeInputHoursScroll);
        addAlarmTimeInputHoursScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                LinearLayout hoursColumn = ((LinearLayout) (addAlarmTimeInputHoursScroll.getChildAt(0)));
                int destinationBetweenScrollItems = 105;
                int selectedLabelIndex = ((int) (Math.floor(i1 / destinationBetweenScrollItems)));
                int hoursColumnLabelsCount = hoursColumn.getChildCount();
                boolean isChildrenAccess = selectedLabelIndex < hoursColumnLabelsCount;
                if (isChildrenAccess) {
                    for (int hoursLabelIndex = 1; hoursLabelIndex < hoursColumnLabelsCount; hoursLabelIndex++) {
                        TextView currentLabel = ((TextView)(hoursColumn.getChildAt(hoursLabelIndex)));
                        Typeface labelTypeface = Typeface.DEFAULT;
                        currentLabel.setTypeface(labelTypeface, Typeface.NORMAL);
                    }
                    TextView selectedLabel = ((TextView)(hoursColumn.getChildAt(selectedLabelIndex)));
                    Typeface labelTypeface = selectedLabel.getTypeface();
                    selectedLabel.setTypeface(labelTypeface, Typeface.BOLD);
                    CharSequence rawSelectLabelContent = selectedLabel.getText();
                    String selectLabelContent = rawSelectLabelContent.toString();
                    addAlarmHours = selectLabelContent;
                }
            }
        });

        ScrollView addAlarmTimeInputMinutesScroll = findViewById(R.id.addAlarmTimeInputMinutesScroll);
        addAlarmTimeInputMinutesScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                LinearLayout minutesColumn = ((LinearLayout) (addAlarmTimeInputMinutesScroll.getChildAt(0)));
                int destinationBetweenScrollItems = 109;
                int selectedLabelIndex = ((int) (Math.floor(i1 / destinationBetweenScrollItems)));
                int minutesColumnLabelsCount = minutesColumn.getChildCount();
                boolean isChildrenAccess = selectedLabelIndex < minutesColumnLabelsCount;
                if (isChildrenAccess) {
                    for (int minutesLabelIndex = 1; minutesLabelIndex < minutesColumnLabelsCount; minutesLabelIndex++) {
                        TextView currentLabel = ((TextView)(minutesColumn.getChildAt(minutesLabelIndex)));
                        Typeface labelTypeface = Typeface.DEFAULT;
                        currentLabel.setTypeface(labelTypeface, Typeface.NORMAL);
                    }
                    TextView selectedLabel = ((TextView)(minutesColumn.getChildAt(selectedLabelIndex)));
                    Typeface labelTypeface = selectedLabel.getTypeface();
                    selectedLabel.setTypeface(labelTypeface, Typeface.BOLD);
                    CharSequence rawSelectLabelContent = selectedLabel.getText();
                    String selectLabelContent = rawSelectLabelContent.toString();
                    addAlarmMinutes = selectLabelContent;
                }
            }
        });

        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        String rawDay = String.valueOf(day);
        int month = cldr.get(Calendar.MONTH);
        String rawMonth = String.valueOf(month + 1);
        int year = cldr.get(Calendar.YEAR);
        if (rawDay.length() == 1) {
            rawDay = oneCharPrefix + rawDay;
        }
        if (rawMonth.length() == 1) {
            rawMonth = oneCharPrefix + rawMonth;
        }
        alarmDate = rawDay + "/" + rawMonth + "/" + year;

        soundAlarmSwitch = findViewById(R.id.soundAlarmSwitch);
        soundAlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean isSoundPermission = soundAlarmSwitch.isChecked();
                if (isSoundPermission) {
                    Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                    startActivityForResult(intent, 5);
                }
            }
        });

        vibrationAlarmSwitch = findViewById(R.id.vibrationAlarmSwitch);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setAlarm(int alarmId) {
        AlarmManager mAlarmManager;
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long millis = 5000l;

        Intent alarmIntent = new Intent(getApplicationContext(), MainActivity.class);
        alarmIntent.putExtra("created", false);
        alarmIntent.putExtra("runed", true);
        alarmIntent.putExtra("id", 0);

        Intent broadcastContext = new Intent(getApplicationContext(), AlarmManagerBroadcastReceiver.class);
        broadcastContext.putExtra("id", alarmId);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),0, broadcastContext,0);

        Calendar calendar = Calendar.getInstance();
        String[] alarmDateParts = alarmDate.split("/");
        String rawAlarmDateDay = alarmDateParts[0];
        int alarmDateDay = Integer.valueOf(rawAlarmDateDay);
        String rawAlarmDateMonth = alarmDateParts[1];
        int alarmDateMonth = Integer.valueOf(rawAlarmDateMonth);
        String rawAlarmDateYear = alarmDateParts[2];
        int alarmDateYear = Integer.valueOf(rawAlarmDateYear);
        String[] alarmTimeParts = alarmTime.split(":");
        String rawHoursAlarmTime = alarmTimeParts[0];
        int hoursAlarmTime = Integer.valueOf(rawHoursAlarmTime);
        String rawMinutesAlarmTime = alarmTimeParts[1];
        int minutesAlarmTime = Integer.valueOf(rawMinutesAlarmTime);
        Log.d("debug", String.valueOf(alarmDateYear) + "/" + String.valueOf(alarmDateMonth) + "/" + String.valueOf(alarmDateDay) + " " + String.valueOf(hoursAlarmTime) + ":" + String.valueOf(minutesAlarmTime));
        calendar.set(alarmDateYear, alarmDateMonth, alarmDateDay, hoursAlarmTime, minutesAlarmTime);
        millis = calendar.getTimeInMillis();
        alarmPauseSwitch = findViewById(R.id.alarmPauseSwitch);
        boolean isNeedRepeat = alarmPauseSwitch.isChecked();
        if (isNeedRepeat) {
            mAlarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 5000, 5000, pi);
        } else {
            mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 5000, pi);
        }
        boolean isNeedVibrate = vibrationAlarmSwitch.isChecked();
        if (isNeedVibrate) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            VibrationEffect vibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE);
            v.vibrate(vibrationEffect);
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("isEnabled", false);
        db.update("alarms", contentValues, "_id = ? ", new String[] { Integer.toString(alarmId) } );

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK && requestCode == 5) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null) {
                chosenRingtone = uri.toString();
            } else {
                chosenRingtone = "";
            }
        }
    }


}
