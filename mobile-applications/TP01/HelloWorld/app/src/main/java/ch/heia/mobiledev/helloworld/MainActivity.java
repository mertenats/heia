    package ch.heia.mobiledev.helloworld;

    import android.os.Bundle;
    import android.support.v7.app.AppCompatActivity;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.util.DisplayMetrics;

    public class MainActivity extends AppCompatActivity {

        // Declaration of a tag for debugging purposes
        private static final String TAG = MainActivity.class.getSimpleName();

        // References to the UI elements
        private EditText edit_name;
        private Button btn_display;
        private Button btn_clear;
        private TextView txt_result;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            initDisplayButton();

            Log.d(TAG, "MyActivity.onCreate function called");

            // Get screen device informations
            // http://developer.android.com/reference/android/util/DisplayMetrics.html
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            float density_px_per_dp = metrics.density;
            float density_bucket_dpi = metrics.densityDpi;
            float xdpi = metrics.xdpi;
            float ydpi = metrics.ydpi;
            float width_px = metrics.widthPixels;
            float height_px = metrics.heightPixels;

            Log.d(TAG,  "Density bucket [dpi] : " + density_bucket_dpi);
            Log.d(TAG,  "Density scaler [px/dp] : " + density_px_per_dp);
            Log.d(TAG,  "Xdpi / Ydpi : " + xdpi + " / " + ydpi);
            Log.d(TAG,  "Screen width / height [px] : " + width_px + " / " + height_px);
            Log.d(TAG,  "Screen width / height [dp] : " + width_px * 160 / xdpi + " / " + height_px * 160 / ydpi);
        }

        public void initDisplayButton() {
            // Get the reference of each element from the view
            edit_name = (EditText) findViewById(R.id.edit_name);
            btn_display = (Button) findViewById(R.id.button_display);
            btn_clear = (Button) findViewById(R.id.button_clear);
            txt_result = (TextView) findViewById(R.id.text_hello);

            // Add a listener to the Display button
            btn_display.setOnClickListener(new View.OnClickListener() {
                @Override
                // Called when the user clicks the Display button
                public void onClick(View v) {
                    String name = edit_name.getText().toString();
                    if (!name.equals("")) {
                        txt_result.setText("Hello " + name + "!");
                    } else {
                        txt_result.setText("Hello stranger!");
                    }
                    Log.d(TAG, "Display button pressed");
                }
            });

            // Add a listener to the Clear button
            btn_clear.setOnClickListener(new View.OnClickListener() {
                @Override
                // Called when the user clicks the Clear button
                public void onClick(View v) {
                    txt_result.setText("Hello World");
                    edit_name.setText("");
                    Log.d(TAG, "Clear button pressed");
                }
            });
        }
    }
