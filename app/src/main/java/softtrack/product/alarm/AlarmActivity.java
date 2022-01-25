package softtrack.product.alarm;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

public class AlarmActivity extends Fragment {

    public ArrayList<HashMap<String, Object>> havedAlarms;

    public AlarmActivity() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // redrawAlarms();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_alarm, container, false);
        // redrawAlarms();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        redrawAlarms();
    }

    public void redrawAlarms() {
        LinearLayout alarms = getActivity().findViewById(R.id.alarms);
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
            LinearLayout newAlarm = new LinearLayout(getActivity().getApplicationContext());
            newAlarm.setBackgroundColor(Color.rgb(255, 255, 255));
            LinearLayout.LayoutParams newAlarmLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250);
            newAlarmLayoutParams.setMargins(0, 50, 0, 50);
            newAlarm.setLayoutParams(newAlarmLayoutParams);
            newAlarm.setOrientation(LinearLayout.HORIZONTAL);
            TextView newAlarmTime = new TextView(getActivity().getApplicationContext());
            Object rawAlarmTime = alarm.get("time");
            String alarmTime = rawAlarmTime.toString();
            newAlarmTime.setText(alarmTime);
            LinearLayout.LayoutParams newAlarmTimeLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            newAlarmTimeLayoutParams.setMargins(50, 0, 0, 0);
            newAlarmTime.setLayoutParams(newAlarmTimeLayoutParams);
            newAlarm.addView(newAlarmTime);
            Switch newAlarmDateAndIsEnabled = new Switch(getActivity().getApplicationContext());
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
        Button addAlarmBtn = getActivity().findViewById(R.id.addAlarmBtn);
        addAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddAlarmActivity.class);
                intent.putExtra("filesType", "none");
                intent.putExtra("currentPath", getActivity().getApplicationContext().getCacheDir().getPath());
                getActivity().startActivity(intent);
            }
        });
        Button alarmsContextMenuBtn = getActivity().findViewById(R.id.alarmsContextMenuBtn);
        alarmsContextMenuBtn.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(Menu.NONE, 101, Menu.NONE, "Открыть");
                contextMenu.add(Menu.NONE, 102, Menu.NONE, "Открыть");
                contextMenu.add(Menu.NONE, 103, Menu.NONE, "Открыть");
                contextMenu.add(Menu.NONE, 104, Menu.NONE, "Открыть");
                contextMenu.add(Menu.NONE, 105, Menu.NONE, "Открыть");
            }
        });
    }

}
