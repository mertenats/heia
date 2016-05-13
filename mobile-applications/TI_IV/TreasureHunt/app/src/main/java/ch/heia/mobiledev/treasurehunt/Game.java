package ch.heia.mobiledev.treasurehunt;

import java.util.ArrayList;

/**
 * This class is used to store the state of the game after a pressure on the "back" button
 * We use Gson to serialize the class/create a instance
 */

class Game {
    private int mDiscoverableBeaconsIndex;
    private final ArrayList<DiscoverableBeacon> mDiscoverableBeacons;

    public Game() {
        // create manually the list of beacons to be discover during the game
        ArrayList<DiscoverableBeacon> discoverableBeacons = new ArrayList<>();
        discoverableBeacons.add(new DiscoverableBeacon(246, "https://onedrive.live.com/download.aspx?cid=9029D0C756D0A25C&resid=9029d0c756d0a25c%21434&authkey=%21AOyXHfk56pXiPPQ&canary=", -1, -1, -1, null));
        discoverableBeacons.add(new DiscoverableBeacon(247, "https://onedrive.live.com/download?cid=9029D0C756D0A25C&resid=9029D0C756D0A25C%21435&authkey=AF_xhDl4O43BH5c", -1, -1, -1, null));
        discoverableBeacons.add(new DiscoverableBeacon(248, "https://onedrive.live.com/download?cid=9029D0C756D0A25C&resid=9029D0C756D0A25C%21345&authkey=AFom4lFP0egO6Qs", -1, -1, -1, null));
        discoverableBeacons.add(new DiscoverableBeacon(249, "https://onedrive.live.com/download?cid=9029D0C756D0A25C&resid=9029D0C756D0A25C%21436&authkey=AIWa3ZShZQ-hJ8Q", -1, -1, -1, null));

        // assign the first beacon's ID and the beacon's list to the game instance
        this.mDiscoverableBeacons = discoverableBeacons;
        //int mCurrentBeaconMinor = this.mDiscoverableBeacons.get(0).getMinor();
        this.mDiscoverableBeaconsIndex = 0;
    }

    public ArrayList<DiscoverableBeacon> getDiscoverableBeacons() {
        return mDiscoverableBeacons;
    }

    public int getDiscoverableBeaconsIndex() {
        return mDiscoverableBeaconsIndex;
    }

    public void setDiscoverableBeaconsIndex(int mDiscoverableBeaconsIndex) {
        this.mDiscoverableBeaconsIndex = mDiscoverableBeaconsIndex;
    }

    public int getCurrentBeaconMinor() {
        return this.getDiscoverableBeacons().get(this.getDiscoverableBeaconsIndex()).getMinor();
    }
}
