package ch.heia.mobiledev.treasurehunt;

class DiscoverableBeacon {
    private final int mMinor;
    private final String mHintUrl;
    private int mStepsNumber;
    private boolean mIsFound;
    private double mLongitude;
    private double mLatitude;
    private String mHints;

    public DiscoverableBeacon(int mId, String mHintUrl, int mStepsNumber, double mLatitude, double mLongitude, String mHints) {
        this.mMinor = mId;
        this.mHintUrl = mHintUrl;
        this.mStepsNumber = mStepsNumber;
        this.mIsFound = false;
        this.mLongitude = mLongitude;
        this.mLatitude = mLatitude;
        this.mHints = mHints;
    }

    public int getMinor() {
        return mMinor;
    }

    public String getHintUrl() {
        return mHintUrl;
    }

    public int getStepsNumber() {
        return mStepsNumber;
    }

    public boolean isFound() {
        return mIsFound;
    }

    public void setIsFound(@SuppressWarnings("SameParameterValue") boolean mIsFound) {
        this.mIsFound = mIsFound;
    }

    public void setStepsNumber(int mStepsNumber) {
        this.mStepsNumber = mStepsNumber;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public void setHints(String mHints) {
        this.mHints = mHints;
    }

    public String getHints() {
        return this.mHints;
    }
}