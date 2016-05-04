package ch.heia.mobiledev.treasurehunt;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

/**
 *  TI - TreasureHunt
 *  Gremaud D., Mertenat S.
 *
 *  This fragment display a popup screen which contains
 *  hints about the position of the next beacon to find
 */
public class HintsDialogFragment extends DialogFragment {
    // used for logging
    private static final String TAG = HintsDialogFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_hint, null, false);
        ListView listHints = (ListView) view.findViewById(R.id.hintListView);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        String hints = getArguments().getString("hints");
        // get every hint for the current beacon (separate by a line)
        String[] hintsTable = hints != null ? hints.split(System.getProperty("line.separator")) : new String[0];
        //Log.d(TAG, "HintsDialogFragment.onCreate() called" + hints);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, hintsTable);
        listHints.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "HintsDialogFragment.onCreate() called");
        super.onCreate(savedInstanceState);
        setStyle(HintsDialogFragment.STYLE_NO_TITLE, R.style.DialogFragment);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "HintsDialogFragment.onStart() called");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "HintsDialogFragment.onResume() called");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "HintsDialogFragment.onPause() called");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "HintsDialogFragment.onStop() called");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "HintsDialogFragment.onDestroy() called");
        super.onDestroy();
    }
}
