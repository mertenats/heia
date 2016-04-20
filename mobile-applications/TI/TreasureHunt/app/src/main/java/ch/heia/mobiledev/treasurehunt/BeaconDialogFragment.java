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
    private final String[] listItems = {"Beacon 1 : 356 steps",
                          "Beacon 2 : 256 steps",
                          "Beacon 3 : 450 steps"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_hint, null, false);
        listHints = (ListView) view.findViewById(R.id.hintListView);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        /*
            TODO : Retrieve data from beacon and display hints

            Note : following lines are for testing purpose
         */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_expandable_list_item_1, listItems);
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
