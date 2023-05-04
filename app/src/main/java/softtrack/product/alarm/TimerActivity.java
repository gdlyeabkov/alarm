package softtrack.product.alarm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

public class TimerActivity  extends Fragment {

    @SuppressLint("WrongConstant") public SQLiteDatabase db;
    public ArrayList<HashMap<String, Object>> havedTimers;
    public ScrollView hoursColumnScroll;
    public ScrollView minutesColumnScroll;
    public ScrollView secondsColumnScroll;

    public TimerActivity() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_timer, container, false);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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
                EditText hoursLabel = (EditText) dialogView.findViewById(R.id.addTimerHours);
                String initialAddTimerHours = MainActivity.timerHours;
                hoursLabel.setText(initialAddTimerHours);
                EditText minutesLabel = (EditText) dialogView.findViewById(R.id.addTimerMinutes);
                String initialAddTimerMinutes = MainActivity.timerMinutes;
                minutesLabel.setText(initialAddTimerMinutes);
                EditText secondsLabel = (EditText) dialogView.findViewById(R.id.addTimerSeconds);
                String initialAddTimerSeconds = MainActivity.timerSeconds;
                secondsLabel.setText(initialAddTimerSeconds);
                builder.setView(dialogView);
                builder.setCancelable(false);
                builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CharSequence rawHours = hoursLabel.getText();
                        String hours = rawHours.toString();
                        CharSequence rawMinutes = minutesLabel.getText();
                        String minutes = rawMinutes.toString();
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

        hoursColumnScroll = getActivity().findViewById(R.id.hoursColumnScroll);
        minutesColumnScroll = getActivity().findViewById(R.id.minutesColumnScroll);
        secondsColumnScroll = getActivity().findViewById(R.id.secondsColumnScroll);

        hoursColumnScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                deactivateTimers();
                LinearLayout hoursColumn = ((LinearLayout)(hoursColumnScroll.getChildAt(0)));
                int destinationBetweenScrollItems = 95;
                int selectedLabelIndex = ((int)(Math.floor(i1 / destinationBetweenScrollItems)));
                int hoursColumnLabelsCount = hoursColumn.getChildCount();
                boolean isChildrenAccess = selectedLabelIndex < hoursColumnLabelsCount && selectedLabelIndex > 0;
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
                    MainActivity.timerHours = selectLabelContent;
                }
            }
        });

        minutesColumnScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                deactivateTimers();
                LinearLayout minutesColumn = ((LinearLayout)(minutesColumnScroll.getChildAt(0)));
                int destinationBetweenScrollItems = 105;
                int selectedLabelIndex = ((int)(Math.floor(i1 / destinationBetweenScrollItems)));
                int minutesColumnLabelsCount = minutesColumn.getChildCount();
                boolean isChildrenAccess = selectedLabelIndex < minutesColumnLabelsCount && selectedLabelIndex > 0;
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
                    MainActivity.timerMinutes = selectLabelContent;
                }
            }
        });

        secondsColumnScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                deactivateTimers();
                LinearLayout secondsColumn = ((LinearLayout)(secondsColumnScroll.getChildAt(0)));
                int destinationBetweenScrollItems = 105;
                int selectedLabelIndex = ((int)(Math.floor(i1 / destinationBetweenScrollItems)));
                int secondsColumnLabelsCount = secondsColumn.getChildCount();
                boolean isChildrenAccess = selectedLabelIndex < secondsColumnLabelsCount && selectedLabelIndex > 0;
                if (isChildrenAccess) {
                    for (int secondsLabelIndex = 1; secondsLabelIndex < secondsColumnLabelsCount; secondsLabelIndex++) {
                        TextView currentLabel = ((TextView)(secondsColumn.getChildAt(secondsLabelIndex)));
                        Typeface labelTypeface = Typeface.DEFAULT;
                        currentLabel.setTypeface(labelTypeface, Typeface.NORMAL);
                    }
                    TextView selectedLabel = ((TextView)(secondsColumn.getChildAt(selectedLabelIndex)));
                    Typeface labelTypeface = selectedLabel.getTypeface();
                    selectedLabel.setTypeface(labelTypeface, Typeface.BOLD);
                    CharSequence rawSelectLabelContent = selectedLabel.getText();
                    String selectLabelContent = rawSelectLabelContent.toString();
                    MainActivity.timerSeconds = selectLabelContent;
                }
            }
        });

    }

    public void createCustomTimer (String timerName, String timerHours, String timerMinutes, String timerSeconds, boolean isActive) {
        LinearLayout timers = getActivity().findViewById(R.id.timers);
        Button newTimer = new Button(getActivity());
        String timerTime = timerHours + ":" + timerMinutes + ":" + timerSeconds;
        newTimer.setContentDescription(timerTime);
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
        CharSequence rawTimerTime = timer.getContentDescription();
        String timerTime = rawTimerTime.toString();

        LinearLayout hoursColumn = ((LinearLayout)(getActivity().findViewById(R.id.hoursColumn)));
        int hoursColumnLabelsCount = hoursColumn.getChildCount();
        for (int hoursLabelIndex = 1; hoursLabelIndex < hoursColumnLabelsCount; hoursLabelIndex++) {
            TextView currentLabel = ((TextView)(hoursColumn.getChildAt(hoursLabelIndex)));
            Typeface labelTypeface = Typeface.DEFAULT;
            currentLabel.setTypeface(labelTypeface, Typeface.NORMAL);
        }
        String[] timerParts = timerTime.split(":");
        String rawHours = timerParts[0];
        Log.d("debug", "time: " + rawHours);
        if (rawHours == "00") {
            rawHours = "0";
        }
        int hours = Integer.valueOf(rawHours);
        int selectedLabelIndex = hours + 1;
        TextView selectedLabel = ((TextView)(hoursColumn.getChildAt(selectedLabelIndex)));
        Typeface labelTypeface = selectedLabel.getTypeface();
        selectedLabel.setTypeface(labelTypeface, Typeface.BOLD);
        CharSequence rawSelectLabelContent = selectedLabel.getText();
        String selectLabelContent = rawSelectLabelContent.toString();
        MainActivity.timerHours = selectLabelContent;
        int destinationBetweenScrollItems = 95;
        int scrollY = selectedLabelIndex * destinationBetweenScrollItems;
        hoursColumnScroll.scrollTo(0, scrollY);

        LinearLayout minutesColumn = ((LinearLayout)(getActivity().findViewById(R.id.minutesColumn)));
        int minutesColumnLabelsCount = minutesColumn.getChildCount();
        for (int minutesLabelIndex = 1; minutesLabelIndex < minutesColumnLabelsCount; minutesLabelIndex++) {
            TextView currentLabel = ((TextView)(minutesColumn.getChildAt(minutesLabelIndex)));
            labelTypeface = Typeface.DEFAULT;
            currentLabel.setTypeface(labelTypeface, Typeface.NORMAL);
        }
        String rawMinutes = timerParts[1];
        int minutes = Integer.valueOf(rawMinutes);
        selectedLabelIndex = minutes + 1;
        selectedLabel = ((TextView)(minutesColumn.getChildAt(selectedLabelIndex)));
        labelTypeface = selectedLabel.getTypeface();
        selectedLabel.setTypeface(labelTypeface, Typeface.BOLD);
        rawSelectLabelContent = selectedLabel.getText();
        selectLabelContent = rawSelectLabelContent.toString();
        MainActivity.timerMinutes = selectLabelContent;
        destinationBetweenScrollItems = 105;
        scrollY = selectedLabelIndex * destinationBetweenScrollItems;
        minutesColumnScroll.scrollTo(0, scrollY);

        LinearLayout secondsColumn = ((LinearLayout)(getActivity().findViewById(R.id.secondsColumn)));
        int secondsColumnLabelsCount = secondsColumn.getChildCount();
        for (int secondsLabelIndex = 1; secondsLabelIndex < secondsColumnLabelsCount; secondsLabelIndex++) {
            TextView currentLabel = ((TextView)(secondsColumn.getChildAt(secondsLabelIndex)));
            labelTypeface = Typeface.DEFAULT;
            currentLabel.setTypeface(labelTypeface, Typeface.NORMAL);
        }
        String rawSeconds = timerParts[2];
        int seconds = Integer.valueOf(rawSeconds);
        selectedLabelIndex = seconds + 1;
        selectedLabel = ((TextView)(secondsColumn.getChildAt(selectedLabelIndex)));
        labelTypeface = selectedLabel.getTypeface();
        selectedLabel.setTypeface(labelTypeface, Typeface.BOLD);
        rawSelectLabelContent = selectedLabel.getText();
        selectLabelContent = rawSelectLabelContent.toString();
        MainActivity.timerMinutes = selectLabelContent;
        destinationBetweenScrollItems = 105;
        scrollY = selectedLabelIndex * destinationBetweenScrollItems;
        secondsColumnScroll.scrollTo(0, scrollY);

        deactivateTimers();
        GradientDrawable shape =  new GradientDrawable();
        shape.setStroke(5, Color.rgb(0, 0, 0));
        shape.setColor(Color.rgb(255, 255, 255));
        shape.setCornerRadius(500);
        timer.setBackground(shape);

    }

}
