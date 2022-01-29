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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabs = findViewById(R.id.mainTabs);
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

}