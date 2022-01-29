package softtrack.product.alarm;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    @SuppressLint("WrongConstant") public SQLiteDatabase db;

    @SuppressLint("WrongConstant")
    @Override
    public void onReceive(Context context, Intent intent) {

        db = MainActivity.getContext().openOrCreateDatabase("alarms-database.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        Bundle extras = intent.getExtras();
        int alarmId = extras.getInt("id");
        Cursor alarmsCursor = db.rawQuery("Select * from alarms where _id=" + Integer.toString(alarmId), null);
        alarmsCursor.moveToFirst();
        String soundPath = alarmsCursor.getString(5);
        boolean isPlayNotification = soundPath.length() >= 1;
        if (isPlayNotification) {
            Uri parsedPath = Uri.parse(soundPath);
            MediaPlayer alarmSoundNotification = MediaPlayer.create(MainActivity.getContext(), parsedPath);
            alarmSoundNotification.start();
            Log.d("debug", "путь к звуку " + soundPath);
        }

//        MediaPlayer audio = MediaPlayer.create(MainActivity.getContext(), R.raw.wake_snd);
//        audio.start();

    }
}
