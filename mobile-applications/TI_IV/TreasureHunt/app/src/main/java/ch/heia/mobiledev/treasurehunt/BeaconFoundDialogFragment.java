package ch.heia.mobiledev.treasurehunt;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 *  TI - TreasureHunt
 *  Gremaud D., Mertenat S.
 *
 *  This fragment display a popup screen which contains
 *  a message displayed after a beacon is found
 */
public class BeaconFoundDialogFragment extends DialogFragment {
    // used for logging
    private static final String TAG = BeaconFoundDialogFragment.class.getSimpleName();

    private ListView listMessage;
    private String message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_hint, null, false);
        listMessage = (ListView) view.findViewById(R.id.hintListView);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        message = getArguments().getString("message");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        // get every hint for the current beacon (separate by a line)
        String[] beaconsTable = message.split(System.getProperty("line.separator"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, beaconsTable);
        listMessage.setAdapter(adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "BeaconFoundDialogFragment.onCreate() called");
        super.onCreate(savedInstanceState);
        setStyle(HintsDialogFragment.STYLE_NO_TITLE, R.style.DialogFragment);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "BeaconFoundDialogFragment.onStart() called");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "BeaconFoundDialogFragment.onResume() called");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "BeaconFoundDialogFragment.onPause() called");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "BeaconFoundDialogFragment.onStop() called");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "BeaconFoundDialogFragment.onDestroy() called");
        super.onDestroy();
    }
}
