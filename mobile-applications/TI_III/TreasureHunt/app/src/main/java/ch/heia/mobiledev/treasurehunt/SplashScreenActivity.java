package ch.heia.mobiledev.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 *  TI - TreasureHunt
 *  Gremaud D., Mertenat S.
 *
 *  This activity displays a splash screen at startup
 *  http://www.coderefer.com/android-splash-screen-example-tutorial/
 */
public class SplashScreenActivity extends Activity {
    // Attribute for logging
    private static final String TAG = SplashScreenActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "SplashScreenActivity.onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // creation of a thread for displaying the splash screen
        Thread l_thread = new Thread() {
            public void run() {
                try {
                    // duration of 2 seconds
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // finally it starts the NavigationHomeActivity
                    Intent l_intent = new Intent(SplashScreenActivity.this, NavigationHomeActivity.class);
                    startActivity(l_intent);
                }
            }
        };
        l_thread.start(); // start the thread
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "SplashScreenActivity.onPause() called");
        super.onPause();
        // destroy the activity if the user press on the 'back' button (HomeNavigationActivity)
        // in this case, the splash screen isn't show again
        finish();

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "SplashScreenActivity.onStart() called");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "SplashScreenActivity.onRestart() called");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "SplashScreenActivity.onResume() called");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "SplashScreenActivity.onStop() called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "SplashScreenActivity.onDestroy() called");
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "SplashScreenActivity.onNewIntent() called");
        super.onNewIntent(intent);
    }
}