package ch.heia.mobiledev.launchactivity;

/**
 * TP 2
 * Gremaud D. & Mertenat S.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class LaunchActivity extends Activity {

    private static final String TAG = LaunchActivity.class.getSimpleName();
    //private Button btn_launch_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "LaunchActivity.onCreate called");
        initDisplayButton();
    }

    // Button Start activity initialization (step 4)
    private void initDisplayButton() {
        // Get the reference of each element on the view
        Button btn_launch_activity = (Button) findViewById(R.id.launch_button);

        // add a listener to the startActivity button
        btn_launch_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchActivity();
            }
        });
    }

    // method to handle the launch button's click (step 4)
    private void onLaunchActivity() {
        Log.d(TAG, "LaunchActivity.onLaunchActivity() called");
        Intent simpleUpActivityIntent = new Intent();
        // Explicit intent with args (step 6)
        // simpleUpActivityIntent = new Intent(LaunchActivity.this, SimpleUpActivity.class);

        // Explicit intent without args
        //Intent simpleUpActivityIntent2 = new Intent();
        simpleUpActivityIntent.setClassName(LaunchActivity.class.getPackage().getName(),
                SimpleUpActivity.class.getName());
        startActivity(simpleUpActivityIntent);

        // navigate without destroy activity (step 9)
        simpleUpActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // launch activity
        startActivity(simpleUpActivityIntent);
    }

    // Override all lifecycle callback methods (step 2)
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "LaunchActivity.onStart() called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "LaunchActivity.onRestart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "LaunchActivity.onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "LaunchActivity.onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "LaunchActivity.onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "LaunchActivity.onDestroy() called");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "LaunchActivity.onNewIntent() called");
    }

    // Recreating an activity (step 2)
    // http://developer.android.com/training/basics/activity-lifecycle/recreating.html
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "LaunchActivity.onSaveInstanceState() called");
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "LaunchActivity.onRestoreInstanceState() called");
    }

    // disable the menu (step 3)
    // http://developer.android.com/guide/topics/ui/menus.html
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.game_menu, menu);
        //return true;
        Log.d(TAG, "LaunchActivity.onCreateOptionsMenu() called");
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Log.d(TAG, "LaunchActivity.onOptionsItemSelected() called");
        return false;
    }
}
