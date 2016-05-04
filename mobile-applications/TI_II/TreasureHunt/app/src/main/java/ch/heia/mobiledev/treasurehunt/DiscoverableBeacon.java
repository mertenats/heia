package ch.heia.mobiledev.treasurehunt;

class DiscoverableBeacon {
    private final int mMinor;
    private final String mHintUrl;
    private int mStepsNumber;
    private boolean mIsFound;
    private double mLongitute;
    private double mLatitude;

    public DiscoverableBeacon(int mId, String mHintUrl, int mStepsNumber, double mLatitude, double mLongitute) {
        this.mMinor = mId;
        this.mHintUrl = mHintUrl;
        this.mStepsNumber = mStepsNumber;
        this.mIsFound = false;
        this.mLongitute = mLongitute;
        this.mLatitude = mLatitude;
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

    public double getLongitute() {
        return mLongitute;
    }

    public void setLongitute(double mLongitute) {
        this.mLongitute = mLongitute;
    }
}