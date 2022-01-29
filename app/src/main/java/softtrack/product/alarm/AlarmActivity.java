package softtrack.product.alarm;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AlarmActivity extends Fragment {

    public ArrayList<HashMap<String, Object>> havedAlarms;
    @SuppressLint("WrongConstant") public SQLiteDatabase db;
    public final String notFoundAlarmsLabel = "Нет будильников";
    public final float notFoundAlarmsLabelSize = 20f;
    public int alarmInitialBackgroundColor;
    public int alarmSelectedBackgroundColor;
    public LinearLayout alarms;
    public TextView alarmsTitle;
    public String emptySelectedAlarmLabel = "Выберите сигналы";
    public ArrayList<String> monthLabels;
    public ArrayList<String> dayOfWeekLabels;
    public CheckBox allAlarmsSelector;

    public AlarmActivity() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_alarm, container, false);
        return view;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onStart() {
        super.onStart();
        getDebug();
        alarmInitialBackgroundColor = Color.rgb(255, 255, 255);
        alarmSelectedBackgroundColor = Color.rgb(185, 185, 185);
        alarms = getActivity().findViewById(R.id.alarms);

        initializeAlarmDateLabels();

        showNearAlarmInfo();

        LinearLayout alarmFooterToggleIsEnabled = getActivity().findViewById(R.id.alarmFooterToggleIsEnabled);
        alarmFooterToggleIsEnabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleAlarm();
            }
        });

        LinearLayout alarmFooterRemove = getActivity().findViewById(R.id.alarmFooterRemove);
        alarmFooterRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAlarms();
            }
        });

        allAlarmsSelector = getActivity().findViewById(R.id.allAlarmsSelector);
        allAlarmsSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean isSelectAllAlarms = b;
                if (isSelectAllAlarms) {
                    int alarmsCount = alarms.getChildCount();
                    for (int alarmIndex = 0; alarmIndex < alarmsCount; alarmIndex++) {
                        LinearLayout alarm = ((LinearLayout) (alarms.getChildAt(alarmIndex)));
                        CheckBox alarmSelector = ((CheckBox)(alarm.getChildAt(0)));
                        alarmSelector.setChecked(true);
                    }
                }
            }
        });

    }

    @SuppressLint("WrongConstant")
    public void redrawAlarms() {
        db = getActivity().openOrCreateDatabase("alarms-database.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        alarms.removeAllViews();
        havedAlarms = new ArrayList<HashMap<String, Object>>();
        Cursor alarmsCursor = db.rawQuery("Select * from alarms", null);
        alarmsCursor.moveToFirst();
        long alarmsCount = DatabaseUtils.queryNumEntries(db, "alarms");
        boolean isHaveAlarms = alarmsCount >= 1;
        if (isHaveAlarms) {
            for (int alarmIndex = 0; alarmIndex < alarmsCount; alarmIndex++)  {
                int alarmId = alarmsCursor.getInt(0);
                String alarmTime = alarmsCursor.getString(1);
                String alarmDate = alarmsCursor.getString(2);
                int rawAlarmIsEnabled = alarmsCursor.getInt(3);
                boolean alarmIsEnabled = false;
                if (rawAlarmIsEnabled == 1) {
                    alarmIsEnabled = true;
                }
                HashMap<String, Object> newAlarm = new HashMap<String, Object>();
                newAlarm.put("id", alarmId);
                newAlarm.put("time", alarmTime);
                newAlarm.put("date", alarmDate);
                newAlarm.put("isEnabled", alarmIsEnabled);
                havedAlarms.add(newAlarm);
                alarmsCursor.moveToNext();
            }
            for (HashMap<String, Object> alarm : havedAlarms) {
                LinearLayout newAlarm = new LinearLayout(getActivity().getApplicationContext());
                newAlarm.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                        ColorDrawable rawAlarmColor = ((ColorDrawable)(newAlarm.getBackground()));
                        int alarmColor = rawAlarmColor.getColor();
                        boolean isAlarmUnselected = alarmColor == alarmInitialBackgroundColor;
                        /*if (isAlarmUnselected) {
                            newAlarm.setBackgroundColor(alarmSelectedBackgroundColor);
                        } else {
                            newAlarm.setBackgroundColor(alarmInitialBackgroundColor);
                        }*/
                        boolean isTabsVisible = !MainActivity.isSelectionFooter;
                        if (isTabsVisible) {
                            openAlarmFooter();
                        }
                        int countSelectedAlarms = 0;
                        for (int alarmIndex = 0; alarmIndex < alarmsCount; alarmIndex++) {
                            LinearLayout alarm = ((LinearLayout) (alarms.getChildAt(alarmIndex)));
                            ColorDrawable rawCurrentAlarmColor = ((ColorDrawable) (alarm.getBackground()));
                            int currentAlarmColor = rawCurrentAlarmColor.getColor();
                            boolean isAlarmSelected = currentAlarmColor == alarmSelectedBackgroundColor;
                            boolean isSelectionMode = MainActivity.isSelectionFooter;
                            if (isSelectionMode) {
                                CheckBox alarmSelector = ((CheckBox) (alarm.getChildAt(0)));
                                isAlarmSelected = isAlarmSelected = alarmSelector.isChecked();
                                if (isAlarmSelected) {
                                    countSelectedAlarms++;
                                }
                            }
                        }
                        String rawCountSelectedAlarms = String.valueOf(countSelectedAlarms);
                        String countSelectedAlarmsLabel = "Выбрано: " + rawCountSelectedAlarms;
                        boolean isCountSelectedAlarmsEmpty = countSelectedAlarms == 0;
                        if (isCountSelectedAlarmsEmpty) {
                            countSelectedAlarmsLabel = emptySelectedAlarmLabel;
                        }
                        alarmsTitle.setText(countSelectedAlarmsLabel);
                    }
                });
                newAlarm.setBackgroundColor(alarmInitialBackgroundColor);
                LinearLayout.LayoutParams newAlarmLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250);
                newAlarmLayoutParams.setMargins(0, 50, 0, 50);
                newAlarm.setLayoutParams(newAlarmLayoutParams);
                newAlarm.setOrientation(LinearLayout.HORIZONTAL);
                TextView newAlarmTime = new TextView(getActivity().getApplicationContext());
                Object rawAlarmTime = alarm.get("time");
                String alarmTime = rawAlarmTime.toString();
                newAlarmTime.setText(alarmTime);
                LinearLayout.LayoutParams newAlarmTimeLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                newAlarmTimeLayoutParams.setMargins(50, 65, 0, 0);
                newAlarmTime.setLayoutParams(newAlarmTimeLayoutParams);
                newAlarmTime.setTextSize(24);
                newAlarm.addView(newAlarmTime);
                Switch newAlarmDateAndIsEnabled = new Switch(getActivity().getApplicationContext());
                Object rawAlarmDate = alarm.get("date");
                String alarmDate = rawAlarmDate.toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String alarmDateContent = alarmDate;
                try {
                    Date d1 = sdf.parse(alarmDate);
                    Calendar parsedAlarmDate = Calendar.getInstance();
                    parsedAlarmDate.setTime(d1);
                    int dayOfWeekIndex = parsedAlarmDate.get(Calendar.DAY_OF_WEEK) - 1;
                    String dayOfWeekLabel = dayOfWeekLabels.get(dayOfWeekIndex);
                    int rawDayLabel = parsedAlarmDate.get(Calendar.DATE);
                    String dayLabel = String.valueOf(rawDayLabel);
                    int monthIndex = parsedAlarmDate.get(Calendar.MONTH);
                    String monthLabel = monthLabels.get(monthIndex);
                    alarmDateContent = dayOfWeekLabel + ", " + dayLabel + " " + monthLabel;
                } catch (ParseException e) {
                    Log.d("debug", "ошибка парсинга даты будильника");
                }
                newAlarmDateAndIsEnabled.setText(alarmDateContent);
                Object rawAlarmId = alarm.get("id");
                String parsedRawAlarmId = String.valueOf(rawAlarmId);
                newAlarmDateAndIsEnabled.setContentDescription(parsedRawAlarmId);
                Object rawAlarmIsEnabled = alarm.get("isEnabled");
                boolean alarmIsEnabled = ((boolean)(rawAlarmIsEnabled));
                if (alarmIsEnabled) {
                    newAlarmDateAndIsEnabled.setChecked(true);
                }
                newAlarmDateAndIsEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        // переключение будильника
                        CharSequence rawAlarmId = compoundButton.getContentDescription();
                        String parsedRawAlarmId = rawAlarmId.toString();
                        int alarmId = Integer.valueOf(parsedRawAlarmId);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("isEnabled", compoundButton.isChecked());
                        db.update("alarms", contentValues, "_id = ? ", new String[] { Integer.toString(alarmId) } );
                        boolean isAlarmEnabled = b;
                        if (isAlarmEnabled) {
                            Cursor alarmsCursor = db.rawQuery("Select * from alarms where _id=" + Integer.toString(alarmId), null);
                            alarmsCursor.moveToFirst();
                            String alarmTime = alarmsCursor.getString(1);
                            String alarmDate = alarmsCursor.getString(2);
                            String timeForNearAlarm = alarmTime + " " + alarmDate;
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                String start_date = alarmDate + " " + alarmTime;
                                Date d1 = sdf.parse(start_date);
                                Date d2 = new Date();
                                d2.setTime(Calendar.getInstance().getTimeInMillis());
                                long endDateMillis = d2.getTime();
                                endDateMillis = System.currentTimeMillis();
                                long difference_In_Time = endDateMillis - d1.getTime();
                                long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;
                                String minutesPostfix = " минут";
                                String rawMinutes = String.valueOf(difference_In_Minutes);
                                boolean isOneMinutePostfix = rawMinutes.endsWith("1");
                                boolean isManyMinutesPostfix = rawMinutes.endsWith("2") || rawMinutes.endsWith("3") || rawMinutes.endsWith("4");
                                if (isOneMinutePostfix) {
                                    minutesPostfix += "у";
                                } else if (isManyMinutesPostfix) {
                                    minutesPostfix += "ы";
                                }
                                long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;
                                String hoursPostfix = " час";
                                String rawHours = String.valueOf(difference_In_Hours);
                                boolean isMultipleHoursPostfix = rawHours.endsWith("2") || rawHours.endsWith("3") || rawHours.endsWith("4");
                                boolean isManyHoursPostfix = difference_In_Hours >= 5 || difference_In_Hours == 0;
                                if (isMultipleHoursPostfix) {
                                    hoursPostfix += "а";
                                } else if (isManyHoursPostfix) {
                                    hoursPostfix += "ов";
                                }
                                long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
                                String daysPostfix = " д";
                                String rawDays = String.valueOf(difference_In_Days);
                                boolean isOneDayPostfix = rawDays.endsWith("1");
                                boolean isMultipleDaysPostfix = rawDays.endsWith("2") || rawDays.endsWith("3") || rawDays.endsWith("4");
                                boolean isManyDaysPostfix = difference_In_Days >= 5 || difference_In_Days == 0;
                                if (isOneDayPostfix) {
                                    daysPostfix += "ень";
                                } else if (isMultipleDaysPostfix) {
                                    daysPostfix += "ня";
                                } else if (isManyDaysPostfix) {
                                    daysPostfix += "ней";
                                }
                                long difference_In_Years = (difference_In_Time / (1000l * 60 * 60 * 24 * 365));
                                timeForNearAlarm = difference_In_Days  + daysPostfix + " " + difference_In_Hours  + hoursPostfix + " " + difference_In_Minutes + minutesPostfix;
                            } catch (ParseException e) {
                                Log.d("debug", "Не могу пропарсить дату " + e.getMessage());
                            }
                            String toastMessage = "Будильник \n\nсработает \nчерез \n" + timeForNearAlarm;
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(), toastMessage, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
                LinearLayout.LayoutParams newAlarmDateAndIsEnabledLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                newAlarmDateAndIsEnabledLayoutParams.setMargins(800, 0, 0, 0);
                newAlarmDateAndIsEnabled.setLayoutParams(newAlarmDateAndIsEnabledLayoutParams);
                newAlarm.addView(newAlarmDateAndIsEnabled);
                newAlarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean isCanEdit = !MainActivity.isSelectionFooter;
                        if (isCanEdit) {
                            Intent intent = new Intent(getActivity(), AddAlarmActivity.class);
                            getActivity().startActivity(intent);
                        }
                    }
                });
                alarms.addView(newAlarm);
            }
        } else {
            alarmsTitle.setText("Будильник");
            TextView notFoundAlarms = new TextView(getActivity());
            notFoundAlarms.setText(notFoundAlarmsLabel);
            notFoundAlarms.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            notFoundAlarms.setTextSize(notFoundAlarmsLabelSize);
            LinearLayout.LayoutParams notFoundAlarmsLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250);
            notFoundAlarms.setLayoutParams(notFoundAlarmsLayoutParams);
            alarms.addView(notFoundAlarms);
        }

        Button addAlarmBtn = getActivity().findViewById(R.id.addAlarmBtn);
        addAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddAlarmActivity.class);
                getActivity().startActivity(intent);
            }
        });

        Button alarmsContextMenuBtn = getActivity().findViewById(R.id.alarmsContextMenuBtn);
        alarmsContextMenuBtn.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(Menu.NONE, 101, Menu.NONE, "Установить время отхода ко сну и пробуждения");
                contextMenu.add(Menu.NONE, 102, Menu.NONE, "Изменить");
                contextMenu.add(Menu.NONE, 103, Menu.NONE, "Настройки");
                contextMenu.add(Menu.NONE, 104, Menu.NONE, "Свяжитесь с нами");
            }
        });

    }

    public void showNearAlarmInfo() {
        alarmsTitle = getActivity().findViewById(R.id.alarmsTitle);
        redrawAlarms();
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null) {
            boolean isCreatedAlarm = extras.getBoolean("created");
            boolean isRunAlarm = extras.getBoolean("runed");
            if (isCreatedAlarm) {
                String timeForNearAlarm = "Неизвестно";
                String toastMessage = "Будильник \n\nсработает \nчерез \n" + timeForNearAlarm;
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), toastMessage, Toast.LENGTH_SHORT);
                toast.show();
            } else if (isRunAlarm) {
                MediaPlayer audio = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.wake_snd);
                audio.start();
            }
        }
    }

    public void getDebug() {
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String parsedUri = uri.getPath();
        Log.d("debug", parsedUri);
    }

    public void removeAlarms() {
        int alarmsCount = alarms.getChildCount();
        for (int alarmIndex = 0; alarmIndex < alarmsCount; alarmIndex++) {
            LinearLayout alarm = ((LinearLayout)(alarms.getChildAt(alarmIndex)));
            ColorDrawable rawAlarmColor = ((ColorDrawable)(alarm.getBackground()));
            int alarmColor = rawAlarmColor.getColor();
            boolean isAlarmSelected = alarmColor == alarmSelectedBackgroundColor;
            boolean isSelectionMode = MainActivity.isSelectionFooter;
            if (isSelectionMode) {
                CheckBox alarmSelector = ((CheckBox)(alarm.getChildAt(0)));
                isAlarmSelected = isAlarmSelected = alarmSelector.isChecked();
                if (isAlarmSelected) {
                    View alarmAside = alarm.getChildAt(2);
                    CharSequence rawAlarmAsideData = alarmAside.getContentDescription();
                    String alarmAsideData = rawAlarmAsideData.toString();
                    int alarmId = Integer.valueOf(alarmAsideData);
                    db.execSQL("DELETE FROM alarms WHERE _id=" + alarmId + ";");
                }
            }
        }
        closeAlarmFooter();
        redrawAlarms();
    }

    public void unselectAlarms() {
        int alarmsCount = alarms.getChildCount();
        for (int alarmIndex = 0; alarmIndex < alarmsCount; alarmIndex++) {
            LinearLayout alarm = ((LinearLayout)(alarms.getChildAt(alarmIndex)));
            alarm.setBackgroundColor(alarmInitialBackgroundColor);
        }
    }

    public void openAlarmFooter() {
        int isVisible = View.VISIBLE;
        CheckBox allAlarmsSelector = getActivity().findViewById(R.id.allAlarmsSelector);
        allAlarmsSelector.setVisibility(isVisible);
        MainActivity.gateway.tabs.setVisibility(View.GONE);
        View alarmFooter = getActivity().findViewById(R.id.alarmFooter);
        alarmFooter.setVisibility(isVisible);
        MainActivity.isSelectionFooter = true;

        int alarmsCount = alarms.getChildCount();
        for (int alarmIndex = 0; alarmIndex < alarmsCount; alarmIndex++) {
            LinearLayout alarm = ((LinearLayout)(alarms.getChildAt(alarmIndex)));
            CheckBox alarmSelector = new CheckBox(getActivity());
            GradientDrawable shape =  new GradientDrawable();
            shape.setCornerRadius(500);
            alarmSelector.setBackground(shape);
            alarmSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    boolean isAlarmUnselected = b;
                    LinearLayout alarm = ((LinearLayout)(compoundButton.getParent()));
                    if (isAlarmUnselected) {
                        alarm.setBackgroundColor(alarmSelectedBackgroundColor);
                    } else {
                        alarm.setBackgroundColor(alarmInitialBackgroundColor);
                    }
                }
            });
            LinearLayout.LayoutParams alarmSelectorLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            alarmSelector.setLayoutParams(alarmSelectorLayoutParams);
            alarm.addView(alarmSelector, 0);
        }

    }

    public void closeAlarmFooter() {
        MainActivity.isSelectionFooter = false;
        int isVisible = View.VISIBLE;
        MainActivity.gateway.tabs.setVisibility(isVisible);
        View alarmFooter = getActivity().findViewById(R.id.alarmFooter);
        int isUnvisible = View.GONE;
        alarmFooter.setVisibility(isUnvisible);
        unselectAlarms();
        alarmsTitle.setText("Все будильники\nотключены");

        int alarmsCount = alarms.getChildCount();
        for (int alarmIndex = 0; alarmIndex < alarmsCount; alarmIndex++) {
            LinearLayout alarm = ((LinearLayout)(alarms.getChildAt(alarmIndex)));
            alarm.removeViewAt(0);
        }

        CheckBox allAlarmsSelector = getActivity().findViewById(R.id.allAlarmsSelector);
        allAlarmsSelector.setVisibility(isUnvisible);

    }

    public void toggleAlarm() {
        int alarmsCount = alarms.getChildCount();
        for (int alarmIndex = 0; alarmIndex < alarmsCount; alarmIndex++) {
            LinearLayout alarm = ((LinearLayout)(alarms.getChildAt(alarmIndex)));
            ColorDrawable rawAlarmColor = ((ColorDrawable)(alarm.getBackground()));
            int alarmColor = rawAlarmColor.getColor();
            boolean isAlarmSelected = alarmColor == alarmSelectedBackgroundColor;
            boolean isSelectionMode = MainActivity.isSelectionFooter;
            if (isSelectionMode) {
                CheckBox alarmSelector = ((CheckBox) (alarm.getChildAt(0)));
                isAlarmSelected = isAlarmSelected = alarmSelector.isChecked();
                if (isAlarmSelected) {
                    Switch alarmAside = ((Switch) (alarm.getChildAt(2)));
                    CharSequence rawAlarmAsideData = alarmAside.getContentDescription();
                    String alarmAsideData = rawAlarmAsideData.toString();
                    int alarmId = Integer.valueOf(alarmAsideData);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("isEnabled", !alarmAside.isChecked());
                    db.update("alarms", contentValues, "_id = ? ", new String[]{Integer.toString(alarmId)});
                }
            }
        }
        closeAlarmFooter();
        redrawAlarms();
    }

    public int getDayNumberOld(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public void initializeAlarmDateLabels() {
        dayOfWeekLabels = new ArrayList<String>();
        dayOfWeekLabels.add("воскр.");
        dayOfWeekLabels.add("пон.");
        dayOfWeekLabels.add("втор.");
        dayOfWeekLabels.add("сред.");
        dayOfWeekLabels.add("четв.");
        dayOfWeekLabels.add("пятн.");
        dayOfWeekLabels.add("суб.");
        monthLabels = new ArrayList<String>();
        monthLabels.add("янв.");
        monthLabels.add("фев.");
        monthLabels.add("мар.");
        monthLabels.add("апр.");
        monthLabels.add("мая");
        monthLabels.add("июн.");
        monthLabels.add("июл.");
        monthLabels.add("авг.");
        monthLabels.add("сен.");
        monthLabels.add("окт.");
        monthLabels.add("ноя.");
        monthLabels.add("дек.");
    }

}
