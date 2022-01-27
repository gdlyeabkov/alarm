package softtrack.product.alarm;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddCityActivity extends AppCompatActivity {

    @SuppressLint("WrongConstant") public SQLiteDatabase db;
    public int cityId;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        db = openOrCreateDatabase("alarms-database.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);

        Button addCityCancelBtn = findViewById(R.id.addCityCancelBtn);
        addCityCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddCityActivity.this, MainActivity.class);
                intent.putExtra("created", false);
                AddCityActivity.this.startActivity(intent);
            }
        });

        Button addCitySearchBtn = findViewById(R.id.addCitySearchBtn);
        addCitySearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Spinner addCitySelector = findViewById(R.id.addCitySelector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addCitySelector.setAdapter(adapter);

        Button addCityBtn = findViewById(R.id.addCityBtn);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String rawCityId = extras.getString("id");
            cityId = Integer.valueOf(rawCityId);
        }
        if (cityId >= 1) {
            addCityBtn.setText("Изменить");
            addCityBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Object selectedCity = addCitySelector.getSelectedItem();
                    String cityName = selectedCity.toString();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("name", cityName);
                    db.update("cities", contentValues, "_id = ? ", new String[] { Integer.toString(cityId) } );
                    Intent intent = new Intent(AddCityActivity.this, MainActivity.class);
                    intent.putExtra("created", false);
                    AddCityActivity.this.startActivity(intent);
                }
            });
        } else if (cityId == 0) {
            addCityBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Object selectedCity = addCitySelector.getSelectedItem();
                    String cityName = selectedCity.toString();
                    db.execSQL("INSERT INTO \"cities\"(name) VALUES (\"" + cityName + "\");");
                    Intent intent = new Intent(AddCityActivity.this, MainActivity.class);
                    intent.putExtra("created", false);
                    AddCityActivity.this.startActivity(intent);
                }
            });
        }

    }

}
