package softtrack.product.alarm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TimerActivity  extends Fragment {
    public TimerActivity() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_timer, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Button timerAddBtn = getActivity().findViewById(R.id.timerAddBtn);
        timerAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Добавление готового таймера");
                builder.setView(R.layout.activity_add_timer);
                builder.setCancelable(false);
                builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO добавить таймер в БД
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
    }
}
