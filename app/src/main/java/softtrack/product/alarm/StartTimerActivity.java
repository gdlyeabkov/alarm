package softtrack.product.alarm;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Timer;
import java.util.TimerTask;

public class StartTimerActivity extends Fragment {

    public Timer timer;
    public String timePartsSeparator = ":";
    public final int millisecondsInSecond = 1000;
    public final int countSecondsInMinute = -1;
    public final int initialSeconds = 59;
    public final String oneCharPrefix = "0";
    public boolean isStart = true;
    public Button startTimerTitle;
    public Button startTimerPauseBtn;
    public final String resumeLabel = "Продолжить";
    public int resumeColor;
    public int pauseColor;
    public String pauseLabel;
    public static StartTimerActivity activityContext;
    public int initialMinutes = 59;
    public int countMinutesInHour = -1;
    public int delayForDisplayInitialSeconds = 1000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_start_timer, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Button startTimerCancelBtn = getActivity().findViewById(R.id.startTimerCancelBtn);
        startTimerCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer();
                MainActivity mainActivity = ((MainActivity) (getActivity()));
                mainActivity.viewPager.setCurrentItem(3);
            }
        });

        startTimerTitle = getActivity().findViewById(R.id.startTimerTItle);
        startTimerTitle.setText("09:60");

        startTimerPauseBtn = getActivity().findViewById(R.id.startTimerPauseBtn);
        CharSequence rawPauseLabel = startTimerPauseBtn.getText();
        pauseLabel = rawPauseLabel.toString();
        startTimerPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isNotStart = !isStart;
                if (isNotStart) {
                    startTimer();
                } else {
                    stopTimer();
                }
                isStart = !isStart;
            }
        });

        pauseColor = Color.rgb(255, 0, 0);
        resumeColor = Color.rgb(0, 0, 255);

        activityContext = StartTimerActivity.this;

    }

    public void startTimer() {
        startTimerPauseBtn.setText(pauseLabel);
        startTimerPauseBtn.setBackgroundColor(pauseColor);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
            CharSequence rawTitleText = startTimerTitle.getText();
            String titleText = rawTitleText.toString();
            String[] timeParts = titleText.split(timePartsSeparator);
            String rawHours = timeParts[0];
            String rawMinutes = timeParts[1];
            String rawSeconds = timeParts[2];
            int hours = Integer.valueOf(rawHours);
            int minutes = Integer.valueOf(rawMinutes);
            int seconds = Integer.valueOf(rawSeconds);
            seconds--;
            boolean isToggleSecond = seconds == countSecondsInMinute;
            if (isToggleSecond) {
                seconds = initialSeconds;
                minutes--;
                boolean isToggleMinute = minutes == countMinutesInHour;
                if (isToggleMinute) {
                    minutes = initialMinutes;
                    hours--;
                }
            }
            String updatedHoursText = String.valueOf(hours);
            int countHoursChars = updatedHoursText.length();
            boolean isAddHoursPrefix = countHoursChars == 1;
            if (isAddHoursPrefix) {
                updatedHoursText = oneCharPrefix + updatedHoursText;
            }
            String updatedMinutesText = String.valueOf(minutes);
            int countMinutesChars = updatedMinutesText.length();
            boolean isAddMinutesPrefix = countMinutesChars == 1;
            if (isAddMinutesPrefix) {
                updatedMinutesText = oneCharPrefix + updatedMinutesText;
            }
            String updatedSecondsText = String.valueOf(seconds);
            int countSecondsChars = updatedSecondsText.length();
            boolean isAddSecondsPrefix = countSecondsChars == 1;
            if (isAddSecondsPrefix) {
                updatedSecondsText = oneCharPrefix + updatedSecondsText;
            }
            String currentTime = updatedHoursText + ":" + updatedMinutesText + ":" + updatedSecondsText;
            startTimerTitle.setText(currentTime);
            boolean isTimerEnd = hours < 0 || minutes < 0 || seconds < 0;
            if (isTimerEnd) {
                MainActivity.viewPager.setCurrentItem(3);
            }
        };
    }, delayForDisplayInitialSeconds, millisecondsInSecond);
    }

    public void stopTimer() {
        boolean isTimerExists = timer != null;
        if (isTimerExists) {
            startTimerPauseBtn.setText(resumeLabel);
            startTimerPauseBtn.setBackgroundColor(resumeColor);
            timer.cancel();
            timer.purge();
        }
    }


}