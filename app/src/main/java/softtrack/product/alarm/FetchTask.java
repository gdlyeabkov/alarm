package softtrack.product.alarm;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class FetchTask<ResponseType> extends AsyncTask<String, Integer, ResponseType> {

    @Override
    protected ResponseType doInBackground(String... urls) {
        String responseJson = "";
        ResponseType transferJson = null;
        boolean isGetWeatherRoute = urls[0].contains("api.openweathermap.org/data");
        if (isGetWeatherRoute) {
            try {
                responseJson = UrlFetcher.getText(urls[0]);
            } catch(Exception e) {

            }
            try {
                JSONObject obj = new JSONObject(responseJson);
                transferJson = ((ResponseType)(obj));
            } catch(JSONException e) {
                Log.d("debug", "ошибка парсинга json");
            }
        }
        return transferJson;
    }


}
