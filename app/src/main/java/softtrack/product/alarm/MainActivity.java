package softtrack.product.alarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.WindowDecorActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    public LinearLayout tabsLabels;
    @SuppressLint("WrongConstant") public SQLiteDatabase db;
    public static ViewPager2 viewPager;
    private static Context instance;
    public static String timerHours = "00";
    public static String timerMinutes = "00";
    public static String timerSeconds = "00";
    public static MainActivity gateway;
    public TabLayout tabs;
    public static boolean isSelectionFooter = false;
    public LinearLayout alarms;
    public int alarmInitialBackgroundColor;
    public LinearLayout cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabs = findViewById(R.id.mainTabs);
        viewPager = findViewById(R.id.currentTab);

        FragmentManager fm = getSupportFragmentManager();
        ViewStateAdapter sa = new ViewStateAdapter(fm, getLifecycle());
        viewPager.setAdapter(sa);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                boolean isStartTimerActivity = position == 4;
                if (isStartTimerActivity) {
                    tabs.selectTab(tabs.getTabAt(position));
                    StartTimerActivity.activityContext.startTimerTitle.setText(MainActivity.timerHours + ":" + MainActivity.timerMinutes + ":" + MainActivity.timerSeconds);
                    StartTimerActivity.activityContext.startTimer();
                }
                else {
                    boolean isStartTimerActivityExists = StartTimerActivity.activityContext != null;
                    if (isStartTimerActivityExists) {
                        /*
                         *  Пытался остановить таймер при каждом переходе на любую вкладку кроме StartTimer но возникает ошибка
                         */
                        // StartTimerActivity.activityContext.stopTimer();
                    }
                }
            }
        });

        db = openOrCreateDatabase("alarms-database.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS alarms (_id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, date TEXT, isEnabled BOOLEAN, name TEXT, sound TEXT, isVibrationEnabled BOOLEAN);");
        db.execSQL("CREATE TABLE IF NOT EXISTS cities (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS timers (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, hours TEXT, minutes TEXT, seconds TEXT);");

        instance = MainActivity.this;
        gateway = MainActivity.this;

    }

    public void toggleTabHandler(View view) {
        CharSequence rawTabData = view.getContentDescription();
        String tabData = rawTabData.toString();
        for (int tabIndex = 0; tabIndex < tabsLabels.getChildCount(); tabIndex++) {
            View rawTab = tabsLabels.getChildAt(tabIndex);
            TextView tab = ((TextView)(rawTab));
            Typeface tabTypeface = tab.getTypeface();
            CharSequence currentRawTabData = tab.getContentDescription();
            String currentTabData = currentRawTabData.toString();
            boolean isToggleTab = tabData == currentTabData;
            if (isToggleTab) {
                tab.setTypeface(tabTypeface, Typeface.BOLD);
            } else {
                tabTypeface = Typeface.DEFAULT;
                tab.setTypeface(tabTypeface, Typeface.NORMAL);
            }
        }
    }

    private class ViewStateAdapter extends FragmentStateAdapter {

        public ViewStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Hardcoded in this order, you'll want to use lists and make sure the titles match
            if (position == 0) {
                return new AlarmActivity();
            } else if (position == 1) {
                return new WorldTimeActivity();
            } else if (position == 2) {
                return new StopWatchActivity();
            } else if (position == 3) {
                return new TimerActivity();
            } else if (position == 4) {
                StartTimerActivity startTimerActivity = new StartTimerActivity();
                return startTimerActivity;
            }
            return new AlarmActivity();
        }

        @Override
        public int getItemCount() {
            // Hardcoded, use lists
            return 5;
        }
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public void setTimerHours(View hoursLabel) {
        TextView timeLabel = ((TextView)(hoursLabel));
        CharSequence rawTimerHours = timeLabel.getText();
        String parsedRawTimerHours = rawTimerHours.toString();
        // timerHours = Integer.valueOf(parsedRawTimerHours);
        timerHours = parsedRawTimerHours;
    }

    public void setTimerMinutes(View minutesLabel) {
        TextView timeLabel = ((TextView)(minutesLabel));
        CharSequence rawTimerMinutes = timeLabel.getText();
        String parsedRawTimerMinutes = rawTimerMinutes.toString();
//        timerMinutes = Integer.valueOf(parsedRawTimerMinutes);
        timerMinutes = parsedRawTimerMinutes;
    }

    public void setTimerSeconds(View secondsLabel) {
        TextView timeLabel = ((TextView)(secondsLabel));
        CharSequence rawTimerSeconds = timeLabel.getText();
        String parsedRawTimerSeconds = rawTimerSeconds.toString();
//        timerSeconds = Integer.valueOf(parsedRawTimerSeconds);
        timerSeconds = parsedRawTimerSeconds;
    }

    @Override
    public void onBackPressed() {
        if (!isSelectionFooter) {
            super.onBackPressed();
        } else {
            boolean isAlarmActivity = viewPager.getCurrentItem() == 0;
            boolean isWorldTimeActivity = viewPager.getCurrentItem() == 1;
            if (isAlarmActivity) {
                closeAlarmFooter();
            } else if (isWorldTimeActivity) {
                closeWorldTimeFooter();
            }
        }

    }

    public void unselectAlarms() {
        alarmInitialBackgroundColor = Color.rgb(255, 255, 255);
        alarms = findViewById(R.id.alarms);
        int alarmsCount = alarms.getChildCount();
        for (int alarmIndex = 0; alarmIndex < alarmsCount; alarmIndex++) {
            LinearLayout alarm = ((LinearLayout)(alarms.getChildAt(alarmIndex)));
            alarm.setBackgroundColor(alarmInitialBackgroundColor);
        }
    }

    public void closeAlarmFooter() {
        isSelectionFooter = false;
        int isVisible = View.VISIBLE;
        MainActivity.gateway.tabs.setVisibility(isVisible);
        View alarmFooter = findViewById(R.id.alarmFooter);
        int isUnvisible = View.GONE;
        alarmFooter.setVisibility(isUnvisible);
        unselectAlarms();
        TextView alarmsTitle = findViewById(R.id.alarmsTitle);
        alarmsTitle.setText("Все будильники\nотключены");

        int alarmsCount = alarms.getChildCount();
        for (int alarmIndex = 0; alarmIndex < alarmsCount; alarmIndex++) {
            LinearLayout alarm = ((LinearLayout)(alarms.getChildAt(alarmIndex)));
            alarm.removeViewAt(0);
        }

        CheckBox allAlarmsSelector = findViewById(R.id.allAlarmsSelector);
        allAlarmsSelector.setChecked(false);
        allAlarmsSelector.setVisibility(isUnvisible);

        computeNearAlarm();

    }

    public void closeWorldTimeFooter() {
        isSelectionFooter = false;
        int isVisible = View.VISIBLE;
        MainActivity.gateway.tabs.setVisibility(isVisible);

        View worldTimeFooter = findViewById(R.id.worldTimeFooter);
        int isUnvisible = View.GONE;
        worldTimeFooter.setVisibility(isUnvisible);
        unselectCities();
        TextView worldTimeTitle = findViewById(R.id.worldTimeHeader);
        worldTimeTitle.setText("Все будильники\nотключены");

        int citiesCount = cities.getChildCount();
        for (int cityIndex = 0; cityIndex < citiesCount; cityIndex++) {
            LinearLayout city = ((LinearLayout)(cities.getChildAt(cityIndex)));
            city.removeViewAt(0);
        }

    }

    public void unselectCities() {
        cities = findViewById(R.id.cities);
    }

    public void computeNearAlarm() {
        TextView alarmsTitle = findViewById(R.id.alarmsTitle);
        ArrayList<HashMap<String, Object>> enabledAlarms = new ArrayList<HashMap<String, Object>>();
        int countAlarms = alarms.getChildCount();
        for (int alarmIndex = 0; alarmIndex < countAlarms; alarmIndex++) {
            LinearLayout alarm = ((LinearLayout)(alarms.getChildAt(alarmIndex)));
            Switch alarmSwitch = ((Switch)(alarm.getChildAt(1)));
            boolean isAlarmEnabled = alarmSwitch.isChecked();
            if (isAlarmEnabled) {
                HashMap<String, Object> rawAlarm = new HashMap<String, Object>();
                TextView alarmTimeLabel = ((TextView)(alarm.getChildAt(0)));
                CharSequence rawAlarmTime = alarmTimeLabel.getText();
                String alarmTime = rawAlarmTime.toString();
                rawAlarm.put("time", alarmTime);
                CharSequence rawAlarmDate = alarmSwitch.getText();
                String alarmDate = rawAlarmDate.toString();
                rawAlarm.put("date", alarmDate);
                enabledAlarms.add(rawAlarm);
            }
        }
        boolean isHaveEnabledAlarms = enabledAlarms.size() >= 1;
        if (isHaveEnabledAlarms) {
            String whenAlarmCalled = "1 ч. 1м.";
            String alarmTime = enabledAlarms.get(0).get("time").toString();
            String alarmDate = enabledAlarms.get(0).get("date").toString();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String alarmTimeContent = alarmTime;
            String alarmDateContent = alarmDate;
            try {
                Date d1 = sdf.parse(alarmTime);
                Calendar parsedAlarmDate = Calendar.getInstance();
                parsedAlarmDate.setTime(d1);

                Date d2 = new Date();
                d2.setTime(Calendar.getInstance().getTimeInMillis());
                long endDateMillis = d2.getTime();
                endDateMillis = System.currentTimeMillis();
                long difference_In_Time = endDateMillis - d1.getTime();
                long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;
                String rawMinutes = String.valueOf(difference_In_Minutes);
                long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;
                String rawHours = String.valueOf(difference_In_Hours);
                alarmTimeContent = rawHours + " ч., " + rawMinutes + " мин.";
                int dayOfWeekIndex = parsedAlarmDate.get(Calendar.DAY_OF_WEEK) - 1;
                int rawDayLabel = parsedAlarmDate.get(Calendar.DATE);
                String dayLabel = String.valueOf(rawDayLabel);
                int monthIndex = parsedAlarmDate.get(Calendar.MONTH);
                whenAlarmCalled = "\n" + alarmTimeContent + "\n" + alarmDateContent;
            } catch (ParseException e) {
                Log.d("debug", "ошибка парсинга даты будильника");
            }
            alarmsTitle.setText("Будильник через " + whenAlarmCalled);
        } else {
            alarmsTitle.setText("Все будильники\nотключены");
        }
    }

}