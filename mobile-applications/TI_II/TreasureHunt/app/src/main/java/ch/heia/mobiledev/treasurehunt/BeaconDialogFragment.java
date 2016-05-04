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
 *  hints about the position of the next beacon to find
 */
public class BeaconDialogFragment extends DialogFragment {
    // used for logging
    private static final String TAG = BeaconDialogFragment.class.getSimpleName();

    private ListView listHints;
    private String beacons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_hint, null, false);
        listHints = (ListView) view.findViewById(R.id.hintListView);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        beacons = getArguments().getString("beacons");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        // get every hint for the current beacon (separate by a line)
        String[] beaconsTable = beacons.split(System.getProperty("line.separator"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, beaconsTable);
        listHints.setAdapter(adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "HintsDialogFragment.onCreate() called");
        super.onCreate(savedInstanceState);
        setStyle(HintsDialogFragment.STYLE_NO_TITLE, R.style.DialogFragment);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "BeaconDialogFragment.onStart() called");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "BeaconDialogFragment.onResume() called");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "BeaconDialogFragment.onPause() called");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "BeaconDialogFragment.onStop() called");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "BeaconDialogFragment.onDestroy() called");
        super.onDestroy();
    }
}
