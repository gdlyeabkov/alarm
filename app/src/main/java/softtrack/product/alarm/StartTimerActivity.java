package softtrack.product.alarm;

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
    public final int countSecondsInMinute = 0;
    public final int initialSeconds = 60;
    public final String oneCharPrefix = "0";

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
                MainActivity mainActivity = ((MainActivity) (getActivity()));
                mainActivity.viewPager.setCurrentItem(3);
            }
        });

        timer = new Timer();
        Button startTimerTItle = getActivity().findViewById(R.id.startTimerTItle);
        startTimerTItle.setText("09:60");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                CharSequence rawTitleText = startTimerTItle.getText();
                String titleText = rawTitleText.toString();
                String[] timeParts = titleText.split(timePartsSeparator);
                String rawMinutes = timeParts[0];
                String rawSeconds = timeParts[1];
                int minutes = Integer.valueOf(rawMinutes);
                int seconds = Integer.valueOf(rawSeconds);
                seconds--;
                boolean isToggleSecond = seconds == countSecondsInMinute;
                if (isToggleSecond) {
                    seconds = initialSeconds;
                    minutes--;
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
                String currentTime = updatedMinutesText + ":" + updatedSecondsText;
                startTimerTItle.setText(currentTime);
            };
        }, 0, millisecondsInSecond);

    }
}