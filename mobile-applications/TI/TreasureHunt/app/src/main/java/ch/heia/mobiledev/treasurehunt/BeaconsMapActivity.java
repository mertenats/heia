package ch.heia.mobiledev.treasurehunt;

import android.Manifest;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 *  TI - TreasureHunt
 *  Gremaud D., Mertenat S.
 *
 *  This activity displays a map and manage the user location and
 *  beacons positions
 */
public class BeaconsMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, LocationListener {
    // Attribute for logging
    private static final String TAG = BeaconsMapActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;

    // Attributes for step counter
    private IntentFilter intentFilter;
    private int steps = 0;
    private Intent serviceIntent;

    private GoogleMap mMap;  // map save

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "BeaconsMapActivity.onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons_map);
        // start service
        serviceIntent = new Intent(this, StepCounterService.class);
        startService(serviceIntent);

        // Prepare broadcast receiver
        intentFilter = new IntentFilter(StepCounterService.ACTION_INCREMENT);
        StepCounterReceiver stepCounterReceiver = new StepCounterReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(stepCounterReceiver, intentFilter);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "BeaconsMapActivity.onMapReady() called");
        mMap = googleMap;

        mMap.setTrafficEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "Permission denied");
            return;
        }
        mMap.setMyLocationEnabled(false);

        buildGoogleApiClient();

        /*
            TODO : Set marker for beacon found
         */
        double latitude = 46.793207;
        double longitude = 7.158639;
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude))
                .title("Beacon 1")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        googleMap.addMarker(marker);

        // set onMarkerListener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                /*
                    TODO : Display position on marker
                 */
                return true;
            }
        });

        mGoogleApiClient.connect();
    }

    private synchronized void buildGoogleApiClient(){
        Log.d(TAG, "buildGoogleApiClient called");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * This function display a popup dialog which show
     * hints for find the next beacon
     *
     * @param v : source view
     */
    public void onShowHints(View v){
        Log.d(TAG, "BeaconsMapActivity.onShowHints called");
        // create hints dialog
        FragmentManager fm = getFragmentManager();

        HintsDialogFragment dialogFragment = new HintsDialogFragment();
        dialogFragment.show(fm, "Hints list");
    }

    /**
     * This function display a popup dialog which show
     * all the beacons found yet
     *
     * @param v : source view
     */
    public void onShowBeacons(View v){
        Log.d(TAG, "BeaconsMapActivity.onShowBeacons called");
        // create beacons dialog
        FragmentManager fm = getFragmentManager();

        BeaconDialogFragment dialogFragment = new BeaconDialogFragment();
        dialogFragment.show(fm, "Beacons found list");
    }

    /**
     * This function search if the next beacon is near and
     * indicate to the user how near he is from the beacon
     *
     * @param v : source view
     */
    public void onSearchProximity(View v){
        Log.d(TAG, "BeaconsMapActivity.onSearchProximity() called");
        /*
            TODO : Implements beacon search and display the correct color
         */
    }

    /**
     * This function locate the user and center the map
     * on his position
     *
     * @param v : source view
     */
    public void onLocateUser(View v){
        Log.d(TAG, "BeaconsMapActivity.onLocateUser called");
        /*
            TODO : Implements centering map on user position
         */
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "BeaconsMapActivity.onBackPressed() called");
        /*
            TODO : Saves the current game state before activity's destruction
         */
        super.onBackPressed();
    }

    @Override
    public void onStart(){
        Log.d(TAG, "BeaconsMapActivity.onStart() called");
        super.onStart();
    }

    @Override
    public void onRestart(){
        Log.d(TAG, "BeaconsMapActivity.onRestart() called");
        super.onRestart();
    }

    @Override
    public void onResume(){
        Log.d(TAG, "BeaconsMapActivity.onResume() called");
        super.onResume();
    }

    @Override
    public void onPause(){
        Log.d(TAG, "BeaconsMapActivity.onPause() called");
        super.onPause();
        stopService(serviceIntent);
    }

    @Override
    public void onStop(){
        Log.d(TAG, "BeaconsMapActivity.onStop() called");
        super.onStop();
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "BeaconsMapActivity.onDestroy() called");
        super.onDestroy();
    }

    @Override
    public void onConnected (Bundle bundle){
        Log.d(TAG, "onConnected called");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "Permission denied");
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLastLocation != null){
            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title("YOU");
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            Marker currLocationMarker = mMap.addMarker(markerOptions);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    /**
     *  TI - TreasureHunt
     *  Gremaud D., Mertenat S.
     *
     *  This class implements the broadcast receiver to get
     *  information from StepCounterService
     *
     */
    private class StepCounterReceiver extends BroadcastReceiver {
        private StepCounterReceiver(){}

        @Override
        public void onReceive(Context context, Intent intent){
            if(intentFilter != null){
                final String event = intent.getAction();
                if(intentFilter.getAction(0).equals(event)){
                    steps++;
                    Button bt = (Button) findViewById(R.id.showStep);
                    String text = "Steps : " + String.valueOf(steps);
                    bt.setText(text);
                }
            }
        }
    }
}
