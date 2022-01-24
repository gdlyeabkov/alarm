package softtrack.product.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public LinearLayout tabsLabels;
    public ArrayList<HashMap<String, Object>> havedAlarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabsLabels = findViewById(R.id.tabsLabels);
        TextView alarmTabLabel = findViewById(R.id.alarmTabLabel);
        TextView worldTabLabel = findViewById(R.id.worldTabLabel);
        TextView stopwatchTabLabel = findViewById(R.id.stopwatchTabLabel);
        TextView timerTabLabel = findViewById(R.id.timerTabLabel);
        LinearLayout alarms = findViewById(R.id.alarms);
        alarmTabLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTabHandler(view);
            }
        });
        worldTabLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTabHandler(view);
            }
        });
        stopwatchTabLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTabHandler(view);
            }
        });
        timerTabLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTabHandler(view);
            }
        });

        havedAlarms = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> firstAlarm = new HashMap<String, Object>();
        firstAlarm.put("time", "17:30");
        firstAlarm.put("date", "2022-01-01");
        firstAlarm.put("isEnabled", true);
        havedAlarms.add(firstAlarm);
        HashMap<String, Object> secondAlarm = new HashMap<String, Object>();
        secondAlarm.put("time", "17:30");
        secondAlarm.put("date", "2022-01-01");
        secondAlarm.put("isEnabled", true);
        havedAlarms.add(secondAlarm);
        HashMap<String, Object> thirdAlarm = new HashMap<String, Object>();
        thirdAlarm.put("time", "17:30");
        thirdAlarm.put("date", "2022-01-01");
        thirdAlarm.put("isEnabled", true);
        havedAlarms.add(thirdAlarm);
        HashMap<String, Object> fourthAlarm = new HashMap<String, Object>();
        fourthAlarm.put("time", "17:30");
        fourthAlarm.put("date", "2022-01-01");
        fourthAlarm.put("isEnabled", true);
        havedAlarms.add(fourthAlarm);
        for (HashMap<String, Object> alarm : havedAlarms) {
            LinearLayout newAlarm = new LinearLayout(MainActivity.this);
            newAlarm.setBackgroundColor(Color.rgb(255, 255, 255));
            LinearLayout.LayoutParams newAlarmLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250);
            newAlarmLayoutParams.setMargins(0, 5, 0,5);
            newAlarm.setLayoutParams(newAlarmLayoutParams);
            newAlarm.setOrientation(LinearLayout.HORIZONTAL);
            TextView newAlarmTime = new TextView(MainActivity.this);
            Object rawAlarmTime = alarm.get("time");
            String alarmTime = rawAlarmTime.toString();
            newAlarmTime.setText(alarmTime);
            LinearLayout.LayoutParams newAlarmTimeLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            newAlarmTimeLayoutParams.setMargins(50, 0, 0, 0);
            newAlarmTime.setLayoutParams(newAlarmTimeLayoutParams);
            newAlarm.addView(newAlarmTime);
            Switch newAlarmDateAndIsEnabled = new Switch(MainActivity.this);
            Object rawAlarmDate = alarm.get("date");
            String alarmDate = rawAlarmDate.toString();
            newAlarmDateAndIsEnabled.setText(alarmDate);
            newAlarmDateAndIsEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    // переключение будильника
                }
            });
            LinearLayout.LayoutParams newAlarmDateAndIsEnabledLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            newAlarmDateAndIsEnabledLayoutParams.setMargins(800, 0, 0, 0);
            newAlarmDateAndIsEnabled.setLayoutParams(newAlarmDateAndIsEnabledLayoutParams);
            newAlarm.addView(newAlarmDateAndIsEnabled);
            alarms.addView(newAlarm);
        }

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

}