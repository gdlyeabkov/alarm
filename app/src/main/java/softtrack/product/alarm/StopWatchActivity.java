package softtrack.product.alarm;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Timer;
import java.util.TimerTask;

public class StopWatchActivity  extends Fragment {

    public int millisecondsInSecond = 1000;
    public String oneCharPrefix = "0";
    public String timePartsSeparator = ":";
    public int countSecondsInMinute = 60;
    public int countMinutesInHour = 60;
    public int initialSeconds = 0;
    public int initialMinutes = 0;
    public boolean isStart = false;
    public Timer stopWatch;
    public TextView title;
    public String initialStopWatchLabel;
    public int stopColorBtn;
    public Button startBtn;
    public String stopBtnLabel = "Стоп";
    public String startBtnLabel = "Начать";
    public String resumeBtnLabel = "Продолжить";
    public String resetBtnLabel = "Сбросить";
    public String intervalBtnLabel = "Интервал";
    public int startColorBtn;
    public Button intervalBtn;
    public LinearLayout intervals;
    public LinearLayout intervalsHeader;
    public int postIntervalsHeaderPosition = 1;
    public Timer circle;
    public int circleSeconds = 0;
    public int circleMinutes = 0;
    public int circleHours = 0;

    public StopWatchActivity() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_stopwatch, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeStopWatch();
    }

    public void initializeStopWatch() {
        startBtn = getActivity().findViewById(R.id.stopWatchStartBtn);
        title = getActivity().findViewById(R.id.stopWatchTitle);
        intervalBtn = getActivity().findViewById(R.id.intervalBtn);
        intervals = getActivity().findViewById(R.id.intervals);
        intervalsHeader = getActivity().findViewById(R.id.intervalsHeader);
        startColorBtn = R.color.purple_500;
        CharSequence rawInitialStopWatchLabel = title.getText();
        initialStopWatchLabel = rawInitialStopWatchLabel.toString();
        stopColorBtn = Color.rgb(255, 0, 0);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isNotStart = !isStart;
                if (isNotStart) {
                    activateIntervalBtn();
                    startBtn.setBackgroundColor(stopColorBtn);
                    startBtn.setText(stopBtnLabel);
                    stopWatch = new Timer();
                    stopWatch.scheduleAtFixedRate(new TimerTask(){
                        @Override
                        public void run() {
                            CharSequence rawSecondsText = title.getText();
                            String secondsText = rawSecondsText.toString();
                            String[] timeParts = secondsText.split(timePartsSeparator);
                            String rawHours = timeParts[0];
                            String rawMinutes = timeParts[1];
                            String rawSeconds = timeParts[2];
                            int hours = Integer.valueOf(rawHours);
                            int minutes = Integer.valueOf(rawMinutes);
                            int seconds = Integer.valueOf(rawSeconds);
                            seconds++;
                            boolean isToggleSecond = seconds == countSecondsInMinute;
                            if (isToggleSecond) {
                                seconds = initialSeconds;
                                minutes++;
                                boolean isToggleHour = minutes == countMinutesInHour;
                                if (isToggleHour) {
                                    minutes = initialMinutes;
                                    hours++;
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
                            title.setText(currentTime);
                        }
                    },0,millisecondsInSecond);
                } else {
                    stopTimer();
                }
                isStart = !isStart;
            }
        });

        intervalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isNotStart = !isStart;
                if (isNotStart) {
                    resetTimer();
                } else {
                    addInterval();
                }
            }
        });

    }

    public void stopTimer() {
        stopWatch.cancel();
        stopWatch.purge();
        intervalBtn.setText(resetBtnLabel);
        startBtn.setBackgroundColor(startColorBtn);
        startBtn.setText(resumeBtnLabel);
    }

    public void activateIntervalBtn() {
        boolean isIntervalButtonDisabled = !intervalBtn.isEnabled();
        if (isIntervalButtonDisabled) {
            intervalBtn.setEnabled(true);
        }
        intervalBtn.setText(intervalBtnLabel);
    }

    public void resetTimer() {
        stopWatch.cancel();
        stopWatch.purge();
        startBtn.setBackgroundColor(startColorBtn);
        startBtn.setText(startBtnLabel);
        title.setText(initialStopWatchLabel);
        intervalBtn.setEnabled(false);
        intervalBtn.setText(intervalBtnLabel);
        int countIntervals = intervals.getChildCount() - 1;
        intervals.removeViews(postIntervalsHeaderPosition, countIntervals);
        hideIntervalsHeader();
    }

    public void addInterval() {
        showIntervalsHeader();
        LinearLayout interval = new LinearLayout(getActivity());
        LinearLayout.LayoutParams intervalLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        intervalLayoutParams.setMargins(0,50, 0,50);
        interval.setLayoutParams(intervalLayoutParams);
        TextView circleLabel = new TextView(getActivity());
        int circlesCount = intervals.getChildCount();
        String parsedCirclesCount = String.valueOf(circlesCount);
        String circleLabelContent = parsedCirclesCount;
        boolean isCirclesTop9 = circlesCount <= 9;
        if (isCirclesTop9) {
            circleLabelContent = oneCharPrefix + circleLabelContent;
        }
        circleLabel.setText(circleLabelContent);
        circleLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        LinearLayout.LayoutParams circleLabelLayoutParams = new LinearLayout.LayoutParams(450, ViewGroup.LayoutParams.WRAP_CONTENT);
        circleLabel.setLayoutParams(circleLabelLayoutParams);
        TextView circleTimeLabel = new TextView(getActivity());
        String parsedCircleSeconds = String.valueOf(circleSeconds);
        int countSecondsChars = parsedCircleSeconds.length();
        boolean isAddSecondsPrefix = countSecondsChars == 1;
        if (isAddSecondsPrefix) {
            parsedCircleSeconds = oneCharPrefix + parsedCircleSeconds;
        }
        String parsedCircleMinutes = String.valueOf(circleMinutes);
        int countMinutesChars = parsedCircleMinutes.length();
        boolean isAddMinutesPrefix = countMinutesChars == 1;
        if (isAddMinutesPrefix) {
            parsedCircleMinutes = oneCharPrefix + parsedCircleMinutes;
        }
        String parsedCircleHours = String.valueOf(circleHours);
        int countHoursChars = parsedCircleHours.length();
        boolean isAddHoursPrefix = countHoursChars == 1;
        if (isAddHoursPrefix) {
            parsedCircleHours = oneCharPrefix + parsedCircleHours;
        }
        String circleTimeLabelContent = parsedCircleHours + timePartsSeparator + parsedCircleMinutes + timePartsSeparator + parsedCircleSeconds;
        circleTimeLabel.setText(circleTimeLabelContent);
        circleTimeLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        LinearLayout.LayoutParams circleTimeLabelLayoutParams = new LinearLayout.LayoutParams(450, ViewGroup.LayoutParams.WRAP_CONTENT);
        circleTimeLabel.setLayoutParams(circleTimeLabelLayoutParams);
        TextView totalTimeLabel = new TextView(getActivity());
        CharSequence rawTotalTimeLabelContent = title.getText();
        String totalTimeLabelContent = rawTotalTimeLabelContent.toString();
        totalTimeLabel.setText(totalTimeLabelContent);
        totalTimeLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        LinearLayout.LayoutParams totalTimeLabelLayoutParams = new LinearLayout.LayoutParams(450, ViewGroup.LayoutParams.WRAP_CONTENT);
        totalTimeLabel.setLayoutParams(totalTimeLabelLayoutParams);
        interval.addView(circleLabel);
        interval.addView(circleTimeLabel);
        interval.addView(totalTimeLabel);
        intervals.addView(interval, postIntervalsHeaderPosition);
        startNewCircle();
    }

    public void showIntervalsHeader() {
        int invisible = View.INVISIBLE;
        int visible = View.VISIBLE;
        int intervalsHeaderVisibility = intervalsHeader.getVisibility();
        boolean isIntervalsHeaderInvisible = intervalsHeaderVisibility == invisible;
        if (isIntervalsHeaderInvisible) {
            intervalsHeader.setVisibility(visible);
        }
    }

    public void hideIntervalsHeader() {
        int invisible = View.INVISIBLE;
        int visible = View.VISIBLE;
        int intervalsHeaderVisibility = intervalsHeader.getVisibility();
        boolean isIntervalsHeaderVisible = intervalsHeaderVisibility == visible;
        if (isIntervalsHeaderVisible) {
            intervalsHeader.setVisibility(invisible);
        }
    }

    public void startNewCircle() {
        circleSeconds = initialSeconds;
        circleMinutes = initialMinutes;
        boolean isNotFirstCircle = intervals.getChildCount() >= 3;
        if (isNotFirstCircle) {
            circle.purge();
            circle.cancel();
        }
        circle = new Timer();
        circle.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                circleSeconds++;
                boolean isToggleSecond = circleSeconds == countSecondsInMinute;
                if (isToggleSecond) {
                    circleSeconds = initialSeconds;
                    circleMinutes++;
                    boolean isToggleHour = circleMinutes == countMinutesInHour;
                    if (isToggleHour) {
                        circleMinutes = initialMinutes;
                        circleHours++;
                    }
                }
            }
        }, 0, millisecondsInSecond);
    }

}
