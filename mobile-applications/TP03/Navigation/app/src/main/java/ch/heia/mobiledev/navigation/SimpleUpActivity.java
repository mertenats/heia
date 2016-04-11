package ch.heia.mobiledev.navigation;

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

public class SimpleUpActivity extends Activity {
    private static final String TAG = SimpleUpActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_up);
        Log.d(TAG, "SimpleUpActivity.onCreate() called");

        // manifest : http://developer.android.com/training/implementing-navigation/ancestral.html
        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    // Override lifecycle callback methods (step 5)
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "SimpleUpActivity.onStart() called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "SimpleUpActivity.onRestart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "SimpleUpActivity.onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "SimpleUpActivity.onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "SimpleUpActivity.onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "SimpleUpActivity.onDestroy() called");
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        Log.d(TAG, "SimpleUpActivity.onNewIntent() called");
    }

    // Recreating an activity (step 5)
    // http://developer.android.com/training/basics/activity-lifecycle/recreating.html
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "SimpleUpActivity.onSaveInstanceState() called");
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "SimpleUpActivity.onRestoreInstanceState() called");
    }

    // disable the menu (step 5)
    // http://developer.android.com/guide/topics/ui/menus.html
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.game_menu, menu);
        //return true;
        Log.d(TAG, "SimpleUpActivity.onCreateOptionsMenu() called");
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Log.d(TAG, "SimpleUpActivity.onOptionsItemSelected() called");
        return false;
    }
}
