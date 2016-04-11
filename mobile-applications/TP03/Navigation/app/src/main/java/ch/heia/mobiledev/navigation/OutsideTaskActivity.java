package ch.heia.mobiledev.navigation;

/**
 * TP 2
 * Gremaud D. & Mertenat S.
 */

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

// step 14
public class OutsideTaskActivity extends Activity {
    private static final String TAG = OutsideTaskActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_up);
        Log.d(TAG, "SimpleUpActivity.onCreate() called");

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    // Override lifecycle callback methods
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "OutsideTaskActivity.onStart() called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "OutsideTaskActivity.onRestart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "OutsideTaskActivity.onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "OutsideTaskActivity.onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "OutsideTaskActivity.onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OutsideTaskActivity.onDestroy() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "OutsideTaskActivity.onSaveInstanceState() called");
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "OutsideTaskActivity.onRestoreInstanceState() called");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "OutsideTaskActivity.onBackPressed() called");
    }
}