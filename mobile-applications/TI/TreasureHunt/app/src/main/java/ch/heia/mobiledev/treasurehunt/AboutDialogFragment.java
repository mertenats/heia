package ch.heia.mobiledev.treasurehunt;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 *  TI - TreasureHunt
 *  Gremaud D., Mertenat S.
 *
 *  This fragment display a popup screen which contains
 *  information about the application
 */
public class AboutDialogFragment extends DialogFragment {
    // used for logging
    private static final String TAG = AboutDialogFragment.class.getSimpleName();

    // Attributes contains text for About dialog
    private String legal;
    private String info;

    @Override
    public void onCreate(Bundle savedInstanceState){
        Log.d(TAG, "AboutDialogFragment.onCreate() called");

        super.onCreate(savedInstanceState);

        // retrieve arguments from instance creation
        legal = getArguments().getString("legal");
        info = getArguments().getString("info");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "AboutDialogFragment.onCreateView() called");
        // Prepare view
        View dialogView = inflater.inflate(R.layout.fragment_about, container, false);
        TextView tv = (TextView) dialogView.findViewById(R.id.legal_text);
        tv.setText(legal);
        tv = (TextView) dialogView.findViewById(R.id.info_text);
        tv.setText(Html.fromHtml(info));

        // Manage event on the dismiss button
        Button dismiss = (Button) dialogView.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return dialogView;
    }

    /**
     * This method prepare the instance of AboutDialogFragment by passing
     * two arguments for transmit data
     *
     * @param legal : contain corps text for AboutDialogFragment
     * @param info : contain header text for AboutDialogFragment
     * @return an instance of AboutDialogFragment
     */
    static AboutDialogFragment newInstance(String legal, String info){
        Log.d(TAG, "AboutDialogFragment.newInstance()");
        AboutDialogFragment f = new AboutDialogFragment();
        Bundle args = new Bundle();
        args.putString("legal", legal);
        args.putString("info", info);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "AboutDialogFragment.onStart() called");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "AboutDialogFragment.onResume() called");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "AboutDialogFragment.onPause() called");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "AboutDialogFragment.onStop() called");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "AboutDialogFragment.onDestroy() called");
        super.onDestroy();
    }
}
