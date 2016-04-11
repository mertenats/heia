package ch.heia.mobiledev.navigation;

/**
 * TP 2
 * Gremaud D. & Mertenat S.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// step 12
public class PeerActivity extends Activity {
    private static final String TAG = PeerActivity.class.getSimpleName();
    private static int m_peer_counter = 0; // peer counter

    private static final String EXTRA_PEER_COUNT = "extra_peer_count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peer);
        Log.d(TAG, "PeerActivity.onCreate() called");

        // retrieve the number of instances and update the view
        TextView l_tv = (TextView) findViewById(R.id.counter);

        // get the intent which has started the activity
        // http://stackoverflow.com/questions/4233873/how-do-i-get-extra-data-from-intent-on-android
        Intent l_intent = getIntent();
        int l_counter = l_intent.getIntExtra(EXTRA_PEER_COUNT, -1);
        if (l_counter == -1)
            l_counter = PeerActivity.m_peer_counter;
        l_tv.setText(String.valueOf(l_counter + 1));

        //l_tv.setText(String.valueOf(PeerActivity.m_peer_counter));

        initDisplayButton();
    }

    // Button initialization
    private void initDisplayButton() {
        // Get the reference of each element on the view
        Button btn_launch_peer_activity = (Button) findViewById(R.id.launch_peer_activity);

        // add a listener to the startActivity button
        btn_launch_peer_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchPeer();
            }
        });
    }


    // Override lifecycle callback methods (step 5)
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "PeerActivity.onStart() called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "PeerActivity.onRestart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "PeerActivity.onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "PeerActivity.onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "PeerActivity.onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "PeerActivity.onDestroy() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "PeerActivity.onSaveInstanceState() called");
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "PeerActivity.onRestoreInstanceState() called");
    }

    private void onLaunchPeer() {
        Log.d(TAG, "PeerActivity.onLaunchPeer() called");

        // create a new intent
        Intent l_intent = new Intent();
        l_intent.setClassName(PeerActivity.this, "ch.heia.mobiledev.navigation.PeerActivity");

        l_intent.setAction(Intent.ACTION_MAIN);
        l_intent.addCategory(Intent.CATEGORY_DEFAULT);
        PeerActivity.m_peer_counter++;

        // send extra data with an intent
        // http://stackoverflow.com/questions/4233873/how-do-i-get-extra-data-from-intent-on-android
        l_intent.putExtra(EXTRA_PEER_COUNT, PeerActivity.m_peer_counter);

        startActivity(l_intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "PeerActivity.onBackPressed() called");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        // http://stackoverflow.com/questions/31867292/how-to-detect-if-the-up-button-was-pressed
        Log.d(TAG, "PeerActivity.onOptionsItemSelected() called");

        switch (item.getItemId()) {
            case android.R.id.home:
                // clear the class counter
                PeerActivity.m_peer_counter = 0;

                // clear the back stack
                // http://stackoverflow.com/questions/5794506/android-clear-the-back-stack
                Intent l_intent = new Intent(this, NavigationHomeActivity.class);
                l_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(l_intent);

                finish(); // call this to finish the current activity

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}