package softtrack.product.alarm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

public class TimerActivity  extends Fragment {

    @SuppressLint("WrongConstant") public SQLiteDatabase db;
    public ArrayList<HashMap<String, Object>> havedTimers;

    public TimerActivity() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_timer, container, false);
        return view;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onStart() {
        super.onStart();

        db = getActivity().openOrCreateDatabase("alarms-database.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);

        Button timerAddBtn = getActivity().findViewById(R.id.timerAddBtn);
        timerAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Добавление готового таймера");
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.activity_add_timer, null);
                builder.setView(dialogView);
                builder.setCancelable(false);
                builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO добавить таймер в БД
                        EditText hoursLabel = (EditText) dialogView.findViewById(R.id.addTimerHours);
                        CharSequence rawHours = hoursLabel.getText();
                        String hours = rawHours.toString();
                        EditText minutesLabel = (EditText) dialogView.findViewById(R.id.addTimerMinutes);
                        CharSequence rawMinutes = minutesLabel.getText();
                        String minutes = rawMinutes.toString();
                        EditText secondsLabel = (EditText) dialogView.findViewById(R.id.addTimerSeconds);
                        CharSequence rawSeconds = secondsLabel.getText();
                        String seconds = rawSeconds.toString();
                        db.execSQL("INSERT INTO \"timers\"(name, hours, minutes, seconds) VALUES (\"" + "timer_name" + "\", \"" + hours + "\", \"" + minutes + "\", \"" + seconds + "\");");
                        createCustomTimer("timer_name", hours, minutes, seconds, true);
                    }
                });
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alert = builder.create();
                alert.setTitle("Добавление готового таймера");
                alert.show();

            }
        });
        Button timerStartBtn = getActivity().findViewById(R.id.timerStartBtn);
        timerStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = ((MainActivity)(getActivity()));
                mainActivity.viewPager.setCurrentItem(4);
            }
        });

        Button timerContextMenuBtn = getActivity().findViewById(R.id.timerContextMenuBtn);
        timerContextMenuBtn.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(Menu.NONE, 301, Menu.NONE, "Изменить установленные таймеры");
                contextMenu.add(Menu.NONE, 302, Menu.NONE, "Настройки");
                contextMenu.add(Menu.NONE, 303, Menu.NONE, "Свяжитесь с нами");
            }
        });

        havedTimers = new ArrayList<HashMap<String, Object>>();
        Cursor timersCursor = db.rawQuery("Select * from timers", null);
        timersCursor.moveToFirst();
        long timersCount = DatabaseUtils.queryNumEntries(db, "timers");
        boolean isHaveAlarms = timersCount >= 1;
        if (isHaveAlarms) {
            for (int timerIndex = 0; timerIndex < timersCount; timerIndex++) {
                HashMap<String, Object> newTimer = new HashMap<String, Object>();
                String timerName = timersCursor.getString(1);
                newTimer.put("name", timerName);
                String timerHours = timersCursor.getString(2);
                newTimer.put("hours", timerHours);
                String timerMinutes = timersCursor.getString(3);
                newTimer.put("minutes", timerMinutes);
                String timerSeconds = timersCursor.getString(4);
                newTimer.put("seconds", timerSeconds);
                havedTimers.add(newTimer);
                timersCursor.moveToNext();
            }
            for (HashMap<String, Object> timer : havedTimers) {
                Object rawTimerName = timer.get("name");
                String timerName = rawTimerName.toString();
                Object rawTimerHours = timer.get("hours");
                String timerHours = rawTimerHours.toString();
                Object rawTimerMinutes = timer.get("minutes");
                String timerMinutes = rawTimerMinutes.toString();
                Object rawTimerSeconds = timer.get("seconds");
                String timerSeconds = rawTimerSeconds.toString();
                createCustomTimer(timerName, timerHours, timerMinutes, timerSeconds, false);
            }
        }

    }

    public void createCustomTimer (String timerName, String timerHours, String timerMinutes, String timerSeconds, boolean isActive) {
        LinearLayout timers = getActivity().findViewById(R.id.timers);
        Button newTimer = new Button(getActivity());
        String timerTime = timerHours + ":" + timerMinutes + ":" + timerSeconds;
        String timerInfo = timerTime;
        boolean isSetTimerName = timerName.length() >= 1;
        if (isSetTimerName) {
            timerInfo = timerName + "\n" + timerTime;
        }
        newTimer.setText(timerInfo);
        newTimer.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        newTimer.setBackgroundColor(Color.parseColor("#C8C8C8"));
        LinearLayout.LayoutParams newTimerLayoutParams = new LinearLayout.LayoutParams(500, 500);
        newTimerLayoutParams.setMargins(25, 0, 25, 0);
        newTimer.setLayoutParams(newTimerLayoutParams);
        GradientDrawable shape =  new GradientDrawable();
        if (isActive) {
            activateTimer(newTimer);
            shape.setStroke(5, Color.rgb(0, 0, 0));
            shape.setColor(Color.rgb(255, 255, 255));
        } else {
            shape.setColor(Color.parseColor("#C8C8C8"));
        }
        shape.setCornerRadius(500);
        newTimer.setBackground(shape);
        newTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button currentTimer = ((Button)(view));
                activateTimer(currentTimer);
            }
        });
        timers.addView(newTimer);
    }

    public void deactivateTimers() {
        LinearLayout timers = getActivity().findViewById(R.id.timers);
        int timersCount = timers.getChildCount();
        for(int timerIndex = 0; timerIndex < timersCount; timerIndex++) {
            GradientDrawable shape =  new GradientDrawable();
            shape.setStroke(0, Color.argb(0, 0, 0, 0));
            shape.setColor(Color.parseColor("#C8C8C8"));
            shape.setCornerRadius(500);
            timers.getChildAt(timerIndex).setBackground(shape);
        }
    }

    public void activateTimer(Button timer) {
        deactivateTimers();
        GradientDrawable shape =  new GradientDrawable();
        shape.setStroke(5, Color.rgb(0, 0, 0));
        shape.setColor(Color.rgb(255, 255, 255));
        shape.setCornerRadius(500);
        timer.setBackground(shape);
    }

}
