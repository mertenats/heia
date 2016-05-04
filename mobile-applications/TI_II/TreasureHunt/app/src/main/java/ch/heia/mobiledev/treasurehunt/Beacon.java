package ch.heia.mobiledev.treasurehunt;


import android.bluetooth.le.ScanResult;
import android.util.Log;

import java.util.Arrays;

public class Beacon {

    // used for logging
    private static final String TAG = Beacon.class.getSimpleName();

    // Less than a meter away
    public static final int PROXIMITY_IMMEDIATE = 1;
    // More than  meter away, but less than four meters away
    public static final int PROXIMITY_NEAR = 2;
    // More than four meters away
    public static final int PROXIMITY_FAR = 3;
    // No distance estimate was possible due to a bad RSSI value or measured TX power
    private static final int PROXIMITY_UNKNOWN = 0;

    private static final int B_INDEX= 2;
    private static final int P_INDEX = 4;
    private static final int P_LENGTH = 16;
    private static final int M_INDEX = 20;
    private static final int N_INDEX = 22;
    private static final int PO_INDEX = 24;

    private static int next_beacon = 246;

    private final String uuid;
    private final int major;
    private final int minor;
    private final int txLevel;
    private final int rssi;

    /**
     * Beacon constructor
     */
    private Beacon(String uuid, int major, int minor, int txLevel, int rssi){
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.txLevel = txLevel;
        this.rssi = rssi;
    }

    /**
     * This function increment the next minor number to find next beacon
     */
    public static void incrementNextBeacon(){
        next_beacon++;
    }

    /**
     * This function parse the result of detected beacon and return
     * an instance of Beacon with all data collected
     *
     * @param result : data to parse
     * @return Beacon instance
     */
    public static Beacon createFromScanResult(ScanResult result){
        //Log.d(TAG, "Beacon.createFromScanResult called");
        byte[] scanRecord = result.getScanRecord().getBytes();
        boolean isOk = false;

        int start = 2;
        while (start <= 5) {
            if (((int) scanRecord[start+B_INDEX] & 0xff) == 0x02 && //Identifies an iBeacon
                    ((int) scanRecord[start+B_INDEX+1] & 0xff) == 0x15) { //Identifies correct data length
                isOk = true;
                break;
            }
            start++;
        }
        if(!isOk) return null;

        // parse information
        byte[] proxyUuid = new byte[P_LENGTH];
        System.arraycopy(scanRecord, start+P_INDEX, proxyUuid, 0, P_LENGTH);
        String brutUuid = bytesToHex(proxyUuid);
        String uuid = brutUuid.substring(0,8) + "-" +
                brutUuid.substring(8,12) + "-" +
                brutUuid.substring(12,16) + "-" +
                brutUuid.substring(16,20) + "-" +
                brutUuid.substring(20,32);
        int major = (scanRecord[start+M_INDEX] & 0xff) * 0x100 + (scanRecord[start+M_INDEX+1] & 0xff);
        int minor = (scanRecord[start+N_INDEX] & 0xff) * 0x100 + (scanRecord[start+N_INDEX+1] & 0xff);
        int txLevel = (scanRecord[start+PO_INDEX]);
        int rssi = result.getRssi();

        // verification of the beacon data
        if(major != 1) return null;
        if(minor != next_beacon) return null;

        return new Beacon(uuid, major, minor, txLevel, rssi);
    }

    /**
     * This function analyse a certain number of measure and take
     * the median value for calculated distance
     *
     * @param measure : array which contain measure to analyse
     * @return the median value for calculated distance
     */
    public static double analyzeLastData(Beacon[] measure){
        double[] arrayDist = new double[measure.length];
        for(int i=0; i<measure.length; i++){
            arrayDist[i] = measure[i].calculateAccuracy();
        }
        double median;
        Arrays.sort(arrayDist);
        if(arrayDist.length % 2 == 0){
            median = ( arrayDist[arrayDist.length/2] + arrayDist[arrayDist.length/2 - 1])/2;
        }
        else {
            median = (arrayDist[arrayDist.length/2]);
        }
        return median;
    }

    /**
     * This function transform a byte array into hexadecimal String
     *
     * src : http://stackoverflow.com/a/9855338
     *
     * @param bytes : bytes array to transform
     * @return String of hexadecimal
     */
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * This function evaluate the distance between user and the beacon
     *
     * RSSI[dBm] = -10*n*log10(distance)+Tx, n = signal propagation constant (e.g. 2 in free space)
     *
     * src : http://s2is.org/Issues/v1/n2/papers/paper14.pdf
     *
     * @return the distance between user and beacon
     */
    public double calculateAccuracy(){
        double n = 2.0;
        return Math.pow(10d, ((double) rssi - txLevel) / (10 * n));
    }

    /**
     * This function give a general idea of how far the beacon is away
     *
     * @return a general idea of how far is the beacon
     */
    public static int calculateProximity(Beacon[] mesure) {
        double accuracy = analyzeLastData(mesure);
        Log.d("Accuracy", "Accuracy = " + accuracy);
        /*if (accuracy < 0) {
            return PROXIMITY_UNKNOWN;
        }
        if (accuracy < 1) {
            return PROXIMITY_IMMEDIATE;
        }
        if (accuracy <= 4.0) {
            return PROXIMITY_NEAR;
        }
        return PROXIMITY_FAR;
        */
        return PROXIMITY_IMMEDIATE;
    }

    /**
     * This function display display the information
     * of a beacon in console
     */
    public void showBeaconLog(){
        Log.i(TAG,"UUID: "+uuid);
        Log.i(TAG,"Major: "+major);
        Log.i(TAG,"Minor: "+minor);
        Log.i(TAG,"TxLevel: "+txLevel);
        Log.i(TAG,"RSSI: "+rssi);
    }

    public int showRssi(){
        return rssi;
    }
}
