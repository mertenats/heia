package ch.heia.mobiledev.treasurehunt;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;


/**
 *  TI - TreasureHunt
 *  Gremaud D., Mertenat S.
 *
 *  This class implements the service of step counter
 *
 */
public class StepCounterService extends Service implements SensorEventListener {

    private static final String TAG = StepCounterService.class.getSimpleName();

    public static final String ACTION_INCREMENT =
            "ch.heia.mobiledev.stepCounter.action.increment";
    public static final String EXTRA_PARAM_STEP =
            "ch.heia.mobiledev.stepCounter.extra.step";

    private SensorManager sensorManager;

    @Override
    public void onCreate() {
        Log.d(TAG, "StepCounterService.onCreate() called");
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        }
        else {
            Toast.makeText(this, "StepCounterService.onStartCommand() called : " +
                    "Count sensor not available!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "StepCounterService.onStartCommand() called");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "StepCounterService.onDestroy() called");
        sensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "StepCounterService.onSensorChanged() called");
        Intent localIntent = new Intent();
        localIntent.setAction(ACTION_INCREMENT);
        localIntent.putExtra(EXTRA_PARAM_STEP, event.values[0]);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}