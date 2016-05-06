package ch.heia.mobiledev.treasurehunt;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * TI - TreasureHunt
 * Gremaud D., Mertenat S.
 * <p/>
 * This activity displays and manages the home view
 */
public class NavigationHomeActivity extends AppCompatActivity {
    // Attribute for logging purpose
    private static final String TAG = NavigationHomeActivity.class.getSimpleName();
    public static final String EXTRA_NEW_GAME = "new_game"; // true/false

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "NavigationHomeActivity.onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_home);
    }

    /**
     * This function is called when user press the new game button
     * and open the map game
     *
     * @param v : source view
     */
    public void onNewGame(View v) {
        Log.d(TAG, "NavigationHomeActivity.onNewGame() called");
        // start beaconsMap fragment activity
        Intent intent = new Intent(this, BeaconsMapActivity.class);
        // launch the new activity with a new game
        intent.putExtra(EXTRA_NEW_GAME, true);
        startActivity(intent);
    }

    /**
     * This function is called when user press the load game button,
     * open the map game and load hints
     *
     * @param v : source view
     */
    public void onLoadGame(View v) {
        Log.d(TAG, "NavigationHomeActivity.onLoadGame() called");
        Intent intent = new Intent(this, BeaconsMapActivity.class);
        // launch the new activity with a previous game
        intent.putExtra(EXTRA_NEW_GAME, false);
        startActivity(intent);
    }

    /**
     * This function is called when user press the about button
     * and open a dialog for display information
     *
     * @param v : source view
     */
    public void onReadAbout(View v) {
        Log.d(TAG, "NavigationHomeActivity.onReadAbout() called");
        // create fragment about
        FragmentManager fm = getFragmentManager();
        String legalText;
        String infoText;
        try {
            // get text to display
            legalText = readStream(getAssets().open("legal.txt"));
            infoText = readStream(getAssets().open("info.html"));
        } catch (IOException e) {
            Log.d(TAG, "NavigationHomeActivity.onReadAbout called : An error occurred while reading stream resources");
            legalText = "";
            infoText = "About";
        }

        AboutDialogFragment dialogFragment = AboutDialogFragment.newInstance(legalText, infoText);
        dialogFragment.show(fm, "Dialog Fragment");
    }

    /**
     * This function compute an inputStream into a string
     *
     * @param is : inputStream to compute
     * @return the inputStream in a string
     * @throws IOException if an error occurred while compute inputStream
     */
    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "NavigationHomeActivity.onRestart() called");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "NavigationHomeActivity.onStart() called");
        Button bt = (Button) findViewById(R.id.load_game_bt);

        File file = getBaseContext().getFileStreamPath(BeaconsMapActivity.FILENAME);
        if (!file.exists()) {
            // hide the load game button
            bt.setVisibility(View.GONE);
        } else {
            bt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "NavigationHomeActivity.onResume() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "NavigationHomeActivity.onStop() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "NavigationHomeActivity.onPause() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "NavigationHomeActivity.onDestroy() called");
    }
}