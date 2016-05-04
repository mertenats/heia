package ch.heia.mobiledev.treasurehunt;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * TI - TreasureHunt
 * Gremaud D., Mertenat S.
 *
 * This activity displays a map and manage the user location and
 * beacons positions
 */
public class BeaconsMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, LocationListener {
    // Attribute for logging
    private static final String TAG = BeaconsMapActivity.class.getSimpleName();

    private static final int REQUEST_STORAGE = 3;
    private static final int REQUEST_LOCATION = 2;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int SCAN_PERIOD = 15000;


    private GoogleApiClient mGoogleApiClient;

    // Attributes for bluetooth low energy
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;
    private boolean scanning = false;

    // Attributes for step counter
    private IntentFilter intentFilter;
    private int initialSteps = -1;
    private int mSteps = 0;
    private int mCurrentSteps = 0; // steps # for the current beacon
    private Intent serviceIntent;

    private GoogleMap mMap;  // map save

    // used for performing download with an async task
    private DownloadAsyncTask mDownloadAsyncTask;

    // list of the discoverable beacons
    //private ArrayList<DiscoverableBeacon> mDiscoverableBeacons;
    //private int mDiscoverableBeaconsIndex = 0; // 0 = no beacon found
    private Game mGame;

    private static final int SIZE_OF_SCANMESURE = 10;
    private int scanCount = 0;
    private Beacon[] scanMesure = new Beacon[SIZE_OF_SCANMESURE];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "BeaconsMapActivity.onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons_map);

        // TODO: load game if one was already instancieted
        //String json  = new String("{\"mCurrentBeaconMinor\":246,\"mDiscoverableBeacons\":[{\"mHintUrl\":\"https://onedrive.live.com/download.aspx?cid\u003d9029D0C756D0A25C\u0026resid\u003d9029d0c756d0a25c%21434\u0026authkey\u003d%21AOyXHfk56pXiPPQ\u0026canary\u003d\",\"mIsFound\":true,\"mLatitude\":46.7928364,\"mLongitute\":7.1599716,\"mMinor\":246,\"mStepsNumber\":0},{\"mHintUrl\":\"https://onedrive.live.com/download?cid\u003d9029D0C756D0A25C\u0026resid\u003d9029D0C756D0A25C%21435\u0026authkey\u003dAF_xhDl4O43BH5c\",\"mIsFound\":false,\"mLatitude\":-1.0,\"mLongitute\":-1.0,\"mMinor\":247,\"mStepsNumber\":-1}");
        //mGame = Game.loadGame(json);
        mGame = new Game();

       /*
       mDiscoverableBeacons = new ArrayList<>();
        mDiscoverableBeacons.add(new DiscoverableBeacon(246, "https://onedrive.live.com/download.aspx?cid=9029D0C756D0A25C&resid=9029d0c756d0a25c%21434&authkey=%21AOyXHfk56pXiPPQ&canary=", -1, -1, -1));
        mDiscoverableBeacons.add(new DiscoverableBeacon(247, "https://onedrive.live.com/download?cid=9029D0C756D0A25C&resid=9029D0C756D0A25C%21435&authkey=AF_xhDl4O43BH5c", -1, -1, -1));
        mDiscoverableBeacons.add(new DiscoverableBeacon(248, "https://onedrive.live.com/download?cid=9029D0C756D0A25C&resid=9029D0C756D0A25C%21345&authkey=AFom4lFP0egO6Qs", -1, -1, -1));
        mDiscoverableBeacons.add(new DiscoverableBeacon(249, "https://onedrive.live.com/download?cid=9029D0C756D0A25C&resid=9029D0C756D0A25C%21436&authkey=AIWa3ZShZQ-hJ8Q", -1, -1, -1));
*/
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        LocationRequest mLocationRequestHighAccuracy = LocationRequest.create();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Requesting enabling location
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequestHighAccuracy);
        final PendingResult<LocationSettingsResult> result = LocationServices.
                SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(BeaconsMapActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            Log.d(TAG, e.toString());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });

        // initialize Bluetooth
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        handler = new Handler();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
        switch(requestCode){
            case REQUEST_LOCATION:
                switch(resultCode){
                    case Activity.RESULT_OK:
                        zoomOnUser();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                    default:
                        break;
                }
                break;
            default:
                break;
        }
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            mMap.setTrafficEnabled(false);
            mMap.setMyLocationEnabled(false);
            //placeBeaconMarker();
        }
    }

    private void placeBeaconMarker(LatLng latLng) {
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).title("Beacon " + (mGame.getDiscoverableBeaconsIndex() + 1) + " (minor: " + mGame.getDiscoverableBeacons().get(mGame.getDiscoverableBeaconsIndex()).getMinor() + ", position: " + latLng.latitude + " " + latLng.longitude + ")")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(marker);

        // set onMarkerListener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });
    }

    private synchronized void buildGoogleApiClient() {
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
    public void onShowHints(View v) {
        Log.d(TAG, "BeaconsMapActivity.onShowHints() called");

        // create a new asynctask and retrieve the hints from the onedrive
        mDownloadAsyncTask = new DownloadAsyncTask();
        //mDownloadAsyncTask.execute(mDiscoverableBeacons.get(mDiscoverableBeaconsIndex).getHintUrl());
        mDownloadAsyncTask.execute(mGame.getDiscoverableBeacons().get(mGame.getDiscoverableBeaconsIndex()).getHintUrl());
    }

    /**
     * This function display a popup dialog which show
     * all the beacons found yet
     *
     * @param v : source view
     */
    public void onShowBeacons(View v) {
        Log.d(TAG, "BeaconsMapActivity.onShowBeacons called");

        // create a bundle with the hints and send them to the fragment
        Bundle bundle = new Bundle();
        String beacons = "No beacons found";

        // create a string which contains the informations to display
        //noinspection StatementWithEmptyBody
        //if (mDiscoverableBeaconsIndex == 0) {
        if (mGame.getDiscoverableBeaconsIndex() == 0) {
            // nothing to do
        } else {
            beacons = ""; // clear the string
            //for (int i = 0; i < mDiscoverableBeaconsIndex; i++) {
            for (int i = 0; i < mGame.getDiscoverableBeaconsIndex(); i++) {
                // double check if the beacon was found
                /*
                if (mDiscoverableBeacons.get(i).isFound()) {
                    beacons += "Beacon " + (mDiscoverableBeaconsIndex) + " found in " + mDiscoverableBeacons.get(i).getStepsNumber() + " step(s) (minor: " + mDiscoverableBeacons.get(i).getId() + ", position: " + mDiscoverableBeacons.get(i).getLatitude() + " " + mDiscoverableBeacons.get(i).getLongitute() + ")\n";
                }
                */
                if (mGame.getDiscoverableBeacons().get(i).isFound()) {
                    beacons += "Beacon " + (mGame.getDiscoverableBeaconsIndex()) + " found in " + mGame.getDiscoverableBeacons().get(i).getStepsNumber() + " step(s) (minor: " + mGame.getDiscoverableBeacons().get(i) + ", position: " + mGame.getDiscoverableBeacons().get(i).getLatitude() + " " + mGame.getDiscoverableBeacons().get(i).getLongitute() + ")\n";
                }
            }
        }

        // create beacons dialog
        FragmentManager fm = getFragmentManager();

        // create hints dialog
        BeaconDialogFragment dialogFragment = new BeaconDialogFragment();
        bundle.putString("beacons", beacons);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "Beacons found list");
    }

    /**
     * This function search if the next beacon is near and
     * indicate to the user how near he is from the beacon
     *
     * @param v : source view
     */
    public void onSearchProximity(View v) {
        Log.d(TAG, "BeaconsMapActivity.onSearchProximity() called");
        // ask user for activate bluetooth if not
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            scanning = true;
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            scanLeDevice(scanning);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if ((grantResults.length > 0) &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    scanning = true;
                    bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
                    scanLeDevice(scanning);
                }
                break;
            case REQUEST_LOCATION:
                //noinspection StatementWithEmptyBody
                if ((grantResults.length > 0) &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    zoomOnUser();
                    //placeBeaconMarker();
                }
                break;
            case REQUEST_STORAGE:
                //noinspection StatementWithEmptyBody
                if ((grantResults.length > 0) &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    zoomOnUser();

                } else {
                    finish();
                }
                break;
        }
    }

    private void zoomOnUser(){
        Log.d(TAG, "BeaconsMapActivity.zoomOnUser() called");
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Log.d(TAG, "permission granted");
            mMap.setMyLocationEnabled(false);
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                Log.d(TAG, "mLasLocation not null");
                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title("Current Position");
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                mMap.addMarker(markerOptions);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                            //
    //  Bluetooth part : used to discover beacons                                                 //
    //                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    bluetoothLeScanner.stopScan(mLeScanCallback);
                    Log.d(TAG, "Thread stop scan");
                    ImageButton bt = (ImageButton) findViewById(R.id.showProximity);
                    bt.setBackgroundResource(R.drawable.roundbutton);

                }
            }, SCAN_PERIOD);
            bluetoothLeScanner.startScan(mLeScanCallback);
            ImageButton bt = (ImageButton) findViewById(R.id.showProximity);
            bt.setBackgroundResource(R.drawable.roundbuttonblack);
            Log.d(TAG, "Thread start scan");
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(mLeScanCallback);
            Log.d(TAG, "Thread stop scan2");
            ImageButton bt = (ImageButton) findViewById(R.id.showProximity);
            bt.setBackgroundResource(R.drawable.roundbutton);
        }
    }

    private final ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            //Log.i("Result", result.toString());
            Beacon data;
            data = Beacon.createFromScanResult(result);
            if (data != null) {
                scanLeDevice(false);
                data.showBeaconLog();
                scanMesure[scanCount] = data;
                scanCount++;

                // TODO : remove this 2 lines after debug
                double dist = data.calculateAccuracy();
                Log.d("Distance", "distance = " + dist);

                if (scanCount < SIZE_OF_SCANMESURE) {
                    scanLeDevice(true);
                    return;
                }
                double distance = Beacon.analyzeLastData(scanMesure);
                Toast.makeText(BeaconsMapActivity.this, "Distance = " + distance,
                        Toast.LENGTH_LONG).show();
                int prox = Beacon.calculateProximity(scanMesure);
                scanCount = 0;
                scanMesure = new Beacon[SIZE_OF_SCANMESURE];
                ImageButton bt = (ImageButton) findViewById(R.id.showProximity);
                if (prox == Beacon.PROXIMITY_IMMEDIATE) {
                    // button color : red
                    bt.setBackgroundResource(R.drawable.roundbuttonred);

                    // TODO: uncomment this line
                    Beacon.incrementNextBeacon();
                    Log.d(TAG, "beacon found ! ");

                    mGame.getDiscoverableBeacons().get(mGame.getDiscoverableBeaconsIndex()).setIsFound(true);
                    mGame.getDiscoverableBeacons().get(mGame.getDiscoverableBeaconsIndex()).setStepsNumber(mSteps - mCurrentSteps);
                    //mDiscoverableBeacons.get(mDiscoverableBeaconsIndex).setIsFound(true);
                    //mDiscoverableBeacons.get(mDiscoverableBeaconsIndex).setStepsNumber(mSteps - mCurrentSteps);
                    mCurrentSteps = mSteps;

                    // get the current location of the user
                    // create a marker on the map for the beacon
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                    } else {
                        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                        if (mLastLocation != null) {
                            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                            mGame.getDiscoverableBeacons().get(mGame.getDiscoverableBeaconsIndex()).setLatitude(latLng.latitude);
                            mGame.getDiscoverableBeacons().get(mGame.getDiscoverableBeaconsIndex()).setLongitute(latLng.longitude);
                            //mDiscoverableBeacons.get(mDiscoverableBeaconsIndex).setLatitude(latLng.latitude);
                            //mDiscoverableBeacons.get(mDiscoverableBeaconsIndex).setLongitute(latLng.longitude);

                            // place a marker at the position
                            placeBeaconMarker(latLng);
                        }
                    }

                    // create a bundle with the hints and send them to the fragment
                    Bundle bundle = new Bundle();
                    String message = "Congratulation! You have found a beacon";

                    mGame.setDiscoverableBeaconsIndex(mGame.getDiscoverableBeaconsIndex() + 1);
                    //mDiscoverableBeaconsIndex++;
                    /*
                    if (mDiscoverableBeaconsIndex == mDiscoverableBeacons.size()) {
                        message = "Congratulation! You've finished the game.";
                    }
                    */
                    if (mGame.getDiscoverableBeaconsIndex() == mGame.getDiscoverableBeacons().size()) {
                        message = "Congratulation! You've finished the game.";
                    }
                    bundle.putString("message", message);

                    // create hints dialog
                    FragmentManager fm = getFragmentManager();
                    BeaconFoundDialogFragment dialogFragment = new BeaconFoundDialogFragment();
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(fm, "Congratulation");

                    // hide the red circle
                    bt.setBackgroundResource(R.drawable.roundbutton);
                } else if (prox == Beacon.PROXIMITY_NEAR) {
                    // button color : orange
                    bt.setBackgroundResource(R.drawable.roundbuttonorange);
                } else if (prox == Beacon.PROXIMITY_FAR) {
                    // button color : yellow
                    bt.setBackgroundResource(R.drawable.roundbuttonyellow);
                } else {
                    // button color : default
                    bt.setBackgroundResource(R.drawable.roundbutton);
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.i("Error", "ERROR" + errorCode);
        }
    };

    /**
     * This function locate the user and center the map
     * on his position
     *
     * @param v : source view
     */
    public void onLocateUser(View v) {
        Log.d(TAG, "BeaconsMapActivity.onLocateUser called");
        /*
            TODO : Implements centering map on user position
         */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            mMap.setMyLocationEnabled(false);
            zoomOnUser();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "BeaconsMapActivity.onBackPressed() called");
        /*
            TODO : Saves the current game state before activity's destruction
         */

        Gson gson = new Gson();
        String json = gson.toJson(mGame);

        // TODO: save the json into a file

        Log.d(TAG, "BeaconsMapActivity.onBackPressed() called" + json);

        super.onBackPressed();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "BeaconsMapActivity.onStart() called");
        super.onStart();
    }

    @Override
    public void onRestart() {
        Log.d(TAG, "BeaconsMapActivity.onRestart() called");
        super.onRestart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "BeaconsMapActivity.onResume() called");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "BeaconsMapActivity.onPause() called");
        super.onPause();
        //stopService(serviceIntent);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "BeaconsMapActivity.onStop() called");
        stopService(serviceIntent);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "BeaconsMapActivity.onDestroy() called");
        if (mDownloadAsyncTask != null) {
            mDownloadAsyncTask.cancel(true);
            mDownloadAsyncTask = null;
        }
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected called");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title("Your initial position");
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
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    /**
     * TI - TreasureHunt
     * Gremaud D., Mertenat S.
     * <p/>
     * This class implements the broadcast receiver to get
     * information from StepCounterService
     */
    private class StepCounterReceiver extends BroadcastReceiver {
        private StepCounterReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intentFilter != null) {
                final String event = intent.getAction();
                if (intentFilter.getAction(0).equals(event)) {
                    if(initialSteps == -1){
                        initialSteps = (int) intent.getFloatExtra(StepCounterService.EXTRA_PARAM_STEP, 1);
                    }
                    else{
                        mSteps = (int) intent.getFloatExtra(StepCounterService.EXTRA_PARAM_STEP, 1) - initialSteps;
                    }
                    Log.d(TAG, "step : " + mSteps);
                    Button bt = (Button) findViewById(R.id.showStep);
                    String text = "Steps : " + String.valueOf(mSteps);
                    bt.setText(text);
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                            //
    //  DownloadAsyncTask : used to download the hints from the drive                             //
    //                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // internal class implementing the download
    private class DownloadAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // nothing to do on pre-execution
            Log.d(TAG, "BeaconsMapActivity.DownloadAsyncTask.onPreExecute() called");

            ImageButton bt = (ImageButton) findViewById(R.id.show_hints);
            bt.setBackgroundResource(R.drawable.roundbuttonblack);
        }

        @Override
        protected String doInBackground(String... urls) {
            if (urls.length != 1) {
                Log.d(TAG, "BeaconsMapActivity.DownloadAsyncTask.doInBackground() called : ERROR: no url");
                return null;
            }
            Log.d(TAG, "BeaconsMapActivity.DownloadAsyncTask.doInBackground() called");

            // build the output path and create the output file
            String fileName = "hints.txt";
            File output = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            if (output.exists()) {
                boolean rc = output.delete();
                if (!rc) {
                    Log.d(TAG, "BeaconsMapActivity.DownloadAsyncTask.doInBackground() called : ERROR: could not delete output file");
                    return null;
                }
            }

            // start download and save the results to the output file
            InputStream stream = null;
            FileOutputStream fos = null;
            try {
                // open the url connection
                URL url = new URL(urls[0]);
                URLConnection urlConnection = url.openConnection();
                stream = urlConnection.getInputStream();

                // create the output sream
                fos = new FileOutputStream(output.getPath());

                // download using a buffer of buffer_size bytes
                final int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int next;
                while ((next = stream.read(buffer, 0, bufferSize)) != -1 && !isCancelled()) {
                    fos.write(buffer, 0, next);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            String hints = "";
            FileInputStream fis;
            try {
                File input = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                fis = new FileInputStream(input.getPath());
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bf = new BufferedReader(isr);
                String line;
                while ((line = bf.readLine()) != null) {
                    //noinspection StatementWithEmptyBody
                    if (line.contains("minor")) {
                        // do nothing
                        // first line of the text hint, which contains "minor"
                        // example: Note: minor 247
                    } else //noinspection StatementWithEmptyBody
                        if (line.trim().length() == 0) {
                            // do nothing
                            // second line of the text hint
                            // the line contains no letters/numbers
                        } else {
                            // add the line to the string
                            hints += line + "\n";
                        }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return hints;
        }

        @Override
        protected void onPostExecute(String hints) {
            Log.d(TAG, "BeaconsMapActivity.DownloadAsyncTask.onPostExecute() called");

            ImageButton bt = (ImageButton) findViewById(R.id.show_hints);
            bt.setBackgroundResource(R.drawable.roundbutton);

            // create a bundle with the hints and send them to the fragment
            Bundle bundle = new Bundle();
            bundle.putString("hints", hints);

            // create hints dialog
            FragmentManager fm = getFragmentManager();
            HintsDialogFragment dialogFragment = new HintsDialogFragment();
            dialogFragment.setArguments(bundle);
            dialogFragment.show(fm, "Hints list");
        }
    }
}
