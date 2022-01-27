package softtrack.product.alarm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

public class WorldTimeActivity  extends Fragment {

    public ArrayList<HashMap<String, Object>> havedCities;
    @SuppressLint("WrongConstant") public SQLiteDatabase db;
    public int newCityBackgroundColor;

    public WorldTimeActivity() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_worldtime, container, false);
        return view;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onStart() {
        super.onStart();

        db = getActivity().openOrCreateDatabase("alarms-database.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        newCityBackgroundColor = Color.rgb(255, 255, 255);

        Button addWorldTimeBtn = getActivity().findViewById(R.id.addWorldTimeBtn);
        addWorldTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddCityActivity.class);
                intent.putExtra("id", "0");
                getActivity().startActivity(intent);
            }
        });

        Button worldTimeContextMenuBtn = getActivity().findViewById(R.id.worldTimeContextMenuBtn);
        worldTimeContextMenuBtn.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(Menu.NONE, 201, Menu.NONE, "Изменить");
                contextMenu.add(Menu.NONE, 202, Menu.NONE, "Конвертер часовых поясов");
                contextMenu.add(Menu.NONE, 203, Menu.NONE, "Настройки");
                contextMenu.add(Menu.NONE, 204, Menu.NONE, "Свяжитесь с нами");
            }
        });

        showCitiesWorldTime();

    }

    public void showCitiesWorldTime() {
        LinearLayout cities = getActivity().findViewById(R.id.cities);
        havedCities = new ArrayList<HashMap<String, Object>>();
        Cursor citiesCursor = db.rawQuery("Select * from cities", null);
        citiesCursor.moveToFirst();
        long citiesCount = DatabaseUtils.queryNumEntries(db, "cities");
        boolean isHaveCities = citiesCount >= 1;
        if (isHaveCities) {
            for (int cityIndex = 0; cityIndex < citiesCount; cityIndex++) {
                int cityId = citiesCursor.getInt(0);
                String cityName = citiesCursor.getString(1);
                HashMap<String, Object> havedCity = new HashMap<String, Object>();
                havedCity.put("id", cityId);
                havedCity.put("name", cityName);
                havedCities.add(havedCity);
                citiesCursor.moveToNext();
            }
            for (HashMap<String, Object> city : havedCities) {
                LinearLayout newCity = new LinearLayout(getActivity());
                LinearLayout.LayoutParams newCityLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
                newCityLayoutParams.setMargins(0, 25, 0, 25);
                newCity.setLayoutParams(newCityLayoutParams);
                newCity.setOrientation(LinearLayout.HORIZONTAL);
                newCity.setBackgroundColor(newCityBackgroundColor);
                TextView cityName = new TextView(getActivity());
                Object rawCityNameContent = city.get("name");
                LinearLayout newCityAside = new LinearLayout(getActivity());
                LinearLayout.LayoutParams newCityAsideLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                newCityAsideLayoutParams.setMargins(20, 10, 0, 0);
                newCityAside.setLayoutParams(newCityAsideLayoutParams);
                newCityAside.setOrientation(LinearLayout.VERTICAL);
                newCity.addView(newCityAside);
                String cityNameContent = rawCityNameContent.toString();
                cityName.setText(cityNameContent);
                newCityAside.addView(cityName);
                TextView cityTimeDiff = new TextView(getActivity());
                cityTimeDiff.setText("На 2 часа раньше");
                newCityAside.addView(cityTimeDiff);
                TextView newCityTime = new TextView(getActivity());
                newCityTime.setText("16:00");
                newCityTime.setTextSize(24);
                LinearLayout.LayoutParams newCityTimeLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                newCityTimeLayoutParams.setMargins(500, 10, 0, 0);
                newCityTime.setLayoutParams(newCityTimeLayoutParams);
                newCity.addView(newCityTime);
                LinearLayout newCityWheather = new LinearLayout(getActivity());
                newCityWheather.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams newCityWheatherLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                newCityWheatherLayoutParams.setMargins(100, 10, 0, 0);
                newCityWheather.setLayoutParams(newCityWheatherLayoutParams);
                ImageView newCityWheatherIcon = new ImageView(getActivity());
                LinearLayout.LayoutParams newCityWheatherIconLayoutParams = new LinearLayout.LayoutParams(100, 100);
                newCityWheatherIcon.setLayoutParams(newCityWheatherIconLayoutParams);
                newCityWheatherIcon.setImageResource(R.drawable.weather);
                newCityWheather.addView(newCityWheatherIcon);
                TextView newCityWheatherLabel = new TextView(getActivity());
                newCityWheatherLabel.setText("2°");
                newCityWheather.addView(newCityWheatherLabel);
                newCity.addView(newCityWheather);
                cities.addView(newCity);
                Object rawCityId = city.get("id");
                String cityId = rawCityId.toString();
                newCity.setContentDescription(cityId);
                newCity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), AddCityActivity.class);
                        intent.putExtra("id", cityId);
                        getActivity().startActivity(intent);
                    }
                });
            }
        } else {
            TextView notFoundCitiesLabel = new TextView(getActivity());
            notFoundCitiesLabel.setText("Нет городов");
            notFoundCitiesLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            cities.addView(notFoundCitiesLabel);
        }
    }

}
